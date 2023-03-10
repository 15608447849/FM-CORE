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

/**
 * The PropertiesAdmin interface provides remote access to the properties
 * of a communicator.
 *
 **/
public interface _PropertiesAdminOperations
{
    /**
     * Get a property by key. If the property is not set, an empty
     * string is returned.
     *
     * @param key The property key.
     *
     * @param __current The Current object for the invocation.
     * @return The property value.
     *
     **/
    String getProperty(String key, Ice.Current __current);

    /**
     * Get all properties whose keys begin with <em>prefix</em>. If
     * <em>prefix</em> is an empty string then all properties are returned.
     *
     * @param prefix The prefix to search for (empty string if none).
     * @param __current The Current object for the invocation.
     * @return The matching property set.
     *
     **/
    java.util.Map<java.lang.String, java.lang.String> getPropertiesForPrefix(String prefix, Ice.Current __current);

    /**
     * Update the communicator's properties with the given property set.
     * 
     * @param __cb The callback object for the operation.
     * @param newProperties Properties to be added, changed, or removed.
     * If an entry in <em>newProperties</em> matches the name of an existing property,
     * that property's value is replaced with the new value. If the new value
     * is an empty string, the property is removed. Any existing properties
     * that are not modified or removed by the entries in newProperties are
     * retained with their original values.
     * 
     * @param __current The Current object for the invocation.
     **/
    void setProperties_async(AMD_PropertiesAdmin_setProperties __cb, java.util.Map<java.lang.String, java.lang.String> newProperties, Ice.Current __current);
}
