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
 * detachRemoteLogger is called to detach a {@link RemoteLogger} object from
 * the local {@link Logger}.
 * 
 **/

public abstract class Callback_LoggerAdmin_detachRemoteLogger
    extends IceInternal.TwowayCallback implements Ice.TwowayCallbackBool
{
    public final void __completed(Ice.AsyncResult __result)
    {
        LoggerAdminPrxHelper.__detachRemoteLogger_completed(this, __result);
    }
}
