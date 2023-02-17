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
// Generated from file `Router.ice'
//
// Warning: do not edit this file.
//
// </auto-generated>
//

package Ice;

/**
 * This inferface should be implemented by services implementing the
 * Ice::Router interface. It should be advertised through an Ice
 * object with the identity `Ice/RouterFinder'. This allows clients to
 * retrieve the router proxy with just the endpoint information of the
 * service.
 *
 **/
public interface RouterFinder extends Ice.Object,
                                      _RouterFinderOperations, _RouterFinderOperationsNC
{
    public static final String ice_staticId = "::Ice::RouterFinder";

    public static final long serialVersionUID = -5251473827376119829L;
}
