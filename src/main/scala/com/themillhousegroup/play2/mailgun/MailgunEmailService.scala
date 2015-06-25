package com.themillhousegroup.play2.mailgun

import play.api.libs.concurrent.Execution.Implicits._
import play.api.{ Play }
import scala.concurrent.Future
import org.apache.commons.lang3.StringUtils
import play.api.http._
import play.api.libs.ws._
import play.api.Play.current
import com.ning.http.client.FluentCaseInsensitiveStringsMap
import com.ning.http.multipart._
import java.io.ByteArrayOutputStream

import com.ning.http.multipart.{ FilePart, MultipartRequestEntity, Part }
import play.api.libs.json

object MailgunEmailService extends MailgunEmailService(
  Play.current.configuration.getString("mailgun.api.key").get,
  Play.current.configuration.getString("mailgun.default.sender"))(
  WS.url(Play.current.configuration.getString("mailgun.api.url").get))

class MailgunEmailService(val mailgunApiKey: String, val defaultSender: Option[String])(val ws: WSRequestHolder) extends MailgunResponseJson {

  def send(message: EssentialEmailMessage): Future[MailgunResponse] = {

    if (defaultSender.isEmpty && message.from.isEmpty) {
      Future.failed(new IllegalStateException("From: field is None and no default sender configured"))
    } else {
      val sender = message.from.getOrElse(defaultSender.get)
      val mpre = buildMultipartRequest(sender, message)

      ws.withAuth("api", mailgunApiKey, WSAuthScheme.BASIC)
        .post(requestBytes(mpre))(Writeable.wBytes, contentType(mpre)).flatMap(handleMailgunResponse)
    }
  }

  private def buildMultipartRequest(sender: String, message: EssentialEmailMessage): MultipartRequestEntity = {
    //    val logo = Play.getExistingFile("/public/images/logo.png").get
    //    form.bodyPart(new FileDataBodyPart("inline", logo, MediaType.APPLICATION_OCTET_STREAM_TYPE))

    // Use the Ning AsyncHttpClient multipart class to get the bytes
    val parts = Array[Part](
      new StringPart("from", sender),
      new StringPart("to", message.to),
      new StringPart("subject", message.subject),
      new StringPart("text", message.text),
      new StringPart("html", message.html.toString())
    )
    //      new FilePart("attachment", file)

    new MultipartRequestEntity(parts, new FluentCaseInsensitiveStringsMap)
  }

  private def requestBytes(mpre: MultipartRequestEntity): Array[Byte] = {
    val baos = new ByteArrayOutputStream
    mpre.writeRequest(baos)
    baos.toByteArray
  }

  private def contentType(mpre: MultipartRequestEntity) = {
    val contentType = mpre.getContentType
    ContentTypeOf(Some(contentType))
  }

  /**
   * As per https://documentation.mailgun.com/api-intro.html#errors
   */
  private def handleMailgunResponse(response: WSResponse): Future[MailgunResponse] = {
    if (response.status == Status.OK) {
      Future.successful(response.json.as[MailgunResponse])
    } else {
      Future.failed(
        response.status match {
          case Status.UNAUTHORIZED => new MailgunAuthenticationException((response.json \ "message").as[String])
          case _ => new MailgunSendingException((response.json \ "message").as[String])
        }
      )
    }

  }
}
