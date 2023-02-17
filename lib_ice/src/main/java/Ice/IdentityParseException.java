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
 * This exception is raised if there was an error while parsing a
 * stringified identity.
 *
 **/
public class IdentityParseException extends Ice.LocalException
{
    public IdentityParseException()
    {
        str = "";
    }

    public IdentityParseException(Throwable __cause)
    {
        super(__cause);
        str = "";
    }

    public IdentityParseException(String str)
    {
        this.str = str;
    }

    public IdentityParseException(String str, Throwable __cause)
    {
        super(__cause);
        this.str = str;
    }

    public String
    ice_name()
    {
        return "Ice::IdentityParseException";
    }

    /**
     * Describes the failure and includes the string that could not be parsed.
     *
     **/
    public String str;

    public static final long serialVersionUID = 8547577763521735682L;
}
