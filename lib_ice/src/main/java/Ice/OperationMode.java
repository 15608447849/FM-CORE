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
// Generated from file `Current.ice'
//
// Warning: do not edit this file.
//
// </auto-generated>
//

package Ice;

/**
 * The {@link OperationMode} determines the retry behavior an
 * invocation in case of a (potentially) recoverable error.
 *
 **/
public enum OperationMode implements java.io.Serializable
{
    
    /**
     * Ordinary operations have <tt>Normal</tt> mode.  These operations
     * modify object state; invoking such an operation twice in a row
     * has different semantics than invoking it once. The Ice run time
     * guarantees that it will not violate at-most-once semantics for
     * <tt>Normal</tt> operations.
     **/
    Normal(0),
    
    /**
     * Operations that use the Slice <tt>nonmutating</tt> keyword must not
     * modify object state. For C++, nonmutating operations generate
     * <tt>const</tt> member functions in the skeleton. In addition, the Ice
     * run time will attempt to transparently recover from certain
     * run-time errors by re-issuing a failed request and propagate
     * the failure to the application only if the second attempt
     * fails.
     *
     * <p class="Deprecated"><tt>Nonmutating</tt> is deprecated; Use the
     * <tt>idempotent</tt> keyword instead. For C++, to retain the mapping
     * of <tt>nonmutating</tt> operations to C++ <tt>const</tt>
     * member functions, use the <tt>\["cpp:const"]</tt> metadata
     * directive.
     **/
    Nonmutating(1),
    
    /**
     * Operations that use the Slice <tt>idempotent</tt> keyword can modify
     * object state, but invoking an operation twice in a row must
     * result in the same object state as invoking it once.  For
     * example, <tt>x = 1</tt> is an idempotent statement,
     * whereas <tt>x += 1</tt> is not. For idempotent
     * operations, the Ice run-time uses the same retry behavior
     * as for nonmutating operations in case of a potentially
     * recoverable error.
     **/
    Idempotent(2);

    public int
    value()
    {
        return __value;
    }

    public static OperationMode
    valueOf(int __v)
    {
        switch(__v)
        {
        case 0:
            return Normal;
        case 1:
            return Nonmutating;
        case 2:
            return Idempotent;
        }
        return null;
    }

    private
    OperationMode(int __v)
    {
        __value = __v;
    }

    public void
    __write(IceInternal.BasicStream __os)
    {
        __os.writeEnum(value(), 2);
    }

    public static void
    __write(IceInternal.BasicStream __os, OperationMode __v)
    {
        if(__v == null)
        {
            __os.writeEnum(Ice.OperationMode.Normal.value(), 2);
        }
        else
        {
            __os.writeEnum(__v.value(), 2);
        }
    }

    public static OperationMode
    __read(IceInternal.BasicStream __is)
    {
        int __v = __is.readEnum(2);
        return __validate(__v);
    }

    private static OperationMode
    __validate(int __v)
    {
        final OperationMode __e = valueOf(__v);
        if(__e == null)
        {
            throw new Ice.MarshalException("enumerator value " + __v + " is out of range");
        }
        return __e;
    }

    private final int __value;
}
