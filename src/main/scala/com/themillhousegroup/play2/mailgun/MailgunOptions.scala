package com.themillhousegroup.play2.mailgun

import play.api.mvc.MultipartFormData.DataPart

sealed trait MailgunOption {
  def renderAsApiParameter: DataPart
}

case object SendInTestMode extends MailgunOption {
  def renderAsApiParameter: DataPart = DataPart("o:testmode", "true")
}

case class ScheduledSendAt(dateInMillis: Long) extends MailgunOption {
  def renderAsApiParameter: DataPart = DataPart("o:deliverytime", dateInMillis.toString)
}
