package controllers

import java.nio.ByteBuffer

import trash.backend.TestSourceToLeonString
import boopickle.Default._
import play.api._
import play.api.mvc._
import play.api.routing.JavaScriptReverseRouter
import services.ApiService
import shared.{Api, SharedMessages}

import scala.concurrent.ExecutionContext.Implicits.global

/** Content of this file is inspired from https://github.com/ochrons/scalajs-spa-tutorial/blob/master/server/src/main/scala/controllers/Application.scala **/

object Router extends autowire.Server[ByteBuffer, Pickler, Pickler] {
  override def read[R: Pickler](p: ByteBuffer) = Unpickle[R].fromBytes(p)
  override def write[R: Pickler](r: R) = Pickle.intoBytes(r)
}

object Application extends Controller {

  val apiService = new ApiService()
  def autowireApi(path: String) = Action.async(parse.raw) {
    implicit request =>
      println(s"Request path: $path")
      // get the request body as Array[Byte]
      val b = request.body.asBytes(parse.UNLIMITED).get

      // call Autowire route
      Router.route[Api](apiService)(
        autowire.Core.Request(path.split("/"), Unpickle[Map[String, ByteBuffer]].fromBytes(ByteBuffer.wrap(b)))
      ).map(buffer => {
        val data = Array.ofDim[Byte](buffer.remaining())
        buffer.get(data)
        Ok(data)
      })
  }

//  def index = Action {
////    val pathToSourceProgram = "src/main/scala-2.11/SourceCode.txt"
//    val pathToSourceProgram = "server/app/trash.manipulatedFiles/SourceCode.txt"
//    val leonStringOfSourceProgram = TestSourceToLeonString.path2LeonString(pathToSourceProgram)
//
//    Ok(views.html.index(leonStringOfSourceProgram))
//  }

  def index = Action {
//    Ok(views.html.aceeditor())
    Ok(views.html.index(SharedMessages.itWorks))
  }


  //Source = https://www.playframework.com/documentation/2.4.x/ScalaJavascriptRouting
//  def javaScriptRoutes = Action { implicit request =>
//    Ok(
//      JavaScriptReverseRouter("jsRoutes")(
//        routes.javascript.
//      )
//    )
//  }



}
