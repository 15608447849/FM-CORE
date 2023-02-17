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
 * This exception indicates that a message size is less
 * than the minimum required size.
 *
 **/
public class IllegalMessageSizeException extends ProtocolException
{
    public IllegalMessageSizeException()
    {
        super();
    }

    public IllegalMessageSizeException(Throwable __cause)
    {
        super(__cause);
    }

    public IllegalMessageSizeException(String reason)
    {
        super(reason);
    }

    public IllegalMessageSizeException(String reason, Throwable __cause)
    {
        super(reason, __cause);
    }

    public String
    ice_name()
    {
        return "Ice::IllegalMessageSizeException";
    }

    public static final long serialVersionUID = 3581741698610780247L;
}
