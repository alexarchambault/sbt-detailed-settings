package com.github.alexarchambault.sbtdetailedsettings

import sbt._, Keys._

object DetailedSettingsPlugin extends AutoPlugin {

  case class Module(organization: String,
                    name: String,
                    version: String,
                    classifiers: String)

  case class Projects(ids: List[String],
                      default: String)

  case class ProjectModule(projectId: String,
                           projectDependencies: List[String],
                           module: Module)

  case class ProjectModuleSettings(projectId: String,
                                   module: Module,
                                   dependencies: List[Module],
                                   exportedProducts: List[String],
                                   unmanagedClasspath: List[String])

  object autoImport {
    val detailedProjects = taskKey[Projects]("Detailed projects")
    val detailedProjectModule = taskKey[ProjectModule]("Detailed project module")
    val detailedProjectModuleSettings = taskKey[ProjectModuleSettings]("Detailed project module settings")
  }

  import autoImport._

  override def trigger = allRequirements

  def mapModuleId(id: ModuleID, scalaBinaryVersion: String, scalaVersion: String): Module = {
    val m = id.crossVersion match {
      case _: CrossVersion.Disabled.type => id
      case b: CrossVersion.Binary => id.copy(name = id.name + "_" + b.remapVersion(scalaBinaryVersion), crossVersion = CrossVersion.Disabled)
      case f: CrossVersion.Full => id.copy(name = id.name + "_" + f.remapVersion(scalaVersion), crossVersion = CrossVersion.Disabled)
    }

    Module(m.organization, m.name, m.revision, m.configurations.getOrElse(""))
  }

  val detailedProjectsInit = Seq[Setting[_]](
    detailedProjects := {
      val bs = buildStructure.value
      Projects(
        bs.allProjectRefs.map(_.project).toList,
        bs.units(bs.root).root
      )
    }
  )

  override lazy val globalSettings = detailedProjectsInit

  override lazy val projectSettings = detailedProjectsInit ++ Seq(
    detailedProjectModule := {
      val (scalaVersionValue, scalaBinaryVersionValue) =
        moduleSettings.value match {
          case settings: InlineConfigurationWithExcludes =>
            (settings.ivyScala.map(_.scalaFullVersion) getOrElse scalaVersion.value,
              settings.ivyScala.map(_.scalaBinaryVersion) getOrElse scalaBinaryVersion.value)

          case other =>
            (scalaVersion.value, scalaBinaryVersion.value)
        }

      val m = mapModuleId(projectID.value, scalaBinaryVersionValue, scalaVersionValue)
      ProjectModule(
        thisProjectRef.value.project,
        thisProject.value.dependencies.map(_.project).collect{case ProjectRef(_, id) => id}.toList,
        m
      )
    },
    detailedProjectModuleSettings <<= Def.task {
      val (module, deps) =
        moduleSettings.value match {
          case settings: InlineConfigurationWithExcludes =>
            val scalaVersionValue = settings.ivyScala.map(_.scalaFullVersion) getOrElse scalaVersion.value
            val scalaBinaryVersionValue = settings.ivyScala.map(_.scalaBinaryVersion) getOrElse scalaBinaryVersion.value

            def map(id: ModuleID) = mapModuleId(id, scalaBinaryVersionValue, scalaVersionValue)

            (map(settings.module), settings.dependencies.map(map).toList)

          case other =>
            throw new Exception(s"Unsupported module settings: $other")
        }

      ProjectModuleSettings(
        thisProjectRef.value.project,
        module, deps,
        (exportedProducts in Compile).value.toList.map(_.data.toString),
        (unmanagedClasspath in Compile).value.toList.map(_.data.toString)
      )
    }
  )

}
