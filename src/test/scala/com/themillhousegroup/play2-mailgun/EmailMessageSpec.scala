package com.themillhousegroup.play2.mailgun

import org.specs2.mutable._
import play.twirl.api.Html

class EmailMessageSpec extends Specification {

  val emailMessage = EmailMessage(None, "to@to.com", "subject", "text", Html("<em>text</em>"))

  "EmailMessage" should {
    "Set the cc field to None" in {
      emailMessage.cc must beNone
    }

    "Set the bcc field to None" in {
      emailMessage.bcc must beNone
    }
  }
}
