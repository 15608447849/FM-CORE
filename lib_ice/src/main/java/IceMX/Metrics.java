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
// Generated from file `Metrics.ice'
//
// Warning: do not edit this file.
//
// </auto-generated>
//

package IceMX;

/**
 * The base class for metrics. A metrics object represents a
 * collection of measurements associated to a given a system.
 *
 **/
public class Metrics extends Ice.ObjectImpl
{
    public Metrics()
    {
        id = "";
        total = 0L;
        current = 0;
        totalLifetime = 0L;
        failures = 0;
    }

    public Metrics(String id, long total, int current, long totalLifetime, int failures)
    {
        this.id = id;
        this.total = total;
        this.current = current;
        this.totalLifetime = totalLifetime;
        this.failures = failures;
    }

    private static class __F implements Ice.ObjectFactory
    {
        public Ice.Object create(String type)
        {
            assert(type.equals(ice_staticId()));
            return new Metrics();
        }

        public void destroy()
        {
        }
    }
    private static Ice.ObjectFactory _factory = new __F();

    public static Ice.ObjectFactory
    ice_factory()
    {
        return _factory;
    }

    public static final String[] __ids =
    {
        "::Ice::Object",
        "::IceMX::Metrics"
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

    protected void __writeImpl(IceInternal.BasicStream __os)
    {
        __os.startWriteSlice(ice_staticId(), -1, true);
        __os.writeString(id);
        __os.writeLong(total);
        __os.writeInt(current);
        __os.writeLong(totalLifetime);
        __os.writeInt(failures);
        __os.endWriteSlice();
    }

    protected void __readImpl(IceInternal.BasicStream __is)
    {
        __is.startReadSlice();
        id = __is.readString();
        total = __is.readLong();
        current = __is.readInt();
        totalLifetime = __is.readLong();
        failures = __is.readInt();
        __is.endReadSlice();
    }

    /**
     * The metrics identifier.
     *
     **/
    public String id;

    /**
     * The total number of objects that were observed by this metrics.
     *
     **/
    public long total;

    /**
     * The current number of objects observed by this metrics.
     *
     **/
    public int current;

    /**
     * The sum of the lifetime of each observed objects. This does not
     * include the lifetime of objects which are currently observed.
     *
     **/
    public long totalLifetime;

    /**
     * The number of failures observed.
     *
     **/
    public int failures;

    public Metrics
    clone()
    {
        return (Metrics)super.clone();
    }

    public static final long serialVersionUID = 5637578887472768063L;
}