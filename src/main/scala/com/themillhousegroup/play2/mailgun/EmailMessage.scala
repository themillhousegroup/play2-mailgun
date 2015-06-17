package com.themillhousegroup.play2.mailgun

case class EmailMessage(
  from: String,
  to: String,
  subject: String,
  text: String,
  html: play.twirl.api.Html)
