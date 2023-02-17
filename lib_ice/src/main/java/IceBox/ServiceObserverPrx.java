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
 * An Observer interface implemented by admin clients
 * interested in the status of services
 *
 * @see ServiceManager
 *
 **/
public interface ServiceObserverPrx extends Ice.ObjectPrx
{
    public void servicesStarted(String[] services);

    public void servicesStarted(String[] services, java.util.Map<String, String> __ctx);

    public Ice.AsyncResult begin_servicesStarted(String[] services);

    public Ice.AsyncResult begin_servicesStarted(String[] services, java.util.Map<String, String> __ctx);

    public Ice.AsyncResult begin_servicesStarted(String[] services, Ice.Callback __cb);

    public Ice.AsyncResult begin_servicesStarted(String[] services, java.util.Map<String, String> __ctx, Ice.Callback __cb);

    public Ice.AsyncResult begin_servicesStarted(String[] services, Callback_ServiceObserver_servicesStarted __cb);

    public Ice.AsyncResult begin_servicesStarted(String[] services, java.util.Map<String, String> __ctx, Callback_ServiceObserver_servicesStarted __cb);

    public Ice.AsyncResult begin_servicesStarted(String[] services, 
                                                 IceInternal.Functional_VoidCallback __responseCb, 
                                                 IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb);

    public Ice.AsyncResult begin_servicesStarted(String[] services, 
                                                 IceInternal.Functional_VoidCallback __responseCb, 
                                                 IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb, 
                                                 IceInternal.Functional_BoolCallback __sentCb);

    public Ice.AsyncResult begin_servicesStarted(String[] services, 
                                                 java.util.Map<String, String> __ctx, 
                                                 IceInternal.Functional_VoidCallback __responseCb, 
                                                 IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb);

    public Ice.AsyncResult begin_servicesStarted(String[] services, 
                                                 java.util.Map<String, String> __ctx, 
                                                 IceInternal.Functional_VoidCallback __responseCb, 
                                                 IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb, 
                                                 IceInternal.Functional_BoolCallback __sentCb);

    public void end_servicesStarted(Ice.AsyncResult __result);

    public void servicesStopped(String[] services);

    public void servicesStopped(String[] services, java.util.Map<String, String> __ctx);

    public Ice.AsyncResult begin_servicesStopped(String[] services);

    public Ice.AsyncResult begin_servicesStopped(String[] services, java.util.Map<String, String> __ctx);

    public Ice.AsyncResult begin_servicesStopped(String[] services, Ice.Callback __cb);

    public Ice.AsyncResult begin_servicesStopped(String[] services, java.util.Map<String, String> __ctx, Ice.Callback __cb);

    public Ice.AsyncResult begin_servicesStopped(String[] services, Callback_ServiceObserver_servicesStopped __cb);

    public Ice.AsyncResult begin_servicesStopped(String[] services, java.util.Map<String, String> __ctx, Callback_ServiceObserver_servicesStopped __cb);

    public Ice.AsyncResult begin_servicesStopped(String[] services, 
                                                 IceInternal.Functional_VoidCallback __responseCb, 
                                                 IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb);

    public Ice.AsyncResult begin_servicesStopped(String[] services, 
                                                 IceInternal.Functional_VoidCallback __responseCb, 
                                                 IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb, 
                                                 IceInternal.Functional_BoolCallback __sentCb);

    public Ice.AsyncResult begin_servicesStopped(String[] services, 
                                                 java.util.Map<String, String> __ctx, 
                                                 IceInternal.Functional_VoidCallback __responseCb, 
                                                 IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb);

    public Ice.AsyncResult begin_servicesStopped(String[] services, 
                                                 java.util.Map<String, String> __ctx, 
                                                 IceInternal.Functional_VoidCallback __responseCb, 
                                                 IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb, 
                                                 IceInternal.Functional_BoolCallback __sentCb);

    public void end_servicesStopped(Ice.AsyncResult __result);
}