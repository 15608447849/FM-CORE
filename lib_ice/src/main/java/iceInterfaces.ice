[["java:package:com.onek.server"]]
#pragma once
#include <D:/Ice-3.6.3/slice/Ice/Identity.ice>
/**
ice 接口调用
*/
module inf{

    /** 字符串数组 */
   sequence<string> stringArray;

   /** 字节数组 */
   sequence<byte> byteArray;

    /** 方法参数 */
      struct IParam{
          string json;
          stringArray arrays;
          byteArray bytes;
          int pageIndex;
          int pageNumber;
          string extend;
          string token;
      };

      /** 接口调用结构体 */
      struct IRequest{
        string pkg;
        string cls;
        string method;
        IParam param;
      };

      /** 服务接口 interface */
      interface Interfaces{
          /** 公共接口请求 */
          string accessService(IRequest request);
          /** 长连接: 客户端主动上线  */
          void online(Ice::Identity identity);
          /** 长连接: 服务端向指定客户端发送消息 */
          void sendMessageToClient(string identityName,string message);
      };


    /** 长连接客户端,需要具体客户端实现 */
    interface PushMessageClient{
        /** 客户端接收服务端 消息 */
        void receive(string message);
    };

};
