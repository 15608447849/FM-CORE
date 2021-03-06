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
public abstract class _LocatorRegistryDisp extends Ice.ObjectImpl implements LocatorRegistry
{
    protected void
    ice_copyStateFrom(Ice.Object __obj)
        throws java.lang.CloneNotSupportedException
    {
        throw new java.lang.CloneNotSupportedException();
    }

    public static final String[] __ids =
    {
        "::Ice::LocatorRegistry",
        "::Ice::Object"
    };

    public boolean ice_isA(String s)
    {
        return java.util.Arrays.binarySearch(__ids, s) >= 0;
    }

    public boolean ice_isA(String s, Ice.Current __current)
    {
        return java.util.Arrays.binarySearch(__ids, s) >= 0;
    }

    public String[] ice_ids()
    {
        return __ids;
    }

    public String[] ice_ids(Ice.Current __current)
    {
        return __ids;
    }

    public String ice_id()
    {
        return __ids[0];
    }

    public String ice_id(Ice.Current __current)
    {
        return __ids[0];
    }

    public static String ice_staticId()
    {
        return __ids[0];
    }

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
    public final void setAdapterDirectProxy_async(AMD_LocatorRegistry_setAdapterDirectProxy __cb, String id, Ice.ObjectPrx proxy)
        throws AdapterAlreadyActiveException,
               AdapterNotFoundException
    {
        setAdapterDirectProxy_async(__cb, id, proxy, null);
    }

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
    public final void setReplicatedAdapterDirectProxy_async(AMD_LocatorRegistry_setReplicatedAdapterDirectProxy __cb, String adapterId, String replicaGroupId, Ice.ObjectPrx p)
        throws AdapterAlreadyActiveException,
               AdapterNotFoundException,
               InvalidReplicaGroupIdException
    {
        setReplicatedAdapterDirectProxy_async(__cb, adapterId, replicaGroupId, p, null);
    }

    /**
     * Set the process proxy for a server.
     * 
     * @param __cb The callback object for the operation.
     * @param id The server id.
     * 
     * @param proxy The process proxy.
     * 
     **/
    public final void setServerProcessProxy_async(AMD_LocatorRegistry_setServerProcessProxy __cb, String id, ProcessPrx proxy)
        throws ServerNotFoundException
    {
        setServerProcessProxy_async(__cb, id, proxy, null);
    }

    public static Ice.DispatchStatus ___setAdapterDirectProxy(LocatorRegistry __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Idempotent, __current.mode);
        IceInternal.BasicStream __is = __inS.startReadParams();
        String id;
        Ice.ObjectPrx proxy;
        id = __is.readString();
        proxy = __is.readProxy();
        __inS.endReadParams();
        _AMD_LocatorRegistry_setAdapterDirectProxy __cb = new _AMD_LocatorRegistry_setAdapterDirectProxy(__inS);
        try
        {
            __obj.setAdapterDirectProxy_async(__cb, id, proxy, __current);
        }
        catch(java.lang.Exception ex)
        {
            __cb.ice_exception(ex);
        }
        catch(java.lang.Error ex)
        {
            __cb.__error(ex);
        }
        return Ice.DispatchStatus.DispatchAsync;
    }

    public static Ice.DispatchStatus ___setReplicatedAdapterDirectProxy(LocatorRegistry __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Idempotent, __current.mode);
        IceInternal.BasicStream __is = __inS.startReadParams();
        String adapterId;
        String replicaGroupId;
        Ice.ObjectPrx p;
        adapterId = __is.readString();
        replicaGroupId = __is.readString();
        p = __is.readProxy();
        __inS.endReadParams();
        _AMD_LocatorRegistry_setReplicatedAdapterDirectProxy __cb = new _AMD_LocatorRegistry_setReplicatedAdapterDirectProxy(__inS);
        try
        {
            __obj.setReplicatedAdapterDirectProxy_async(__cb, adapterId, replicaGroupId, p, __current);
        }
        catch(java.lang.Exception ex)
        {
            __cb.ice_exception(ex);
        }
        catch(java.lang.Error ex)
        {
            __cb.__error(ex);
        }
        return Ice.DispatchStatus.DispatchAsync;
    }

    public static Ice.DispatchStatus ___setServerProcessProxy(LocatorRegistry __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Idempotent, __current.mode);
        IceInternal.BasicStream __is = __inS.startReadParams();
        String id;
        ProcessPrx proxy;
        id = __is.readString();
        proxy = ProcessPrxHelper.__read(__is);
        __inS.endReadParams();
        _AMD_LocatorRegistry_setServerProcessProxy __cb = new _AMD_LocatorRegistry_setServerProcessProxy(__inS);
        try
        {
            __obj.setServerProcessProxy_async(__cb, id, proxy, __current);
        }
        catch(java.lang.Exception ex)
        {
            __cb.ice_exception(ex);
        }
        catch(java.lang.Error ex)
        {
            __cb.__error(ex);
        }
        return Ice.DispatchStatus.DispatchAsync;
    }

    private final static String[] __all =
    {
        "ice_id",
        "ice_ids",
        "ice_isA",
        "ice_ping",
        "setAdapterDirectProxy",
        "setReplicatedAdapterDirectProxy",
        "setServerProcessProxy"
    };

    public Ice.DispatchStatus __dispatch(IceInternal.Incoming in, Ice.Current __current)
    {
        int pos = java.util.Arrays.binarySearch(__all, __current.operation);
        if(pos < 0)
        {
            throw new Ice.OperationNotExistException(__current.id, __current.facet, __current.operation);
        }

        switch(pos)
        {
            case 0:
            {
                return ___ice_id(this, in, __current);
            }
            case 1:
            {
                return ___ice_ids(this, in, __current);
            }
            case 2:
            {
                return ___ice_isA(this, in, __current);
            }
            case 3:
            {
                return ___ice_ping(this, in, __current);
            }
            case 4:
            {
                return ___setAdapterDirectProxy(this, in, __current);
            }
            case 5:
            {
                return ___setReplicatedAdapterDirectProxy(this, in, __current);
            }
            case 6:
            {
                return ___setServerProcessProxy(this, in, __current);
            }
        }

        assert(false);
        throw new Ice.OperationNotExistException(__current.id, __current.facet, __current.operation);
    }

    protected void __writeImpl(IceInternal.BasicStream __os)
    {
        __os.startWriteSlice(ice_staticId(), -1, true);
        __os.endWriteSlice();
    }

    protected void __readImpl(IceInternal.BasicStream __is)
    {
        __is.startReadSlice();
        __is.endReadSlice();
    }

    public static final long serialVersionUID = 0L;
}
