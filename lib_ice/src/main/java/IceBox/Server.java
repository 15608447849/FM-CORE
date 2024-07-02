// **********************************************************************
//
// Copyright (c) 2003-2017 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

package IceBox;
import bottle.util.Log4j;

import java.lang.management.ManagementFactory;
public final class Server extends Ice.Application
{

    public static void
    main(String[] args)
    {
        Log4j.info("Ice 节点服务启动 , 进程ID: "+ ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
//        args = new String[]{"--Ice.Config=C:/IDEAWORK/FM-CORE/lib_ice/src/main/node-db/servers/imServer-box-1/config/config"};
        Ice.InitializationData initData = new Ice.InitializationData();
        initData.properties = Ice.Util.createProperties();
        initData.properties.setProperty("Ice.Admin.DelayCreation", "1");
        Server server = new Server();
        int status = server.main("IceBox.Server", args, initData);
        Log4j.info("Ice 节点服务结束 , 进程退出状态码: "+ status);
        System.exit(status);
    }

    private static void
    usage()
    {
        Log4j.info("Usage: IceBox.Server [options] --Ice.Config=<file>\n");
        Log4j.info(
                "Options:\n" +
                        "-h, --help           Show this message.\n"
        );
    }

    @Override
    public int
    run(String[] args)
    {
        final String prefix = "IceBox.Service.";
        Ice.Properties properties = communicator().getProperties();
        java.util.Map<String, String> services = properties.getPropertiesForPrefix(prefix);
        java.util.List<String> argSeq = new java.util.ArrayList<String>(args.length);
        for(String s : args)
        {
            argSeq.add(s);
        }

        for(java.util.Map.Entry<String, String> entry : services.entrySet())
        {
            String name = entry.getKey().substring(prefix.length());
            for(int i = 0; i < argSeq.size(); ++i)
            {
                if(argSeq.get(i).startsWith("--" + name))
                {
                    argSeq.remove(i);
                    i--;
                }
            }
        }

        for(String arg : argSeq)
        {
            if(arg.equals("-h") || arg.equals("--help"))
            {
                usage();
                return 0;
            }
            else
            {
                Log4j.info("Server: unknown option `" + arg + "'");
                usage();
                return 1;
            }
        }
        ServiceManagerI serviceManagerImpl = new ServiceManagerI(communicator(), args);
        return serviceManagerImpl.run();
    }
}
