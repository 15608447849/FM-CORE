// **********************************************************************
//
// Copyright (c) 2003-2017 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

package IceInternal;

import bottle.util.Log4j;

public class ProcessI extends Ice._ProcessDisp
{
    public ProcessI(Ice.Communicator communicator)
    {
        _communicator = communicator;
    }

    @Override
    public void
    shutdown(Ice.Current current)
    {
        _communicator.shutdown();
    }

    @Override
    public void
    writeMessage(String message, int fd, Ice.Current current)
    {
        switch(fd)
        {
            case 1:
            {
                Log4j.info(message);
                break;
            }
            case 2:
            {
                Log4j.info(message);
                break;
            }
        }
    }

    private Ice.Communicator _communicator;
}
