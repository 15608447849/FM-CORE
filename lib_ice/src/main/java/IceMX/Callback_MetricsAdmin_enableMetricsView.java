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

/**
 * Enables a metrics view.
 * 
 **/

public abstract class Callback_MetricsAdmin_enableMetricsView
    extends IceInternal.TwowayCallback implements Ice.TwowayCallbackVoidUE
{
    public final void __completed(Ice.AsyncResult __result)
    {
        MetricsAdminPrxHelper.__enableMetricsView_completed(this, __result);
    }
}
