play2-mailgun
============================

Features:

  - Requires no extra dependencies (uses Play Framework libraries only)
  - Super-easy to wire in (just add two values to `application.conf`)
  - Send plain-text and/or HTML emails _with the one Twirl template_ (not finished yet)

[![Build Status](https://travis-ci.org/themillhousegroup/play2-mailgun.svg?branch=master)](https://travis-ci.org/themillhousegroup/play2-mailgun)



### Installation


#### Add the release repository
Add the following to your ```build.sbt```:

```
   resolvers ++= Seq(
     "Millhouse Bintray"  at "http://dl.bintray.com/themillhousegroup/maven"
   )
```

#### Pick the right version for your Play app
There are versions available for Play 2.3 to 2.5. 

If you are on Play 2.5, you'll need to use the latest from the `0.3.x` family, as shown below:

```
   libraryDependencies ++= Seq(
     "com.themillhousegroup" %% "play2-mailgun" % "0.3.284"
   )

```

Development of new features for the Play 2.3/4 has stopped, but the library is still available and works well. Bugfixes (where applicable) from `master` are backported to the `0.2.x` family (i.e. Play 2.4). Just substitute the appropriate version number in the above specifier:

- For **Play 2.3** the version you want is `0.1.256`
- For **Play 2.4** the version you want is `0.2.285`

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

val html = Html("<h5>This is <em>actual</em> <strong>HTML!</strong></h5>")

val m = EmailMessage(
      Some("sender@example.com"),
      "destination@example.com",
      "This is the subject",
      plainText,
      html
    )
```

##### Sender addressing
You might have noticed the first argument to `EmailMessage` was `Some("sender@example.com")` - 
if you want emails to come from different senders depending on context, you should pass the sender's
email address in like this.

If your requirements are simpler, and you just have a global email address that all emails should "come from"
(like a `do-not-reply@example.com` or similar) then you can simply set that in your `application.conf` as follows:

```
mailgun.default.sender="do-not-reply@example.com"
```

And then just pass `None` as the first argument to `EmailMessage()` - you can still override it on a case-by-case basis if necessary.


#### Pass the `EmailMessage` to `MailgunEmailService.send()`
It returns a `Future[MailgunResponse]` (which you can ignore if you don't care):

##### Play 2.3 static-object style:

```
import com.themillhousegroup.play2.mailgun.MailgunEmailService

...

MailgunEmailService.send(m).map { mailgunResponse =>
	s"id: ${mailgunResponse.id} - message: ${mailgunResponse.message}"
}
```

##### Play 2.4+ dependency-injected style:

```
import play.api.mvc._
import com.google.inject.Inject
import com.themillhousegroup.play2.mailgun.MailgunEmailService

class MyController @Inject() (val emailService:MailgunEmailService) extends Controller  {

  ...
		emailService.send(m).map { mailgunResponse =>
			s"id: ${mailgunResponse.id} - message: ${mailgunResponse.message}"
		}
	...
}
```

You can of course use the `MailgunEmailService` in static style, but it's more in keeping with the Play
2.4+ philosophy to inject this dependency.

### Advanced Usage


#### Sending to multiple recipients
Use a `MulticastEmailMessage` instance instead of an `EmailMessage` - it gives you the opportunity to specify multiple **To**, **CC** and **BCC** recipients

```
import com.themillhousegroup.play2.mailgun.MulticastEmailMessage
import play.twirl.api.Html

val multiEmail = MulticastEmailMessage(
            None,
            None,
            Seq("first@first.com", "second@second.com"),
            Seq("cc1@cc.com", "cc2@cc.com"),
            Seq("bcc1@blind.com", "bcc2@blind.com"),
            subject,
            plainText,
            html
          )
```

You can then pass your `MulticastEmailMessage` to the `MailgunEmailService` as before.

#### Using a custom `Reply-To` address
Use a `MulticastEmailMessage` as above, and set the second parameter to a `Some`. If you don't need multiple recipients, just create a `Seq` around the single recipient, or use `Nil` as required:

```
import com.themillhousegroup.play2.mailgun.MulticastEmailMessage
import play.twirl.api.Html

val replyToEmail = MulticastEmailMessage(
            None,
            Some("replytome@example.com"),
            Seq("sendtome@recipient.com"),
            Nil, // No CCs
            Nil, // No BCCs
            subject,
            plainText,
            html
          )
```

### Still To-Do
Use one custom template (with `.scala.email` extension) to define both plain text and HTML message bodies.

### Credits / References
- [MailGun](http://www.mailgun.com/) 
- [This Issue in Play](https://github.com/playframework/playframework/issues/902)
- [This Stack Overflow question](http://stackoverflow.com/questions/10890362/play-2-0-how-to-post-multipartformdata-using-ws-url-or-ws-wsrequest)
- [Play Custom Template Formats](https://www.playframework.com/documentation/2.3.x/ScalaCustomTemplateFormat)
