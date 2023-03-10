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

public final class DoubleSeqHelper
{
    public static void
    write(IceInternal.BasicStream __os, double[] __v)
    {
        __os.writeDoubleSeq(__v);
    }

    public static double[]
    read(IceInternal.BasicStream __is)
    {
        double[] __v;
        __v = __is.readDoubleSeq();
        return __v;
    }

    public static void write(Ice.OutputStream __outS, double[] __v)
    {
        __outS.writeDoubleSeq(__v);
    }

    public static double[] read(Ice.InputStream __inS)
    {
        double[] __v;
        __v = __inS.readDoubleSeq();
        return __v;
    }

    public static Ice.OptionalFormat optionalFormat()
    {
        return Ice.OptionalFormat.VSize;
    }
}
