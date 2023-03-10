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
 * Provides type-specific helper functions.
 **/
public final class ServiceManagerPrxHelper extends Ice.ObjectPrxHelperBase implements ServiceManagerPrx
{
    private static final String __addObserver_name = "addObserver";

    public void addObserver(ServiceObserverPrx observer)
    {
        addObserver(observer, null, false);
    }

    public void addObserver(ServiceObserverPrx observer, java.util.Map<String, String> __ctx)
    {
        addObserver(observer, __ctx, true);
    }

    private void addObserver(ServiceObserverPrx observer, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        end_addObserver(begin_addObserver(observer, __ctx, __explicitCtx, true, null));
    }

    public Ice.AsyncResult begin_addObserver(ServiceObserverPrx observer)
    {
        return begin_addObserver(observer, null, false, false, null);
    }

    public Ice.AsyncResult begin_addObserver(ServiceObserverPrx observer, java.util.Map<String, String> __ctx)
    {
        return begin_addObserver(observer, __ctx, true, false, null);
    }

    public Ice.AsyncResult begin_addObserver(ServiceObserverPrx observer, Ice.Callback __cb)
    {
        return begin_addObserver(observer, null, false, false, __cb);
    }

    public Ice.AsyncResult begin_addObserver(ServiceObserverPrx observer, java.util.Map<String, String> __ctx, Ice.Callback __cb)
    {
        return begin_addObserver(observer, __ctx, true, false, __cb);
    }

    public Ice.AsyncResult begin_addObserver(ServiceObserverPrx observer, Callback_ServiceManager_addObserver __cb)
    {
        return begin_addObserver(observer, null, false, false, __cb);
    }

    public Ice.AsyncResult begin_addObserver(ServiceObserverPrx observer, java.util.Map<String, String> __ctx, Callback_ServiceManager_addObserver __cb)
    {
        return begin_addObserver(observer, __ctx, true, false, __cb);
    }

    public Ice.AsyncResult begin_addObserver(ServiceObserverPrx observer, 
                                             IceInternal.Functional_VoidCallback __responseCb, 
                                             IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb)
    {
        return begin_addObserver(observer, null, false, false, __responseCb, __exceptionCb, null);
    }

    public Ice.AsyncResult begin_addObserver(ServiceObserverPrx observer, 
                                             IceInternal.Functional_VoidCallback __responseCb, 
                                             IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb, 
                                             IceInternal.Functional_BoolCallback __sentCb)
    {
        return begin_addObserver(observer, null, false, false, __responseCb, __exceptionCb, __sentCb);
    }

    public Ice.AsyncResult begin_addObserver(ServiceObserverPrx observer, 
                                             java.util.Map<String, String> __ctx, 
                                             IceInternal.Functional_VoidCallback __responseCb, 
                                             IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb)
    {
        return begin_addObserver(observer, __ctx, true, false, __responseCb, __exceptionCb, null);
    }

    public Ice.AsyncResult begin_addObserver(ServiceObserverPrx observer, 
                                             java.util.Map<String, String> __ctx, 
                                             IceInternal.Functional_VoidCallback __responseCb, 
                                             IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb, 
                                             IceInternal.Functional_BoolCallback __sentCb)
    {
        return begin_addObserver(observer, __ctx, true, false, __responseCb, __exceptionCb, __sentCb);
    }

    private Ice.AsyncResult begin_addObserver(ServiceObserverPrx observer, 
                                              java.util.Map<String, String> __ctx, 
                                              boolean __explicitCtx, 
                                              boolean __synchronous, 
                                              IceInternal.Functional_VoidCallback __responseCb, 
                                              IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb, 
                                              IceInternal.Functional_BoolCallback __sentCb)
    {
        return begin_addObserver(observer, 
                                 __ctx, 
                                 __explicitCtx, 
                                 __synchronous, 
                                 new IceInternal.Functional_OnewayCallback(__responseCb, __exceptionCb, __sentCb));
    }

    private Ice.AsyncResult begin_addObserver(ServiceObserverPrx observer, 
                                              java.util.Map<String, String> __ctx, 
                                              boolean __explicitCtx, 
                                              boolean __synchronous, 
                                              IceInternal.CallbackBase __cb)
    {
        IceInternal.OutgoingAsync __result = getOutgoingAsync(__addObserver_name, __cb);
        try
        {
            __result.prepare(__addObserver_name, Ice.OperationMode.Normal, __ctx, __explicitCtx, __synchronous);
            IceInternal.BasicStream __os = __result.startWriteParams(Ice.FormatType.DefaultFormat);
            ServiceObserverPrxHelper.__write(__os, observer);
            __result.endWriteParams();
            __result.invoke();
        }
        catch(Ice.Exception __ex)
        {
            __result.abort(__ex);
        }
        return __result;
    }

    public void end_addObserver(Ice.AsyncResult __iresult)
    {
        __end(__iresult, __addObserver_name);
    }

    private static final String __getSliceChecksums_name = "getSliceChecksums";

    public java.util.Map<java.lang.String, java.lang.String> getSliceChecksums()
    {
        return getSliceChecksums(null, false);
    }

    public java.util.Map<java.lang.String, java.lang.String> getSliceChecksums(java.util.Map<String, String> __ctx)
    {
        return getSliceChecksums(__ctx, true);
    }

    private java.util.Map<java.lang.String, java.lang.String> getSliceChecksums(java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        __checkTwowayOnly(__getSliceChecksums_name);
        return end_getSliceChecksums(begin_getSliceChecksums(__ctx, __explicitCtx, true, null));
    }

    public Ice.AsyncResult begin_getSliceChecksums()
    {
        return begin_getSliceChecksums(null, false, false, null);
    }

    public Ice.AsyncResult begin_getSliceChecksums(java.util.Map<String, String> __ctx)
    {
        return begin_getSliceChecksums(__ctx, true, false, null);
    }

    public Ice.AsyncResult begin_getSliceChecksums(Ice.Callback __cb)
    {
        return begin_getSliceChecksums(null, false, false, __cb);
    }

    public Ice.AsyncResult begin_getSliceChecksums(java.util.Map<String, String> __ctx, Ice.Callback __cb)
    {
        return begin_getSliceChecksums(__ctx, true, false, __cb);
    }

    public Ice.AsyncResult begin_getSliceChecksums(Callback_ServiceManager_getSliceChecksums __cb)
    {
        return begin_getSliceChecksums(null, false, false, __cb);
    }

    public Ice.AsyncResult begin_getSliceChecksums(java.util.Map<String, String> __ctx, Callback_ServiceManager_getSliceChecksums __cb)
    {
        return begin_getSliceChecksums(__ctx, true, false, __cb);
    }

    public Ice.AsyncResult begin_getSliceChecksums(IceInternal.Functional_GenericCallback1<java.util.Map<java.lang.String, java.lang.String>> __responseCb, 
                                                   IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb)
    {
        return begin_getSliceChecksums(null, false, false, __responseCb, __exceptionCb, null);
    }

    public Ice.AsyncResult begin_getSliceChecksums(IceInternal.Functional_GenericCallback1<java.util.Map<java.lang.String, java.lang.String>> __responseCb, 
                                                   IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb, 
                                                   IceInternal.Functional_BoolCallback __sentCb)
    {
        return begin_getSliceChecksums(null, false, false, __responseCb, __exceptionCb, __sentCb);
    }

    public Ice.AsyncResult begin_getSliceChecksums(java.util.Map<String, String> __ctx, 
                                                   IceInternal.Functional_GenericCallback1<java.util.Map<java.lang.String, java.lang.String>> __responseCb, 
                                                   IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb)
    {
        return begin_getSliceChecksums(__ctx, true, false, __responseCb, __exceptionCb, null);
    }

    public Ice.AsyncResult begin_getSliceChecksums(java.util.Map<String, String> __ctx, 
                                                   IceInternal.Functional_GenericCallback1<java.util.Map<java.lang.String, java.lang.String>> __responseCb, 
                                                   IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb, 
                                                   IceInternal.Functional_BoolCallback __sentCb)
    {
        return begin_getSliceChecksums(__ctx, true, false, __responseCb, __exceptionCb, __sentCb);
    }

    private Ice.AsyncResult begin_getSliceChecksums(java.util.Map<String, String> __ctx, 
                                                    boolean __explicitCtx, 
                                                    boolean __synchronous, 
                                                    IceInternal.Functional_GenericCallback1<java.util.Map<java.lang.String, java.lang.String>> __responseCb, 
                                                    IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb, 
                                                    IceInternal.Functional_BoolCallback __sentCb)
    {
        return begin_getSliceChecksums(__ctx, __explicitCtx, __synchronous, 
                                       new IceInternal.Functional_TwowayCallbackArg1<java.util.Map<java.lang.String, java.lang.String>>(__responseCb, __exceptionCb, __sentCb)
                                           {
                                               public final void __completed(Ice.AsyncResult __result)
                                               {
                                                   ServiceManagerPrxHelper.__getSliceChecksums_completed(this, __result);
                                               }
                                           });
    }

    private Ice.AsyncResult begin_getSliceChecksums(java.util.Map<String, String> __ctx, 
                                                    boolean __explicitCtx, 
                                                    boolean __synchronous, 
                                                    IceInternal.CallbackBase __cb)
    {
        __checkAsyncTwowayOnly(__getSliceChecksums_name);
        IceInternal.OutgoingAsync __result = getOutgoingAsync(__getSliceChecksums_name, __cb);
        try
        {
            __result.prepare(__getSliceChecksums_name, Ice.OperationMode.Nonmutating, __ctx, __explicitCtx, __synchronous);
            __result.writeEmptyParams();
            __result.invoke();
        }
        catch(Ice.Exception __ex)
        {
            __result.abort(__ex);
        }
        return __result;
    }

    public java.util.Map<java.lang.String, java.lang.String> end_getSliceChecksums(Ice.AsyncResult __iresult)
    {
        IceInternal.OutgoingAsync __result = IceInternal.OutgoingAsync.check(__iresult, this, __getSliceChecksums_name);
        try
        {
            if(!__result.__wait())
            {
                try
                {
                    __result.throwUserException();
                }
                catch(Ice.UserException __ex)
                {
                    throw new Ice.UnknownUserException(__ex.ice_name(), __ex);
                }
            }
            IceInternal.BasicStream __is = __result.startReadParams();
            java.util.Map<java.lang.String, java.lang.String> __ret;
            __ret = Ice.SliceChecksumDictHelper.read(__is);
            __result.endReadParams();
            return __ret;
        }
        finally
        {
            if(__result != null)
            {
                __result.cacheMessageBuffers();
            }
        }
    }

    static public void __getSliceChecksums_completed(Ice.TwowayCallbackArg1<java.util.Map<java.lang.String, java.lang.String>> __cb, Ice.AsyncResult __result)
    {
        IceBox.ServiceManagerPrx __proxy = (IceBox.ServiceManagerPrx)__result.getProxy();
        java.util.Map<java.lang.String, java.lang.String> __ret = null;
        try
        {
            __ret = __proxy.end_getSliceChecksums(__result);
        }
        catch(Ice.LocalException __ex)
        {
            __cb.exception(__ex);
            return;
        }
        catch(Ice.SystemException __ex)
        {
            __cb.exception(__ex);
            return;
        }
        __cb.response(__ret);
    }

    private static final String __shutdown_name = "shutdown";

    public void shutdown()
    {
        shutdown(null, false);
    }

    public void shutdown(java.util.Map<String, String> __ctx)
    {
        shutdown(__ctx, true);
    }

    private void shutdown(java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        end_shutdown(begin_shutdown(__ctx, __explicitCtx, true, null));
    }

    public Ice.AsyncResult begin_shutdown()
    {
        return begin_shutdown(null, false, false, null);
    }

    public Ice.AsyncResult begin_shutdown(java.util.Map<String, String> __ctx)
    {
        return begin_shutdown(__ctx, true, false, null);
    }

    public Ice.AsyncResult begin_shutdown(Ice.Callback __cb)
    {
        return begin_shutdown(null, false, false, __cb);
    }

    public Ice.AsyncResult begin_shutdown(java.util.Map<String, String> __ctx, Ice.Callback __cb)
    {
        return begin_shutdown(__ctx, true, false, __cb);
    }

    public Ice.AsyncResult begin_shutdown(Callback_ServiceManager_shutdown __cb)
    {
        return begin_shutdown(null, false, false, __cb);
    }

    public Ice.AsyncResult begin_shutdown(java.util.Map<String, String> __ctx, Callback_ServiceManager_shutdown __cb)
    {
        return begin_shutdown(__ctx, true, false, __cb);
    }

    public Ice.AsyncResult begin_shutdown(IceInternal.Functional_VoidCallback __responseCb, 
                                          IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb)
    {
        return begin_shutdown(null, false, false, __responseCb, __exceptionCb, null);
    }

    public Ice.AsyncResult begin_shutdown(IceInternal.Functional_VoidCallback __responseCb, 
                                          IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb, 
                                          IceInternal.Functional_BoolCallback __sentCb)
    {
        return begin_shutdown(null, false, false, __responseCb, __exceptionCb, __sentCb);
    }

    public Ice.AsyncResult begin_shutdown(java.util.Map<String, String> __ctx, 
                                          IceInternal.Functional_VoidCallback __responseCb, 
                                          IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb)
    {
        return begin_shutdown(__ctx, true, false, __responseCb, __exceptionCb, null);
    }

    public Ice.AsyncResult begin_shutdown(java.util.Map<String, String> __ctx, 
                                          IceInternal.Functional_VoidCallback __responseCb, 
                                          IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb, 
                                          IceInternal.Functional_BoolCallback __sentCb)
    {
        return begin_shutdown(__ctx, true, false, __responseCb, __exceptionCb, __sentCb);
    }

    private Ice.AsyncResult begin_shutdown(java.util.Map<String, String> __ctx, 
                                           boolean __explicitCtx, 
                                           boolean __synchronous, 
                                           IceInternal.Functional_VoidCallback __responseCb, 
                                           IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb, 
                                           IceInternal.Functional_BoolCallback __sentCb)
    {
        return begin_shutdown(__ctx, 
                              __explicitCtx, 
                              __synchronous, 
                              new IceInternal.Functional_OnewayCallback(__responseCb, __exceptionCb, __sentCb));
    }

    private Ice.AsyncResult begin_shutdown(java.util.Map<String, String> __ctx, 
                                           boolean __explicitCtx, 
                                           boolean __synchronous, 
                                           IceInternal.CallbackBase __cb)
    {
        IceInternal.OutgoingAsync __result = getOutgoingAsync(__shutdown_name, __cb);
        try
        {
            __result.prepare(__shutdown_name, Ice.OperationMode.Normal, __ctx, __explicitCtx, __synchronous);
            __result.writeEmptyParams();
            __result.invoke();
        }
        catch(Ice.Exception __ex)
        {
            __result.abort(__ex);
        }
        return __result;
    }

    public void end_shutdown(Ice.AsyncResult __iresult)
    {
        __end(__iresult, __shutdown_name);
    }

    private static final String __startService_name = "startService";

    public void startService(String service)
        throws AlreadyStartedException,
               NoSuchServiceException
    {
        startService(service, null, false);
    }

    public void startService(String service, java.util.Map<String, String> __ctx)
        throws AlreadyStartedException,
               NoSuchServiceException
    {
        startService(service, __ctx, true);
    }

    private void startService(String service, java.util.Map<String, String> __ctx, boolean __explicitCtx)
        throws AlreadyStartedException,
               NoSuchServiceException
    {
        __checkTwowayOnly(__startService_name);
        end_startService(begin_startService(service, __ctx, __explicitCtx, true, null));
    }

    public Ice.AsyncResult begin_startService(String service)
    {
        return begin_startService(service, null, false, false, null);
    }

    public Ice.AsyncResult begin_startService(String service, java.util.Map<String, String> __ctx)
    {
        return begin_startService(service, __ctx, true, false, null);
    }

    public Ice.AsyncResult begin_startService(String service, Ice.Callback __cb)
    {
        return begin_startService(service, null, false, false, __cb);
    }

    public Ice.AsyncResult begin_startService(String service, java.util.Map<String, String> __ctx, Ice.Callback __cb)
    {
        return begin_startService(service, __ctx, true, false, __cb);
    }

    public Ice.AsyncResult begin_startService(String service, Callback_ServiceManager_startService __cb)
    {
        return begin_startService(service, null, false, false, __cb);
    }

    public Ice.AsyncResult begin_startService(String service, java.util.Map<String, String> __ctx, Callback_ServiceManager_startService __cb)
    {
        return begin_startService(service, __ctx, true, false, __cb);
    }

    public Ice.AsyncResult begin_startService(String service, 
                                              IceInternal.Functional_VoidCallback __responseCb, 
                                              IceInternal.Functional_GenericCallback1<Ice.UserException> __userExceptionCb, 
                                              IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb)
    {
        return begin_startService(service, null, false, false, __responseCb, __userExceptionCb, __exceptionCb, null);
    }

    public Ice.AsyncResult begin_startService(String service, 
                                              IceInternal.Functional_VoidCallback __responseCb, 
                                              IceInternal.Functional_GenericCallback1<Ice.UserException> __userExceptionCb, 
                                              IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb, 
                                              IceInternal.Functional_BoolCallback __sentCb)
    {
        return begin_startService(service, null, false, false, __responseCb, __userExceptionCb, __exceptionCb, __sentCb);
    }

    public Ice.AsyncResult begin_startService(String service, 
                                              java.util.Map<String, String> __ctx, 
                                              IceInternal.Functional_VoidCallback __responseCb, 
                                              IceInternal.Functional_GenericCallback1<Ice.UserException> __userExceptionCb, 
                                              IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb)
    {
        return begin_startService(service, __ctx, true, false, __responseCb, __userExceptionCb, __exceptionCb, null);
    }

    public Ice.AsyncResult begin_startService(String service, 
                                              java.util.Map<String, String> __ctx, 
                                              IceInternal.Functional_VoidCallback __responseCb, 
                                              IceInternal.Functional_GenericCallback1<Ice.UserException> __userExceptionCb, 
                                              IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb, 
                                              IceInternal.Functional_BoolCallback __sentCb)
    {
        return begin_startService(service, __ctx, true, false, __responseCb, __userExceptionCb, __exceptionCb, __sentCb);
    }

    private Ice.AsyncResult begin_startService(String service, 
                                               java.util.Map<String, String> __ctx, 
                                               boolean __explicitCtx, 
                                               boolean __synchronous, 
                                               IceInternal.Functional_VoidCallback __responseCb, 
                                               IceInternal.Functional_GenericCallback1<Ice.UserException> __userExceptionCb, 
                                               IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb, 
                                               IceInternal.Functional_BoolCallback __sentCb)
    {
        return begin_startService(service, __ctx, __explicitCtx, __synchronous, 
                                  new IceInternal.Functional_TwowayCallbackVoidUE(__responseCb, __userExceptionCb, __exceptionCb, __sentCb)
                                      {
                                          public final void __completed(Ice.AsyncResult __result)
                                          {
                                              ServiceManagerPrxHelper.__startService_completed(this, __result);
                                          }
                                      });
    }

    private Ice.AsyncResult begin_startService(String service, 
                                               java.util.Map<String, String> __ctx, 
                                               boolean __explicitCtx, 
                                               boolean __synchronous, 
                                               IceInternal.CallbackBase __cb)
    {
        __checkAsyncTwowayOnly(__startService_name);
        IceInternal.OutgoingAsync __result = getOutgoingAsync(__startService_name, __cb);
        try
        {
            __result.prepare(__startService_name, Ice.OperationMode.Normal, __ctx, __explicitCtx, __synchronous);
            IceInternal.BasicStream __os = __result.startWriteParams(Ice.FormatType.DefaultFormat);
            __os.writeString(service);
            __result.endWriteParams();
            __result.invoke();
        }
        catch(Ice.Exception __ex)
        {
            __result.abort(__ex);
        }
        return __result;
    }

    public void end_startService(Ice.AsyncResult __iresult)
        throws AlreadyStartedException,
               NoSuchServiceException
    {
        IceInternal.OutgoingAsync __result = IceInternal.OutgoingAsync.check(__iresult, this, __startService_name);
        try
        {
            if(!__result.__wait())
            {
                try
                {
                    __result.throwUserException();
                }
                catch(AlreadyStartedException __ex)
                {
                    throw __ex;
                }
                catch(NoSuchServiceException __ex)
                {
                    throw __ex;
                }
                catch(Ice.UserException __ex)
                {
                    throw new Ice.UnknownUserException(__ex.ice_name(), __ex);
                }
            }
            __result.readEmptyParams();
        }
        finally
        {
            if(__result != null)
            {
                __result.cacheMessageBuffers();
            }
        }
    }

    static public void __startService_completed(Ice.TwowayCallbackVoidUE __cb, Ice.AsyncResult __result)
    {
        IceBox.ServiceManagerPrx __proxy = (IceBox.ServiceManagerPrx)__result.getProxy();
        try
        {
            __proxy.end_startService(__result);
        }
        catch(Ice.UserException __ex)
        {
            __cb.exception(__ex);
            return;
        }
        catch(Ice.LocalException __ex)
        {
            __cb.exception(__ex);
            return;
        }
        catch(Ice.SystemException __ex)
        {
            __cb.exception(__ex);
            return;
        }
        __cb.response();
    }

    private static final String __stopService_name = "stopService";

    public void stopService(String service)
        throws AlreadyStoppedException,
               NoSuchServiceException
    {
        stopService(service, null, false);
    }

    public void stopService(String service, java.util.Map<String, String> __ctx)
        throws AlreadyStoppedException,
               NoSuchServiceException
    {
        stopService(service, __ctx, true);
    }

    private void stopService(String service, java.util.Map<String, String> __ctx, boolean __explicitCtx)
        throws AlreadyStoppedException,
               NoSuchServiceException
    {
        __checkTwowayOnly(__stopService_name);
        end_stopService(begin_stopService(service, __ctx, __explicitCtx, true, null));
    }

    public Ice.AsyncResult begin_stopService(String service)
    {
        return begin_stopService(service, null, false, false, null);
    }

    public Ice.AsyncResult begin_stopService(String service, java.util.Map<String, String> __ctx)
    {
        return begin_stopService(service, __ctx, true, false, null);
    }

    public Ice.AsyncResult begin_stopService(String service, Ice.Callback __cb)
    {
        return begin_stopService(service, null, false, false, __cb);
    }

    public Ice.AsyncResult begin_stopService(String service, java.util.Map<String, String> __ctx, Ice.Callback __cb)
    {
        return begin_stopService(service, __ctx, true, false, __cb);
    }

    public Ice.AsyncResult begin_stopService(String service, Callback_ServiceManager_stopService __cb)
    {
        return begin_stopService(service, null, false, false, __cb);
    }

    public Ice.AsyncResult begin_stopService(String service, java.util.Map<String, String> __ctx, Callback_ServiceManager_stopService __cb)
    {
        return begin_stopService(service, __ctx, true, false, __cb);
    }

    public Ice.AsyncResult begin_stopService(String service, 
                                             IceInternal.Functional_VoidCallback __responseCb, 
                                             IceInternal.Functional_GenericCallback1<Ice.UserException> __userExceptionCb, 
                                             IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb)
    {
        return begin_stopService(service, null, false, false, __responseCb, __userExceptionCb, __exceptionCb, null);
    }

    public Ice.AsyncResult begin_stopService(String service, 
                                             IceInternal.Functional_VoidCallback __responseCb, 
                                             IceInternal.Functional_GenericCallback1<Ice.UserException> __userExceptionCb, 
                                             IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb, 
                                             IceInternal.Functional_BoolCallback __sentCb)
    {
        return begin_stopService(service, null, false, false, __responseCb, __userExceptionCb, __exceptionCb, __sentCb);
    }

    public Ice.AsyncResult begin_stopService(String service, 
                                             java.util.Map<String, String> __ctx, 
                                             IceInternal.Functional_VoidCallback __responseCb, 
                                             IceInternal.Functional_GenericCallback1<Ice.UserException> __userExceptionCb, 
                                             IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb)
    {
        return begin_stopService(service, __ctx, true, false, __responseCb, __userExceptionCb, __exceptionCb, null);
    }

    public Ice.AsyncResult begin_stopService(String service, 
                                             java.util.Map<String, String> __ctx, 
                                             IceInternal.Functional_VoidCallback __responseCb, 
                                             IceInternal.Functional_GenericCallback1<Ice.UserException> __userExceptionCb, 
                                             IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb, 
                                             IceInternal.Functional_BoolCallback __sentCb)
    {
        return begin_stopService(service, __ctx, true, false, __responseCb, __userExceptionCb, __exceptionCb, __sentCb);
    }

    private Ice.AsyncResult begin_stopService(String service, 
                                              java.util.Map<String, String> __ctx, 
                                              boolean __explicitCtx, 
                                              boolean __synchronous, 
                                              IceInternal.Functional_VoidCallback __responseCb, 
                                              IceInternal.Functional_GenericCallback1<Ice.UserException> __userExceptionCb, 
                                              IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb, 
                                              IceInternal.Functional_BoolCallback __sentCb)
    {
        return begin_stopService(service, __ctx, __explicitCtx, __synchronous, 
                                 new IceInternal.Functional_TwowayCallbackVoidUE(__responseCb, __userExceptionCb, __exceptionCb, __sentCb)
                                     {
                                         public final void __completed(Ice.AsyncResult __result)
                                         {
                                             ServiceManagerPrxHelper.__stopService_completed(this, __result);
                                         }
                                     });
    }

    private Ice.AsyncResult begin_stopService(String service, 
                                              java.util.Map<String, String> __ctx, 
                                              boolean __explicitCtx, 
                                              boolean __synchronous, 
                                              IceInternal.CallbackBase __cb)
    {
        __checkAsyncTwowayOnly(__stopService_name);
        IceInternal.OutgoingAsync __result = getOutgoingAsync(__stopService_name, __cb);
        try
        {
            __result.prepare(__stopService_name, Ice.OperationMode.Normal, __ctx, __explicitCtx, __synchronous);
            IceInternal.BasicStream __os = __result.startWriteParams(Ice.FormatType.DefaultFormat);
            __os.writeString(service);
            __result.endWriteParams();
            __result.invoke();
        }
        catch(Ice.Exception __ex)
        {
            __result.abort(__ex);
        }
        return __result;
    }

    public void end_stopService(Ice.AsyncResult __iresult)
        throws AlreadyStoppedException,
               NoSuchServiceException
    {
        IceInternal.OutgoingAsync __result = IceInternal.OutgoingAsync.check(__iresult, this, __stopService_name);
        try
        {
            if(!__result.__wait())
            {
                try
                {
                    __result.throwUserException();
                }
                catch(AlreadyStoppedException __ex)
                {
                    throw __ex;
                }
                catch(NoSuchServiceException __ex)
                {
                    throw __ex;
                }
                catch(Ice.UserException __ex)
                {
                    throw new Ice.UnknownUserException(__ex.ice_name(), __ex);
                }
            }
            __result.readEmptyParams();
        }
        finally
        {
            if(__result != null)
            {
                __result.cacheMessageBuffers();
            }
        }
    }

    static public void __stopService_completed(Ice.TwowayCallbackVoidUE __cb, Ice.AsyncResult __result)
    {
        IceBox.ServiceManagerPrx __proxy = (IceBox.ServiceManagerPrx)__result.getProxy();
        try
        {
            __proxy.end_stopService(__result);
        }
        catch(Ice.UserException __ex)
        {
            __cb.exception(__ex);
            return;
        }
        catch(Ice.LocalException __ex)
        {
            __cb.exception(__ex);
            return;
        }
        catch(Ice.SystemException __ex)
        {
            __cb.exception(__ex);
            return;
        }
        __cb.response();
    }

    /**
     * Contacts the remote server to verify that the object implements this type.
     * Raises a local exception if a communication error occurs.
     * @param __obj The untyped proxy.
     * @return A proxy for this type, or null if the object does not support this type.
     **/
    public static ServiceManagerPrx checkedCast(Ice.ObjectPrx __obj)
    {
        return checkedCastImpl(__obj, ice_staticId(), ServiceManagerPrx.class, ServiceManagerPrxHelper.class);
    }

    /**
     * Contacts the remote server to verify that the object implements this type.
     * Raises a local exception if a communication error occurs.
     * @param __obj The untyped proxy.
     * @param __ctx The Context map to send with the invocation.
     * @return A proxy for this type, or null if the object does not support this type.
     **/
    public static ServiceManagerPrx checkedCast(Ice.ObjectPrx __obj, java.util.Map<String, String> __ctx)
    {
        return checkedCastImpl(__obj, __ctx, ice_staticId(), ServiceManagerPrx.class, ServiceManagerPrxHelper.class);
    }

    /**
     * Contacts the remote server to verify that a facet of the object implements this type.
     * Raises a local exception if a communication error occurs.
     * @param __obj The untyped proxy.
     * @param __facet The name of the desired facet.
     * @return A proxy for this type, or null if the object does not support this type.
     **/
    public static ServiceManagerPrx checkedCast(Ice.ObjectPrx __obj, String __facet)
    {
        return checkedCastImpl(__obj, __facet, ice_staticId(), ServiceManagerPrx.class, ServiceManagerPrxHelper.class);
    }

    /**
     * Contacts the remote server to verify that a facet of the object implements this type.
     * Raises a local exception if a communication error occurs.
     * @param __obj The untyped proxy.
     * @param __facet The name of the desired facet.
     * @param __ctx The Context map to send with the invocation.
     * @return A proxy for this type, or null if the object does not support this type.
     **/
    public static ServiceManagerPrx checkedCast(Ice.ObjectPrx __obj, String __facet, java.util.Map<String, String> __ctx)
    {
        return checkedCastImpl(__obj, __facet, __ctx, ice_staticId(), ServiceManagerPrx.class, ServiceManagerPrxHelper.class);
    }

    /**
     * Downcasts the given proxy to this type without contacting the remote server.
     * @param __obj The untyped proxy.
     * @return A proxy for this type.
     **/
    public static ServiceManagerPrx uncheckedCast(Ice.ObjectPrx __obj)
    {
        return uncheckedCastImpl(__obj, ServiceManagerPrx.class, ServiceManagerPrxHelper.class);
    }

    /**
     * Downcasts the given proxy to this type without contacting the remote server.
     * @param __obj The untyped proxy.
     * @param __facet The name of the desired facet.
     * @return A proxy for this type.
     **/
    public static ServiceManagerPrx uncheckedCast(Ice.ObjectPrx __obj, String __facet)
    {
        return uncheckedCastImpl(__obj, __facet, ServiceManagerPrx.class, ServiceManagerPrxHelper.class);
    }

    public static final String[] __ids =
    {
        "::Ice::Object",
        "::IceBox::ServiceManager"
    };

    /**
     * Provides the Slice type ID of this type.
     * @return The Slice type ID.
     **/
    public static String ice_staticId()
    {
        return __ids[1];
    }

    public static void __write(IceInternal.BasicStream __os, ServiceManagerPrx v)
    {
        __os.writeProxy(v);
    }

    public static ServiceManagerPrx __read(IceInternal.BasicStream __is)
    {
        Ice.ObjectPrx proxy = __is.readProxy();
        if(proxy != null)
        {
            ServiceManagerPrxHelper result = new ServiceManagerPrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }

    public static final long serialVersionUID = 0L;
}
