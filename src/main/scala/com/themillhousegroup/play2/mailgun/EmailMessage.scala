package com.themillhousegroup.play2.mailgun

/**
 * The bare minimum of a MailGun API email message.
 * Basically a simplified representation of
 * https://documentation.mailgun.com/api-sending.html#sending
 */
trait EssentialEmailMessage {
  val from: Option[String]
  val to: String
  val cc: Option[String]
  val bcc: Option[String]
  val subject: String
  val text: String
  val html: play.twirl.api.Html
}

/**
 * An EssentialEmailMessage
 * which is easy to use for the (arguably) most-common use case
 * of sending to exactly one recipient in the to: field.
 */
case class EmailMessage(
    from: Option[String],
    to: String,
    subject: String,
    text: String,
    html: play.twirl.api.Html) extends EssentialEmailMessage {
  val cc = None
  val bcc = None
}

/**
 * An EssentialEmailMessage
 * which offers the ability to send to multiple recipients
 * in the to:, cc: and bcc: fields
 * of sending to exactly one recipient in the to: field.
 */
case class MulticastEmailMessage(
    from: Option[String],
    tos: Seq[String],
    ccs: Seq[String],
    bccs: Seq[String],
    subject: String,
    text: String,
    html: play.twirl.api.Html) extends EssentialEmailMessage {

  val to = tos.mkString(", ")
  val cc = ccs.headOption.map(_ => ccs.mkString(", "))
  val bcc = bccs.headOption.map(_ => ccs.mkString(", "))
}
