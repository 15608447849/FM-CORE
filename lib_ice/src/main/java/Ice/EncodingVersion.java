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
 * A version structure for the encoding version.
 *
 **/
public class EncodingVersion implements java.lang.Cloneable, java.io.Serializable
{
    public byte major;

    public byte minor;

    public EncodingVersion()
    {
    }

    public EncodingVersion(byte major, byte minor)
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
        EncodingVersion _r = null;
        if(rhs instanceof EncodingVersion)
        {
            _r = (EncodingVersion)rhs;
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
        __h = IceInternal.HashUtil.hashAdd(__h, "::Ice::EncodingVersion");
        __h = IceInternal.HashUtil.hashAdd(__h, major);
        __h = IceInternal.HashUtil.hashAdd(__h, minor);
        return __h;
    }

    public EncodingVersion
    clone()
    {
        EncodingVersion c = null;
        try
        {
            c = (EncodingVersion)super.clone();
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
    __write(IceInternal.BasicStream __os, EncodingVersion __v)
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

    static public EncodingVersion
    __read(IceInternal.BasicStream __is, EncodingVersion __v)
    {
        if(__v == null)
        {
             __v = new EncodingVersion();
        }
        __v.__read(__is);
        return __v;
    }
    
    private static final EncodingVersion __nullMarshalValue = new EncodingVersion();

    public static final long serialVersionUID = 4211648668174918225L;
}
