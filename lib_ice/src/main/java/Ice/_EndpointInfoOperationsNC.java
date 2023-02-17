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
 * Base class providing access to the endpoint details.
 *
 **/
public interface _EndpointInfoOperationsNC
{
    /**
     * Returns the type of the endpoint.
     *
     **/
    short type();

    /**
     * Returns true if this endpoint is a datagram endpoint.
     *
     **/
    boolean datagram();

    /**
     * Returns true if this endpoint is a secure endpoint.
     *
     **/
    boolean secure();
}