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
 * Provides access to the connection details of an IP connection
 *
 **/
public class IPConnectionInfo extends ConnectionInfo
{
    public IPConnectionInfo()
    {
        super();
        localAddress = "";
        localPort = -1;
        remoteAddress = "";
        remotePort = -1;
    }

    public IPConnectionInfo(boolean incoming, String adapterName, String connectionId, int rcvSize, int sndSize, String localAddress, int localPort, String remoteAddress, int remotePort)
    {
        super(incoming, adapterName, connectionId, rcvSize, sndSize);
        this.localAddress = localAddress;
        this.localPort = localPort;
        this.remoteAddress = remoteAddress;
        this.remotePort = remotePort;
    }

    /**
     * The local address.
     **/
    public String localAddress;

    /**
     * The local port.
     **/
    public int localPort;

    /**
     * The remote address.
     **/
    public String remoteAddress;

    /**
     * The remote port.
     **/
    public int remotePort;

    public IPConnectionInfo
    clone()
    {
        return (IPConnectionInfo)super.clone();
    }

    public static final long serialVersionUID = 8533006463792298184L;
}
