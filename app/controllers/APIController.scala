package controllers

import javax.inject.Inject

import play.api.Logger
import models.ServiceCall
import org.joda.time.DateTime
import play.api.mvc._
import services.InUseService

class APIController @Inject() (backend: InUseService) extends Controller {

  // PUT request
  def addService(service: String) = Action {

    Logger.info("Registering new service: " + service)

    backend.registerService(service)

    Ok

  }

  // POST request
  def addRecord(service: String) = Action { request =>

    Logger.info("Registering new call in service: " + service)

    backend.registerCall(
      service,
      ServiceCall(service, DateTime.now.getMillis, request.body.asText.getOrElse("") )
    )

    Ok

  }


}
