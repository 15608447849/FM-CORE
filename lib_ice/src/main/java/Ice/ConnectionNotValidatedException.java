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
 * This exception is raised if a message is received over a connection
 * that is not yet validated.
 *
 **/
public class ConnectionNotValidatedException extends ProtocolException
{
    public ConnectionNotValidatedException()
    {
        super();
    }

    public ConnectionNotValidatedException(Throwable __cause)
    {
        super(__cause);
    }

    public ConnectionNotValidatedException(String reason)
    {
        super(reason);
    }

    public ConnectionNotValidatedException(String reason, Throwable __cause)
    {
        super(reason, __cause);
    }

    public String
    ice_name()
    {
        return "Ice::ConnectionNotValidatedException";
    }

    public static final long serialVersionUID = 1338347369471941150L;
}