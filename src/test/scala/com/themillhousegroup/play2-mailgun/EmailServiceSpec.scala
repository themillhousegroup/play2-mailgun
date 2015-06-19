package com.themillhousegroup.play2.mailgun

import org.specs2.mutable._
import org.specs2.mock.Mockito
import play.twirl.api.Html
import play.api.libs.ws._
import scala.concurrent.Await
import scala.concurrent.duration.Duration

class EmailServiceSpec extends Specification with Mockito {
  val timeout = Duration(10, "seconds")
  val mockWS = mock[WSRequestHolder]

  val emailMessage = EmailMessage(None, "to@to.com", "subject", "text", Html("<em>text</em>"))
  val emailService = new EmailService("apiKey", None)(mockWS)

  "EmailService" should {
    "Bomb out if no default from address and none in supplied message" in {
      Await.result(emailService.send(emailMessage), timeout) must throwAn[IllegalStateException]
    }
  }
}
