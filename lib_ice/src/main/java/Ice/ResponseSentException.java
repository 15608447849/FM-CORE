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
 * Indicates that the response to a request has already been sent;
 * re-dispatching such a request is not possible.
 *
 **/
public class ResponseSentException extends Ice.LocalException
{
    public ResponseSentException()
    {
    }

    public ResponseSentException(Throwable __cause)
    {
        super(__cause);
    }

    public String
    ice_name()
    {
        return "Ice::ResponseSentException";
    }

    public static final long serialVersionUID = -8662310375309760496L;
}