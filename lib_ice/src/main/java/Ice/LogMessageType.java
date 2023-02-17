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
 * An enumeration representing the different types of log messages.
 *
 **/
public enum LogMessageType implements java.io.Serializable
{
    
    /**
     * The {@link Logger} received a print message.
     *
     **/
    PrintMessage(0),
    
    /**
     * The {@link Logger} received a trace message.
     *
     **/
    TraceMessage(1),
    
    /**
     * The {@link Logger} received a warning message.
     *
     **/
    WarningMessage(2),
    
    /**
     * The {@link Logger} received an error message.
     *
     **/
    ErrorMessage(3);

    public int
    value()
    {
        return __value;
    }

    public static LogMessageType
    valueOf(int __v)
    {
        switch(__v)
        {
        case 0:
            return PrintMessage;
        case 1:
            return TraceMessage;
        case 2:
            return WarningMessage;
        case 3:
            return ErrorMessage;
        }
        return null;
    }

    private
    LogMessageType(int __v)
    {
        __value = __v;
    }

    public void
    __write(IceInternal.BasicStream __os)
    {
        __os.writeEnum(value(), 3);
    }

    public static void
    __write(IceInternal.BasicStream __os, LogMessageType __v)
    {
        if(__v == null)
        {
            __os.writeEnum(Ice.LogMessageType.PrintMessage.value(), 3);
        }
        else
        {
            __os.writeEnum(__v.value(), 3);
        }
    }

    public static LogMessageType
    __read(IceInternal.BasicStream __is)
    {
        int __v = __is.readEnum(3);
        return __validate(__v);
    }

    private static LogMessageType
    __validate(int __v)
    {
        final LogMessageType __e = valueOf(__v);
        if(__e == null)
        {
            throw new Ice.MarshalException("enumerator value " + __v + " is out of range");
        }
        return __e;
    }

    private final int __value;
}
