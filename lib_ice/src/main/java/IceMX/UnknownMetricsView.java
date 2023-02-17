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
 * Raised if a metrics view cannot be found.
 *
 **/
public class UnknownMetricsView extends Ice.UserException
{
    public UnknownMetricsView()
    {
    }

    public UnknownMetricsView(Throwable __cause)
    {
        super(__cause);
    }

    public String
    ice_name()
    {
        return "IceMX::UnknownMetricsView";
    }

    protected void
    __writeImpl(IceInternal.BasicStream __os)
    {
        __os.startWriteSlice("::IceMX::UnknownMetricsView", -1, true);
        __os.endWriteSlice();
    }

    protected void
    __readImpl(IceInternal.BasicStream __is)
    {
        __is.startReadSlice();
        __is.endReadSlice();
    }

    public static final long serialVersionUID = -4401900178865435057L;
}