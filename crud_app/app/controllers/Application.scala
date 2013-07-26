package controllers

import play.api._
import play.api.mvc._
import models.Permit

object Application extends Controller {
  
  def index = Action {
    val socketHandlerEndpoint = Play.current.configuration.getString("socket.handler.endpoint").getOrElse("localhost")
    Ok(views.html.index("Permits", Permit.list, PermitsController.permitForm, socketHandlerEndpoint))
  }
  
}