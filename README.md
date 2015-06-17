play2-mailgun
============================

Features:
	- Requires no extra dependencies (uses Play Framework libraries only)
  - Super-easy to wire in (just add two values to `application.conf`)
  - Send plain-text and/or HTML emails _with the one Twirl template_


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
     "com.themillhousegroup" %% "play2-mailgun" % "0.1.0"
   )

```

### Usage

Once you have __play2-mailgun__ added to your project, you can start using it like this:

```
foo
bar
baz 
```


### Still To-Do
Ummm, all of it.

### Credits
https://github.com/playframework/playframework/issues/902
http://stackoverflow.com/questions/10890362/play-2-0-how-to-post-multipartformdata-using-ws-url-or-ws-wsrequest
http://www.mailgun.com/ 

