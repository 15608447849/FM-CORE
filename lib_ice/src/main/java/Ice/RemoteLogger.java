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
// Generated from file `RemoteLogger.ice'
//
// Warning: do not edit this file.
//
// </auto-generated>
//

package Ice;

/**
 * The Ice remote logger interface. An application can implement a
 * RemoteLogger to receive the log messages sent to the local {@link Logger}
 * of another Ice application.
 *
 **/
public interface RemoteLogger extends Ice.Object,
                                      _RemoteLoggerOperations, _RemoteLoggerOperationsNC
{
    public static final String ice_staticId = "::Ice::RemoteLogger";

    public static final long serialVersionUID = -3739177759224702622L;
}
