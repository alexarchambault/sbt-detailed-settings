# sbt-detailed-settings

[![Build Status](https://travis-ci.org/alexarchambault/sbt-detailed-settings.svg?branch=master)](https://travis-ci.org/alexarchambault/sbt-detailed-settings)

A SBT plugin that adds extra tasks related to modules,
which return more precise outputs, regarding cross versioning, than their counterpart defined in SBT.
In particular, it defines `detailedModuleSettings` (better `moduleSettings`)
and `detailedProjectID` (better `projectID`).

`detailedModuleSettings` provides information about module organization/name/version,
and dependencies, about each project.
This plugin allows external programs to re-use these module settings
in other contexts, like [Jupyter Scala](https://github.com/alexarchambault/jupyter-scala) does.

## Quick start

Add to `~/.sbt/0.13/plugins/build.sbt`,
```scala
resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)

addSbtPlugin("com.github.alexarchambault" %% "sbt-detailed-settings" % "0.1.0-SNAPSHOT")
```

Then `sbt "show `*project name*`/detailedModuleSettings"` will print re-usable
module settings of the sub-project *project name*.

Compatible with Scala 2.10.3 to 2.10.5, and 2.11.0 to 2.11.6.

Requires SBT >= **0.13.8** (there seems to be binary incompatibility
issues with lower versions).

## Notice

Copyright 2015, Alexandre Archambault

Released under a MIT license
