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
 * The user-level interface to an endpoint.
 *
 **/
public interface _EndpointOperationsNC
{
    /**
     * Return a string representation of the endpoint.
     *
     * @return The string representation of the endpoint.
     *
     **/
    String _toString();

    /**
     * Returns the endpoint information.
     *
     * @return The endpoint information class.
     *
     **/
    EndpointInfo getInfo();
}
