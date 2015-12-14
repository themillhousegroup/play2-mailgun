package com.themillhousegroup.play2.mailgun

import org.specs2.mutable._
import org.specs2.mock.Mockito
import org.mockito.ArgumentCaptor
import play.twirl.api.Html
import play.api.libs.ws._
import play.api.http._
import play.api.libs.json._
import scala.concurrent.Future
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import org.apache.commons.fileupload.disk._
import scala.Predef._
import scala.Some
import org.apache.commons.fileupload.{ UploadContext, FileUpload, FileItem }

class EmailServiceSpec extends Specification with Mockito {

  val noSenderEmailMessage = EmailMessage(None, "to@to.com", "subject", "text", Html("<em>text</em>"))
  val senderEmailMessage = EmailMessage(Some("from@from.com"), "to@to.com", "subject", "text", Html("<em>text</em>"))

  def givenAnEmailServiceThatReturns(statusCode: Int, message: String = "OK", defSender: Option[String] = None): (MailgunEmailService, WSRequest) = {
    val mockWS = mock[WSRequest]
    val mockResponse = mock[WSResponse]
    mockResponse.json returns Json.obj("message" -> message, "id" -> "abc123")
    mockResponse.status returns statusCode
    mockWS.withHeaders(any[(String, String)]) returns mockWS
    mockWS.withAuth(any[String], any[String], any[WSAuthScheme]) returns mockWS
    mockWS.post[Array[Byte]](any[Array[Byte]])(any[Writeable[Array[Byte]]]) returns Future.successful(mockResponse)
    new MailgunEmailService {
      override lazy val mailgunApiKey = "apiKey"
      override lazy val defaultSender = defSender
      override lazy val mailgunUrl = "url"
      override lazy val ws = mockWS
    } -> mockWS
  }

  def whenTheServiceSends(emailService: MailgunEmailService, msg: EssentialEmailMessage, options: Set[MailgunOption] = Set()) = {
    val timeout = Duration(10, "seconds")

    Await.result(emailService.send(msg, options), timeout)
  }

  def thenMailgunShouldReceive(ws: WSRequest): List[FileItem] = {
    import scala.collection.JavaConverters._

    val byteCaptor = ArgumentCaptor.forClass(classOf[Array[Byte]])
    val headerTupleCaptor = ArgumentCaptor.forClass(classOf[(String, String)])

    there was one(ws).withHeaders(headerTupleCaptor.capture())
    there was one(ws).post(byteCaptor.capture())(any[Writeable[Array[Byte]]])
    val theBytes = byteCaptor.getValue
    theBytes must not beNull

    // Hack to avoid weird casting problems - use toString
    val ct = headerTupleCaptor.getAllValues.toString.substring(28)
    val ctHeader = ct.substring(0, ct.length - 3)

    println(new String(theBytes))

    val fu = new FileUpload(new DiskFileItemFactory())
    val ctx = new UploadContext {
      def getCharacterEncoding = "UTF-8"

      def getContentType = ctHeader

      def contentLength = theBytes.length
      def getContentLength = contentLength.toInt

      def getInputStream: java.io.InputStream = {
        new java.io.ByteArrayInputStream(theBytes)
      }
    }
    fu.parseRequest(ctx).asScala.toList
  }

  "EmailService" should {
    "Bomb out with IllegalStateException if no default from address and none in supplied message" in {
      val (emailService, _) = givenAnEmailServiceThatReturns(200)
      whenTheServiceSends(emailService, noSenderEmailMessage) must throwAn[IllegalStateException]
    }

    "Bomb out with MailgunAuthenticationException wrapping the message if remote API returns a 401" in {
      val (emailService, _) = givenAnEmailServiceThatReturns(401, "Unauthorized - No valid API key provided")

      whenTheServiceSends(emailService, senderEmailMessage) must throwAn[MailgunAuthenticationException]("Unauthorized - No valid API key provided")
    }

    "Bomb out with MailgunSendingException wrapping the message if remote API returns some other status" in {
      val (emailService, _) = givenAnEmailServiceThatReturns(400, "Bad Request")

      whenTheServiceSends(emailService, senderEmailMessage) must throwAn[MailgunSendingException]("Bad Request")
    }
    "Use the sender from the message if supplied" in {
      val (emailService, mockWS) = givenAnEmailServiceThatReturns(200)

      val response = whenTheServiceSends(emailService, senderEmailMessage)
      response must beEqualTo(MailgunResponse("OK", "abc123"))

      val multipartItems = thenMailgunShouldReceive(mockWS)

      multipartItems.size must beEqualTo(5)
      val fieldNames = multipartItems.map(_.getFieldName)
      fieldNames must contain("from")
      val fromField = multipartItems.find(_.getFieldName == "from").get
      fromField.getString must beEqualTo("from@from.com")
    }

    "Use the default sender if not supplied from the message " in {
      val (emailService, mockWS) = givenAnEmailServiceThatReturns(200, "OK", Some("default-sender@from.com"))
      val response = whenTheServiceSends(emailService, noSenderEmailMessage)
      response must beEqualTo(MailgunResponse("OK", "abc123"))

      val multipartItems = thenMailgunShouldReceive(mockWS)

      multipartItems.size must beEqualTo(5)
      val fieldNames = multipartItems.map(_.getFieldName)
      fieldNames must contain("from")
      val fromField = multipartItems.find(_.getFieldName == "from").get
      fromField.getString must beEqualTo("default-sender@from.com")
    }
  }

  "Option support" should {
    "Pass through the appropriate form part for the 'testmode' option" in {
      val (emailService, mockWS) = givenAnEmailServiceThatReturns(200)

      val response = whenTheServiceSends(emailService, senderEmailMessage, Set(SendInTestMode))
      response must beEqualTo(MailgunResponse("OK", "abc123"))

      val multipartItems = thenMailgunShouldReceive(mockWS)

      multipartItems must not beEmpty

      multipartItems.filter(_.getFieldName == "subject") must not beEmpty // Sanity check

      val testModePart = multipartItems.find(_.getFieldName == "o:testmode")
      testModePart must not beNone

      testModePart.get.getString must beEqualTo("true")
    }

    "Pass through the appropriate form part for the 'deliverytime' option" in {
      val (emailService, mockWS) = givenAnEmailServiceThatReturns(200)

      val nowMillis = System.currentTimeMillis
      val inOneMinute = nowMillis + 60000
      val sendInOneMinute = ScheduledSendAt(inOneMinute)

      val response = whenTheServiceSends(emailService, senderEmailMessage, Set(sendInOneMinute))
      response must beEqualTo(MailgunResponse("OK", "abc123"))

      val multipartItems = thenMailgunShouldReceive(mockWS)

      multipartItems must not beEmpty

      multipartItems.filter(_.getFieldName == "subject") must not beEmpty // Sanity check

      val delTimePart = multipartItems.find(_.getFieldName == "o:deliverytime")
      delTimePart must not beNone

      delTimePart.get.getString must beEqualTo(inOneMinute.toString)
    }
  }
}
