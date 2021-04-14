package leezp.rpc.master

import akka.actor.{Actor, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

class TestMaster extends Actor{


  println("构造>> " + this)

  // receive() 之前,构造方法之后
  override def preStart(): Unit = {
//    println(" preStart ... ")
  }

  // 资源回收
  override def postStop(): Unit = {
//    println(" postStop ... ")
  }

  // 用于接收消息, 接收任何类型,无返回
  override def receive: Receive = {

    case "ON" =>  println(" 节点上线 ")
      sender ! "reply"
    case "UN" =>  println(" 节点下线 ")
      sender ! "reply"
    println("**************")
  }
}

object LaunchMaster{
  def main(args: Array[String]): Unit = {

  val host = "127.0.0.1"
  val port = 9999


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

  println("主节点,配置 "+ configString)

  val config = ConfigFactory.parseString(configString)

  val actorSystem = ActorSystem.create("MasterProc",config)

  val master = actorSystem.actorOf(Props[TestMaster],"masterNode")

}
}
