package com.github.alexarchambault.sbtdetailedsettings

import sbt._, Keys._

object DetailedSettingsPlugin extends AutoPlugin {

  object autoImport {
    val detailedModuleSettings = taskKey[ModuleSettings]("Detailed module settings")
  }

  import autoImport._

  override def trigger = allRequirements

  def mapModuleId(id: ModuleID, scalaBinaryVersion: String, scalaVersion: String): ModuleID =
    id.crossVersion match {
      case _: CrossVersion.Disabled.type => id
      case b: CrossVersion.Binary => id.copy(name = id.name + "_" + b.remapVersion(scalaBinaryVersion), crossVersion = CrossVersion.Disabled)
      case f: CrossVersion.Full => id.copy(name = id.name + "_" + f.remapVersion(scalaVersion), crossVersion = CrossVersion.Disabled)
    }

  def mapExclusionRule(id: SbtExclusionRule, scalaBinaryVersion: String, scalaVersion: String): SbtExclusionRule =
    id.crossVersion match {
      case _: CrossVersion.Disabled.type => id
      case b: CrossVersion.Binary => id.copy(name = id.name + "_" + b.remapVersion(scalaBinaryVersion), crossVersion = CrossVersion.Disabled)
      case f: CrossVersion.Full => id.copy(name = id.name + "_" + f.remapVersion(scalaVersion), crossVersion = CrossVersion.Disabled)
    }

  override lazy val projectSettings = Seq(
    detailedModuleSettings := {
      moduleSettings.value match {
        case settings: InlineConfigurationWithExcludes =>
          val scalaVersionValue = settings.ivyScala.map(_.scalaFullVersion) getOrElse scalaVersion.value
          val scalaBinaryVersionValue = settings.ivyScala.map(_.scalaBinaryVersion) getOrElse scalaBinaryVersion.value

          def map(id: ModuleID) = mapModuleId(id, scalaBinaryVersionValue, scalaVersionValue)
          def mapExclRule(id: SbtExclusionRule) = mapExclusionRule(id, scalaBinaryVersionValue, scalaVersionValue)

          InlineConfigurationWithExcludes(
            map(settings.module),
            settings.moduleInfo,
            settings.dependencies.map(map),
            settings.overrides.map(map),
            settings.excludes.map(mapExclRule),
            settings.ivyXML,
            settings.configurations,
            settings.defaultConfiguration,
            settings.ivyScala,
            settings.validate,
            settings.conflictManager
          )

        case other => other
      }
    }
  )

}
