package com.themillhousegroup.play2.mailgun

import java.io.File

/** Optionally supply a name to use if `file.getName` isn't suitable */
case class MailgunAttachment(file: File, attachmentName: Option[String] = None, contentType: Option[String] = None) {
  val fileName = attachmentName.getOrElse(file.getName)
}

/**
 * The bare minimum of a MailGun API email message.
 * Basically a simplified representation of
 * https://documentation.mailgun.com/api-sending.html#sending
 */
trait EssentialEmailMessage {
  val from: Option[String]
  val replyTo: Option[String]
  val to: String
  val cc: Option[String]
  val bcc: Option[String]
  val subject: String
  val text: String
  val html: play.twirl.api.Html
  val attachments: Seq[MailgunAttachment]
  val additionalHeaders: Seq[(String, String)]

  lazy val computedHeaders: Seq[(String, String)] = {
    additionalHeaders ++ replyTo.map("Reply-To" -> _)
  }
}

/**
 * An EssentialEmailMessage
 * which is easy to use for the (arguably) most-common use case
 * of sending to exactly one recipient in the to: field,
 * with no attachments.
 */
case class EmailMessage(
    from: Option[String],
    to: String,
    subject: String,
    text: String,
    html: play.twirl.api.Html) extends EssentialEmailMessage {
  val cc = None
  val bcc = None
  val replyTo = None
  val attachments = Nil
  val additionalHeaders = Nil
}

/**
 * An EssentialEmailMessage
 * for sending to exactly one recipient in the to: field,
 * with attachments.
 */
case class EmailMessageWithAttachments(
    from: Option[String],
    to: String,
    subject: String,
    text: String,
    html: play.twirl.api.Html,
    attachments: Seq[MailgunAttachment]) extends EssentialEmailMessage {
  val cc = None
  val bcc = None
  val replyTo = None
  val additionalHeaders = Nil
}

/**
 * An EssentialEmailMessage
 * which offers the ability to send to multiple recipients
 * in the to:, cc: and bcc: fields
 * with optional attachments and extra headers.
 */
case class MulticastEmailMessage(
    from: Option[String],
    replyTo: Option[String],
    tos: Seq[String],
    ccs: Seq[String],
    bccs: Seq[String],
    subject: String,
    text: String,
    html: play.twirl.api.Html,
    attachments: Seq[MailgunAttachment] = Nil,
    additionalHeaders: Seq[(String, String)] = Nil) extends EssentialEmailMessage {

  val to = tos.mkString(", ")
  val cc = ccs.headOption.map(_ => ccs.mkString(", "))
  val bcc = bccs.headOption.map(_ => bccs.mkString(", "))
}
