organization := "io.github.gitbucket"
name := "gitbucket-explorer-plugin"
version := "9.0.0"
scalaVersion := "2.13.18"
gitbucketVersion := "4.35.3"

scalacOptions := Seq("-deprecation", "-feature", "-language:postfixOps")
javacOptions in compile ++= Seq("-target", "8", "-source", "8")

useJCenter := true
