package com.themillhousegroup.play2.mailgun

sealed trait MailgunOption {
  def renderAsApiParameter: String
}

case object SendInTestMode extends MailgunOption {
  def renderAsApiParameter: String = "o:testmode=true"
}
