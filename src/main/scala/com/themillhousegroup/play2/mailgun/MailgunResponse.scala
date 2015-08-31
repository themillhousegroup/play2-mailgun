package com.themillhousegroup.play2.mailgun

import play.api.libs.json.Json

sealed trait MailgunSentMessageStatus

case object MessageQueued extends MailgunSentMessageStatus

case class MailgunResponse(message: String, id: String) {
  lazy val status: MailgunSentMessageStatus = MessageQueued
}

trait MailgunResponseJson {
  implicit val responseReads = Json.reads[MailgunResponse]
}
