package jp.yshnb.samples

import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

import akka.actor.{Actor, Props, ActorSystem}
import akka.pattern.ask
import akka.util.Timeout

object TellAndAskPatternsApp extends App {
  import Someone._

  implicit val timeout = Timeout(5 seconds)

  val system = ActorSystem("tell-and-ask")
  val someone = system.actorOf(Props[Someone], "someone")

  val futureForTell = someone ! Hello("Hello Someone!") // actually, it does not return Future (return Unit instead of Future).
  val futureForAsk  = someone ? Hello("Hello Someone!")

  Map("tell" -> futureForTell, "ask" -> futureForAsk).foreach { 
    case (t, reply: Future[Reply]) =>
      reply.foreach { r => println(s"${t}: get reply '${r.text}'") }
    case (t, _) =>
      println(s"${t}: no reply")
  }

  Thread.sleep(1000)
  system.shutdown()
}

class Someone extends Actor {
  import Someone._

  def receive = {
    case Hello(text) =>
      println(s"${self.path.name}: i get message '${text}'")
      sender ! Reply("Hello MainApp!")
  }
}

object Someone {
  case class Hello(text: String)
  case class Reply(text: String)
}
