package com.themillhousegroup.play2.mailgun

/**
 * Represents a problem returned by the Mailgun sending API
 */
class MailgunSendingException(msg: String) extends RuntimeException(msg)

/**
 * Represents the specific case where the caller has used incorrect Mailgun API credentials
 */
class MailgunAuthenticationException(msg: String) extends MailgunSendingException(msg)
