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
 * This exception indicates file errors.
 *
 **/
public class FileException extends SyscallException
{
    public FileException()
    {
        super();
        path = "";
    }

    public FileException(Throwable __cause)
    {
        super(__cause);
        path = "";
    }

    public FileException(int error, String path)
    {
        super(error);
        this.path = path;
    }

    public FileException(int error, String path, Throwable __cause)
    {
        super(error, __cause);
        this.path = path;
    }

    public String
    ice_name()
    {
        return "Ice::FileException";
    }

    /**
     * The path of the file responsible for the error.
     **/
    public String path;

    public static final long serialVersionUID = 8755315548941623583L;
}