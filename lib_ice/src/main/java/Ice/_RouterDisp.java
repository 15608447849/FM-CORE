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
 * The Ice router interface. Routers can be set either globally with
 * {@link Communicator#setDefaultRouter}, or with <tt>ice_router</tt> on specific
 * proxies.
 *
 **/
public abstract class _RouterDisp extends Ice.ObjectImpl implements Router
{
    protected void
    ice_copyStateFrom(Ice.Object __obj)
        throws java.lang.CloneNotSupportedException
    {
        throw new java.lang.CloneNotSupportedException();
    }

    public static final String[] __ids =
    {
        "::Ice::Object",
        "::Ice::Router"
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
        return __ids[1];
    }

    public String ice_id(Ice.Current __current)
    {
        return __ids[1];
    }

    public static String ice_staticId()
    {
        return __ids[1];
    }

    /**
     * Add new proxy information to the router's routing table.
     *
     * @param proxies The proxies to add.
     *
     * @return Proxies discarded by the router.
     *
     **/
    public final Ice.ObjectPrx[] addProxies(Ice.ObjectPrx[] proxies)
    {
        return addProxies(proxies, null);
    }

    /**
     * Get the router's client proxy, i.e., the proxy to use for
     * forwarding requests from the client to the router.
     *
     * If a null proxy is returned, the client will forward requests
     * to the router's endpoints.
     *
     * @return The router's client proxy.
     *
     **/
    public final Ice.ObjectPrx getClientProxy()
    {
        return getClientProxy(null);
    }

    /**
     * Get the router's server proxy, i.e., the proxy to use for
     * forwarding requests from the server to the router.
     *
     * @return The router's server proxy.
     *
     **/
    public final Ice.ObjectPrx getServerProxy()
    {
        return getServerProxy(null);
    }

    public static Ice.DispatchStatus ___getClientProxy(Router __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Idempotent, __current.mode);
        __inS.readEmptyParams();
        Ice.ObjectPrx __ret = __obj.getClientProxy(__current);
        IceInternal.BasicStream __os = __inS.__startWriteParams(Ice.FormatType.DefaultFormat);
        __os.writeProxy(__ret);
        __inS.__endWriteParams(true);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus ___getServerProxy(Router __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Idempotent, __current.mode);
        __inS.readEmptyParams();
        Ice.ObjectPrx __ret = __obj.getServerProxy(__current);
        IceInternal.BasicStream __os = __inS.__startWriteParams(Ice.FormatType.DefaultFormat);
        __os.writeProxy(__ret);
        __inS.__endWriteParams(true);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus ___addProxies(Router __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Idempotent, __current.mode);
        IceInternal.BasicStream __is = __inS.startReadParams();
        Ice.ObjectPrx[] proxies;
        proxies = ObjectProxySeqHelper.read(__is);
        __inS.endReadParams();
        Ice.ObjectPrx[] __ret = __obj.addProxies(proxies, __current);
        IceInternal.BasicStream __os = __inS.__startWriteParams(Ice.FormatType.DefaultFormat);
        ObjectProxySeqHelper.write(__os, __ret);
        __inS.__endWriteParams(true);
        return Ice.DispatchStatus.DispatchOK;
    }

    private final static String[] __all =
    {
        "addProxies",
        "getClientProxy",
        "getServerProxy",
        "ice_id",
        "ice_ids",
        "ice_isA",
        "ice_ping"
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
                return ___addProxies(this, in, __current);
            }
            case 1:
            {
                return ___getClientProxy(this, in, __current);
            }
            case 2:
            {
                return ___getServerProxy(this, in, __current);
            }
            case 3:
            {
                return ___ice_id(this, in, __current);
            }
            case 4:
            {
                return ___ice_ids(this, in, __current);
            }
            case 5:
            {
                return ___ice_isA(this, in, __current);
            }
            case 6:
            {
                return ___ice_ping(this, in, __current);
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
