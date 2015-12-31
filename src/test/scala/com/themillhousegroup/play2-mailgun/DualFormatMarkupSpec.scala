package com.themillhousegroup.play2.mailgun

import org.specs2.mutable.Specification
import com.themillhousegroup.play2.mailgun.utils.DualFormatMarkup
import play.twirl.api.Html

class DualFormatMarkupSpec extends Specification {
  "Dual Format helpers" should {

    "Allow br elements to be rendered in HTML and plain text" in {
      DualFormatMarkup.br(true) must beEqualTo(Html("<br/>"))
      DualFormatMarkup.br(false) must beEqualTo("\n")
    }

    "Render link elements in HTML and just the href in plain text" in {
      DualFormatMarkup.a("http://target.example.com", "text")(true) must beEqualTo(<a href="http://target.example.com">text</a>)
      DualFormatMarkup.a("http://target.example.com", "text")(false) must beEqualTo("http://target.example.com")
    }

  }
}
