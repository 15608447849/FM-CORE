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
// Generated from file `Locator.ice'
//
// Warning: do not edit this file.
//
// </auto-generated>
//

package Ice;

/**
 * This exception is raised if the replica group provided by the
 * server is invalid.
 *
 **/
public class InvalidReplicaGroupIdException extends Ice.UserException
{
    public InvalidReplicaGroupIdException()
    {
    }

    public InvalidReplicaGroupIdException(Throwable __cause)
    {
        super(__cause);
    }

    public String
    ice_name()
    {
        return "Ice::InvalidReplicaGroupIdException";
    }

    protected void
    __writeImpl(IceInternal.BasicStream __os)
    {
        __os.startWriteSlice("::Ice::InvalidReplicaGroupIdException", -1, true);
        __os.endWriteSlice();
    }

    protected void
    __readImpl(IceInternal.BasicStream __is)
    {
        __is.startReadSlice();
        __is.endReadSlice();
    }

    public static final long serialVersionUID = 576886521218122140L;
}
