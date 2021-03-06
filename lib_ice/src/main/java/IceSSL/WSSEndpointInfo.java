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
// Generated from file `EndpointInfo.ice'
//
// Warning: do not edit this file.
//
// </auto-generated>
//

package IceSSL;

/**
 * Provides access to a secure WebSocket endpoint information.
 *
 **/
public abstract class WSSEndpointInfo extends EndpointInfo
{
    public WSSEndpointInfo()
    {
        super();
        resource = "";
    }

    public WSSEndpointInfo(int timeout, boolean compress, String host, int port, String sourceAddress, String resource)
    {
        super(timeout, compress, host, port, sourceAddress);
        this.resource = resource;
    }

    /**
     * The URI configured with the endpoint.
     *
     **/
    public String resource;

    public WSSEndpointInfo
    clone()
    {
        return (WSSEndpointInfo)super.clone();
    }

    public static final long serialVersionUID = 7694742894210807083L;
}
