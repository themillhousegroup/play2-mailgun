package com.themillhousegroup.play2.mailgun

import org.specs2.mutable.Specification
import com.themillhousegroup.play2.mailgun.utils.DualFormatMarkup._
import play.twirl.api.Html
import com.themillhousegroup.play2.mailgun.utils.DualFormatMarkup

class DualFormatMarkupSpec extends Specification {
  "Dual Format helpers" should {

    def rendering(f: => Dual)(inHtml: Boolean): String = {
      DualFormatMarkup.renderDual(f)(inHtml).toString
    }

    "Allow br elements to be rendered in HTML and plain text" in {
      rendering(DualFormatMarkup.br)(true) must beEqualTo("<br/>")
      rendering(DualFormatMarkup.br)(false) must beEqualTo("\n")
    }

    "Render link elements in HTML and just the href in plain text" in {
      val renderingLink =
        rendering(DualFormatMarkup.a("http://target.example.com", "text")) _

      renderingLink(true) must beEqualTo("""<a href="http://target.example.com">text</a>""")
      renderingLink(false) must beEqualTo("http://target.example.com")
    }

    "Render nested elements in HTML and just the inner text in plain text" in {
      val renderingNested =
        rendering(DualFormatMarkup.strong(DualFormatMarkup.tt("bold-tt"))) _

      renderingNested(false) must beEqualTo("bold-tt")
      renderingNested(true) must beEqualTo("<strong><tt>bold-tt</tt></strong>")
    }

    "Render triply-nested elements in HTML and just the inner text in plain text" in {
      val renderingNested =
        rendering(
          DualFormatMarkup.em(
            DualFormatMarkup.strong(
              DualFormatMarkup.tt("bold-tt")))) _

      renderingNested(false) must beEqualTo("bold-tt")
      renderingNested(true) must beEqualTo("<em><strong><tt>bold-tt</tt></strong></em>")
    }
  }
}
