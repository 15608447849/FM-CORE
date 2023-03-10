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
 * The interface of the admin object that allows an Ice application the attach its
 * {@link RemoteLogger} to the {@link Logger} of this admin object's Ice communicator.
 *
 **/
public interface LoggerAdminPrx extends Ice.ObjectPrx
{
    /**
     * attachRemoteLogger is called to attach a {@link RemoteLogger} object to
     * the local {@link Logger}.
     * attachRemoteLogger calls init on the provided {@link RemoteLogger} proxy.
     *
     * @param prx A proxy to the remote logger.
     *
     * @param messageTypes The list of message types that the remote logger wishes to receive.
     * An empty list means no filtering (send all message types).
     *
     * @param traceCategories The categories of traces that the remote logger wishes to receive.
     * This parameter is ignored if messageTypes is not empty and does not include trace.
     * An empty list means no filtering (send all trace categories).
     *
     * @param messageMax The maximum number of log messages (of all types) to be provided
     * to {@link RemoteLogger#init}. A negative value requests all messages available.
     *
     * @throws RemoteLoggerAlreadyAttachedException Raised if this remote logger is already
     * attached to this admin object.
     *
     **/
    public void attachRemoteLogger(RemoteLoggerPrx prx, LogMessageType[] messageTypes, String[] traceCategories, int messageMax)
        throws RemoteLoggerAlreadyAttachedException;

    /**
     * attachRemoteLogger is called to attach a {@link RemoteLogger} object to
     * the local {@link Logger}.
     * attachRemoteLogger calls init on the provided {@link RemoteLogger} proxy.
     *
     * @param prx A proxy to the remote logger.
     *
     * @param messageTypes The list of message types that the remote logger wishes to receive.
     * An empty list means no filtering (send all message types).
     *
     * @param traceCategories The categories of traces that the remote logger wishes to receive.
     * This parameter is ignored if messageTypes is not empty and does not include trace.
     * An empty list means no filtering (send all trace categories).
     *
     * @param messageMax The maximum number of log messages (of all types) to be provided
     * to {@link RemoteLogger#init}. A negative value requests all messages available.
     *
     * @throws RemoteLoggerAlreadyAttachedException Raised if this remote logger is already
     * attached to this admin object.
     *
     * @param __ctx The Context map to send with the invocation.
     **/
    public void attachRemoteLogger(RemoteLoggerPrx prx, LogMessageType[] messageTypes, String[] traceCategories, int messageMax, java.util.Map<String, String> __ctx)
        throws RemoteLoggerAlreadyAttachedException;

    /**
     * attachRemoteLogger is called to attach a {@link RemoteLogger} object to
     * the local {@link Logger}.
     * attachRemoteLogger calls init on the provided {@link RemoteLogger} proxy.
     * 
     * @param prx A proxy to the remote logger.
     * 
     * @param messageTypes The list of message types that the remote logger wishes to receive.
     * An empty list means no filtering (send all message types).
     * 
     * @param traceCategories The categories of traces that the remote logger wishes to receive.
     * This parameter is ignored if messageTypes is not empty and does not include trace.
     * An empty list means no filtering (send all trace categories).
     * 
     * @param messageMax The maximum number of log messages (of all types) to be provided
     * to {@link RemoteLogger#init}. A negative value requests all messages available.
     * 
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_attachRemoteLogger(RemoteLoggerPrx prx, LogMessageType[] messageTypes, String[] traceCategories, int messageMax);

    /**
     * attachRemoteLogger is called to attach a {@link RemoteLogger} object to
     * the local {@link Logger}.
     * attachRemoteLogger calls init on the provided {@link RemoteLogger} proxy.
     * 
     * @param prx A proxy to the remote logger.
     * 
     * @param messageTypes The list of message types that the remote logger wishes to receive.
     * An empty list means no filtering (send all message types).
     * 
     * @param traceCategories The categories of traces that the remote logger wishes to receive.
     * This parameter is ignored if messageTypes is not empty and does not include trace.
     * An empty list means no filtering (send all trace categories).
     * 
     * @param messageMax The maximum number of log messages (of all types) to be provided
     * to {@link RemoteLogger#init}. A negative value requests all messages available.
     * 
     * @param __ctx The Context map to send with the invocation.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_attachRemoteLogger(RemoteLoggerPrx prx, LogMessageType[] messageTypes, String[] traceCategories, int messageMax, java.util.Map<String, String> __ctx);

    /**
     * attachRemoteLogger is called to attach a {@link RemoteLogger} object to
     * the local {@link Logger}.
     * attachRemoteLogger calls init on the provided {@link RemoteLogger} proxy.
     * 
     * @param prx A proxy to the remote logger.
     * 
     * @param messageTypes The list of message types that the remote logger wishes to receive.
     * An empty list means no filtering (send all message types).
     * 
     * @param traceCategories The categories of traces that the remote logger wishes to receive.
     * This parameter is ignored if messageTypes is not empty and does not include trace.
     * An empty list means no filtering (send all trace categories).
     * 
     * @param messageMax The maximum number of log messages (of all types) to be provided
     * to {@link RemoteLogger#init}. A negative value requests all messages available.
     * 
     * @param __cb The asynchronous callback object.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_attachRemoteLogger(RemoteLoggerPrx prx, LogMessageType[] messageTypes, String[] traceCategories, int messageMax, Ice.Callback __cb);

    /**
     * attachRemoteLogger is called to attach a {@link RemoteLogger} object to
     * the local {@link Logger}.
     * attachRemoteLogger calls init on the provided {@link RemoteLogger} proxy.
     * 
     * @param prx A proxy to the remote logger.
     * 
     * @param messageTypes The list of message types that the remote logger wishes to receive.
     * An empty list means no filtering (send all message types).
     * 
     * @param traceCategories The categories of traces that the remote logger wishes to receive.
     * This parameter is ignored if messageTypes is not empty and does not include trace.
     * An empty list means no filtering (send all trace categories).
     * 
     * @param messageMax The maximum number of log messages (of all types) to be provided
     * to {@link RemoteLogger#init}. A negative value requests all messages available.
     * 
     * @param __ctx The Context map to send with the invocation.
     * @param __cb The asynchronous callback object.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_attachRemoteLogger(RemoteLoggerPrx prx, LogMessageType[] messageTypes, String[] traceCategories, int messageMax, java.util.Map<String, String> __ctx, Ice.Callback __cb);

    /**
     * attachRemoteLogger is called to attach a {@link RemoteLogger} object to
     * the local {@link Logger}.
     * attachRemoteLogger calls init on the provided {@link RemoteLogger} proxy.
     * 
     * @param prx A proxy to the remote logger.
     * 
     * @param messageTypes The list of message types that the remote logger wishes to receive.
     * An empty list means no filtering (send all message types).
     * 
     * @param traceCategories The categories of traces that the remote logger wishes to receive.
     * This parameter is ignored if messageTypes is not empty and does not include trace.
     * An empty list means no filtering (send all trace categories).
     * 
     * @param messageMax The maximum number of log messages (of all types) to be provided
     * to {@link RemoteLogger#init}. A negative value requests all messages available.
     * 
     * @param __cb The asynchronous callback object.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_attachRemoteLogger(RemoteLoggerPrx prx, LogMessageType[] messageTypes, String[] traceCategories, int messageMax, Callback_LoggerAdmin_attachRemoteLogger __cb);

    /**
     * attachRemoteLogger is called to attach a {@link RemoteLogger} object to
     * the local {@link Logger}.
     * attachRemoteLogger calls init on the provided {@link RemoteLogger} proxy.
     * 
     * @param prx A proxy to the remote logger.
     * 
     * @param messageTypes The list of message types that the remote logger wishes to receive.
     * An empty list means no filtering (send all message types).
     * 
     * @param traceCategories The categories of traces that the remote logger wishes to receive.
     * This parameter is ignored if messageTypes is not empty and does not include trace.
     * An empty list means no filtering (send all trace categories).
     * 
     * @param messageMax The maximum number of log messages (of all types) to be provided
     * to {@link RemoteLogger#init}. A negative value requests all messages available.
     * 
     * @param __ctx The Context map to send with the invocation.
     * @param __cb The asynchronous callback object.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_attachRemoteLogger(RemoteLoggerPrx prx, LogMessageType[] messageTypes, String[] traceCategories, int messageMax, java.util.Map<String, String> __ctx, Callback_LoggerAdmin_attachRemoteLogger __cb);

    /**
     * attachRemoteLogger is called to attach a {@link RemoteLogger} object to
     * the local {@link Logger}.
     * attachRemoteLogger calls init on the provided {@link RemoteLogger} proxy.
     * 
     * @param prx A proxy to the remote logger.
     * 
     * @param messageTypes The list of message types that the remote logger wishes to receive.
     * An empty list means no filtering (send all message types).
     * 
     * @param traceCategories The categories of traces that the remote logger wishes to receive.
     * This parameter is ignored if messageTypes is not empty and does not include trace.
     * An empty list means no filtering (send all trace categories).
     * 
     * @param messageMax The maximum number of log messages (of all types) to be provided
     * to {@link RemoteLogger#init}. A negative value requests all messages available.
     * 
     * @param __responseCb The lambda response callback.
     * @param __userExceptionCb The lambda user exception callback.
     * @param __exceptionCb The lambda exception callback.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_attachRemoteLogger(RemoteLoggerPrx prx, 
                                                    LogMessageType[] messageTypes, 
                                                    String[] traceCategories, 
                                                    int messageMax, 
                                                    IceInternal.Functional_VoidCallback __responseCb, 
                                                    IceInternal.Functional_GenericCallback1<Ice.UserException> __userExceptionCb, 
                                                    IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb);

    /**
     * attachRemoteLogger is called to attach a {@link RemoteLogger} object to
     * the local {@link Logger}.
     * attachRemoteLogger calls init on the provided {@link RemoteLogger} proxy.
     * 
     * @param prx A proxy to the remote logger.
     * 
     * @param messageTypes The list of message types that the remote logger wishes to receive.
     * An empty list means no filtering (send all message types).
     * 
     * @param traceCategories The categories of traces that the remote logger wishes to receive.
     * This parameter is ignored if messageTypes is not empty and does not include trace.
     * An empty list means no filtering (send all trace categories).
     * 
     * @param messageMax The maximum number of log messages (of all types) to be provided
     * to {@link RemoteLogger#init}. A negative value requests all messages available.
     * 
     * @param __responseCb The lambda response callback.
     * @param __userExceptionCb The lambda user exception callback.
     * @param __exceptionCb The lambda exception callback.
     * @param __sentCb The lambda sent callback.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_attachRemoteLogger(RemoteLoggerPrx prx, 
                                                    LogMessageType[] messageTypes, 
                                                    String[] traceCategories, 
                                                    int messageMax, 
                                                    IceInternal.Functional_VoidCallback __responseCb, 
                                                    IceInternal.Functional_GenericCallback1<Ice.UserException> __userExceptionCb, 
                                                    IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb, 
                                                    IceInternal.Functional_BoolCallback __sentCb);

    /**
     * attachRemoteLogger is called to attach a {@link RemoteLogger} object to
     * the local {@link Logger}.
     * attachRemoteLogger calls init on the provided {@link RemoteLogger} proxy.
     * 
     * @param prx A proxy to the remote logger.
     * 
     * @param messageTypes The list of message types that the remote logger wishes to receive.
     * An empty list means no filtering (send all message types).
     * 
     * @param traceCategories The categories of traces that the remote logger wishes to receive.
     * This parameter is ignored if messageTypes is not empty and does not include trace.
     * An empty list means no filtering (send all trace categories).
     * 
     * @param messageMax The maximum number of log messages (of all types) to be provided
     * to {@link RemoteLogger#init}. A negative value requests all messages available.
     * 
     * @param __ctx The Context map to send with the invocation.
     * @param __responseCb The lambda response callback.
     * @param __userExceptionCb The lambda user exception callback.
     * @param __exceptionCb The lambda exception callback.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_attachRemoteLogger(RemoteLoggerPrx prx, 
                                                    LogMessageType[] messageTypes, 
                                                    String[] traceCategories, 
                                                    int messageMax, 
                                                    java.util.Map<String, String> __ctx, 
                                                    IceInternal.Functional_VoidCallback __responseCb, 
                                                    IceInternal.Functional_GenericCallback1<Ice.UserException> __userExceptionCb, 
                                                    IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb);

    /**
     * attachRemoteLogger is called to attach a {@link RemoteLogger} object to
     * the local {@link Logger}.
     * attachRemoteLogger calls init on the provided {@link RemoteLogger} proxy.
     * 
     * @param prx A proxy to the remote logger.
     * 
     * @param messageTypes The list of message types that the remote logger wishes to receive.
     * An empty list means no filtering (send all message types).
     * 
     * @param traceCategories The categories of traces that the remote logger wishes to receive.
     * This parameter is ignored if messageTypes is not empty and does not include trace.
     * An empty list means no filtering (send all trace categories).
     * 
     * @param messageMax The maximum number of log messages (of all types) to be provided
     * to {@link RemoteLogger#init}. A negative value requests all messages available.
     * 
     * @param __ctx The Context map to send with the invocation.
     * @param __responseCb The lambda response callback.
     * @param __userExceptionCb The lambda user exception callback.
     * @param __exceptionCb The lambda exception callback.
     * @param __sentCb The lambda sent callback.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_attachRemoteLogger(RemoteLoggerPrx prx, 
                                                    LogMessageType[] messageTypes, 
                                                    String[] traceCategories, 
                                                    int messageMax, 
                                                    java.util.Map<String, String> __ctx, 
                                                    IceInternal.Functional_VoidCallback __responseCb, 
                                                    IceInternal.Functional_GenericCallback1<Ice.UserException> __userExceptionCb, 
                                                    IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb, 
                                                    IceInternal.Functional_BoolCallback __sentCb);

    /**
     * attachRemoteLogger is called to attach a {@link RemoteLogger} object to
     * the local {@link Logger}.
     * attachRemoteLogger calls init on the provided {@link RemoteLogger} proxy.
     * 
     * @param __result The asynchronous result object.
     * @throws RemoteLoggerAlreadyAttachedException Raised if this remote logger is already
     * attached to this admin object.
     * 
     **/
    public void end_attachRemoteLogger(Ice.AsyncResult __result)
        throws RemoteLoggerAlreadyAttachedException;

    /**
     * detachRemoteLogger is called to detach a {@link RemoteLogger} object from
     * the local {@link Logger}.
     *
     * @param prx A proxy to the remote logger.
     *
     * @return True if the provided remote logger proxy was detached, and false otherwise.
     *
     **/
    public boolean detachRemoteLogger(RemoteLoggerPrx prx);

    /**
     * detachRemoteLogger is called to detach a {@link RemoteLogger} object from
     * the local {@link Logger}.
     *
     * @param prx A proxy to the remote logger.
     *
     * @param __ctx The Context map to send with the invocation.
     * @return True if the provided remote logger proxy was detached, and false otherwise.
     *
     **/
    public boolean detachRemoteLogger(RemoteLoggerPrx prx, java.util.Map<String, String> __ctx);

    /**
     * detachRemoteLogger is called to detach a {@link RemoteLogger} object from
     * the local {@link Logger}.
     * 
     * @param prx A proxy to the remote logger.
     * 
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_detachRemoteLogger(RemoteLoggerPrx prx);

    /**
     * detachRemoteLogger is called to detach a {@link RemoteLogger} object from
     * the local {@link Logger}.
     * 
     * @param prx A proxy to the remote logger.
     * 
     * @param __ctx The Context map to send with the invocation.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_detachRemoteLogger(RemoteLoggerPrx prx, java.util.Map<String, String> __ctx);

    /**
     * detachRemoteLogger is called to detach a {@link RemoteLogger} object from
     * the local {@link Logger}.
     * 
     * @param prx A proxy to the remote logger.
     * 
     * @param __cb The asynchronous callback object.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_detachRemoteLogger(RemoteLoggerPrx prx, Ice.Callback __cb);

    /**
     * detachRemoteLogger is called to detach a {@link RemoteLogger} object from
     * the local {@link Logger}.
     * 
     * @param prx A proxy to the remote logger.
     * 
     * @param __ctx The Context map to send with the invocation.
     * @param __cb The asynchronous callback object.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_detachRemoteLogger(RemoteLoggerPrx prx, java.util.Map<String, String> __ctx, Ice.Callback __cb);

    /**
     * detachRemoteLogger is called to detach a {@link RemoteLogger} object from
     * the local {@link Logger}.
     * 
     * @param prx A proxy to the remote logger.
     * 
     * @param __cb The asynchronous callback object.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_detachRemoteLogger(RemoteLoggerPrx prx, Callback_LoggerAdmin_detachRemoteLogger __cb);

    /**
     * detachRemoteLogger is called to detach a {@link RemoteLogger} object from
     * the local {@link Logger}.
     * 
     * @param prx A proxy to the remote logger.
     * 
     * @param __ctx The Context map to send with the invocation.
     * @param __cb The asynchronous callback object.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_detachRemoteLogger(RemoteLoggerPrx prx, java.util.Map<String, String> __ctx, Callback_LoggerAdmin_detachRemoteLogger __cb);

    /**
     * detachRemoteLogger is called to detach a {@link RemoteLogger} object from
     * the local {@link Logger}.
     * 
     * @param prx A proxy to the remote logger.
     * 
     * @param __responseCb The lambda response callback.
     * @param __exceptionCb The lambda exception callback.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_detachRemoteLogger(RemoteLoggerPrx prx, 
                                                    IceInternal.Functional_BoolCallback __responseCb, 
                                                    IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb);

    /**
     * detachRemoteLogger is called to detach a {@link RemoteLogger} object from
     * the local {@link Logger}.
     * 
     * @param prx A proxy to the remote logger.
     * 
     * @param __responseCb The lambda response callback.
     * @param __exceptionCb The lambda exception callback.
     * @param __sentCb The lambda sent callback.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_detachRemoteLogger(RemoteLoggerPrx prx, 
                                                    IceInternal.Functional_BoolCallback __responseCb, 
                                                    IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb, 
                                                    IceInternal.Functional_BoolCallback __sentCb);

    /**
     * detachRemoteLogger is called to detach a {@link RemoteLogger} object from
     * the local {@link Logger}.
     * 
     * @param prx A proxy to the remote logger.
     * 
     * @param __ctx The Context map to send with the invocation.
     * @param __responseCb The lambda response callback.
     * @param __exceptionCb The lambda exception callback.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_detachRemoteLogger(RemoteLoggerPrx prx, 
                                                    java.util.Map<String, String> __ctx, 
                                                    IceInternal.Functional_BoolCallback __responseCb, 
                                                    IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb);

    /**
     * detachRemoteLogger is called to detach a {@link RemoteLogger} object from
     * the local {@link Logger}.
     * 
     * @param prx A proxy to the remote logger.
     * 
     * @param __ctx The Context map to send with the invocation.
     * @param __responseCb The lambda response callback.
     * @param __exceptionCb The lambda exception callback.
     * @param __sentCb The lambda sent callback.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_detachRemoteLogger(RemoteLoggerPrx prx, 
                                                    java.util.Map<String, String> __ctx, 
                                                    IceInternal.Functional_BoolCallback __responseCb, 
                                                    IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb, 
                                                    IceInternal.Functional_BoolCallback __sentCb);

    /**
     * detachRemoteLogger is called to detach a {@link RemoteLogger} object from
     * the local {@link Logger}.
     * 
     * @param __result The asynchronous result object.
     * @return True if the provided remote logger proxy was detached, and false otherwise.
     * 
     **/
    public boolean end_detachRemoteLogger(Ice.AsyncResult __result);

    /**
     * getLog retrieves log messages recently logged.
     *
     * @param messageTypes The list of message types that the caller wishes to receive.
     * An empty list means no filtering (send all message types).
     *
     * @param traceCategories The categories of traces that caller wish to receive.
     * This parameter is ignored if messageTypes is not empty and does not include trace.
     * An empty list means no filtering (send all trace categories).
     *
     * @param messageMax The maximum number of log messages (of all types) to be returned.
     * A negative value requests all messages available.
     *
     * @param prefix The prefix of the associated local {@link Logger}.
     *
     * @return The Log messages.
     *
     **/
    public LogMessage[] getLog(LogMessageType[] messageTypes, String[] traceCategories, int messageMax, Ice.StringHolder prefix);

    /**
     * getLog retrieves log messages recently logged.
     *
     * @param messageTypes The list of message types that the caller wishes to receive.
     * An empty list means no filtering (send all message types).
     *
     * @param traceCategories The categories of traces that caller wish to receive.
     * This parameter is ignored if messageTypes is not empty and does not include trace.
     * An empty list means no filtering (send all trace categories).
     *
     * @param messageMax The maximum number of log messages (of all types) to be returned.
     * A negative value requests all messages available.
     *
     * @param prefix The prefix of the associated local {@link Logger}.
     *
     * @param __ctx The Context map to send with the invocation.
     * @return The Log messages.
     *
     **/
    public LogMessage[] getLog(LogMessageType[] messageTypes, String[] traceCategories, int messageMax, Ice.StringHolder prefix, java.util.Map<String, String> __ctx);

    /**
     * getLog retrieves log messages recently logged.
     * 
     * @param messageTypes The list of message types that the caller wishes to receive.
     * An empty list means no filtering (send all message types).
     * 
     * @param traceCategories The categories of traces that caller wish to receive.
     * This parameter is ignored if messageTypes is not empty and does not include trace.
     * An empty list means no filtering (send all trace categories).
     * 
     * @param messageMax The maximum number of log messages (of all types) to be returned.
     * A negative value requests all messages available.
     * 
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_getLog(LogMessageType[] messageTypes, String[] traceCategories, int messageMax);

    /**
     * getLog retrieves log messages recently logged.
     * 
     * @param messageTypes The list of message types that the caller wishes to receive.
     * An empty list means no filtering (send all message types).
     * 
     * @param traceCategories The categories of traces that caller wish to receive.
     * This parameter is ignored if messageTypes is not empty and does not include trace.
     * An empty list means no filtering (send all trace categories).
     * 
     * @param messageMax The maximum number of log messages (of all types) to be returned.
     * A negative value requests all messages available.
     * 
     * @param __ctx The Context map to send with the invocation.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_getLog(LogMessageType[] messageTypes, String[] traceCategories, int messageMax, java.util.Map<String, String> __ctx);

    /**
     * getLog retrieves log messages recently logged.
     * 
     * @param messageTypes The list of message types that the caller wishes to receive.
     * An empty list means no filtering (send all message types).
     * 
     * @param traceCategories The categories of traces that caller wish to receive.
     * This parameter is ignored if messageTypes is not empty and does not include trace.
     * An empty list means no filtering (send all trace categories).
     * 
     * @param messageMax The maximum number of log messages (of all types) to be returned.
     * A negative value requests all messages available.
     * 
     * @param __cb The asynchronous callback object.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_getLog(LogMessageType[] messageTypes, String[] traceCategories, int messageMax, Ice.Callback __cb);

    /**
     * getLog retrieves log messages recently logged.
     * 
     * @param messageTypes The list of message types that the caller wishes to receive.
     * An empty list means no filtering (send all message types).
     * 
     * @param traceCategories The categories of traces that caller wish to receive.
     * This parameter is ignored if messageTypes is not empty and does not include trace.
     * An empty list means no filtering (send all trace categories).
     * 
     * @param messageMax The maximum number of log messages (of all types) to be returned.
     * A negative value requests all messages available.
     * 
     * @param __ctx The Context map to send with the invocation.
     * @param __cb The asynchronous callback object.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_getLog(LogMessageType[] messageTypes, String[] traceCategories, int messageMax, java.util.Map<String, String> __ctx, Ice.Callback __cb);

    /**
     * getLog retrieves log messages recently logged.
     * 
     * @param messageTypes The list of message types that the caller wishes to receive.
     * An empty list means no filtering (send all message types).
     * 
     * @param traceCategories The categories of traces that caller wish to receive.
     * This parameter is ignored if messageTypes is not empty and does not include trace.
     * An empty list means no filtering (send all trace categories).
     * 
     * @param messageMax The maximum number of log messages (of all types) to be returned.
     * A negative value requests all messages available.
     * 
     * @param __cb The asynchronous callback object.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_getLog(LogMessageType[] messageTypes, String[] traceCategories, int messageMax, Callback_LoggerAdmin_getLog __cb);

    /**
     * getLog retrieves log messages recently logged.
     * 
     * @param messageTypes The list of message types that the caller wishes to receive.
     * An empty list means no filtering (send all message types).
     * 
     * @param traceCategories The categories of traces that caller wish to receive.
     * This parameter is ignored if messageTypes is not empty and does not include trace.
     * An empty list means no filtering (send all trace categories).
     * 
     * @param messageMax The maximum number of log messages (of all types) to be returned.
     * A negative value requests all messages available.
     * 
     * @param __ctx The Context map to send with the invocation.
     * @param __cb The asynchronous callback object.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_getLog(LogMessageType[] messageTypes, String[] traceCategories, int messageMax, java.util.Map<String, String> __ctx, Callback_LoggerAdmin_getLog __cb);

    public interface FunctionalCallback_LoggerAdmin_getLog_Response
    {
        void apply(LogMessage[] __ret, String prefix);
    }

    /**
     * getLog retrieves log messages recently logged.
     * 
     * @param messageTypes The list of message types that the caller wishes to receive.
     * An empty list means no filtering (send all message types).
     * 
     * @param traceCategories The categories of traces that caller wish to receive.
     * This parameter is ignored if messageTypes is not empty and does not include trace.
     * An empty list means no filtering (send all trace categories).
     * 
     * @param messageMax The maximum number of log messages (of all types) to be returned.
     * A negative value requests all messages available.
     * 
     * @param __responseCb The lambda response callback.
     * @param __exceptionCb The lambda exception callback.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_getLog(LogMessageType[] messageTypes, 
                                        String[] traceCategories, 
                                        int messageMax, 
                                        FunctionalCallback_LoggerAdmin_getLog_Response __responseCb, 
                                        IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb);

    /**
     * getLog retrieves log messages recently logged.
     * 
     * @param messageTypes The list of message types that the caller wishes to receive.
     * An empty list means no filtering (send all message types).
     * 
     * @param traceCategories The categories of traces that caller wish to receive.
     * This parameter is ignored if messageTypes is not empty and does not include trace.
     * An empty list means no filtering (send all trace categories).
     * 
     * @param messageMax The maximum number of log messages (of all types) to be returned.
     * A negative value requests all messages available.
     * 
     * @param __responseCb The lambda response callback.
     * @param __exceptionCb The lambda exception callback.
     * @param __sentCb The lambda sent callback.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_getLog(LogMessageType[] messageTypes, 
                                        String[] traceCategories, 
                                        int messageMax, 
                                        FunctionalCallback_LoggerAdmin_getLog_Response __responseCb, 
                                        IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb, 
                                        IceInternal.Functional_BoolCallback __sentCb);

    /**
     * getLog retrieves log messages recently logged.
     * 
     * @param messageTypes The list of message types that the caller wishes to receive.
     * An empty list means no filtering (send all message types).
     * 
     * @param traceCategories The categories of traces that caller wish to receive.
     * This parameter is ignored if messageTypes is not empty and does not include trace.
     * An empty list means no filtering (send all trace categories).
     * 
     * @param messageMax The maximum number of log messages (of all types) to be returned.
     * A negative value requests all messages available.
     * 
     * @param __ctx The Context map to send with the invocation.
     * @param __responseCb The lambda response callback.
     * @param __exceptionCb The lambda exception callback.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_getLog(LogMessageType[] messageTypes, 
                                        String[] traceCategories, 
                                        int messageMax, 
                                        java.util.Map<String, String> __ctx, 
                                        FunctionalCallback_LoggerAdmin_getLog_Response __responseCb, 
                                        IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb);

    /**
     * getLog retrieves log messages recently logged.
     * 
     * @param messageTypes The list of message types that the caller wishes to receive.
     * An empty list means no filtering (send all message types).
     * 
     * @param traceCategories The categories of traces that caller wish to receive.
     * This parameter is ignored if messageTypes is not empty and does not include trace.
     * An empty list means no filtering (send all trace categories).
     * 
     * @param messageMax The maximum number of log messages (of all types) to be returned.
     * A negative value requests all messages available.
     * 
     * @param __ctx The Context map to send with the invocation.
     * @param __responseCb The lambda response callback.
     * @param __exceptionCb The lambda exception callback.
     * @param __sentCb The lambda sent callback.
     * @return The asynchronous result object.
     **/
    public Ice.AsyncResult begin_getLog(LogMessageType[] messageTypes, 
                                        String[] traceCategories, 
                                        int messageMax, 
                                        java.util.Map<String, String> __ctx, 
                                        FunctionalCallback_LoggerAdmin_getLog_Response __responseCb, 
                                        IceInternal.Functional_GenericCallback1<Ice.Exception> __exceptionCb, 
                                        IceInternal.Functional_BoolCallback __sentCb);

    /**
     * getLog retrieves log messages recently logged.
     * 
     * @param prefix The prefix of the associated local {@link Logger}.
     * 
     * @param __result The asynchronous result object.
     * @return The Log messages.
     * 
     **/
    public LogMessage[] end_getLog(Ice.StringHolder prefix, Ice.AsyncResult __result);
}
