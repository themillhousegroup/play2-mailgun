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

  def givenAnEmailServiceThatReturns(statusCode: Int, message: String = "OK", defaultSender: Option[String] = None): (MailgunEmailService, WSRequestHolder) = {
    val mockWS = mock[WSRequestHolder]
    val mockResponse = mock[WSResponse]
    mockResponse.json returns Json.obj("message" -> message, "id" -> "abc123")
    mockResponse.status returns statusCode
    mockWS.withAuth(any[String], any[String], any[WSAuthScheme]) returns mockWS
    mockWS.post[Array[Byte]](any[Array[Byte]])(any[Writeable[Array[Byte]]], any[ContentTypeOf[Array[Byte]]]) returns Future.successful(mockResponse)
    (new MailgunEmailService("apiKey", defaultSender)(mockWS), mockWS)
  }

  def whenTheServiceSends(emailService: MailgunEmailService, msg: EssentialEmailMessage) = {
    val timeout = Duration(10, "seconds")

    Await.result(emailService.send(msg), timeout)
  }

  def andMailgunShouldReceive(ws: WSRequestHolder): List[FileItem] = {
    import scala.collection.JavaConverters._

    val byteCaptor = ArgumentCaptor.forClass(classOf[Array[Byte]])
    val contentTypeCaptor = ArgumentCaptor.forClass(classOf[ContentTypeOf[Array[Byte]]])

    there was one(ws).post(byteCaptor.capture())(any[Writeable[Array[Byte]]], contentTypeCaptor.capture())
    val theBytes = byteCaptor.getValue
    theBytes must not beNull

    println(new String(theBytes))

    val fu = new FileUpload(new DiskFileItemFactory())
    val ctx = new UploadContext {
      def getCharacterEncoding = "UTF-8"

      def getContentType = contentTypeCaptor.getValue.mimeType.get

      def getContentLength = contentLength.toInt
      def contentLength = theBytes.length

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

    "Bomb out with MailgunSendingException wrapping the message if remote API doesn't return a 200" in {
      val (emailService, _) = givenAnEmailServiceThatReturns(401, "Unauthorized - No valid API key provided")

      whenTheServiceSends(emailService, senderEmailMessage) must throwAn[MailgunSendingException]("Unauthorized - No valid API key provided")
    }

    "Use the sender from the message if supplied" in {
      val (emailService, mockWS) = givenAnEmailServiceThatReturns(200)

      val response = whenTheServiceSends(emailService, senderEmailMessage)
      response must beEqualTo(MailgunResponse("OK", "abc123"))

      val multipartItems = andMailgunShouldReceive(mockWS)

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

      val multipartItems = andMailgunShouldReceive(mockWS)

      multipartItems.size must beEqualTo(5)
      val fieldNames = multipartItems.map(_.getFieldName)
      fieldNames must contain("from")
      val fromField = multipartItems.find(_.getFieldName == "from").get
      fromField.getString must beEqualTo("default-sender@from.com")
    }
  }
}
