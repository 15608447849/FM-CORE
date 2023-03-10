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
// Generated from file `Logger.ice'
//
// Warning: do not edit this file.
//
// </auto-generated>
//

package Ice;

/**
 * The Ice message logger. Applications can provide their own logger
 * by implementing this interface and installing it in a communicator.
 *
 **/
public interface Logger
{
    /**
     * Print a message. The message is printed literally, without
     * any decorations such as executable name or time stamp.
     *
     **/
    void print(String message);

    /**
     * Log a trace message.
     *
     * @param category The trace category.
     *
     * @param message The trace message to log.
     *
     **/
    void trace(String category, String message);

    /**
     * Log a warning message.
     *
     * @param message The warning message to log.
     *
     * @see #error
     *
     **/
    void warning(String message);

    /**
     * Log an error message.
     *
     * @param message The error message to log.
     *
     * @see #warning
     *
     **/
    void error(String message);

    /**
     * Returns this logger's prefix.
     *
     * @return The prefix.
     *
     **/
    String getPrefix();

    /**
     * Returns a clone of the logger with a new prefix.
     *
     * @param prefix The new prefix for the logger.
     *
     **/
    Logger cloneWithPrefix(String prefix);

    public static final long serialVersionUID = -354672974498205402L;
}
