name         := "akka-http-slick"
scalaVersion := "2.11.6"
version := "1.0.0-SNAPSHOT"

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

Revolver.settings

scalacOptions ++= Seq(
  //  "-Xprint:typer", // Turn this on if WartRemover acts up, to see full syntax tree
  "-deprecation",
  "-encoding", "UTF-8",       // yes, this is 2 args
  "-feature",
  "-unchecked",
//  "-Xfatal-warnings",       // Treat Warnings as Errors
  "-Xlint",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",        // N.B. doesn't work well with the ??? hole
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Xfuture" // Would be nice to have but does not Play well
)

//Only care about main code. Running in tests will inevitably fail with all the ScalaTest matchers on Any
wartremoverWarnings in (Compile, compile) ++= Seq(
  Wart.Any,
  Wart.Any2StringAdd,
  Wart.EitherProjectionPartial,
  Wart.OptionPartial,
  Wart.Product,
  Wart.Serializable,
  Wart.ListOps,
  Wart.Nothing
)


