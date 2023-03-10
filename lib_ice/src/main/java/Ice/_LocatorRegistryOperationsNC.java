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
public interface _LocatorRegistryOperationsNC
{
    /**
     * Set the adapter endpoints with the locator registry.
     * 
     * @param __cb The callback object for the operation.
     * @param id The adapter id.
     * 
     * @param proxy The adapter proxy (a dummy direct proxy created
     * by the adapter). The direct proxy contains the adapter
     * endpoints.
     * 
     **/
    void setAdapterDirectProxy_async(AMD_LocatorRegistry_setAdapterDirectProxy __cb, String id, Ice.ObjectPrx proxy)
        throws AdapterAlreadyActiveException,
               AdapterNotFoundException;

    /**
     * Set the adapter endpoints with the locator registry.
     * 
     * @param __cb The callback object for the operation.
     * @param adapterId The adapter id.
     * 
     * @param replicaGroupId The replica group id.
     * 
     * @param p The adapter proxy (a dummy direct proxy created
     * by the adapter). The direct proxy contains the adapter
     * endpoints.
     * 
     **/
    void setReplicatedAdapterDirectProxy_async(AMD_LocatorRegistry_setReplicatedAdapterDirectProxy __cb, String adapterId, String replicaGroupId, Ice.ObjectPrx p)
        throws AdapterAlreadyActiveException,
               AdapterNotFoundException,
               InvalidReplicaGroupIdException;

    /**
     * Set the process proxy for a server.
     * 
     * @param __cb The callback object for the operation.
     * @param id The server id.
     * 
     * @param proxy The process proxy.
     * 
     **/
    void setServerProcessProxy_async(AMD_LocatorRegistry_setServerProcessProxy __cb, String id, ProcessPrx proxy)
        throws ServerNotFoundException;
}
