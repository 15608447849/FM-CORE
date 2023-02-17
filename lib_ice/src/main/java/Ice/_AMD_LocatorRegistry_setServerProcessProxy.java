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
// Generated from file `Locator.ice'
//
// Warning: do not edit this file.
//
// </auto-generated>
//

package Ice;

final class _AMD_LocatorRegistry_setServerProcessProxy extends IceInternal.IncomingAsync implements AMD_LocatorRegistry_setServerProcessProxy
{
    public _AMD_LocatorRegistry_setServerProcessProxy(IceInternal.Incoming in)
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

    public void ice_exception(java.lang.Exception ex)
    {
        try
        {
            throw ex;
        }
        catch(ServerNotFoundException __ex)
        {
            if(__validateResponse(false))
            {
                __writeUserException(__ex, Ice.FormatType.DefaultFormat);
                __response();
            }
        }
        catch(java.lang.Exception __ex)
        {
            super.ice_exception(__ex);
        }
    }
}
