# sbt-detailed-settings

A SBT plugin that adds a `detailedModuleSettings` to SBT, which
returns a more precise output than the natively defined
`moduleSettings`. In particular, it appends the cross version
suffixes to module names when needed.

`moduleSettings` provides information about module organization/name/version,
and dependencies, about each project.
This plugin allows external programs to re-use these module settings
in other contexts.

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
