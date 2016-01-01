package com.themillhousegroup.play2.mailgun.utils

import scala.xml.{ Node, Text }
import play.twirl.api.Html

object DualFormatMarkup {

  case class Dual(originalMarkup: Node, html: Html, innerText: String)

  implicit def str2Dual(s: String): Dual = Dual(Text(s), Html(s), s)

  implicit def renderDual(d: Dual)(implicit htmlFormat: Boolean) =
    if (htmlFormat) d.html else d.innerText

  def h(markup: Node, alternative: String = ""): Dual = {
    Dual(markup, Html(markup.toString), alternative)
  }

  def a(target: String, text: String) = {
    h(<a href={ target }>{ text }</a>, target)
  }

  def em(d: Dual) = h(<em>{ d.originalMarkup }</em>, d.innerText)

  def strong(d: Dual) = h(<strong>{ d.originalMarkup }</strong>, d.innerText)

  def tt(d: Dual) = h(<tt>{ d.originalMarkup }</tt>, d.innerText)

  def br = h(<br/>, "\n")
}
