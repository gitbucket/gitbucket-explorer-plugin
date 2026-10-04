package io.github.gitbucket.explorer.controllers

import gitbucket.core.api.JsonFormat
import gitbucket.core.controller.ControllerBase
import gitbucket.core.service.{AccountService, RepositoryService}
import gitbucket.core.util.{JGitUtil, ReferrerAuthenticator}
import gitbucket.core.util.Implicits._
import gitbucket.core.util.SyntaxSugars._
import gitbucket.core.util.Directory._
import org.eclipse.jgit.api.Git
import gitbucket.core.view.helpers
import scala.util.Using

/**
  * Created by t_maruyama on 2017/01/31.
  */
class ExplorerController extends ExplorerControllerBase
  with RepositoryService with AccountService with ReferrerAuthenticator

trait ExplorerControllerBase extends ControllerBase {
  self: RepositoryService with AccountService with ReferrerAuthenticator =>

  get("/:owner/:repository/explore")(referrersOnly { repository =>
    contentType = "application/json"
    JsonFormat(explore(repository, "", "."))
  })

  get("/:owner/:repository/explore/*")(referrersOnly { repository =>
    val (id, path) = repository.splitPath(multiParams("splat").head)

    contentType = "application/json"
    JsonFormat(explore(repository, id, if (path.isEmpty) "." else path))
  })

  private def explore(repository: RepositoryService.RepositoryInfo, rev: String, path: String): Option[List[FileNode]] = {
    Using.resource(Git.open(getRepositoryDir(repository.owner, repository.name))){ git =>
      if(!JGitUtil.isEmpty(git)) {
        JGitUtil.getDefaultBranch(git, repository, rev).map {
          case (objectId, revision) =>
            JGitUtil.getRevCommitFromId(git, objectId)
            val pathList = revision :: (if(path == ".") Nil else path.split("/").toList)
            JGitUtil.getFileList(git, revision, path).map { file =>
              FileNode(file.name,
                (helpers.url(repository)
                  :: (if (file.isDirectory) "tree" else "blob")
                  :: (pathList :+ file.name).map(helpers.encodeRefName)
                  ).mkString("/"),
                file.isDirectory)
            }
        }
      } else None
    }
  }
}

case class FileNode(name: String, url: String, isDirectory: Boolean)
