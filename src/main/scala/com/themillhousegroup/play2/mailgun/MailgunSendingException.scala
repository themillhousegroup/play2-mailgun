package com.themillhousegroup.play2.mailgun

/**
 * Represents a problem returned by the Mailgun sending API
 */
class MailgunSendingException(msg: String) extends RuntimeException(msg)