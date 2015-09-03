package com.themillhousegroup.play2.mailgun

import com.ning.http.multipart._

sealed trait MailgunOption {
  def renderAsApiParameter: Part
}

case object SendInTestMode extends MailgunOption {
  def renderAsApiParameter: Part = new StringPart("o:testmode", "true")
}

case class ScheduledSendAt(dateInMillis: Long) extends MailgunOption {
  def renderAsApiParameter: Part = new StringPart("o:deliverytime", dateInMillis.toString)
}
