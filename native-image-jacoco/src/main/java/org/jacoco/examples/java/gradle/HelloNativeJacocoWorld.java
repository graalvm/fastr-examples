/**
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 * This file is made available under version 3 of the GNU General Public License.
 */

package org.jacoco.examples.java.gradle;

import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.VMRuntime;

public class HelloNativeJacocoWorld {

    public static String getMessage(boolean universe) {
        if (universe) {
            return "Hello, Universe!";
        } else {
            return "Hello, World!";
        }
    }

    @CEntryPoint(name = "isolate_test")
    public static void test(IsolateThread thread, boolean flag) {
        System.out.println(getMessage(flag));
    }

    /* For this method we can't get coverage as it exits the VM. */
    @CEntryPoint(name = "tear_down")
    public static void tearDown(IsolateThread thread) {
        VMRuntime.shutdown();
    }
}
