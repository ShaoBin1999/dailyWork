package com.bsren.job.core.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ThrowableUtil {

    public static String toString(Throwable e) {
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }

}