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
 * This exception indicates that an unknown protocol message has been received.
 *
 **/
public class UnknownMessageException extends ProtocolException
{
    public UnknownMessageException()
    {
        super();
    }

    public UnknownMessageException(Throwable __cause)
    {
        super(__cause);
    }

    public UnknownMessageException(String reason)
    {
        super(reason);
    }

    public UnknownMessageException(String reason, Throwable __cause)
    {
        super(reason, __cause);
    }

    public String
    ice_name()
    {
        return "Ice::UnknownMessageException";
    }

    public static final long serialVersionUID = 1625154579332341724L;
}
