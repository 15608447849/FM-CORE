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
 * The Ice locator registry interface. This interface is used by
 * servers to register adapter endpoints with the locator.
 *
 * <p class="Note"> The {@link LocatorRegistry} interface is intended to be used
 * by Ice internals and by locator implementations. Regular user
 * code should not attempt to use any functionality of this interface
 * directly.
 *
 **/
public interface LocatorRegistryPrx extends Ice.ObjectPrx
{
    /**
     * Set the adapter endpoints with the locator registry.
     *
     * @param id The adapter id.
     *
     * @param proxy The adapter proxy (a dummy direct proxy created
     * by the adapter). The direct proxy contains the adapter
     * endpoints.
     *
     * @throws AdapterNotFoundException Raised if the adapter cannot
     * be found, or if the locator only allows
     * registered adapters to set their active proxy and the
     * adapter is not registered with the locator.
     *
     * @throws AdapterAlreadyActiveException Raised if an adapter with the same
     * id is already active.
     *
     **/
    public void setAdapterDirectProxy(String id, Ice.ObjectPrx proxy)
        throws AdapterAlreadyActiveException,
               AdapterNotFoundException;

    /**
     * Set the adapter endpoints with the locator registry.
     *
     * @param id The adapter id.
     *
     * @param proxy The adapter proxy (a dummy direct proxy created
     * by the adapter). The direct proxy contains the adapter
     * endpoints.
     *
     * @throws AdapterNotFoundException Raised if the adapter cannot
     * be found, or if the locator only allows
     * registered adapters to set their active proxy and the
     * adapter is not registered with the locator.
     *
     * @throws AdapterAlreadyActiveException Raised if an adapter with the same
     * id is already active.
     *
     * @param __ctx The Context map to send with the invocation.
     **/
    public void setAdapterDirectProxy(String id, Ice.ObjectPrx proxy, java.util.Map<String, String> __ctx)
        throws AdapterAlreadyActiveException,
               AdapterNotFoundException;

    /**
     * Set the adapter endpoints with the locator registry.
     * 
     * @param id The adapter id.
     * 
     * @param proxy The adapter proxy (a dummy direct proxy created
     * by the adapter). The direct proxy contains the adapter
     * endpoints.
     * 
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_setAdapterDirectProxy(String id, Ice.ObjectPrx proxy);

    /**
     * Set the adapter endpoints with the locator registry.
     * 
     * @param id The adapter id.
     * 
     * @param proxy The adapter proxy (a dummy direct proxy created
     * by the adapter). The direct proxy contains the adapter
     * endpoints.
     * 
     * @param __ctx The Context map to send with the invocation.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_setAdapterDirectProxy(String id, Ice.ObjectPrx proxy, java.util.Map<String, String> __ctx);

    /**
     * Set the adapter endpoints with the locator registry.
     * 
     * @param id The adapter id.
     * 
     * @param proxy The adapter proxy (a dummy direct proxy created
     * by the adapter). The direct proxy contains the adapter
     * endpoints.
     * 
     * @param __cb The asynchronous callback object.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_setAdapterDirectProxy(String id, Ice.ObjectPrx proxy, Ice.Callback __cb);

    /**
     * Set the adapter endpoints with the locator registry.
     * 
     * @param id The adapter id.
     * 
     * @param proxy The adapter proxy (a dummy direct proxy created
     * by the adapter). The direct proxy contains the adapter
     * endpoints.
     * 
     * @param __ctx The Context map to send with the invocation.
     * @param __cb The asynchronous callback object.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_setAdapterDirectProxy(String id, Ice.ObjectPrx proxy, java.util.Map<String, String> __ctx, Ice.Callback __cb);

    /**
     * Set the adapter endpoints with the locator registry.
     * 
     * @param id The adapter id.
     * 
     * @param proxy The adapter proxy (a dummy direct proxy created
     * by the adapter). The direct proxy contains the adapter
     * endpoints.
     * 
     * @param __cb The asynchronous callback object.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_setAdapterDirectProxy(String id, Ice.ObjectPrx proxy, Callback_LocatorRegistry_setAdapterDirectProxy __cb);

    /**
     * Set the adapter endpoints with the locator registry.
     * 
     * @param id The adapter id.
     * 
     * @param proxy The adapter proxy (a dummy direct proxy created
     * by the adapter). The direct proxy contains the adapter
     * endpoints.
     * 
     * @param __ctx The Context map to send with the invocation.
     * @param __cb The asynchronous callback object.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_setAdapterDirectProxy(String id, Ice.ObjectPrx proxy, java.util.Map<String, String> __ctx, Callback_LocatorRegistry_setAdapterDirectProxy __cb);

    /**
     * Set the adapter endpoints with the locator registry.
     * 
     * @param id The adapter id.
     * 
     * @param proxy The adapter proxy (a dummy direct proxy created
     * by the adapter). The direct proxy contains the adapter
     * endpoints.
     * 
     * @param __responseCb The lambda response callback.
     * @param __userExceptionCb The lambda user exception callback.
     * @param __exceptionCb The lambda exception callback.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_setAdapterDirectProxy(String id, 
                                                       Ice.ObjectPrx proxy, 
                                                       IceInternal.Functional_VoidCallback __responseCb, 
                                                       IceInternal.Functional_GenericCallback1<Ice.UserException> __userExceptionCb, 
                                                       IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb);

    /**
     * Set the adapter endpoints with the locator registry.
     * 
     * @param id The adapter id.
     * 
     * @param proxy The adapter proxy (a dummy direct proxy created
     * by the adapter). The direct proxy contains the adapter
     * endpoints.
     * 
     * @param __responseCb The lambda response callback.
     * @param __userExceptionCb The lambda user exception callback.
     * @param __exceptionCb The lambda exception callback.
     * @param __sentCb The lambda sent callback.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_setAdapterDirectProxy(String id, 
                                                       Ice.ObjectPrx proxy, 
                                                       IceInternal.Functional_VoidCallback __responseCb, 
                                                       IceInternal.Functional_GenericCallback1<Ice.UserException> __userExceptionCb, 
                                                       IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb, 
                                                       IceInternal.Functional_BoolCallback __sentCb);

    /**
     * Set the adapter endpoints with the locator registry.
     * 
     * @param id The adapter id.
     * 
     * @param proxy The adapter proxy (a dummy direct proxy created
     * by the adapter). The direct proxy contains the adapter
     * endpoints.
     * 
     * @param __ctx The Context map to send with the invocation.
     * @param __responseCb The lambda response callback.
     * @param __userExceptionCb The lambda user exception callback.
     * @param __exceptionCb The lambda exception callback.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_setAdapterDirectProxy(String id, 
                                                       Ice.ObjectPrx proxy, 
                                                       java.util.Map<String, String> __ctx, 
                                                       IceInternal.Functional_VoidCallback __responseCb, 
                                                       IceInternal.Functional_GenericCallback1<Ice.UserException> __userExceptionCb, 
                                                       IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb);

    /**
     * Set the adapter endpoints with the locator registry.
     * 
     * @param id The adapter id.
     * 
     * @param proxy The adapter proxy (a dummy direct proxy created
     * by the adapter). The direct proxy contains the adapter
     * endpoints.
     * 
     * @param __ctx The Context map to send with the invocation.
     * @param __responseCb The lambda response callback.
     * @param __userExceptionCb The lambda user exception callback.
     * @param __exceptionCb The lambda exception callback.
     * @param __sentCb The lambda sent callback.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_setAdapterDirectProxy(String id, 
                                                       Ice.ObjectPrx proxy, 
                                                       java.util.Map<String, String> __ctx, 
                                                       IceInternal.Functional_VoidCallback __responseCb, 
                                                       IceInternal.Functional_GenericCallback1<Ice.UserException> __userExceptionCb, 
                                                       IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb, 
                                                       IceInternal.Functional_BoolCallback __sentCb);

    /**
     * Set the adapter endpoints with the locator registry.
     * 
     * @param __result The asynchronous result object.
     * @throws AdapterNotFoundException Raised if the adapter cannot
     * be found, or if the locator only allows
     * registered adapters to set their active proxy and the
     * adapter is not registered with the locator.
     * 
     * @throws AdapterAlreadyActiveException Raised if an adapter with the same
     * id is already active.
     * 
     **/
    public void end_setAdapterDirectProxy(Ice.AsyncResult __result)
        throws AdapterAlreadyActiveException,
               AdapterNotFoundException;

    /**
     * Set the adapter endpoints with the locator registry.
     *
     * @param adapterId The adapter id.
     *
     * @param replicaGroupId The replica group id.
     *
     * @param p The adapter proxy (a dummy direct proxy created
     * by the adapter). The direct proxy contains the adapter
     * endpoints.
     *
     * @throws AdapterNotFoundException Raised if the adapter cannot
     * be found, or if the locator only allows registered adapters to
     * set their active proxy and the adapter is not registered with
     * the locator.
     *
     * @throws AdapterAlreadyActiveException Raised if an adapter with the same
     * id is already active.
     *
     * @throws InvalidReplicaGroupIdException Raised if the given
     * replica group doesn't match the one registered with the
     * locator registry for this object adapter.
     *
     **/
    public void setReplicatedAdapterDirectProxy(String adapterId, String replicaGroupId, Ice.ObjectPrx p)
        throws AdapterAlreadyActiveException,
               AdapterNotFoundException,
               InvalidReplicaGroupIdException;

    /**
     * Set the adapter endpoints with the locator registry.
     *
     * @param adapterId The adapter id.
     *
     * @param replicaGroupId The replica group id.
     *
     * @param p The adapter proxy (a dummy direct proxy created
     * by the adapter). The direct proxy contains the adapter
     * endpoints.
     *
     * @throws AdapterNotFoundException Raised if the adapter cannot
     * be found, or if the locator only allows registered adapters to
     * set their active proxy and the adapter is not registered with
     * the locator.
     *
     * @throws AdapterAlreadyActiveException Raised if an adapter with the same
     * id is already active.
     *
     * @throws InvalidReplicaGroupIdException Raised if the given
     * replica group doesn't match the one registered with the
     * locator registry for this object adapter.
     *
     * @param __ctx The Context map to send with the invocation.
     **/
    public void setReplicatedAdapterDirectProxy(String adapterId, String replicaGroupId, Ice.ObjectPrx p, java.util.Map<String, String> __ctx)
        throws AdapterAlreadyActiveException,
               AdapterNotFoundException,
               InvalidReplicaGroupIdException;

    /**
     * Set the adapter endpoints with the locator registry.
     * 
     * @param adapterId The adapter id.
     * 
     * @param replicaGroupId The replica group id.
     * 
     * @param p The adapter proxy (a dummy direct proxy created
     * by the adapter). The direct proxy contains the adapter
     * endpoints.
     * 
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_setReplicatedAdapterDirectProxy(String adapterId, String replicaGroupId, Ice.ObjectPrx p);

    /**
     * Set the adapter endpoints with the locator registry.
     * 
     * @param adapterId The adapter id.
     * 
     * @param replicaGroupId The replica group id.
     * 
     * @param p The adapter proxy (a dummy direct proxy created
     * by the adapter). The direct proxy contains the adapter
     * endpoints.
     * 
     * @param __ctx The Context map to send with the invocation.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_setReplicatedAdapterDirectProxy(String adapterId, String replicaGroupId, Ice.ObjectPrx p, java.util.Map<String, String> __ctx);

    /**
     * Set the adapter endpoints with the locator registry.
     * 
     * @param adapterId The adapter id.
     * 
     * @param replicaGroupId The replica group id.
     * 
     * @param p The adapter proxy (a dummy direct proxy created
     * by the adapter). The direct proxy contains the adapter
     * endpoints.
     * 
     * @param __cb The asynchronous callback object.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_setReplicatedAdapterDirectProxy(String adapterId, String replicaGroupId, Ice.ObjectPrx p, Ice.Callback __cb);

    /**
     * Set the adapter endpoints with the locator registry.
     * 
     * @param adapterId The adapter id.
     * 
     * @param replicaGroupId The replica group id.
     * 
     * @param p The adapter proxy (a dummy direct proxy created
     * by the adapter). The direct proxy contains the adapter
     * endpoints.
     * 
     * @param __ctx The Context map to send with the invocation.
     * @param __cb The asynchronous callback object.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_setReplicatedAdapterDirectProxy(String adapterId, String replicaGroupId, Ice.ObjectPrx p, java.util.Map<String, String> __ctx, Ice.Callback __cb);

    /**
     * Set the adapter endpoints with the locator registry.
     * 
     * @param adapterId The adapter id.
     * 
     * @param replicaGroupId The replica group id.
     * 
     * @param p The adapter proxy (a dummy direct proxy created
     * by the adapter). The direct proxy contains the adapter
     * endpoints.
     * 
     * @param __cb The asynchronous callback object.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_setReplicatedAdapterDirectProxy(String adapterId, String replicaGroupId, Ice.ObjectPrx p, Callback_LocatorRegistry_setReplicatedAdapterDirectProxy __cb);

    /**
     * Set the adapter endpoints with the locator registry.
     * 
     * @param adapterId The adapter id.
     * 
     * @param replicaGroupId The replica group id.
     * 
     * @param p The adapter proxy (a dummy direct proxy created
     * by the adapter). The direct proxy contains the adapter
     * endpoints.
     * 
     * @param __ctx The Context map to send with the invocation.
     * @param __cb The asynchronous callback object.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_setReplicatedAdapterDirectProxy(String adapterId, String replicaGroupId, Ice.ObjectPrx p, java.util.Map<String, String> __ctx, Callback_LocatorRegistry_setReplicatedAdapterDirectProxy __cb);

    /**
     * Set the adapter endpoints with the locator registry.
     * 
     * @param adapterId The adapter id.
     * 
     * @param replicaGroupId The replica group id.
     * 
     * @param p The adapter proxy (a dummy direct proxy created
     * by the adapter). The direct proxy contains the adapter
     * endpoints.
     * 
     * @param __responseCb The lambda response callback.
     * @param __userExceptionCb The lambda user exception callback.
     * @param __exceptionCb The lambda exception callback.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_setReplicatedAdapterDirectProxy(String adapterId, 
                                                                 String replicaGroupId, 
                                                                 Ice.ObjectPrx p, 
                                                                 IceInternal.Functional_VoidCallback __responseCb, 
                                                                 IceInternal.Functional_GenericCallback1<Ice.UserException> __userExceptionCb, 
                                                                 IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb);

    /**
     * Set the adapter endpoints with the locator registry.
     * 
     * @param adapterId The adapter id.
     * 
     * @param replicaGroupId The replica group id.
     * 
     * @param p The adapter proxy (a dummy direct proxy created
     * by the adapter). The direct proxy contains the adapter
     * endpoints.
     * 
     * @param __responseCb The lambda response callback.
     * @param __userExceptionCb The lambda user exception callback.
     * @param __exceptionCb The lambda exception callback.
     * @param __sentCb The lambda sent callback.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_setReplicatedAdapterDirectProxy(String adapterId, 
                                                                 String replicaGroupId, 
                                                                 Ice.ObjectPrx p, 
                                                                 IceInternal.Functional_VoidCallback __responseCb, 
                                                                 IceInternal.Functional_GenericCallback1<Ice.UserException> __userExceptionCb, 
                                                                 IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb, 
                                                                 IceInternal.Functional_BoolCallback __sentCb);

    /**
     * Set the adapter endpoints with the locator registry.
     * 
     * @param adapterId The adapter id.
     * 
     * @param replicaGroupId The replica group id.
     * 
     * @param p The adapter proxy (a dummy direct proxy created
     * by the adapter). The direct proxy contains the adapter
     * endpoints.
     * 
     * @param __ctx The Context map to send with the invocation.
     * @param __responseCb The lambda response callback.
     * @param __userExceptionCb The lambda user exception callback.
     * @param __exceptionCb The lambda exception callback.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_setReplicatedAdapterDirectProxy(String adapterId, 
                                                                 String replicaGroupId, 
                                                                 Ice.ObjectPrx p, 
                                                                 java.util.Map<String, String> __ctx, 
                                                                 IceInternal.Functional_VoidCallback __responseCb, 
                                                                 IceInternal.Functional_GenericCallback1<Ice.UserException> __userExceptionCb, 
                                                                 IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb);

    /**
     * Set the adapter endpoints with the locator registry.
     * 
     * @param adapterId The adapter id.
     * 
     * @param replicaGroupId The replica group id.
     * 
     * @param p The adapter proxy (a dummy direct proxy created
     * by the adapter). The direct proxy contains the adapter
     * endpoints.
     * 
     * @param __ctx The Context map to send with the invocation.
     * @param __responseCb The lambda response callback.
     * @param __userExceptionCb The lambda user exception callback.
     * @param __exceptionCb The lambda exception callback.
     * @param __sentCb The lambda sent callback.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_setReplicatedAdapterDirectProxy(String adapterId, 
                                                                 String replicaGroupId, 
                                                                 Ice.ObjectPrx p, 
                                                                 java.util.Map<String, String> __ctx, 
                                                                 IceInternal.Functional_VoidCallback __responseCb, 
                                                                 IceInternal.Functional_GenericCallback1<Ice.UserException> __userExceptionCb, 
                                                                 IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb, 
                                                                 IceInternal.Functional_BoolCallback __sentCb);

    /**
     * Set the adapter endpoints with the locator registry.
     * 
     * @param __result The asynchronous result object.
     * @throws AdapterNotFoundException Raised if the adapter cannot
     * be found, or if the locator only allows registered adapters to
     * set their active proxy and the adapter is not registered with
     * the locator.
     * 
     * @throws AdapterAlreadyActiveException Raised if an adapter with the same
     * id is already active.
     * 
     * @throws InvalidReplicaGroupIdException Raised if the given
     * replica group doesn't match the one registered with the
     * locator registry for this object adapter.
     * 
     **/
    public void end_setReplicatedAdapterDirectProxy(Ice.AsyncResult __result)
        throws AdapterAlreadyActiveException,
               AdapterNotFoundException,
               InvalidReplicaGroupIdException;

    /**
     * Set the process proxy for a server.
     *
     * @param id The server id.
     *
     * @param proxy The process proxy.
     *
     * @throws ServerNotFoundException Raised if the server cannot
     * be found.
     *
     **/
    public void setServerProcessProxy(String id, ProcessPrx proxy)
        throws ServerNotFoundException;

    /**
     * Set the process proxy for a server.
     *
     * @param id The server id.
     *
     * @param proxy The process proxy.
     *
     * @throws ServerNotFoundException Raised if the server cannot
     * be found.
     *
     * @param __ctx The Context map to send with the invocation.
     **/
    public void setServerProcessProxy(String id, ProcessPrx proxy, java.util.Map<String, String> __ctx)
        throws ServerNotFoundException;

    /**
     * Set the process proxy for a server.
     * 
     * @param id The server id.
     * 
     * @param proxy The process proxy.
     * 
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_setServerProcessProxy(String id, ProcessPrx proxy);

    /**
     * Set the process proxy for a server.
     * 
     * @param id The server id.
     * 
     * @param proxy The process proxy.
     * 
     * @param __ctx The Context map to send with the invocation.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_setServerProcessProxy(String id, ProcessPrx proxy, java.util.Map<String, String> __ctx);

    /**
     * Set the process proxy for a server.
     * 
     * @param id The server id.
     * 
     * @param proxy The process proxy.
     * 
     * @param __cb The asynchronous callback object.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_setServerProcessProxy(String id, ProcessPrx proxy, Ice.Callback __cb);

    /**
     * Set the process proxy for a server.
     * 
     * @param id The server id.
     * 
     * @param proxy The process proxy.
     * 
     * @param __ctx The Context map to send with the invocation.
     * @param __cb The asynchronous callback object.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_setServerProcessProxy(String id, ProcessPrx proxy, java.util.Map<String, String> __ctx, Ice.Callback __cb);

    /**
     * Set the process proxy for a server.
     * 
     * @param id The server id.
     * 
     * @param proxy The process proxy.
     * 
     * @param __cb The asynchronous callback object.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_setServerProcessProxy(String id, ProcessPrx proxy, Callback_LocatorRegistry_setServerProcessProxy __cb);

    /**
     * Set the process proxy for a server.
     * 
     * @param id The server id.
     * 
     * @param proxy The process proxy.
     * 
     * @param __ctx The Context map to send with the invocation.
     * @param __cb The asynchronous callback object.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_setServerProcessProxy(String id, ProcessPrx proxy, java.util.Map<String, String> __ctx, Callback_LocatorRegistry_setServerProcessProxy __cb);

    /**
     * Set the process proxy for a server.
     * 
     * @param id The server id.
     * 
     * @param proxy The process proxy.
     * 
     * @param __responseCb The lambda response callback.
     * @param __userExceptionCb The lambda user exception callback.
     * @param __exceptionCb The lambda exception callback.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_setServerProcessProxy(String id, 
                                                       ProcessPrx proxy, 
                                                       IceInternal.Functional_VoidCallback __responseCb, 
                                                       IceInternal.Functional_GenericCallback1<Ice.UserException> __userExceptionCb, 
                                                       IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb);

    /**
     * Set the process proxy for a server.
     * 
     * @param id The server id.
     * 
     * @param proxy The process proxy.
     * 
     * @param __responseCb The lambda response callback.
     * @param __userExceptionCb The lambda user exception callback.
     * @param __exceptionCb The lambda exception callback.
     * @param __sentCb The lambda sent callback.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_setServerProcessProxy(String id, 
                                                       ProcessPrx proxy, 
                                                       IceInternal.Functional_VoidCallback __responseCb, 
                                                       IceInternal.Functional_GenericCallback1<Ice.UserException> __userExceptionCb, 
                                                       IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb, 
                                                       IceInternal.Functional_BoolCallback __sentCb);

    /**
     * Set the process proxy for a server.
     * 
     * @param id The server id.
     * 
     * @param proxy The process proxy.
     * 
     * @param __ctx The Context map to send with the invocation.
     * @param __responseCb The lambda response callback.
     * @param __userExceptionCb The lambda user exception callback.
     * @param __exceptionCb The lambda exception callback.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_setServerProcessProxy(String id, 
                                                       ProcessPrx proxy, 
                                                       java.util.Map<String, String> __ctx, 
                                                       IceInternal.Functional_VoidCallback __responseCb, 
                                                       IceInternal.Functional_GenericCallback1<Ice.UserException> __userExceptionCb, 
                                                       IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb);

    /**
     * Set the process proxy for a server.
     * 
     * @param id The server id.
     * 
     * @param proxy The process proxy.
     * 
     * @param __ctx The Context map to send with the invocation.
     * @param __responseCb The lambda response callback.
     * @param __userExceptionCb The lambda user exception callback.
     * @param __exceptionCb The lambda exception callback.
     * @param __sentCb The lambda sent callback.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_setServerProcessProxy(String id, 
                                                       ProcessPrx proxy, 
                                                       java.util.Map<String, String> __ctx, 
                                                       IceInternal.Functional_VoidCallback __responseCb, 
                                                       IceInternal.Functional_GenericCallback1<Ice.UserException> __userExceptionCb, 
                                                       IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb, 
                                                       IceInternal.Functional_BoolCallback __sentCb);

    /**
     * Set the process proxy for a server.
     * 
     * @param __result The asynchronous result object.
     * @throws ServerNotFoundException Raised if the server cannot
     * be found.
     * 
     **/
    public void end_setServerProcessProxy(Ice.AsyncResult __result)
        throws ServerNotFoundException;
}
