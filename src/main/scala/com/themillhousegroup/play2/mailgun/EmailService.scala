package com.themillhousegroup.play2.mailgun

import play.api.libs.concurrent.Execution.Implicits._
import play.api.{ Logger, Play }
import scala.concurrent.Future
import org.apache.commons.lang3.StringUtils
import play.api.libs.ws._
import play.api.Play.current

import com.ning.http.multipart.{ FilePart, MultipartRequestEntity, Part }

object EmailService extends EmailService(
  Play.current.configuration.getString("mailgun.api.key").get,
  Play.current.configuration.getString("mailgun.default.sender"))(
  WS.url(Play.current.configuration.getString("mailgun.api.url").get))

class EmailService(val mailgunApiKey: String, val defaultSender: Option[String])(val ws: WSRequestHolder) {

  //client.addFilter(new HTTPBasicAuthFilter("api", mailgunApiKey))
  //val webResource = client.resource(mailgunApiUrl)

  def send(message: EssentialEmailMessage): Future[Option[String]] = Future {

    if (defaultSender.isEmpty && message.from.isEmpty) {
      Future.failed(new IllegalStateException("From: field is None and no default sender configured"))
    }

    val sender = message.from.getOrElse(defaultSender)

    /*
    val form = new FormDataMultiPart
    form.field("from", message.from)
    form.field("to", message.to)
    form.field("subject", message.subject)
    form.field("text", message.text)
    form.field("html", message.html.toString())
    //    val logo = Play.getExistingFile("/public/images/logo.png").get
    //    form.bodyPart(new FileDataBodyPart("inline", logo, MediaType.APPLICATION_OCTET_STREAM_TYPE))

   // webResource.`type`(MediaType.MULTIPART_FORM_DATA_TYPE).post(form)
	*/

    Option(StringUtils.trimToNull(message.to))
  }
}
