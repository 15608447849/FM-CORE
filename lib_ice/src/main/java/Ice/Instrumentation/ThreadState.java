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
// Generated from file `Instrumentation.ice'
//
// Warning: do not edit this file.
//
// </auto-generated>
//

package Ice.Instrumentation;

/**
 * The thread state enumeration keeps track of the different possible
 * states of Ice threads.
 *
 **/
public enum ThreadState
{
    
    /**
     * The thread is idle.
     *
     **/
    ThreadStateIdle(0),
    
    /**
     * The thread is in use performing reads or writes for Ice
     * connections. This state is only for threads from an Ice thread
     * pool.
     *
     **/
    ThreadStateInUseForIO(1),
    
    /**
     * The thread is calling user code (servant implementation, AMI
     * callbacks). This state is only for threads from an Ice thread
     * pool.
     *
     **/
    ThreadStateInUseForUser(2),
    
    /**
     * The thread is performing other internal activities (DNS
     * lookups, timer callbacks, etc).
     *
     **/
    ThreadStateInUseForOther(3);

    public int
    value()
    {
        return __value;
    }

    public static ThreadState
    valueOf(int __v)
    {
        switch(__v)
        {
        case 0:
            return ThreadStateIdle;
        case 1:
            return ThreadStateInUseForIO;
        case 2:
            return ThreadStateInUseForUser;
        case 3:
            return ThreadStateInUseForOther;
        }
        return null;
    }

    private
    ThreadState(int __v)
    {
        __value = __v;
    }

    private final int __value;
}
