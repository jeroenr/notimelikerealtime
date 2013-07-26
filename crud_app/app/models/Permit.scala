package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._

/**
 * Created with IntelliJ IDEA.
 * User: jeroen
 * Date: 7/11/13
 * Time: 4:27 PM
 * To change this template use File | Settings | File Templates.
 */

case class Permit(id: Pk[Long], project: String, owner: String, assignee: String)

object Permit {

  // (de)serialization of primary key ID
  implicit object PkFormat extends Format[Pk[Long]] {
    def reads(json: JsValue): JsResult[Pk[Long]] = JsSuccess(Id(json.as[Long]))

    def writes(id: Pk[Long]): JsNumber = JsNumber(id.get)
  }

  implicit val permitReads: Reads[Permit] = (
    (__ \ "id").readNullable[Pk[Long]].map(_.getOrElse(NotAssigned)) ~
      (__ \ "project").read(minLength[String](1)) ~
      (__ \ "owner").read(minLength[String](1)) ~
      (__ \ "assignee").read(minLength[String](1))
    )(Permit.apply _)

  implicit val permitWrites = Json.writes[Permit]

  val fromSQL = {
    get[Pk[Long]]("permit.id") ~
      get[String]("permit.project") ~
      get[String]("permit.owner") ~
      get[String]("permit.assignee") map {
      case id ~ project ~ owner ~ assignee => Permit(id, project, owner, assignee)
    }
  }

  // CRUD ops

  def save(permit: Permit) = {
    DB.withConnection {
      implicit c =>
        val id = permit.id.getOrElse {
          SQL("select next value for permit_id_seq").as(scalar[Long].single)
        }
        SQL(
          """
          insert into permit values (
            {id}, {project}, {owner}, {assignee}
          )
          """
        ).on(
          'id -> id,
          'project -> permit.project,
          'owner -> permit.owner,
          'assignee -> permit.assignee
        ).executeUpdate()

        permit.copy(id = Id(id))
    }
  }

  def read(id: Long) = {
    DB.withConnection {
      implicit c =>
        SQL("select * from permit where id = {id}").on(
          'id -> id
        ).as(Permit.fromSQL.singleOpt)
    }
  }

  def update(id: Long, permit: Permit) = {
    DB.withConnection {
      implicit c =>
        SQL(
          """
          update permit
          set project={project},owner={owner},assignee={assignee}
          where id = {id}
          """
        ).on(
          'id -> id,
          'project -> permit.project,
          'owner -> permit.owner,
          'assignee -> permit.assignee
        ).executeUpdate()
    }
  }

  def delete(id: Long) = {
    DB.withConnection {
      implicit c =>
        SQL(
          """
          delete from permit
          where id = {id}
          """
        ).on(
          'id -> id
        ).executeUpdate()
    }
  }

  def list = {
    DB.withConnection {
      implicit c =>
        SQL("select * from permit").as(Permit.fromSQL *)
    }
  }
}
