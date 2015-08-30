play2-mailgun
============================

Features:

  - Requires no extra dependencies (uses Play Framework libraries only)
  - Super-easy to wire in (just add two values to `application.conf`)
  - Send plain-text and/or HTML emails _with the one Twirl template_ (not finished yet)

[![Build Status](https://travis-ci.org/themillhousegroup/play2-mailgun.svg?branch=master)](https://travis-ci.org/themillhousegroup/play2-mailgun)



### Installation

Bring in the library by adding the following to your ```build.sbt```. 

  - The release repository: 

```
   resolvers ++= Seq(
     "Millhouse Bintray"  at "http://dl.bintray.com/themillhousegroup/maven"
   )
```
  - The dependency itself: 

```
   libraryDependencies ++= Seq(
     "com.themillhousegroup" %% "play2-mailgun" % "0.1.16"
   )

```

### Usage

Once you have __play2-mailgun__ added to your project, you can start using it like this:

#### Put your Mailgun credentials into `application.conf`
You need the following two entries:

```
mailgun.api.key=key-abcdef123456abcdef123456abc12345
mailgun.api.url="https://api.mailgun.net/v3/mg.example.com/messages"
```

#### Build an `EmailMessage` containing your email content
Supply plain text _and_ HTML versions of your message:

```
import com.themillhousegroup.play2.mailgun.EmailMessage
import play.twirl.api.Html

val plainText = "This is the plain text"

val html = Html("<h5>This is <em>actual</em><strong>HTML!</strong></h5>")

val m = EmailMessage(
      Some("donotreply@example.com"),
      "destination@example.com"",
      "This is the subject",
      plainText,
      html
    )
```

#### Pass the `EmailMessage` to `MailgunEmailService.send()`
It returns a `Future[MailgunResponse]` (which you can ignore if you don't care):

```
import com.themillhousegroup.play2.mailgun.MailgunEmailService

MailgunEmailService.send(m).map { mailgunResponse =>
	s"id: ${mailgunResponse.id} - message: ${mailgunResponse.message}"
}


```

### Still To-Do
Use one custom template (with `.scala.email` extension) to define both plain text and HTML message bodies.

### Credits / References
- [MailGun](http://www.mailgun.com/) 
- [This Issue in Play](https://github.com/playframework/playframework/issues/902)
- [This Stack Overflow question](http://stackoverflow.com/questions/10890362/play-2-0-how-to-post-multipartformdata-using-ws-url-or-ws-wsrequest)
- [Play Custom Template Formats](https://www.playframework.com/documentation/2.3.x/ScalaCustomTemplateFormat)
