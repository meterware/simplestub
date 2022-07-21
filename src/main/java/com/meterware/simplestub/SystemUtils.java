package com.meterware.simplestub;
/*
 * Copyright (c) 2015-2022 Russell Gold
 *
 * Licensed under the Apache License v 2.0 as shown at http://www.apache.org/licenses/LICENSE-2.0.txt.
 */

/**
 * Some common utilities.
 *
 * @author Russell Gold
 */
public class SystemUtils {

    private SystemUtils() {
    }

    /**
     * Returns the integer representing the major version of the JDK. For JDK 1.8, returns 8; for JDK 11, returns 11
     * and so on.
     */
    public static int getJavaVersion() {
        String versionString = System.getProperty("java.version");
        if (versionString.startsWith("1."))
            return toVersionNum(versionString.substring(2));
        else
            return toVersionNum(versionString);
    }

    private static int toVersionNum(String versionString) {
        StringBuilder sb = new StringBuilder(  );
        for (char c : versionString.toCharArray())
            if (Character.isDigit( c ))
                sb.append( c );
            else
                break;

        return Integer.parseInt( sb.toString() );
    }
}
