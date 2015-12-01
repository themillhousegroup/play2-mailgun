package com.themillhousegroup.play2.mailgun.templating

import play.twirl.api._
import com.themillhousegroup.scoup.Scoup
import com.themillhousegroup.scoup.ScoupImplicits

class DualFormatEmail(buffer: String) extends BufferedContent[DualFormatEmail](scala.collection.immutable.Seq[DualFormatEmail](), buffer) with ScoupImplicits {
  val contentType = MimeTypes.HTML
	lazy val toPlainText:String = Scoup.parseHTML(buffer).textNodes.map { elem =>
		elem.text
	}.mkString
}

object DualFormat extends Format[DualFormatEmail] {
  def raw(text: String): DualFormatEmail = ??? // new DualFormatEmail(text) // FIXME need to actually do work 
  def escape(text: String): DualFormatEmail = ??? // new DualFormatEmail(text) // FIXME need to actually do work 

  def empty: com.themillhousegroup.play2.mailgun.templating.DualFormatEmail = ???
  def fill(elements: scala.collection.immutable.Seq[com.themillhousegroup.play2.mailgun.templating.DualFormatEmail]): com.themillhousegroup.play2.mailgun.templating.DualFormatEmail = ???

}
