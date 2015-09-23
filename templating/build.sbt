
libraryDependencies ++= Seq(
		"com.themillhousegroup"       %%  "scoup"		              % "0.2.26"
)


resolvers ++= Seq(  "oss-snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
                    "oss-releases"  at "https://oss.sonatype.org/content/repositories/releases",
                    "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
     								"Millhouse Bintray"  at "http://dl.bintray.com/themillhousegroup/maven"
)
