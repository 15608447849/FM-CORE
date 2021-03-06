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
 * Provides type-specific helper functions.
 **/
public final class RemoteLoggerPrxHelper extends Ice.ObjectPrxHelperBase implements RemoteLoggerPrx
{
    private static final String __init_name = "init";

    public void init(String prefix, LogMessage[] logMessages)
    {
        init(prefix, logMessages, null, false);
    }

    public void init(String prefix, LogMessage[] logMessages, java.util.Map<String, String> __ctx)
    {
        init(prefix, logMessages, __ctx, true);
    }

    private void init(String prefix, LogMessage[] logMessages, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        end_init(begin_init(prefix, logMessages, __ctx, __explicitCtx, true, null));
    }

    public Ice.AsyncResult begin_init(String prefix, LogMessage[] logMessages)
    {
        return begin_init(prefix, logMessages, null, false, false, null);
    }

    public Ice.AsyncResult begin_init(String prefix, LogMessage[] logMessages, java.util.Map<String, String> __ctx)
    {
        return begin_init(prefix, logMessages, __ctx, true, false, null);
    }

    public Ice.AsyncResult begin_init(String prefix, LogMessage[] logMessages, Ice.Callback __cb)
    {
        return begin_init(prefix, logMessages, null, false, false, __cb);
    }

    public Ice.AsyncResult begin_init(String prefix, LogMessage[] logMessages, java.util.Map<String, String> __ctx, Ice.Callback __cb)
    {
        return begin_init(prefix, logMessages, __ctx, true, false, __cb);
    }

    public Ice.AsyncResult begin_init(String prefix, LogMessage[] logMessages, Callback_RemoteLogger_init __cb)
    {
        return begin_init(prefix, logMessages, null, false, false, __cb);
    }

    public Ice.AsyncResult begin_init(String prefix, LogMessage[] logMessages, java.util.Map<String, String> __ctx, Callback_RemoteLogger_init __cb)
    {
        return begin_init(prefix, logMessages, __ctx, true, false, __cb);
    }

    public Ice.AsyncResult begin_init(String prefix, 
                                      LogMessage[] logMessages, 
                                      IceInternal.Functional_VoidCallback __responseCb, 
                                      IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb)
    {
        return begin_init(prefix, logMessages, null, false, false, __responseCb, __exceptionCb, null);
    }

    public Ice.AsyncResult begin_init(String prefix, 
                                      LogMessage[] logMessages, 
                                      IceInternal.Functional_VoidCallback __responseCb, 
                                      IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb, 
                                      IceInternal.Functional_BoolCallback __sentCb)
    {
        return begin_init(prefix, logMessages, null, false, false, __responseCb, __exceptionCb, __sentCb);
    }

    public Ice.AsyncResult begin_init(String prefix, 
                                      LogMessage[] logMessages, 
                                      java.util.Map<String, String> __ctx, 
                                      IceInternal.Functional_VoidCallback __responseCb, 
                                      IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb)
    {
        return begin_init(prefix, logMessages, __ctx, true, false, __responseCb, __exceptionCb, null);
    }

    public Ice.AsyncResult begin_init(String prefix, 
                                      LogMessage[] logMessages, 
                                      java.util.Map<String, String> __ctx, 
                                      IceInternal.Functional_VoidCallback __responseCb, 
                                      IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb, 
                                      IceInternal.Functional_BoolCallback __sentCb)
    {
        return begin_init(prefix, logMessages, __ctx, true, false, __responseCb, __exceptionCb, __sentCb);
    }

    private Ice.AsyncResult begin_init(String prefix, 
                                       LogMessage[] logMessages, 
                                       java.util.Map<String, String> __ctx, 
                                       boolean __explicitCtx, 
                                       boolean __synchronous, 
                                       IceInternal.Functional_VoidCallback __responseCb, 
                                       IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb, 
                                       IceInternal.Functional_BoolCallback __sentCb)
    {
        return begin_init(prefix, 
                          logMessages, 
                          __ctx, 
                          __explicitCtx, 
                          __synchronous, 
                          new IceInternal.Functional_OnewayCallback(__responseCb, __exceptionCb, __sentCb));
    }

    private Ice.AsyncResult begin_init(String prefix, 
                                       LogMessage[] logMessages, 
                                       java.util.Map<String, String> __ctx, 
                                       boolean __explicitCtx, 
                                       boolean __synchronous, 
                                       IceInternal.CallbackBase __cb)
    {
        IceInternal.OutgoingAsync __result = getOutgoingAsync(__init_name, __cb);
        try
        {
            __result.prepare(__init_name, Ice.OperationMode.Normal, __ctx, __explicitCtx, __synchronous);
            IceInternal.BasicStream __os = __result.startWriteParams(Ice.FormatType.DefaultFormat);
            __os.writeString(prefix);
            LogMessageSeqHelper.write(__os, logMessages);
            __result.endWriteParams();
            __result.invoke();
        }
        catch(Ice.Exception __ex)
        {
            __result.abort(__ex);
        }
        return __result;
    }

    public void end_init(Ice.AsyncResult __iresult)
    {
        __end(__iresult, __init_name);
    }

    private static final String __log_name = "log";

    public void log(LogMessage message)
    {
        log(message, null, false);
    }

    public void log(LogMessage message, java.util.Map<String, String> __ctx)
    {
        log(message, __ctx, true);
    }

    private void log(LogMessage message, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        end_log(begin_log(message, __ctx, __explicitCtx, true, null));
    }

    public Ice.AsyncResult begin_log(LogMessage message)
    {
        return begin_log(message, null, false, false, null);
    }

    public Ice.AsyncResult begin_log(LogMessage message, java.util.Map<String, String> __ctx)
    {
        return begin_log(message, __ctx, true, false, null);
    }

    public Ice.AsyncResult begin_log(LogMessage message, Ice.Callback __cb)
    {
        return begin_log(message, null, false, false, __cb);
    }

    public Ice.AsyncResult begin_log(LogMessage message, java.util.Map<String, String> __ctx, Ice.Callback __cb)
    {
        return begin_log(message, __ctx, true, false, __cb);
    }

    public Ice.AsyncResult begin_log(LogMessage message, Callback_RemoteLogger_log __cb)
    {
        return begin_log(message, null, false, false, __cb);
    }

    public Ice.AsyncResult begin_log(LogMessage message, java.util.Map<String, String> __ctx, Callback_RemoteLogger_log __cb)
    {
        return begin_log(message, __ctx, true, false, __cb);
    }

    public Ice.AsyncResult begin_log(LogMessage message, 
                                     IceInternal.Functional_VoidCallback __responseCb, 
                                     IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb)
    {
        return begin_log(message, null, false, false, __responseCb, __exceptionCb, null);
    }

    public Ice.AsyncResult begin_log(LogMessage message, 
                                     IceInternal.Functional_VoidCallback __responseCb, 
                                     IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb, 
                                     IceInternal.Functional_BoolCallback __sentCb)
    {
        return begin_log(message, null, false, false, __responseCb, __exceptionCb, __sentCb);
    }

    public Ice.AsyncResult begin_log(LogMessage message, 
                                     java.util.Map<String, String> __ctx, 
                                     IceInternal.Functional_VoidCallback __responseCb, 
                                     IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb)
    {
        return begin_log(message, __ctx, true, false, __responseCb, __exceptionCb, null);
    }

    public Ice.AsyncResult begin_log(LogMessage message, 
                                     java.util.Map<String, String> __ctx, 
                                     IceInternal.Functional_VoidCallback __responseCb, 
                                     IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb, 
                                     IceInternal.Functional_BoolCallback __sentCb)
    {
        return begin_log(message, __ctx, true, false, __responseCb, __exceptionCb, __sentCb);
    }

    private Ice.AsyncResult begin_log(LogMessage message, 
                                      java.util.Map<String, String> __ctx, 
                                      boolean __explicitCtx, 
                                      boolean __synchronous, 
                                      IceInternal.Functional_VoidCallback __responseCb, 
                                      IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb, 
                                      IceInternal.Functional_BoolCallback __sentCb)
    {
        return begin_log(message, 
                         __ctx, 
                         __explicitCtx, 
                         __synchronous, 
                         new IceInternal.Functional_OnewayCallback(__responseCb, __exceptionCb, __sentCb));
    }

    private Ice.AsyncResult begin_log(LogMessage message, 
                                      java.util.Map<String, String> __ctx, 
                                      boolean __explicitCtx, 
                                      boolean __synchronous, 
                                      IceInternal.CallbackBase __cb)
    {
        IceInternal.OutgoingAsync __result = getOutgoingAsync(__log_name, __cb);
        try
        {
            __result.prepare(__log_name, Ice.OperationMode.Normal, __ctx, __explicitCtx, __synchronous);
            IceInternal.BasicStream __os = __result.startWriteParams(Ice.FormatType.DefaultFormat);
            LogMessage.__write(__os, message);
            __result.endWriteParams();
            __result.invoke();
        }
        catch(Ice.Exception __ex)
        {
            __result.abort(__ex);
        }
        return __result;
    }

    public void end_log(Ice.AsyncResult __iresult)
    {
        __end(__iresult, __log_name);
    }

    /**
     * Contacts the remote server to verify that the object implements this type.
     * Raises a local exception if a communication error occurs.
     * @param __obj The untyped proxy.
     * @return A proxy for this type, or null if the object does not support this type.
     **/
    public static RemoteLoggerPrx checkedCast(Ice.ObjectPrx __obj)
    {
        return checkedCastImpl(__obj, ice_staticId(), RemoteLoggerPrx.class, RemoteLoggerPrxHelper.class);
    }

    /**
     * Contacts the remote server to verify that the object implements this type.
     * Raises a local exception if a communication error occurs.
     * @param __obj The untyped proxy.
     * @param __ctx The Context map to send with the invocation.
     * @return A proxy for this type, or null if the object does not support this type.
     **/
    public static RemoteLoggerPrx checkedCast(Ice.ObjectPrx __obj, java.util.Map<String, String> __ctx)
    {
        return checkedCastImpl(__obj, __ctx, ice_staticId(), RemoteLoggerPrx.class, RemoteLoggerPrxHelper.class);
    }

    /**
     * Contacts the remote server to verify that a facet of the object implements this type.
     * Raises a local exception if a communication error occurs.
     * @param __obj The untyped proxy.
     * @param __facet The name of the desired facet.
     * @return A proxy for this type, or null if the object does not support this type.
     **/
    public static RemoteLoggerPrx checkedCast(Ice.ObjectPrx __obj, String __facet)
    {
        return checkedCastImpl(__obj, __facet, ice_staticId(), RemoteLoggerPrx.class, RemoteLoggerPrxHelper.class);
    }

    /**
     * Contacts the remote server to verify that a facet of the object implements this type.
     * Raises a local exception if a communication error occurs.
     * @param __obj The untyped proxy.
     * @param __facet The name of the desired facet.
     * @param __ctx The Context map to send with the invocation.
     * @return A proxy for this type, or null if the object does not support this type.
     **/
    public static RemoteLoggerPrx checkedCast(Ice.ObjectPrx __obj, String __facet, java.util.Map<String, String> __ctx)
    {
        return checkedCastImpl(__obj, __facet, __ctx, ice_staticId(), RemoteLoggerPrx.class, RemoteLoggerPrxHelper.class);
    }

    /**
     * Downcasts the given proxy to this type without contacting the remote server.
     * @param __obj The untyped proxy.
     * @return A proxy for this type.
     **/
    public static RemoteLoggerPrx uncheckedCast(Ice.ObjectPrx __obj)
    {
        return uncheckedCastImpl(__obj, RemoteLoggerPrx.class, RemoteLoggerPrxHelper.class);
    }

    /**
     * Downcasts the given proxy to this type without contacting the remote server.
     * @param __obj The untyped proxy.
     * @param __facet The name of the desired facet.
     * @return A proxy for this type.
     **/
    public static RemoteLoggerPrx uncheckedCast(Ice.ObjectPrx __obj, String __facet)
    {
        return uncheckedCastImpl(__obj, __facet, RemoteLoggerPrx.class, RemoteLoggerPrxHelper.class);
    }

    public static final String[] __ids =
    {
        "::Ice::Object",
        "::Ice::RemoteLogger"
    };

    /**
     * Provides the Slice type ID of this type.
     * @return The Slice type ID.
     **/
    public static String ice_staticId()
    {
        return __ids[1];
    }

    public static void __write(IceInternal.BasicStream __os, RemoteLoggerPrx v)
    {
        __os.writeProxy(v);
    }

    public static RemoteLoggerPrx __read(IceInternal.BasicStream __is)
    {
        Ice.ObjectPrx proxy = __is.readProxy();
        if(proxy != null)
        {
            RemoteLoggerPrxHelper result = new RemoteLoggerPrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }

    public static final long serialVersionUID = 0L;
}
