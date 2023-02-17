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
 * This exception indicates that a message did not start with the expected
 * magic number ('I', 'c', 'e', 'P').
 *
 **/
public class BadMagicException extends ProtocolException
{
    public BadMagicException()
    {
        super();
    }

    public BadMagicException(Throwable __cause)
    {
        super(__cause);
    }

    public BadMagicException(String reason, byte[] badMagic)
    {
        super(reason);
        this.badMagic = badMagic;
    }

    public BadMagicException(String reason, byte[] badMagic, Throwable __cause)
    {
        super(reason, __cause);
        this.badMagic = badMagic;
    }

    public String
    ice_name()
    {
        return "Ice::BadMagicException";
    }

    /**
     * A sequence containing the first four bytes of the incorrect message.
     *
     **/
    public byte[] badMagic;

    public static final long serialVersionUID = -3934807911473944716L;
}
