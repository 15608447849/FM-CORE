package framework.server;

import bottle.objectref.ObjectRefUtil;
import bottle.properties.abs.ApplicationPropertiesBase;
import bottle.properties.annotations.PropertiesFilePath;
import bottle.properties.annotations.PropertiesName;

/** web服务代理 */
@PropertiesFilePath("/application.properties")
class WebServerProxy {

    @PropertiesName("framework.undertow.launch.imp")
    private static String webServerImp = "framework.webserver.NodeWebFileServer";

    static {
        ApplicationPropertiesBase.initStaticFields(WebServerProxy.class);
    }

    static void startWebServer(int httpPort, String httpFilePath, long httpFileTime){
        try {
            ObjectRefUtil.callStaticMethod(webServerImp,"startWebServer",
                    new Class[]{int.class,String.class,long.class},httpPort,httpFilePath,httpFileTime);
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    static void stopWebServer(){
        try {
            ObjectRefUtil.callStaticMethod(webServerImp,"stopWebServer",null);
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

}
