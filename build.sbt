import Dependencies._
import sbt.Keys.organization

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.tlt.percipio.outliers",
      scalaVersion := "2.11.11"
    )),
    name := "spark-mahalanobis",
    libraryDependencies ++= Seq(
      scalaTest % Test,
      "org.apache.spark" %% "spark-mllib" % "2.1.1" % "provided" excludeAll(
        ExclusionRule("org.apache.hadoop", "hadoop-hdfs"),
        ExclusionRule("org.apache.hadoop", "hadoop-yarn-common"),
        ExclusionRule("org.apache.hadoop", "hadoop-yarn-client"),
        ExclusionRule("org.apache.hadoop", "hadoop-yarn-server-common"),
        ExclusionRule("org.fusesource.leveldbjni", "leveldbjni-all"),
        ExclusionRule("org.apache.curator", "curator-recipes"),
        ExclusionRule("org.apache.zookeeper", "zookeeper"),
        ExclusionRule("org.scala-lang", "scalap"),
        ExclusionRule("org.apache.ivy", "ivy"),
        ExclusionRule("org.apache.commons", "commons-math"),
        ExclusionRule("org.apache.commons", "commons-compress")
      )
    ),
    externalResolvers := Seq("TLT Maven repository" at "http://nexus:8081/nexus/content/repositories/public"),
    publishTo := {
      if ((version in ThisBuild).value.endsWith("SNAPSHOT")) {
        Some("TLT Maven snapshots" at "http://nexus:8081/nexus/content/repositories/snapshots")
      } else {
        Some("TLT Maven releases" at "http://nexus:8081/nexus/content/repositories/releases")
      }
    },
    credentials += Credentials("Sonatype Nexus Repository Manager", "nexus", "deployment", "Liace12345"),
    updateOptions := updateOptions.value.withCachedResolution(true)
  )
