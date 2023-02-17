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

public final class LongSeqHelper
{
    public static void
    write(IceInternal.BasicStream __os, long[] __v)
    {
        __os.writeLongSeq(__v);
    }

    public static long[]
    read(IceInternal.BasicStream __is)
    {
        long[] __v;
        __v = __is.readLongSeq();
        return __v;
    }

    public static void write(Ice.OutputStream __outS, long[] __v)
    {
        __outS.writeLongSeq(__v);
    }

    public static long[] read(Ice.InputStream __inS)
    {
        long[] __v;
        __v = __inS.readLongSeq();
        return __v;
    }

    public static Ice.OptionalFormat optionalFormat()
    {
        return Ice.OptionalFormat.VSize;
    }
}
