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
// Generated from file `Instrumentation.ice'
//
// Warning: do not edit this file.
//
// </auto-generated>
//

package Ice.Instrumentation;

/**
 * The object observer interface used by instrumented objects to
 * notify the observer of their existence.
 *
 **/
public interface _ObserverOperationsNC
{
    /**
     * This method is called when the instrumented object is created
     * or when the observer is attached to an existing object.
     *
     **/
    void attach();

    /**
     * This method is called when the instrumented object is destroyed
     * and as a result the observer detached from the object.
     *
     **/
    void detach();

    /**
     * Notification of a failure.
     *
     * @param exceptionName The name of the exception.
     *
     **/
    void failed(String exceptionName);
}
