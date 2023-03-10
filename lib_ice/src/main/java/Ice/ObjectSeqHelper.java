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
// Generated from file `BuiltinSequences.ice'
//
// Warning: do not edit this file.
//
// </auto-generated>
//

package Ice;

public final class ObjectSeqHelper
{
    public static void
    write(IceInternal.BasicStream __os, Ice.Object[] __v)
    {
        if(__v == null)
        {
            __os.writeSize(0);
        }
        else
        {
            __os.writeSize(__v.length);
            for(int __i0 = 0; __i0 < __v.length; __i0++)
            {
                __os.writeObject(__v[__i0]);
            }
        }
    }

    public static Ice.Object[]
    read(IceInternal.BasicStream __is)
    {
        Ice.Object[] __v;
        final int __len0 = __is.readAndCheckSeqSize(1);
        final String __type0 = Ice.ObjectImpl.ice_staticId();
        __v = new Ice.Object[__len0];
        for(int __i0 = 0; __i0 < __len0; __i0++)
        {
            __is.readObject(new IceInternal.SequencePatcher(__v, Ice.Object.class, __type0, __i0));
        }
        return __v;
    }

    public static void write(Ice.OutputStream __outS, Ice.Object[] __v)
    {
        if(__v == null)
        {
            __outS.writeSize(0);
        }
        else
        {
            __outS.writeSize(__v.length);
            for(int __i0 = 0; __i0 < __v.length; __i0++)
            {
                __outS.writeObject(__v[__i0]);
            }
        }
    }

    public static Ice.Object[] read(Ice.InputStream __inS)
    {
        Ice.Object[] __v;
        final int __len0 = __inS.readAndCheckSeqSize(1);
        final String __type0 = Ice.ObjectImpl.ice_staticId();
        __v = new Ice.Object[__len0];
        for(int __i0 = 0; __i0 < __len0; __i0++)
        {
            __inS.readObject(new IceInternal.SequencePatcher(__v, Ice.Object.class, __type0, __i0));
        }
        return __v;
    }

    public static Ice.OptionalFormat optionalFormat()
    {
        return Ice.OptionalFormat.FSize;
    }
}
