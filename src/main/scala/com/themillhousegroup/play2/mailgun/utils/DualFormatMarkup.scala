package com.themillhousegroup.play2.mailgun.utils

import scala.xml.Node
import play.twirl.api.Html

object DualFormatMarkup {

  def h(markup: Node, alternative: String = "")(implicit htmlFormat: Boolean) = {
    if (htmlFormat) {
      Html(markup.toString)
    } else {
      alternative
    }
  }

  def a(target: String, text: String)(implicit htmlFormat: Boolean) = {
    if (htmlFormat) {
      <a href={ target }>{ text }</a>
    } else {
      target
    }
  }

  def em(text: String)(implicit htmlFormat: Boolean) = h(<em>{ text }</em>, text)

  def strong(text: String)(implicit htmlFormat: Boolean) = h(<strong>{ text }</strong>, text)

  def tt(text: String)(implicit htmlFormat: Boolean) = h(<tt>{ text }</tt>, text)

  def br(implicit htmlFormat: Boolean) = h(<br/>)
}
