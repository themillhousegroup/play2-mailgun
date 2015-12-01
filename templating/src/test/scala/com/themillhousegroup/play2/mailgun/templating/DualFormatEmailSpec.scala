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

		"Convert basic markup text to plain text, dropping elements that are tagged as such" in {
			val dfe = new DualFormatEmail("test <b>bold <i>bold-italic</i></b> <h3 data-ignored-in-plain-text>I should be <i>ignored</i></h3> <i>italic</i>")

			dfe.toPlainText must beEqualTo("test bold bold-italic italic")
		}	
	}
}


