package com.themillhousegroup.play2.mailgun.templating

import play.twirl.api._

class DualFormatEmail(buffer: String) extends BufferedContent[DualFormatEmail](scala.collection.immutable.Seq[DualFormatEmail](), buffer) {
  val contentType = MimeTypes.HTML
	def toPlainText:String = buffer
}

object DualFormat extends Format[DualFormatEmail] {
  def raw(text: String): DualFormatEmail = ??? // new DualFormatEmail(text) // FIXME need to actually do work 
  def escape(text: String): DualFormatEmail = ??? // new DualFormatEmail(text) // FIXME need to actually do work 

  def empty: com.themillhousegroup.play2.mailgun.templating.DualFormatEmail = ???
  def fill(elements: scala.collection.immutable.Seq[com.themillhousegroup.play2.mailgun.templating.DualFormatEmail]): com.themillhousegroup.play2.mailgun.templating.DualFormatEmail = ???

}
