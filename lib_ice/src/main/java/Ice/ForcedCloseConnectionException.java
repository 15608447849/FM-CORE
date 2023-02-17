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
 * This exception is raised by an operation call if the application
 * forcefully closes the connection {@link Connection#close}.
 *
 * @see Connection#close
 *
 **/
public class ForcedCloseConnectionException extends ProtocolException
{
    public ForcedCloseConnectionException()
    {
        super();
    }

    public ForcedCloseConnectionException(Throwable __cause)
    {
        super(__cause);
    }

    public ForcedCloseConnectionException(String reason)
    {
        super(reason);
    }

    public ForcedCloseConnectionException(String reason, Throwable __cause)
    {
        super(reason, __cause);
    }

    public String
    ice_name()
    {
        return "Ice::ForcedCloseConnectionException";
    }

    public static final long serialVersionUID = -8097620318777707546L;
}
