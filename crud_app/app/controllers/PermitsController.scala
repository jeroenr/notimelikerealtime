package controllers

/**
 * Created with IntelliJ IDEA.
 * User: jeroen
 * Date: 7/12/13
 * Time: 10:35 AM
 * To change this template use File | Settings | File Templates.
 */

import play.api.mvc._
import play.api.libs.json._
import play.api.data._
import play.api.data.Forms._
import anorm._
import models._
import com.redis._
import play.api.libs.concurrent._
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import ExecutionContext.Implicits.global
import play.api.Play.current
import utils.Implicits._

object PermitsController extends Controller {
  val redisClient = new RedisClient("localhost", 6379)

  val permitForm = Form(
    mapping(
      "id" -> ignored(NotAssigned:Pk[Long]),
      "project" -> nonEmptyText,
      "owner" -> nonEmptyText,
      "assignee" -> nonEmptyText
    )(Permit.apply)(Permit.unapply)
  )

  def index = Action {
    implicit request =>
      Ok(Json.toJson(Permit.list))
  }

  def show(id: Long) = Action {
    implicit request =>
      Ok(Json.toJson(Permit.read(id)))
  }

  def create = Action(parse.json) {
    implicit request =>
      request.body.validate[Permit].map {
        case permit => {
          Akka.future {
            Json.toJson(Permit.save(permit))
          }.orTimeout("""Timeout occurred while saving permit {permit}""", 2 seconds).map {
            _.fold(
              permitJson => redisClient.publish("newpermit", permitJson.toString),
              timeout => redisClient.publish("error", timeout)
            )
          }
          Accepted(request.body)
        }
      }.recoverTotal {
        e => BadRequest(Json.obj("status" -> "error", "message" -> JsError.toFlatJson(e)))
      }
  }
  def update(id:Long) = Action(parse.json) {
    implicit request =>
      request.body.validate[Permit].map {
        case permit => {
          Akka.future {
            Json.toJson(Permit.update(id, permit))
          }.orTimeout("""Timeout occurred while updating permit {permit}""", 2 seconds).map {
            _.fold(
              _ => redisClient.publish("updatepermit", request.body.toString),
              timeout => redisClient.publish("error", timeout)
            )
          }
          Accepted(request.body)
        }
      }.recoverTotal {
        e => BadRequest(Json.obj("status" -> "error", "message" -> JsError.toFlatJson(e)))
      }
  }

  def delete(id:Long) = Action {
    implicit request =>
      Permit.delete(id) match {
        case 1 => {
          redisClient.publish("deletepermit", Json.obj("permit" -> id).toString)
          Ok(Json.obj("status" -> "OK", "message" -> """Record {id} deleted successfully."""))
        }
        case _ => BadRequest(Json.obj("status" -> "error", "message" -> "Record does not exists"))
      }
  }

  def doSomething = {}
}
