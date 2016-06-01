package com.themillhousegroup.play2.mailgun

import akka.stream.scaladsl.Source
import akka.util.ByteString
import org.specs2.mutable._
import org.specs2.mock.Mockito
import org.mockito.ArgumentCaptor
import play.twirl.api.Html
import play.api.libs.ws._
import play.api._
import play.api.http._
import play.api.libs.json._

import scala.concurrent.Future
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import org.apache.commons.fileupload.disk._

import scala.Predef._
import scala.Some
import org.apache.commons.fileupload.{ FileItem, FileUpload, UploadContext }
import play.api.mvc.MultipartFormData
import play.api.mvc.MultipartFormData.DataPart

class EmailServiceSpec extends Specification with Mockito {

  val noSenderEmailMessage = EmailMessage(None, "to@to.com", "subject", "text", Html("<em>text</em>"))
  val senderEmailMessage = EmailMessage(Some("from@from.com"), "to@to.com", "subject", "text", Html("<em>text</em>"))

  def givenAnEmailServiceThatReturns(statusCode: Int, message: String = "OK", defSender: Option[String] = None): (MailgunEmailService, WSRequest) = {
    val mockWS = mock[WSRequest]
    val mockResponse = mock[WSResponse]
    mockResponse.json returns Json.obj("message" -> message, "id" -> "abc123")
    mockResponse.status returns statusCode
    mockWS.withAuth(any[String], any[String], any[WSAuthScheme]) returns mockWS
    mockWS.post(any[MailgunEmailService.PostData]) returns Future.successful(mockResponse)

    new MailgunEmailService(mock[WSClient], mock[Configuration]) {
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
  }
}
