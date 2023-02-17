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
 * This exception is raised if no suitable object factory was found during
 * unmarshaling of a Slice class instance.
 *
 * @see ObjectFactory
 * @see Communicator#addObjectFactory
 * @see Communicator#findObjectFactory
 *
 **/
public class NoObjectFactoryException extends MarshalException
{
    public NoObjectFactoryException()
    {
        super();
        type = "";
    }

    public NoObjectFactoryException(Throwable __cause)
    {
        super(__cause);
        type = "";
    }

    public NoObjectFactoryException(String reason, String type)
    {
        super(reason);
        this.type = type;
    }

    public NoObjectFactoryException(String reason, String type, Throwable __cause)
    {
        super(reason, __cause);
        this.type = type;
    }

    public String
    ice_name()
    {
        return "Ice::NoObjectFactoryException";
    }

    /**
     * The Slice type ID of the class instance for which no
     * no factory could be found.
     *
     **/
    public String type;

    public static final long serialVersionUID = 7137819965514677580L;
}