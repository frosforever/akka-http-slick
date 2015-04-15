name         := "akka-http-slick"
scalaVersion := "2.11.6"
version := "1.0.0-SNAPSHOT"

scalacOptions in ThisBuild ++= Seq("-encoding", "UTF-8", "-deprecation", "-unchecked")  

libraryDependencies ++= {
  val akkaV       = "2.3.9"
  val akkaStreamV = "1.0-M5"
  val scalaTestV  = "2.2.1"
  val slickV      = "3.0.0-RC3"
  Seq(
    "com.typesafe.akka"  %% "akka-actor"                        % akkaV,
    "com.typesafe.akka"  %% "akka-stream-experimental"          % akkaStreamV,
    "com.typesafe.akka"  %% "akka-http-core-experimental"       % akkaStreamV,
    "com.typesafe.akka"  %% "akka-http-experimental"            % akkaStreamV,
    "com.typesafe.akka"  %% "akka-http-spray-json-experimental" % akkaStreamV,
    "com.typesafe.akka"  %% "akka-http-testkit-experimental"    % akkaStreamV,
    "com.typesafe.slick" %% "slick"                             % slickV,
    "com.h2database"     %  "h2"                                % "1.4.187",
    "org.scalatest"      %% "scalatest"                         % scalaTestV % "test"
  )
}


