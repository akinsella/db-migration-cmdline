/**
 * Copyright (C) 2012 Alexis Kinsella - http://www.helyx.org - <Helyx.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.helyx.dbmigration.service;

import org.slf4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.slf4j.LoggerFactory.getLogger;


public class DBMLogger {

    private static final Logger LOGGER = getLogger("dbmigration");

    public static void trace(String message) {
        // No System.out - Message is for debug purpose - Available only in log file
        LOGGER.trace(message);
    }

    public static void debug(String message) {
        // No System.out - Message is for debug purpose - Available only in log file
        LOGGER.debug(message);
    }

    public static void info(String message) {
//        System.out.println(String.format("%1$s %2$s %3$s", getCurrentFormattedTime(), "INFO", message));
        LOGGER.info(message);
    }

    public static void warn(String message) {
//        System.out.println(String.format("%1$s %2$s %3$s", getCurrentFormattedTime(), "WARN", message));
        LOGGER.warn(message);
    }

    public static void warn(String message, Throwable t) {
//        System.out.println(String.format("%1$s %2$s %3$s", getCurrentFormattedTime(), "ERROR", message));
        LOGGER.warn(message, t);
    }

    public static void error(String message) {
//        System.err.println(String.format("%1$s %2$s %3$s", getCurrentFormattedTime(), "ERROR", message));
        LOGGER.error(message);
    }

    public static void error(String message, Throwable t) {
//        System.err.println(String.format("%1$s %2$s %3$s", getCurrentFormattedTime(), "ERROR", message));
        LOGGER.error(message, t);
    }

    private static String getCurrentFormattedTime() {
        return new SimpleDateFormat("HH:mm:ss,SSS").format(new Date());
    }

}
