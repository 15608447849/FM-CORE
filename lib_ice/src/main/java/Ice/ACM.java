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
// Generated from file `Connection.ice'
//
// Warning: do not edit this file.
//
// </auto-generated>
//

package Ice;

public class ACM implements java.lang.Cloneable
{
    public int timeout;

    public ACMClose close;

    public ACMHeartbeat heartbeat;

    public ACM()
    {
        close = ACMClose.CloseOff;
        heartbeat = ACMHeartbeat.HeartbeatOff;
    }

    public ACM(int timeout, ACMClose close, ACMHeartbeat heartbeat)
    {
        this.timeout = timeout;
        this.close = close;
        this.heartbeat = heartbeat;
    }

    public boolean
    equals(java.lang.Object rhs)
    {
        if(this == rhs)
        {
            return true;
        }
        ACM _r = null;
        if(rhs instanceof ACM)
        {
            _r = (ACM)rhs;
        }

        if(_r != null)
        {
            if(timeout != _r.timeout)
            {
                return false;
            }
            if(close != _r.close)
            {
                if(close == null || _r.close == null || !close.equals(_r.close))
                {
                    return false;
                }
            }
            if(heartbeat != _r.heartbeat)
            {
                if(heartbeat == null || _r.heartbeat == null || !heartbeat.equals(_r.heartbeat))
                {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    public int
    hashCode()
    {
        int __h = 5381;
        __h = IceInternal.HashUtil.hashAdd(__h, "::Ice::ACM");
        __h = IceInternal.HashUtil.hashAdd(__h, timeout);
        __h = IceInternal.HashUtil.hashAdd(__h, close);
        __h = IceInternal.HashUtil.hashAdd(__h, heartbeat);
        return __h;
    }

    public ACM
    clone()
    {
        ACM c = null;
        try
        {
            c = (ACM)super.clone();
        }
        catch(CloneNotSupportedException ex)
        {
            assert false; // impossible
        }
        return c;
    }

    public static final long serialVersionUID = -969527045191053991L;
}
