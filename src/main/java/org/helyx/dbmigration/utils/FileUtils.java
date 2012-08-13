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
package org.helyx.dbmigration.utils;

import org.helyx.dbmigration.service.DBMLogger;
import org.helyx.dbmigration.service.IORuntimeException;

import java.io.File;
import java.io.IOException;

import static java.lang.String.format;

public class FileUtils {


    public static File createTempDirectory() {

        try {
            File temp = File.createTempFile("dbmigration", Long.toString(System.nanoTime()));

            if(!temp.delete()) {
                throw new IORuntimeException(format("Could not delete temporary created file: %1$s", temp.getAbsolutePath()));
            }

            if(!(temp.mkdirs())) {
                throw new IOException("Could not create temp directory: " + temp.getAbsolutePath());
            }

            return (temp);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public static void deleteDirectoryOnExit(final File directory) {

        if (!directory.exists()) {
            throw new IORuntimeException(format("Cannot delete a directory that does not exist: %1$s", directory.getAbsolutePath()));
        }

        if (!directory.isDirectory()) {
            throw new IORuntimeException(format("Argument is not a directory: %1$s", directory.getAbsolutePath()));
        }

        if (!directory.canWrite()) {
            throw new IORuntimeException(format("Directory is not writable: %1$s", directory.getAbsolutePath()));
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    org.apache.commons.io.FileUtils.deleteDirectory(directory);
                } catch (IOException e) {
                    DBMLogger.warn(format("Could not delete tmp directory: %1$s", directory));
                }
            }
        });
    }


}
