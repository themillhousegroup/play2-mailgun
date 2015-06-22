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

class EmailServiceSpec extends Specification with Mockito {
  val timeout = Duration(10, "seconds")
  val mockWS = mock[WSRequestHolder]
  val mockResponse = mock[WSResponse]
  mockResponse.json returns Json.obj("message" -> "OK", "id" -> "abc123")
  mockWS.post[Array[Byte]](any[Array[Byte]])(any[Writeable[Array[Byte]]], any[ContentTypeOf[Array[Byte]]]) returns Future.successful(mockResponse)

  val noSenderEmailMessage = EmailMessage(None, "to@to.com", "subject", "text", Html("<em>text</em>"))
  val senderEmailMessage = EmailMessage(Some("from@from.com"), "to@to.com", "subject", "text", Html("<em>text</em>"))
  val emailService = new EmailService("apiKey", None)(mockWS)

  "EmailService" should {
    "Bomb out if no default from address and none in supplied message" in {
      Await.result(emailService.send(noSenderEmailMessage), timeout) must throwAn[IllegalStateException]
    }

    "Use the sender from the message if supplied" in {
      val response = Await.result(emailService.send(senderEmailMessage), timeout)
      response must beEqualTo(MailgunResponse("OK", "abc123"))

      val captor = ArgumentCaptor.forClass(classOf[Array[Byte]])

      val expected = List[Byte](77, 77, 88, 88, 99).toArray
      there was one(mockWS).post(captor.capture())(any[Writeable[Array[Byte]]], any[ContentTypeOf[Array[Byte]]])

      val theBytes = captor.getValue
      println(new String(theBytes))

      theBytes must not beNull
    }
  }
}
