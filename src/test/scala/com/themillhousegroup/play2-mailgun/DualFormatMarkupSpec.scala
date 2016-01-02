package com.themillhousegroup.play2.mailgun

import org.specs2.mutable.Specification
import com.themillhousegroup.play2.mailgun.utils.DualFormatMarkup._
import play.twirl.api.Html
import com.themillhousegroup.play2.mailgun.utils.DualFormatMarkup
import scala.xml.NodeSeq

class DualFormatMarkupSpec extends Specification {
  "Dual Format helpers" should {

    "Allow br elements to be rendered in HTML and plain text" in {

      DualFormatMarkup.br(true).toString must beEqualTo("<br/>")
      DualFormatMarkup.br(false).toString must beEqualTo("")
    }

    "Render arbitrary HTML elements expressed as XML" in {
      def renderingHtml(inHtml: Boolean) =
        DualFormatMarkup.h(<h1>Big Heading</h1>, "Big Heading")(inHtml).toString

      renderingHtml(true) must beEqualTo("<h1>Big Heading</h1>")
      renderingHtml(false) must beEqualTo("Big Heading")
    }

    "Render link elements in HTML and just the href in plain text" in {
      def renderingLink(inHtml: Boolean) =
        DualFormatMarkup.a("http://target.example.com", "text")(inHtml).toString

      renderingLink(true) must beEqualTo("""<a href="http://target.example.com">text</a>""")
      renderingLink(false) must beEqualTo("http://target.example.com")
    }

    "Render nested elements in HTML and just the inner text in plain text" in {
      def renderingNested(inHtml: Boolean) =
        DualFormatMarkup.strong(DualFormatMarkup.tt("bold-tt")(inHtml))(inHtml).toString

      renderingNested(false) must beEqualTo("bold-tt")
      renderingNested(true) must beEqualTo("<strong><tt>bold-tt</tt></strong>")
    }

    "Render triply-nested elements in HTML and just the inner text in plain text" in {
      def renderingNested(inHtml: Boolean) =
        DualFormatMarkup.em(
          DualFormatMarkup.strong(
            DualFormatMarkup.tt("bold-tt")(inHtml))(inHtml))(inHtml).toString

      renderingNested(false) must beEqualTo("bold-tt")
      renderingNested(true) must beEqualTo("<em><strong><tt>bold-tt</tt></strong></em>")
    }
  }
}
