package controllers

import play.api.cache.Cache
import play.api.libs.json._
import play.api.mvc.Action
import play.api.mvc.Controller
import play.api.mvc.Result

import play.api.libs.ws._

import scala.Any

import scala.concurrent._
import scala.util.Failure
import scala.util.Success
import scala.util.Try

import play.api.Play.current
import ExecutionContext.Implicits.global

object Application extends Controller {
  
  def index = Action {
    Redirect(routes.Application.repositories)
  }

  def repositories = Action {
  	Ok(views.html.repositories(null))
  }

  def displayRepositories(searchQuery: String) = Action {
  	Async {
      val url = "https://api.github.com/legacy/repos/search/" + searchQuery

      val maybeJSon: Option[JsArray] = Cache.getAs[JsArray](url)
      if (maybeJSon isDefined) {
        val jsArray = maybeJSon.map(data => data.as[JsArray].value)
        Future(Ok(views.html.repositories(jsArray getOrElse null)))  
      } else {
        WSHelper.getRepositories(url).map { jsArray =>
            Cache.set(url, jsArray, 10800)
            Ok(views.html.repositories(jsArray.value))
          }
      }
  	}
  }

  def JSONRepositories(searchQuery: String) = Action {
    Async {
      val repositories: Future[Response] = WS.url("https://api.github.com/legacy/repos/search/" + searchQuery).get()
      repositories.map { response => 
        val responseJson = response.json.as[JsObject]
        Ok(responseJson)
      }
    }
  }

  def displayRepository(owner: String, repo: String) = Action {
    Async {
      val url = "https://api.github.com/repos/" + owner + "/" + repo

      val maybeJSon: Option[JsObject] = Cache.getAs[JsObject](url)
      if (maybeJSon isDefined) {
        val responseJSon = maybeJSon.map(data => data.as[JsObject])
        Future(Ok(views.html.repository(responseJSon getOrElse null, owner, repo)))  
      } else {
        WSHelper.getJsObject(url).map { jsObject =>
            Cache.set(url, jsObject, 10800)
            Ok(views.html.repository(jsObject, owner, repo))
          }
      }
    }
  }

  def displayJSONRepository(owner: String, repo: String) = Action {
    Async {
      val repoData = WS.url("https://api.github.com/repos/" + owner + "/" + repo).get()
      repoData.map { response =>
        val responseJson = response.json.as[JsObject]
        Ok(responseJson)
      }
    }
  }

  def JSONContributors(owner: String, repo: String) = Action {
    Async {
      val url = "https://api.github.com/repos/" + owner + "/" + repo + "/contributors"

      val maybeJSon: Option[JsArray] = Cache.getAs[JsArray](url)
      if (maybeJSon isDefined) {
        maybeJSon.map { data => 
            val jsArray: JsArray = data.as[JsArray]
            Future(Ok(jsArray))
          } get
      } else {
        WSHelper.getJsArray(url).map { jsArray =>
            Cache.set(url, jsArray, 10800)
            Ok(jsArray)
          }
      }

      /*val contributorsData = WS.url("https://api.github.com/repos/" + owner + "/" + repo + "/contributors").get()
      contributorsData.map { response =>
        val responseJson = response.json.as[JsArray]
        Ok(responseJson)
      }*/
    }
  }

  def JSONCommits(owner: String, repo: String) = Action {
    Async {
      val url = "https://api.github.com/repos/" + owner + "/" + repo + "/commits?per_page=100"

      val maybeJSon: Option[JsArray] = Cache.getAs[JsArray](url)
      if (maybeJSon isDefined) {
        maybeJSon.map { data => 
            val jsArray: JsArray = data.as[JsArray]
            Future(Ok(jsArray))
          } get
      } else {
        WSHelper.getJsArray(url).map { jsArray =>
            Cache.set(url, jsArray, 10800)
            Ok(jsArray)
          }
      }

      /*val contributorsData = WS.url("https://api.github.com/repos/" + owner + "/" + repo + "/commits?per_page=100").get()
      contributorsData.map { response =>
        val responseJson = response.json.as[JsArray]
        Ok(responseJson)
      }*/
    }
  }

  def displayStats(owner: String, repo: String) = Action {
    Async {
      val repoData = WS.url("https://api.github.com/repos/" + owner + "/" + repo).get()
      repoData.flatMap { response =>
        val responseJson = response.json.as[JsObject]
        val contributorsUrl = (responseJson \ "contributors_url").asOpt[String]

        val contributorsData = WS.url(contributorsUrl.get).get()
        contributorsData.flatMap {
          response =>
            val responseJson = response.json.as[JsArray].value
            Future(Ok(views.html.stats(responseJson)))
        }
      }
    }
  }

  def shortenString(original: Option[String], maxSize: Int) = {
    original.map(result => {
          if (result.size > maxSize) {
            result.substring(0, maxSize) + "..."
          } else {
            result
          }
      })
  }
    /*Async {
      val repoData: Future[Response] = WS.url("https://api.github.com/repo/" + owner + "/" + "repo").get()
      repoData.map { response =>
        val responseJson = response.json.as[JsObject]
        val contributorsUrl = (responseJson \ "contributors_url").asOpt[String]

        val contributorsData: Future[Response] = WS.url(contributorsUrl.get)get()
        contributorsData.map {
          response =>
            val responseJson = response.json.as[JsArray].value
            Ok(views.html.stats(responseJson))
        }
      }*/


      /*contributorsUrl onComplete {
        case Success(url) => {
          val contributorsData: Future[Response] = WS.url(url.get)get()
          contributorsData.map {
            response =>
            val responseJson = response.json.as[JsArray].value
            Ok(views.html.stats(responseJson))
          }
        }
        case Failure(e) => InternalServerError("Unable to contact GitHub. " + e.getMessage())
      }*/

    

    
      /*val result = contributors.onComplete {
       case Success(contributors) => Ok(views.html.stats(contributors))
       case Failure(e) => InternalServerError("Unable to contact GitHub. " + e.getMessage())
      }
      
      Async(result)*/

      
       


      /*contributors.onSuccess {
        case futureCont => Ok(views.html.stats(futureCont))
      }
      contributors.onFailure {
        case error => InternalServerError("Unable to contact GitHub. " + error.getMessage())
      }*/
    
  
  
}