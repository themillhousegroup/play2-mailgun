package com.themillhousegroup.play2.mailgun.templating

import play.twirl.api._

class DualFormatEmail(buffer: String) extends BufferedContent[DualFormatEmail](scala.collection.immutable.Seq[DualFormatEmail](), buffer) {
	val contentType = MimeTypes.HTML
}
