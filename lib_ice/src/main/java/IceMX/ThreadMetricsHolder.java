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

public final class ThreadMetricsHolder extends Ice.ObjectHolderBase<ThreadMetrics>
{
    public
    ThreadMetricsHolder()
    {
    }

    public
    ThreadMetricsHolder(ThreadMetrics value)
    {
        this.value = value;
    }

    public void
    patch(Ice.Object v)
    {
        if(v == null || v instanceof ThreadMetrics)
        {
            value = (ThreadMetrics)v;
        }
        else
        {
            IceInternal.Ex.throwUOE(type(), v);
        }
    }

    public String
    type()
    {
        return ThreadMetrics.ice_staticId();
    }
}
