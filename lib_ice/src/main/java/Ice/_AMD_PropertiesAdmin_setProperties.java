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
// Generated from file `PropertiesAdmin.ice'
//
// Warning: do not edit this file.
//
// </auto-generated>
//

package Ice;

final class _AMD_PropertiesAdmin_setProperties extends IceInternal.IncomingAsync implements AMD_PropertiesAdmin_setProperties
{
    public _AMD_PropertiesAdmin_setProperties(IceInternal.Incoming in)
    {
        super(in);
    }

    public void ice_response()
    {
        if(__validateResponse(true))
        {
            __writeEmptyParams();
            __response();
        }
    }
}