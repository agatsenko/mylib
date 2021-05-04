/**
 * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
 * Created: 2019-12-14
 */
package io.agatsenko.sbt.plugins.npm

import scala.sys.process.Process

import java.nio.file.{Files, Path}

import sbt._
import sbt.complete.DefaultParsers._
import sbt.complete.Parser

object NpmPlugin extends AutoPlugin {
  object autoImport {
    object NpmKeys {
      val npmProjectPath = Def.settingKey[Path]("Path to npm project")

      val npm = inputKey[Unit]("execute `npm` command")
      val npmRun = inputKey[Unit]("Execute `npm run` command")
    }
  }

  import autoImport.NpmKeys._

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // npm task

  lazy val npmTask = Def.inputTask {
    shellCmd(npmProjectPath.value, "npm" +: npmArgsParser.parsed: _*)
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // npmRun task

  lazy val npmRunTask = Def.inputTask {
    ensureNpmInstall(npmProjectPath.value)
    shellCmd(npmProjectPath.value, Seq("npm", "run") ++ npmArgsParser.parsed: _*)
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // npm helpers

  private lazy val npmArgsParser: Parser[Seq[String]] = Space ~> StringBasic +

  private def ensureNpmInstall(projectPath: Path): Unit = {
    val nodeModulesPath = projectPath.toAbsolutePath.resolve("node_modules")
    if (!Files.exists(nodeModulesPath)) {
      shellCmd(projectPath, "npm", "install")
    }
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // configure project settings

  override def projectSettings = Seq(
    npmProjectPath := Keys.baseDirectory.value.toPath,

    npm := npmTask.evaluated,
    npmRun := npmRunTask.evaluated,
  )

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // shell command execution

  private val isWindowsOs = System.getProperty("os.name") match {
    case null => false
    case osName => osName.toLowerCase().startsWith("win")
  }

  private def shellCmd(cwd: Path, cmd: String*): Int = {
    val osCmd = if (isWindowsOs) Seq("cmd", "/C") ++ cmd else cmd
    println(s"$cwd> ${cmd.mkString(" ")}")
    val pb = Process(osCmd, Some(cwd.normalize().toAbsolutePath.toFile))
    val returnCode = pb.!
    if (returnCode != 0) {
      sys.error(s"shell command [${osCmd.mkString(" ")}] is failed with #$returnCode error code")
    }
    returnCode
  }
}
