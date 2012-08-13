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
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipUtils {

    public static void unzipArchive(File archive, File outputDir) {
        try {
            ZipFile zipfile = new ZipFile(archive);
            for (Enumeration e = zipfile.entries(); e.hasMoreElements(); ) {
                ZipEntry entry = (ZipEntry) e.nextElement();
                unzipEntry(zipfile, entry, outputDir);
            }
        } catch (Exception e) {
            DBMLogger.error("Error while extracting file " + archive);
            throw new UnzipException(e);
        }
    }

    private static void unzipEntry(ZipFile zipfile, ZipEntry entry, File outputDir) throws IOException {


        if (entry.isDirectory()) {
            createDir(new File(outputDir, entry.getName()));
            return;
        }

        File outputFile = new File(outputDir, entry.getName());
        if (!outputFile.getParentFile().exists()){
            createDir(outputFile.getParentFile());
        }

        DBMLogger.debug("Extracting: " + entry);
        BufferedInputStream inputStream = new BufferedInputStream(zipfile.getInputStream(entry));
        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFile));

        try {
            IOUtils.copy(inputStream, outputStream);
        } finally {
            outputStream.close();
            inputStream.close();
        }
    }

    private static void createDir(File dir) {
        DBMLogger.debug("Creating dir " + dir.getName());
        if(!dir.mkdirs()) {
            throw new RuntimeException("Can not create dir "+dir);
        }
    }

    public static class UnzipException extends RuntimeException {

        public UnzipException() {
        }

        public UnzipException(String message) {
            super(message);
        }

        public UnzipException(String message, Throwable cause) {
            super(message, cause);
        }

        public UnzipException(Throwable cause) {
            super(cause);
        }

    }

}