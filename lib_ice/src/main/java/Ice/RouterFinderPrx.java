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
// Generated from file `Router.ice'
//
// Warning: do not edit this file.
//
// </auto-generated>
//

package Ice;

/**
 * This inferface should be implemented by services implementing the
 * Ice::Router interface. It should be advertised through an Ice
 * object with the identity `Ice/RouterFinder'. This allows clients to
 * retrieve the router proxy with just the endpoint information of the
 * service.
 *
 **/
public interface RouterFinderPrx extends Ice.ObjectPrx
{
    /**
     * Get the router proxy implemented by the process hosting this
     * finder object. The proxy might point to several replicas.
     *
     * @return The router proxy.
     *
     **/
    public RouterPrx getRouter();

    /**
     * Get the router proxy implemented by the process hosting this
     * finder object. The proxy might point to several replicas.
     *
     * @param __ctx The Context map to send with the invocation.
     * @return The router proxy.
     *
     **/
    public RouterPrx getRouter(java.util.Map<String, String> __ctx);

    /**
     * Get the router proxy implemented by the process hosting this
     * finder object. The proxy might point to several replicas.
     * 
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_getRouter();

    /**
     * Get the router proxy implemented by the process hosting this
     * finder object. The proxy might point to several replicas.
     * 
     * @param __ctx The Context map to send with the invocation.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_getRouter(java.util.Map<String, String> __ctx);

    /**
     * Get the router proxy implemented by the process hosting this
     * finder object. The proxy might point to several replicas.
     * 
     * @param __cb The asynchronous callback object.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_getRouter(Ice.Callback __cb);

    /**
     * Get the router proxy implemented by the process hosting this
     * finder object. The proxy might point to several replicas.
     * 
     * @param __ctx The Context map to send with the invocation.
     * @param __cb The asynchronous callback object.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_getRouter(java.util.Map<String, String> __ctx, Ice.Callback __cb);

    /**
     * Get the router proxy implemented by the process hosting this
     * finder object. The proxy might point to several replicas.
     * 
     * @param __cb The asynchronous callback object.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_getRouter(Callback_RouterFinder_getRouter __cb);

    /**
     * Get the router proxy implemented by the process hosting this
     * finder object. The proxy might point to several replicas.
     * 
     * @param __ctx The Context map to send with the invocation.
     * @param __cb The asynchronous callback object.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_getRouter(java.util.Map<String, String> __ctx, Callback_RouterFinder_getRouter __cb);

    /**
     * Get the router proxy implemented by the process hosting this
     * finder object. The proxy might point to several replicas.
     * 
     * @param __responseCb The lambda response callback.
     * @param __exceptionCb The lambda exception callback.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_getRouter(IceInternal.Functional_GenericCallback1<RouterPrx> __responseCb, 
                                           IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb);

    /**
     * Get the router proxy implemented by the process hosting this
     * finder object. The proxy might point to several replicas.
     * 
     * @param __responseCb The lambda response callback.
     * @param __exceptionCb The lambda exception callback.
     * @param __sentCb The lambda sent callback.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_getRouter(IceInternal.Functional_GenericCallback1<RouterPrx> __responseCb, 
                                           IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb, 
                                           IceInternal.Functional_BoolCallback __sentCb);

    /**
     * Get the router proxy implemented by the process hosting this
     * finder object. The proxy might point to several replicas.
     * 
     * @param __ctx The Context map to send with the invocation.
     * @param __responseCb The lambda response callback.
     * @param __exceptionCb The lambda exception callback.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_getRouter(java.util.Map<String, String> __ctx, 
                                           IceInternal.Functional_GenericCallback1<RouterPrx> __responseCb, 
                                           IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb);

    /**
     * Get the router proxy implemented by the process hosting this
     * finder object. The proxy might point to several replicas.
     * 
     * @param __ctx The Context map to send with the invocation.
     * @param __responseCb The lambda response callback.
     * @param __exceptionCb The lambda exception callback.
     * @param __sentCb The lambda sent callback.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_getRouter(java.util.Map<String, String> __ctx, 
                                           IceInternal.Functional_GenericCallback1<RouterPrx> __responseCb, 
                                           IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb, 
                                           IceInternal.Functional_BoolCallback __sentCb);

    /**
     * Get the router proxy implemented by the process hosting this
     * finder object. The proxy might point to several replicas.
     * 
     * @param __result The asynchronous result object.
     * @return The router proxy.
     * 
     **/
    public RouterPrx end_getRouter(Ice.AsyncResult __result);
}
