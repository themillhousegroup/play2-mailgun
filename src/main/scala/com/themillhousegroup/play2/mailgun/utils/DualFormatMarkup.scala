package com.themillhousegroup.play2.mailgun.utils

import scala.xml.{ NodeSeq, Node, Text }
import play.twirl.api.{ HtmlFormat, Html, Content }

object DualFormatMarkup {

  implicit def str2NodeSeq(s: String): NodeSeq =
    Text(s)

  def h(markup: NodeSeq, alternative: String = "")(implicit htmlFormat: Boolean): NodeSeq = {
    if (htmlFormat) {
      markup
    } else {
      Text(alternative)
    }
  }

  def a(target: String, text: String)(implicit htmlFormat: Boolean) = {
    h(<a href={ target }>{ text }</a>, target)
  }

  def em(d: NodeSeq)(implicit htmlFormat: Boolean) =
    h(<em>{ d }</em>, d.text)

  def strong(d: NodeSeq)(implicit htmlFormat: Boolean) =
    h(<strong>{ d }</strong>, d.text)

  def tt(d: NodeSeq)(implicit htmlFormat: Boolean) =
    h(<tt>{ d }</tt>, d.text)

  def br(implicit htmlFormat: Boolean) = h(<br/>)
}
