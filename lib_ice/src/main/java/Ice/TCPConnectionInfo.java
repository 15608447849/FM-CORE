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

/**
 * Provides access to the connection details of a TCP connection
 *
 **/
public class TCPConnectionInfo extends IPConnectionInfo
{
    public TCPConnectionInfo()
    {
        super();
    }

    public TCPConnectionInfo(boolean incoming, String adapterName, String connectionId, int rcvSize, int sndSize, String localAddress, int localPort, String remoteAddress, int remotePort)
    {
        super(incoming, adapterName, connectionId, rcvSize, sndSize, localAddress, localPort, remoteAddress, remotePort);
    }

    public TCPConnectionInfo
    clone()
    {
        return (TCPConnectionInfo)super.clone();
    }

    public static final long serialVersionUID = -7368309784238898141L;
}
