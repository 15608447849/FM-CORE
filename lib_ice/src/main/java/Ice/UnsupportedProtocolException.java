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
 * This exception indicates an unsupported protocol version.
 *
 **/
public class UnsupportedProtocolException extends ProtocolException
{
    public UnsupportedProtocolException()
    {
        super();
        bad = new ProtocolVersion();
        supported = new ProtocolVersion();
    }

    public UnsupportedProtocolException(Throwable __cause)
    {
        super(__cause);
        bad = new ProtocolVersion();
        supported = new ProtocolVersion();
    }

    public UnsupportedProtocolException(String reason, ProtocolVersion bad, ProtocolVersion supported)
    {
        super(reason);
        this.bad = bad;
        this.supported = supported;
    }

    public UnsupportedProtocolException(String reason, ProtocolVersion bad, ProtocolVersion supported, Throwable __cause)
    {
        super(reason, __cause);
        this.bad = bad;
        this.supported = supported;
    }

    public String
    ice_name()
    {
        return "Ice::UnsupportedProtocolException";
    }

    /**
     * The version of the unsupported protocol.
     *
     **/
    public ProtocolVersion bad;

    /**
     * The version of the protocol that is supported.
     *
     **/
    public ProtocolVersion supported;

    public static final long serialVersionUID = 811091383730564025L;
}