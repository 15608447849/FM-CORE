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
 * This exception is raised if an operation for a given object does
 * not exist on the server. Typically this is caused by either the
 * client or the server using an outdated Slice specification.
 *
 **/
public class OperationNotExistException extends RequestFailedException
{
    public OperationNotExistException()
    {
        super();
    }

    public OperationNotExistException(Throwable __cause)
    {
        super(__cause);
    }

    public OperationNotExistException(Identity id, String facet, String operation)
    {
        super(id, facet, operation);
    }

    public OperationNotExistException(Identity id, String facet, String operation, Throwable __cause)
    {
        super(id, facet, operation, __cause);
    }

    public String
    ice_name()
    {
        return "Ice::OperationNotExistException";
    }

    public static final long serialVersionUID = 3973646568523472620L;
}
