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
// Generated from file `Metrics.ice'
//
// Warning: do not edit this file.
//
// </auto-generated>
//

package IceMX;

public final class MetricsFailuresSeqHelper
{
    public static void
    write(IceInternal.BasicStream __os, MetricsFailures[] __v)
    {
        if(__v == null)
        {
            __os.writeSize(0);
        }
        else
        {
            __os.writeSize(__v.length);
            for(int __i0 = 0; __i0 < __v.length; __i0++)
            {
                MetricsFailures.__write(__os, __v[__i0]);
            }
        }
    }

    public static MetricsFailures[]
    read(IceInternal.BasicStream __is)
    {
        MetricsFailures[] __v;
        final int __len0 = __is.readAndCheckSeqSize(2);
        __v = new MetricsFailures[__len0];
        for(int __i0 = 0; __i0 < __len0; __i0++)
        {
            __v[__i0] = MetricsFailures.__read(__is, __v[__i0]);
        }
        return __v;
    }
}