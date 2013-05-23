package controllers

import play.api.libs.json._
import play.api.libs.ws._
import play.api.libs.ws.WS
import play.api.libs.ws.WS.WSRequestHolder

import play.api.mvc.Action
import play.api.mvc.Controller
import play.api.mvc.Result

import scala.concurrent._
import scala.util.Failure
import scala.util.Success
import scala.util.Try

import ExecutionContext.Implicits.global

object WSHelper {

  def login = "";
  def password = "";

  def getWSCaller(url: String): WSRequestHolder = {
    if (login != "" && password != "") {
      WS.url(url).withAuth(login, password, com.ning.http.client.Realm.AuthScheme.BASIC)  
    } else {
      WS.url(url)
    }
  }

	def getRepositories(url: String): Future[JsArray] = {
  		val repositories: Future[Response] = getWSCaller(url).get()
  		repositories.map { response => 
			val responseJson = response.json.as[JsObject]
			(responseJson \ "repositories").as[JsArray]
  		}
  	}

  	def getJsObject(url: String): Future[JsObject] = {
  		val repository: Future[Response] = getWSCaller(url).get()
  		repository.map { response => 
			response.json.as[JsObject]
  		}
  	}

  	def getJsArray(url: String): Future[JsArray] = {
  		val repository: Future[Response] = getWSCaller(url).get()
  		repository.map { response => 
			response.json.as[JsArray]
  		}
  	}

}