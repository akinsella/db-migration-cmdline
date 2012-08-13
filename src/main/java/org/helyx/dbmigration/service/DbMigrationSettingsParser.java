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

import org.helyx.dbmigration.model.DatabaseConfiguration;
import org.helyx.dbmigration.model.DbMigrationSettings;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.net.URL;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class DbMigrationSettingsParser {

    public static final String DATABASE_PATH = "/dbmigration/databases/database";
    private String dcPath;

    private DbMigrationSettingsParser(String dcPath) {
        this.dcPath = dcPath;
    }

    private DbMigrationSettings parse() {

        try {
            DbMigrationSettings.Builder dmsBuilder = DbMigrationSettings.newBuilder();

            URL dcUrl = new File(dcPath).getAbsoluteFile().toURI().toURL();

            Document document = new SAXReader().read(dcUrl);
            parseDatabaseConfigurations(dmsBuilder, document);

            return dmsBuilder.build();
        } catch (Exception e) {
            throw new ParserException(e);
        }
    }

    private void parseDatabaseConfigurations(DbMigrationSettings.Builder dmsBuilder, Document document) {
        @SuppressWarnings("unchecked")
        List<Element> databaseList = document.selectNodes(DATABASE_PATH);

        for (Element dbElt : databaseList) {

            DatabaseConfiguration.Builder dcBuilder = DatabaseConfiguration.newBuilder();

            dcBuilder.withName(dbElt.attributeValue("name"));
            if (Boolean.parseBoolean(dbElt.attributeValue("prod"))) {
                dcBuilder.isProductionDatabase();
            }

            dcBuilder.withDriver(dbElt.elementTextTrim("driver"));
            dcBuilder.withUrl(dbElt.elementTextTrim("url"));
            dcBuilder.withUsername(dbElt.elementTextTrim("username"));
            dcBuilder.withPassword(dbElt.elementTextTrim("password"));

            dmsBuilder.withDatabaseConfiguration(dcBuilder.build());
        }
    }

    public static DbMigrationSettings parse(String dcPath) {
        return new DbMigrationSettingsParser(dcPath).parse();
    }

}
