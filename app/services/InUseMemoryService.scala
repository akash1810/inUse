package services

import javax.inject.Singleton

import models.ServiceCall
import org.joda.time.DateTime

@Singleton
class InUseMemoryService extends InUseService {

  var calls: Map[String, List[ServiceCall]] = Map()

  override def registerService(service: String): Unit = {

    // append service into map with no records

    calls = calls + (service -> List())

  }

  override def registerCall(service: String, record: ServiceCall): Unit = {

    // append record into map(service)

    calls.get(service) match {

      case Some(records) =>  {
        val newRecords: List[ServiceCall] = records :+ record
        calls = calls + (service -> newRecords)
      }

      case None => {
        // no service, do nothing
      }

    }

  }


  def getRecentServiceCallsMap(): Map[String, List[ServiceCall]] = calls

  val today = DateTime.now.getMillis
  val yesterday = DateTime.now.minusDays(1).getMillis

  // stub data

  registerService("example service")
  registerService("empty service")
  registerService("another service")
  registerCall("example service", ServiceCall("example service", today, "{\"user\": \"ted\"}"))
  registerCall("example service", ServiceCall("example service", today, "{\"user\": \"bill\"}"))
  registerCall("example service", ServiceCall("example service", yesterday, "{\"user\": \"rufus\"}"))

}
