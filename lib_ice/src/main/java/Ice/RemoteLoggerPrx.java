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
// Generated from file `RemoteLogger.ice'
//
// Warning: do not edit this file.
//
// </auto-generated>
//

package Ice;

/**
 * The Ice remote logger interface. An application can implement a
 * RemoteLogger to receive the log messages sent to the local {@link Logger}
 * of another Ice application.
 *
 **/
public interface RemoteLoggerPrx extends Ice.ObjectPrx
{
    /**
     * init is called by {@link LoggerAdmin#attachRemoteLogger} when a
     * RemoteLogger proxy is attached.
     *
     * @param prefix The prefix of the associated local {@link Logger}
     *
     * @param logMessages Old log messages generated before "now".
     *
     * @see LoggerAdmin#attachRemoteLogger
     *
     *
     **/
    public void init(String prefix, LogMessage[] logMessages);

    /**
     * init is called by {@link LoggerAdmin#attachRemoteLogger} when a
     * RemoteLogger proxy is attached.
     *
     * @param prefix The prefix of the associated local {@link Logger}
     *
     * @param logMessages Old log messages generated before "now".
     *
     * @see LoggerAdmin#attachRemoteLogger
     *
     *
     * @param __ctx The Context map to send with the invocation.
     **/
    public void init(String prefix, LogMessage[] logMessages, java.util.Map<String, String> __ctx);

    /**
     * init is called by {@link LoggerAdmin#attachRemoteLogger} when a
     * RemoteLogger proxy is attached.
     * 
     * @param prefix The prefix of the associated local {@link Logger}
     * 
     * @param logMessages Old log messages generated before "now".
     * 
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_init(String prefix, LogMessage[] logMessages);

    /**
     * init is called by {@link LoggerAdmin#attachRemoteLogger} when a
     * RemoteLogger proxy is attached.
     * 
     * @param prefix The prefix of the associated local {@link Logger}
     * 
     * @param logMessages Old log messages generated before "now".
     * 
     * @param __ctx The Context map to send with the invocation.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_init(String prefix, LogMessage[] logMessages, java.util.Map<String, String> __ctx);

    /**
     * init is called by {@link LoggerAdmin#attachRemoteLogger} when a
     * RemoteLogger proxy is attached.
     * 
     * @param prefix The prefix of the associated local {@link Logger}
     * 
     * @param logMessages Old log messages generated before "now".
     * 
     * @param __cb The asynchronous callback object.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_init(String prefix, LogMessage[] logMessages, Ice.Callback __cb);

    /**
     * init is called by {@link LoggerAdmin#attachRemoteLogger} when a
     * RemoteLogger proxy is attached.
     * 
     * @param prefix The prefix of the associated local {@link Logger}
     * 
     * @param logMessages Old log messages generated before "now".
     * 
     * @param __ctx The Context map to send with the invocation.
     * @param __cb The asynchronous callback object.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_init(String prefix, LogMessage[] logMessages, java.util.Map<String, String> __ctx, Ice.Callback __cb);

    /**
     * init is called by {@link LoggerAdmin#attachRemoteLogger} when a
     * RemoteLogger proxy is attached.
     * 
     * @param prefix The prefix of the associated local {@link Logger}
     * 
     * @param logMessages Old log messages generated before "now".
     * 
     * @param __cb The asynchronous callback object.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_init(String prefix, LogMessage[] logMessages, Callback_RemoteLogger_init __cb);

    /**
     * init is called by {@link LoggerAdmin#attachRemoteLogger} when a
     * RemoteLogger proxy is attached.
     * 
     * @param prefix The prefix of the associated local {@link Logger}
     * 
     * @param logMessages Old log messages generated before "now".
     * 
     * @param __ctx The Context map to send with the invocation.
     * @param __cb The asynchronous callback object.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_init(String prefix, LogMessage[] logMessages, java.util.Map<String, String> __ctx, Callback_RemoteLogger_init __cb);

    /**
     * init is called by {@link LoggerAdmin#attachRemoteLogger} when a
     * RemoteLogger proxy is attached.
     * 
     * @param prefix The prefix of the associated local {@link Logger}
     * 
     * @param logMessages Old log messages generated before "now".
     * 
     * @param __responseCb The lambda response callback.
     * @param __exceptionCb The lambda exception callback.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_init(String prefix, 
                                      LogMessage[] logMessages, 
                                      IceInternal.Functional_VoidCallback __responseCb, 
                                      IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb);

    /**
     * init is called by {@link LoggerAdmin#attachRemoteLogger} when a
     * RemoteLogger proxy is attached.
     * 
     * @param prefix The prefix of the associated local {@link Logger}
     * 
     * @param logMessages Old log messages generated before "now".
     * 
     * @param __responseCb The lambda response callback.
     * @param __exceptionCb The lambda exception callback.
     * @param __sentCb The lambda sent callback.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_init(String prefix, 
                                      LogMessage[] logMessages, 
                                      IceInternal.Functional_VoidCallback __responseCb, 
                                      IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb, 
                                      IceInternal.Functional_BoolCallback __sentCb);

    /**
     * init is called by {@link LoggerAdmin#attachRemoteLogger} when a
     * RemoteLogger proxy is attached.
     * 
     * @param prefix The prefix of the associated local {@link Logger}
     * 
     * @param logMessages Old log messages generated before "now".
     * 
     * @param __ctx The Context map to send with the invocation.
     * @param __responseCb The lambda response callback.
     * @param __exceptionCb The lambda exception callback.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_init(String prefix, 
                                      LogMessage[] logMessages, 
                                      java.util.Map<String, String> __ctx, 
                                      IceInternal.Functional_VoidCallback __responseCb, 
                                      IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb);

    /**
     * init is called by {@link LoggerAdmin#attachRemoteLogger} when a
     * RemoteLogger proxy is attached.
     * 
     * @param prefix The prefix of the associated local {@link Logger}
     * 
     * @param logMessages Old log messages generated before "now".
     * 
     * @param __ctx The Context map to send with the invocation.
     * @param __responseCb The lambda response callback.
     * @param __exceptionCb The lambda exception callback.
     * @param __sentCb The lambda sent callback.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_init(String prefix, 
                                      LogMessage[] logMessages, 
                                      java.util.Map<String, String> __ctx, 
                                      IceInternal.Functional_VoidCallback __responseCb, 
                                      IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb, 
                                      IceInternal.Functional_BoolCallback __sentCb);

    /**
     * init is called by {@link LoggerAdmin#attachRemoteLogger} when a
     * RemoteLogger proxy is attached.
     * 
     * @param __result The asynchronous result object.
     * @see LoggerAdmin#attachRemoteLogger
     * 
     * 
     **/
    public void end_init(Ice.AsyncResult __result);

    /**
     * Log a {@link LogMessage}. Note that log may be called by {@link LoggerAdmin}
     * before {@link #init}.
     *
     * @param message The message to log.
     *
     * @see Logger
     *
     **/
    public void log(LogMessage message);

    /**
     * Log a {@link LogMessage}. Note that log may be called by {@link LoggerAdmin}
     * before {@link #init}.
     *
     * @param message The message to log.
     *
     * @see Logger
     *
     * @param __ctx The Context map to send with the invocation.
     **/
    public void log(LogMessage message, java.util.Map<String, String> __ctx);

    /**
     * Log a {@link LogMessage}. Note that log may be called by {@link LoggerAdmin}
     * before {@link #init}.
     * 
     * @param message The message to log.
     * 
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_log(LogMessage message);

    /**
     * Log a {@link LogMessage}. Note that log may be called by {@link LoggerAdmin}
     * before {@link #init}.
     * 
     * @param message The message to log.
     * 
     * @param __ctx The Context map to send with the invocation.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_log(LogMessage message, java.util.Map<String, String> __ctx);

    /**
     * Log a {@link LogMessage}. Note that log may be called by {@link LoggerAdmin}
     * before {@link #init}.
     * 
     * @param message The message to log.
     * 
     * @param __cb The asynchronous callback object.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_log(LogMessage message, Ice.Callback __cb);

    /**
     * Log a {@link LogMessage}. Note that log may be called by {@link LoggerAdmin}
     * before {@link #init}.
     * 
     * @param message The message to log.
     * 
     * @param __ctx The Context map to send with the invocation.
     * @param __cb The asynchronous callback object.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_log(LogMessage message, java.util.Map<String, String> __ctx, Ice.Callback __cb);

    /**
     * Log a {@link LogMessage}. Note that log may be called by {@link LoggerAdmin}
     * before {@link #init}.
     * 
     * @param message The message to log.
     * 
     * @param __cb The asynchronous callback object.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_log(LogMessage message, Callback_RemoteLogger_log __cb);

    /**
     * Log a {@link LogMessage}. Note that log may be called by {@link LoggerAdmin}
     * before {@link #init}.
     * 
     * @param message The message to log.
     * 
     * @param __ctx The Context map to send with the invocation.
     * @param __cb The asynchronous callback object.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_log(LogMessage message, java.util.Map<String, String> __ctx, Callback_RemoteLogger_log __cb);

    /**
     * Log a {@link LogMessage}. Note that log may be called by {@link LoggerAdmin}
     * before {@link #init}.
     * 
     * @param message The message to log.
     * 
     * @param __responseCb The lambda response callback.
     * @param __exceptionCb The lambda exception callback.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_log(LogMessage message, 
                                     IceInternal.Functional_VoidCallback __responseCb, 
                                     IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb);

    /**
     * Log a {@link LogMessage}. Note that log may be called by {@link LoggerAdmin}
     * before {@link #init}.
     * 
     * @param message The message to log.
     * 
     * @param __responseCb The lambda response callback.
     * @param __exceptionCb The lambda exception callback.
     * @param __sentCb The lambda sent callback.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_log(LogMessage message, 
                                     IceInternal.Functional_VoidCallback __responseCb, 
                                     IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb, 
                                     IceInternal.Functional_BoolCallback __sentCb);

    /**
     * Log a {@link LogMessage}. Note that log may be called by {@link LoggerAdmin}
     * before {@link #init}.
     * 
     * @param message The message to log.
     * 
     * @param __ctx The Context map to send with the invocation.
     * @param __responseCb The lambda response callback.
     * @param __exceptionCb The lambda exception callback.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_log(LogMessage message, 
                                     java.util.Map<String, String> __ctx, 
                                     IceInternal.Functional_VoidCallback __responseCb, 
                                     IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb);

    /**
     * Log a {@link LogMessage}. Note that log may be called by {@link LoggerAdmin}
     * before {@link #init}.
     * 
     * @param message The message to log.
     * 
     * @param __ctx The Context map to send with the invocation.
     * @param __responseCb The lambda response callback.
     * @param __exceptionCb The lambda exception callback.
     * @param __sentCb The lambda sent callback.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_log(LogMessage message, 
                                     java.util.Map<String, String> __ctx, 
                                     IceInternal.Functional_VoidCallback __responseCb, 
                                     IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb, 
                                     IceInternal.Functional_BoolCallback __sentCb);

    /**
     * Log a {@link LogMessage}. Note that log may be called by {@link LoggerAdmin}
     * before {@link #init}.
     * 
     * @param __result The asynchronous result object.
     * @see Logger
     * 
     **/
    public void end_log(Ice.AsyncResult __result);
}