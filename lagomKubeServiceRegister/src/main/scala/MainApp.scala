
import me.alexray.lagom.kube.discovery.KubeServiceLocatorServer
import play.api.Logger
import collection.JavaConverters._


import scala.concurrent.Future

/**
  * Created by Alexander Ray on 25.04.17.
  **/

object MainApp extends App {

  private def startLocatorService() = {
    val service = new KubeServiceLocatorServer()

    service.start(8000, 9000, Map.empty[String, String].asJava)

//    service.close()
  }

  private val logger: Logger = Logger(this.getClass)

  println("hallo")
  logger.info("hallo from Logger")

//  testEtcd()
  startLocatorService()


}
