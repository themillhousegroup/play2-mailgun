package com.themillhousegroup.play2.mailgun.templating

import org.specs2.mutable.Specification 

class DualFormatEmailSpec extends Specification {
	"Dual-Format emails" should {
		"Convert plain text to plain text" in {
			val dfe = new DualFormatEmail("test text")

			dfe.toPlainText must beEqualTo("test text")
		}	

		"Convert basic markup text to plain text" in {
			val dfe = new DualFormatEmail("test <b>bold <i>bold-italic</i></b> <i>italic</i>")

			dfe.toPlainText must beEqualTo("test bold bold-italic italic")
		}	
	}
}


