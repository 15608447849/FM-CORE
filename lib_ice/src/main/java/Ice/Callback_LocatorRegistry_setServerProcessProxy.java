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
 * Set the process proxy for a server.
 * 
 **/

public abstract class Callback_LocatorRegistry_setServerProcessProxy
    extends IceInternal.TwowayCallback implements Ice.TwowayCallbackVoidUE
{
    public final void __completed(Ice.AsyncResult __result)
    {
        LocatorRegistryPrxHelper.__setServerProcessProxy_completed(this, __result);
    }
}