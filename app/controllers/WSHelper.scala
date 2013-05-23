package controllers

import play.api.libs.json._
import play.api.libs.ws._

import play.api.mvc.Action
import play.api.mvc.Controller
import play.api.mvc.Result

import scala.concurrent._
import scala.util.Failure
import scala.util.Success
import scala.util.Try

import ExecutionContext.Implicits.global

object WSHelper {

  def login = "fcassin";
  def password = "retycui0";

	def getRepositories(url: String): Future[JsArray] = {
  		val repositories: Future[Response] = WS.url(url).withAuth(login, password, com.ning.http.client.Realm.AuthScheme.BASIC).get()
  		repositories.map { response => 
			val responseJson = response.json.as[JsObject]
			(responseJson \ "repositories").as[JsArray]
  		}
  	}

  	def getJsObject(url: String): Future[JsObject] = {
  		val repository: Future[Response] = WS.url(url).withAuth(login, password, com.ning.http.client.Realm.AuthScheme.BASIC).get()
  		repository.map { response => 
			response.json.as[JsObject]
  		}
  	}

  	def getJsArray(url: String): Future[JsArray] = {
  		val repository: Future[Response] = WS.url(url).withAuth(login, password, com.ning.http.client.Realm.AuthScheme.BASIC).get()
  		repository.map { response => 
			response.json.as[JsArray]
  		}
  	}

}