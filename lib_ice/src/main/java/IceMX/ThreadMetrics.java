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
 * Provides information on the number of threads currently in use and
 * their activity.
 *
 **/
public class ThreadMetrics extends Metrics
{
    public ThreadMetrics()
    {
        super();
        inUseForIO = 0;
        inUseForUser = 0;
        inUseForOther = 0;
    }

    public ThreadMetrics(String id, long total, int current, long totalLifetime, int failures, int inUseForIO, int inUseForUser, int inUseForOther)
    {
        super(id, total, current, totalLifetime, failures);
        this.inUseForIO = inUseForIO;
        this.inUseForUser = inUseForUser;
        this.inUseForOther = inUseForOther;
    }

    private static class __F implements Ice.ObjectFactory
    {
        public Ice.Object create(String type)
        {
            assert(type.equals(ice_staticId()));
            return new ThreadMetrics();
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
        "::IceMX::Metrics",
        "::IceMX::ThreadMetrics"
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
        return __ids[2];
    }

    public String ice_id(Ice.Current __current)
    {
        return __ids[2];
    }

    public static String ice_staticId()
    {
        return __ids[2];
    }

    protected void __writeImpl(IceInternal.BasicStream __os)
    {
        __os.startWriteSlice(ice_staticId(), -1, false);
        __os.writeInt(inUseForIO);
        __os.writeInt(inUseForUser);
        __os.writeInt(inUseForOther);
        __os.endWriteSlice();
        super.__writeImpl(__os);
    }

    protected void __readImpl(IceInternal.BasicStream __is)
    {
        __is.startReadSlice();
        inUseForIO = __is.readInt();
        inUseForUser = __is.readInt();
        inUseForOther = __is.readInt();
        __is.endReadSlice();
        super.__readImpl(__is);
    }

    /**
     * The number of threads which are currently performing socket
     * read or writes.
     *
     **/
    public int inUseForIO;

    /**
     * The number of threads which are currently calling user code
     * (servant dispatch, AMI callbacks, etc).
     *
     **/
    public int inUseForUser;

    /**
     * The number of threads which are currently performing other
     * activities. These are all other that are not counted with
     * {@link #inUseForUser} or {@link #inUseForIO}, such as DNS
     * lookups, garbage collection).
     *
     **/
    public int inUseForOther;

    public ThreadMetrics
    clone()
    {
        return (ThreadMetrics)super.clone();
    }

    public static final long serialVersionUID = -7413833790150540460L;
}
