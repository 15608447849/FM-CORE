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
 * This exception is raised to reject an illegal servant (typically
 * a null servant)
 *
 **/
public class IllegalServantException extends Ice.LocalException
{
    public IllegalServantException()
    {
        reason = "";
    }

    public IllegalServantException(Throwable __cause)
    {
        super(__cause);
        reason = "";
    }

    public IllegalServantException(String reason)
    {
        this.reason = reason;
    }

    public IllegalServantException(String reason, Throwable __cause)
    {
        super(__cause);
        this.reason = reason;
    }

    public String
    ice_name()
    {
        return "Ice::IllegalServantException";
    }

    /**
     * Describes why this servant is illegal.
     *
     **/
    public String reason;

    public static final long serialVersionUID = 1134807368810099935L;
}