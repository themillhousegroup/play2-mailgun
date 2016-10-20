package com.themillhousegroup.play2.mailgun

import org.specs2.mutable._
import play.twirl.api.Html

class EmailMessageSpec extends Specification {

  "EmailMessage" should {
    val emailMessage = EmailMessage(None, "to@to.com", "subject", "text", Html("<em>text</em>"))

    "Set the to field as required" in {
      emailMessage.to must beEqualTo("to@to.com")
    }

    "Set the cc field to None" in {
      emailMessage.cc must beNone
    }

    "Set the bcc field to None" in {
      emailMessage.bcc must beNone
    }
  }

  "MulticastEmailMessage" should {

    val multicastEmailMessage = MulticastEmailMessage(
      None,
      None,
      Seq("to1@to.com", "to2@to.com"),
      Seq("cc1@cc.com", "cc2@cc.com"),
      Seq("bcc1@bcc.com", "bcc2@bcc.com"),
      "subject", "text", Html("<em>text</em>")
    )

    "Set the to field as required, comma-separated" in {
      multicastEmailMessage.to must beEqualTo("to1@to.com, to2@to.com")
    }

    "Set the cc field to None" in {
      multicastEmailMessage.cc must beSome("cc1@cc.com, cc2@cc.com")
    }

    "Set the bcc field to None" in {
      multicastEmailMessage.bcc must beSome("bcc1@bcc.com, bcc2@bcc.com")
    }

    "Have no additional headers by default" in {
      multicastEmailMessage.computedHeaders must beEmpty
    }

    "Make a replyTo field into an additional header" in {

      val multicastEmailMessageWithReplyTo = MulticastEmailMessage(
        None,
        Some("reply-to@rt.com"),
        Seq("to1@to.com", "to2@to.com"),
        Seq("cc1@cc.com", "cc2@cc.com"),
        Seq("bcc1@bcc.com", "bcc2@bcc.com"),
        "subject", "text", Html("<em>text</em>")
      )

      multicastEmailMessageWithReplyTo.computedHeaders must beEqualTo(Seq("Reply-To" -> "reply-to@rt.com"))
    }
  }
}
