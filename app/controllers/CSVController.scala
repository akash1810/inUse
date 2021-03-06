package controllers

import java.io.File
import javax.inject.Inject

import models.ServiceCall
import org.joda.time.format.DateTimeFormat
import play.api.mvc.{Action, Controller}
import services.{Config, Dynamo, InUseService}

class CSVController @Inject() (backend: InUseService) extends Controller {

  def millisToDate(ms: Long): String = DateTimeFormat.forPattern("yyyy-MM-dd").print(ms)

  def generateCsvDownload() = Action {
    Dynamo.getServiceCallsCsv()
    Ok.sendFile(new File(s"${Config.homeDirectory}/export.csv"))
  }

  def generateCsvString() = Action {
    val content = Dynamo.getServiceCallsCsv()
    Ok(content)
  }

  def forService(service: String) = Action {

    val serviceCalls: List[ServiceCall] = backend.getRecentServiceCallsMap().get(service) match {
      case Some(calls) => calls
      case None => List()
    }

    Ok("date,count\n" + serviceCalls
      .groupBy(call => millisToDate(call.createdAt))  // group calls by date
      .map({case (k, v) => (k, v.size)})              // convert to date->count map
      .toList
      .sortWith((p1, p2) => p1._1 < p2._1)            // sorted by date alpha
      .map{ case (date, count) => s"$date,$count" }   // entries as strings
      .mkString("\n")                                 // delimited by newlines
    )

  }

  def forServiceByUser(service: String) = Action {

    val serviceCalls: List[ServiceCall] = backend.getRecentServiceCallsMap().get(service) match {
      case Some(calls) => calls
      case None => List()
    }

    // workaround for https://issues.scala-lang.org/browse/SI-6476
    def quote(s: String) = "\""+s+"\""

    Ok("date,user,count\n" + serviceCalls
      .map(call=>call.toItem.withString("date", millisToDate(call.createdAt)))
      .groupBy(call => (call.getString("date"), call.getString("user")))
      .map({case (k, v) => (k, v.size)})
      .toList
      .sortBy(r => (r._1._1, r._1._2))
      .map{ case (dateuser, count) => s"${dateuser._1},${quote(dateuser._2)},$count" }
      .mkString("\n")
    )

  }

}
