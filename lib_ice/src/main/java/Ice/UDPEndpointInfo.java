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
// Generated from file `Endpoint.ice'
//
// Warning: do not edit this file.
//
// </auto-generated>
//

package Ice;

/**
 * Provides access to an UDP endpoint information.
 *
 * @see Endpoint
 *
 **/
public abstract class UDPEndpointInfo extends IPEndpointInfo
{
    public UDPEndpointInfo()
    {
        super();
        mcastInterface = "";
    }

    public UDPEndpointInfo(int timeout, boolean compress, String host, int port, String sourceAddress, String mcastInterface, int mcastTtl)
    {
        super(timeout, compress, host, port, sourceAddress);
        this.mcastInterface = mcastInterface;
        this.mcastTtl = mcastTtl;
    }

    /**
     * The multicast interface.
     *
     **/
    public String mcastInterface;

    /**
     * The multicast time-to-live (or hops).
     *
     **/
    public int mcastTtl;

    public UDPEndpointInfo
    clone()
    {
        return (UDPEndpointInfo)super.clone();
    }

    public static final long serialVersionUID = 6545930812316183136L;
}
