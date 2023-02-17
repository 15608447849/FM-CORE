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
// Generated from file `IceBox.ice'
//
// Warning: do not edit this file.
//
// </auto-generated>
//

package IceBox;

/**
 * This exception is thrown if an attempt is made to start an
 * already-started service.
 *
 **/
public class AlreadyStartedException extends Ice.UserException
{
    public AlreadyStartedException()
    {
    }

    public AlreadyStartedException(Throwable __cause)
    {
        super(__cause);
    }

    public String
    ice_name()
    {
        return "IceBox::AlreadyStartedException";
    }

    protected void
    __writeImpl(IceInternal.BasicStream __os)
    {
        __os.startWriteSlice("::IceBox::AlreadyStartedException", -1, true);
        __os.endWriteSlice();
    }

    protected void
    __readImpl(IceInternal.BasicStream __is)
    {
        __is.startReadSlice();
        __is.endReadSlice();
    }

    public static final long serialVersionUID = 115156757889290093L;
}