// **********************************************************************
//
// Copyright (c) 2003-2017 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

package IceUtilInternal;

import bottle.util.Log4j;

public final class Assert
{
    //
    // The JVM ignores exceptions raised in finalizers, therefore finalizers
    // that use assertions should call this method instead of assert().
    //
    public static void
    FinalizerAssert(boolean b)
    {
        if(!b)
        {
            //
            // Create a Throwable to obtain the stack trace.
            //
            Throwable t = new Throwable();
            StackTraceElement[] trace = t.getStackTrace();
            if(trace.length > 1)
            {
                //
                // Skip the first frame, which represents this method.
                //
                Log4j.info("Assertion failure:");
                for(StackTraceElement e : trace)
                {
                    Log4j.info("\tat " + e);
                }
            }
            else
            {
                Log4j.info("Assertion failure (no stack trace information)");
            }
        }
    }
}
