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
 * getLog retrieves log messages recently logged.
 * 
 **/

public interface _Callback_LoggerAdmin_getLog extends Ice.TwowayCallback
{
    public void response(LogMessage[] __ret, String prefix);
}
