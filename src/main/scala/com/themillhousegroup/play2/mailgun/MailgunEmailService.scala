package com.themillhousegroup.play2.mailgun

import akka.stream.scaladsl.Source
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc.MultipartFormData.DataPart
import play.api.Play
import play.api.Logger

import scala.concurrent.Future
import play.api._
import play.api.http._
import play.api.libs.ws._
import play.api.Play.current
import javax.inject.Inject

import akka.util.ByteString
import play.api.mvc.MultipartFormData

/** For static-style usage: */
object MailgunEmailService extends MailgunEmailService(WS.client, Play.current) {
  type PostData = Source[MultipartFormData.Part[Source[ByteString, _]], _]
}

class MailgunEmailService @Inject() (wsClient: WSClient, app: Application) extends MailgunResponseJson {

  lazy val configuration = app.configuration
  lazy val mailgunApiKey: String = configuration.getString("mailgun.api.key").get
  lazy val defaultSender: Option[String] = configuration.getString("mailgun.default.sender")
  lazy val mailgunUrl: String = configuration.getString("mailgun.api.url").get
  lazy val ws: WSRequest = wsClient.url(mailgunUrl)

  /** Sends the message via Mailgun's API, respecting any options provided */
  def send(message: EssentialEmailMessage, options: Set[MailgunOption] = Set()): Future[MailgunResponse] = {
    if (defaultSender.isEmpty && message.from.isEmpty) {
      Future.failed(new IllegalStateException("From: field is None and no default sender configured"))
    } else {
      val sender = message.from.getOrElse(defaultSender.get)
      println(s"None-Empty and will user sender $sender")

      ws
        .withAuth("api", mailgunApiKey, WSAuthScheme.BASIC)
        .post(buildMultipartRequest(sender, message, options))
        .flatMap(handleMailgunResponse)
    }
  }

  private def buildMultipartRequest(sender: String, message: EssentialEmailMessage, options: Set[MailgunOption]): MailgunEmailService.PostData = {
    val parts = List(
      DataPart("from", sender),
      DataPart("to", message.to),
      DataPart("subject", message.subject),
      DataPart("text", message.text),
      DataPart("html", message.html.toString()))

    Source(addOptions(parts, options))
  }

  private def addOptions(basicParts: List[DataPart], options: Set[MailgunOption]): List[DataPart] = {
    basicParts ++ options.map { o =>
      Logger.debug(s"Adding option $o: ${o.renderAsApiParameter}")
      o.renderAsApiParameter
    }
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
