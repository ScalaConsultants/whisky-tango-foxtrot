version := "1.0.0"

scalaVersion := "2.11.8"

resolvers ++= Seq(
  "snapshots"           at "http://oss.sonatype.org/content/repositories/snapshots",
  "releases"            at "http://oss.sonatype.org/content/repositories/releases",
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
  "softprops-maven"     at "http://dl.bintray.com/content/softprops/maven"
)

scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  Seq(
    "org.slf4j"          % "slf4j-nop"                          % "1.6.4",
    "com.typesafe.akka"  %% "akka-http-experimental"            % "2.4.4",
    "com.typesafe.akka"  %% "akka-http-spray-json-experimental" % "2.4.7",
    "com.typesafe.slick" %% "slick"                             % "3.1.1",
    "org.typelevel"      %% "cats"                              % "0.5.0",
    "com.h2database"     % "h2"                                 % "1.3.170",
    "org.scalactic"      %% "scalactic"                         % "2.2.6",
    "org.scalatest"      %% "scalatest"                         % "2.2.6" % "test",
    "me.lessis"          %% "courier"                           % "0.1.3"
  )
}
