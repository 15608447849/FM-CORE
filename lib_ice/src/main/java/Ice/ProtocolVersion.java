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
// Generated from file `Version.ice'
//
// Warning: do not edit this file.
//
// </auto-generated>
//

package Ice;

/**
 * A version structure for the protocol version.
 *
 **/
public class ProtocolVersion implements java.lang.Cloneable, java.io.Serializable
{
    public byte major;

    public byte minor;

    public ProtocolVersion()
    {
    }

    public ProtocolVersion(byte major, byte minor)
    {
        this.major = major;
        this.minor = minor;
    }

    public boolean
    equals(java.lang.Object rhs)
    {
        if(this == rhs)
        {
            return true;
        }
        ProtocolVersion _r = null;
        if(rhs instanceof ProtocolVersion)
        {
            _r = (ProtocolVersion)rhs;
        }

        if(_r != null)
        {
            if(major != _r.major)
            {
                return false;
            }
            if(minor != _r.minor)
            {
                return false;
            }

            return true;
        }

        return false;
    }

    public int
    hashCode()
    {
        int __h = 5381;
        __h = IceInternal.HashUtil.hashAdd(__h, "::Ice::ProtocolVersion");
        __h = IceInternal.HashUtil.hashAdd(__h, major);
        __h = IceInternal.HashUtil.hashAdd(__h, minor);
        return __h;
    }

    public ProtocolVersion
    clone()
    {
        ProtocolVersion c = null;
        try
        {
            c = (ProtocolVersion)super.clone();
        }
        catch(CloneNotSupportedException ex)
        {
            assert false; // impossible
        }
        return c;
    }

    public void
    __write(IceInternal.BasicStream __os)
    {
        __os.writeByte(major);
        __os.writeByte(minor);
    }

    public void
    __read(IceInternal.BasicStream __is)
    {
        major = __is.readByte();
        minor = __is.readByte();
    }

    static public void
    __write(IceInternal.BasicStream __os, ProtocolVersion __v)
    {
        if(__v == null)
        {
            __nullMarshalValue.__write(__os);
        }
        else
        {
            __v.__write(__os);
        }
    }

    static public ProtocolVersion
    __read(IceInternal.BasicStream __is, ProtocolVersion __v)
    {
        if(__v == null)
        {
             __v = new ProtocolVersion();
        }
        __v.__read(__is);
        return __v;
    }
    
    private static final ProtocolVersion __nullMarshalValue = new ProtocolVersion();

    public static final long serialVersionUID = -5263487476542015228L;
}
