package com.themillhousegroup.play2.mailgun

import play.api.libs.json.Json

case class MailgunResponse(message: String, id: String)

trait MailgunResponseJson {
  implicit val responseReads = Json.reads[MailgunResponse]
}
