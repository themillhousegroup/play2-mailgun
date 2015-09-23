package com.themillhousegroup.play2.mailgun.templating

import org.specs2.mutable.Specification 

class DualFormatEmailSpec extends Specification {
	"Dual-Format emails" should {
		"Convert plain text to plain text" in {
			val dfe = new DualFormatEmail("test text")

			dfe.toPlainText must beEqualTo("test text")
		}	
	}
}


