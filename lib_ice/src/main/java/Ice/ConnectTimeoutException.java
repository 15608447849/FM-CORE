// **********************************************************************
//
// Copyright (c) 2003-2017 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************
//
// Ice version 3.6.4
//
// <auto-generated>
//
// Generated from file `LocalException.ice'
//
// Warning: do not edit this file.
//
// </auto-generated>
//

package Ice;

/**
 * This exception indicates a connection establishment timeout condition.
 *
 **/
public class ConnectTimeoutException extends TimeoutException
{
    public ConnectTimeoutException()
    {
        super();
    }

    public ConnectTimeoutException(Throwable __cause)
    {
        super(__cause);
    }

    public String
    ice_name()
    {
        return "Ice::ConnectTimeoutException";
    }

    public static final long serialVersionUID = -1271371420507272518L;
}
