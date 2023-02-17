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

public final class StringIntDictHelper
{
    public static void
    write(IceInternal.BasicStream __os, java.util.Map<java.lang.String, java.lang.Integer> __v)
    {
        if(__v == null)
        {
            __os.writeSize(0);
        }
        else
        {
            __os.writeSize(__v.size());
            for(java.util.Map.Entry<java.lang.String, java.lang.Integer> __e : __v.entrySet())
            {
                __os.writeString(__e.getKey());
                __os.writeInt(__e.getValue());
            }
        }
    }

    public static java.util.Map<java.lang.String, java.lang.Integer>
    read(IceInternal.BasicStream __is)
    {
        java.util.Map<java.lang.String, java.lang.Integer> __v;
        __v = new java.util.HashMap<java.lang.String, java.lang.Integer>();
        int __sz0 = __is.readSize();
        for(int __i0 = 0; __i0 < __sz0; __i0++)
        {
            String __key;
            __key = __is.readString();
            int __value;
            __value = __is.readInt();
            __v.put(__key, __value);
        }
        return __v;
    }
}