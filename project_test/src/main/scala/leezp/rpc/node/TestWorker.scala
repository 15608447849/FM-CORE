package leezp.rpc.node

import akka.actor.{Actor, ActorSelection, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

class TestWorker(masterActorUrl:String)  extends Actor{

  println("构造>> " + this)

  var master : ActorSelection = _

  override def preStart(): Unit = {
    master = context.actorSelection(masterActorUrl)
    println(master)
    master ! "ON"
  }

  override def postStop(): Unit = super.postStop()

  override def receive: Receive = {
    case "reply" =>{
      println( "收到master节点信息!" )
    }
  }
}



object LaunchWorker {

  def main(args: Array[String]): Unit = {

    val host = "127.0.0.1"
    val port = 8888

    val configString =
      s"""
     akka{
         actor{
             provider="akka.remote.RemoteActorRefProvider"
             allow-java-serialization=on
         }
         remote{
             enabled-transports=["akka.remote.netty.tcp"]
             artery{
                 transport=tcp
                 canonical.hostname=$host
                 canonical.port=$port
             }
         }
     }
       """

    println("子节点,配置 "+ configString)

    val config = ConfigFactory.parseString(configString)

    val actorSystem = ActorSystem("WorkerProc",config)

    val worker = actorSystem.actorOf(Props(new TestWorker("akka://MasterProc@127.0.0.1:9999/user/masterNode")),"workerNode")







  }
}