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
// Generated from file `Process.ice'
//
// Warning: do not edit this file.
//
// </auto-generated>
//

package Ice;

/**
 * An administrative interface for process management. Managed servers must
 * implement this interface.
 *
 * <p class="Note">A servant implementing this interface is a potential target
 * for denial-of-service attacks, therefore proper security precautions
 * should be taken. For example, the servant can use a UUID to make its
 * identity harder to guess, and be registered in an object adapter with
 * a secured endpoint.
 *
 **/
public interface _ProcessOperations
{
    /**
     * Initiate a graceful shut-down.
     *
     * @see Communicator#shutdown
     *
     * @param __current The Current object for the invocation.
     **/
    void shutdown(Ice.Current __current);

    /**
     * Write a message on the process' stdout or stderr.
     *
     * @param message The message.
     *
     * @param fd 1 for stdout, 2 for stderr.
     *
     * @param __current The Current object for the invocation.
     **/
    void writeMessage(String message, int fd, Ice.Current __current);
}
