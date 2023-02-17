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

public final class LocatorRegistryHolder extends Ice.ObjectHolderBase<LocatorRegistry>
{
    public
    LocatorRegistryHolder()
    {
    }

    public
    LocatorRegistryHolder(LocatorRegistry value)
    {
        this.value = value;
    }

    public void
    patch(Ice.Object v)
    {
        if(v == null || v instanceof LocatorRegistry)
        {
            value = (LocatorRegistry)v;
        }
        else
        {
            IceInternal.Ex.throwUOE(type(), v);
        }
    }

    public String
    type()
    {
        return _LocatorRegistryDisp.ice_staticId();
    }
}