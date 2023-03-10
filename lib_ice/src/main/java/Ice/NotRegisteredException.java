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
// Generated from file `LocalException.ice'
//
// Warning: do not edit this file.
//
// </auto-generated>
//

package Ice;

/**
 * An attempt was made to find or deregister something that is not
 * registered with the Ice run time or Ice locator.
 *
 * This exception is raised if an attempt is made to remove a servant,
 * servant locator, facet, object factory, plug-in, object adapter,
 * object, or user exception factory that is not currently registered.
 *
 * It's also raised if the Ice locator can't find an object or object
 * adapter when resolving an indirect proxy or when an object adapter
 * is activated.
 *
 **/
public class NotRegisteredException extends Ice.LocalException
{
    public NotRegisteredException()
    {
        kindOfObject = "";
        id = "";
    }

    public NotRegisteredException(Throwable __cause)
    {
        super(__cause);
        kindOfObject = "";
        id = "";
    }

    public NotRegisteredException(String kindOfObject, String id)
    {
        this.kindOfObject = kindOfObject;
        this.id = id;
    }

    public NotRegisteredException(String kindOfObject, String id, Throwable __cause)
    {
        super(__cause);
        this.kindOfObject = kindOfObject;
        this.id = id;
    }

    public String
    ice_name()
    {
        return "Ice::NotRegisteredException";
    }

    /**
     * The kind of object that could not be removed: "servant",
     * "servant locator", "object factory", "plug-in",
     * "object adapter", "object", or "user exception factory".
     *
     **/
    public String kindOfObject;

    /**
     * The ID (or name) of the object that could not be removed.
     *
     **/
    public String id;

    public static final long serialVersionUID = 3335358291266771447L;
}
