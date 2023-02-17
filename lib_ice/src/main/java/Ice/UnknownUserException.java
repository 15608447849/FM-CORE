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
 * An operation raised an incorrect user exception.
 *
 * This exception is raised if an operation raises a
 * user exception that is not declared in the exception's
 * <tt>throws</tt> clause. Such undeclared exceptions are
 * not transmitted from the server to the client by the Ice
 * protocol, but instead the client just gets an
 * {@link UnknownUserException}. This is necessary in order to not violate
 * the contract established by an operation's signature: Only local
 * exceptions and user exceptions declared in the
 * <tt>throws</tt> clause can be raised.
 *
 **/
public class UnknownUserException extends UnknownException
{
    public UnknownUserException()
    {
        super();
    }

    public UnknownUserException(Throwable __cause)
    {
        super(__cause);
    }

    public UnknownUserException(String unknown)
    {
        super(unknown);
    }

    public UnknownUserException(String unknown, Throwable __cause)
    {
        super(unknown, __cause);
    }

    public String
    ice_name()
    {
        return "Ice::UnknownUserException";
    }

    public static final long serialVersionUID = -6046568406824082586L;
}
