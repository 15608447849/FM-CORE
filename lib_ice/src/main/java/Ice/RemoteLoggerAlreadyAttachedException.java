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
// Generated from file `RemoteLogger.ice'
//
// Warning: do not edit this file.
//
// </auto-generated>
//

package Ice;

/**
 * An exception thrown by {@link LoggerAdmin#attachRemoteLogger} to report
 * that the provided {@link RemoteLogger} was previously attached to this
 * {@link LoggerAdmin}.
 *
 **/
public class RemoteLoggerAlreadyAttachedException extends Ice.UserException
{
    public RemoteLoggerAlreadyAttachedException()
    {
    }

    public RemoteLoggerAlreadyAttachedException(Throwable __cause)
    {
        super(__cause);
    }

    public String
    ice_name()
    {
        return "Ice::RemoteLoggerAlreadyAttachedException";
    }

    protected void
    __writeImpl(IceInternal.BasicStream __os)
    {
        __os.startWriteSlice("::Ice::RemoteLoggerAlreadyAttachedException", -1, true);
        __os.endWriteSlice();
    }

    protected void
    __readImpl(IceInternal.BasicStream __is)
    {
        __is.startReadSlice();
        __is.endReadSlice();
    }

    public static final long serialVersionUID = 8261337785094940035L;
}
