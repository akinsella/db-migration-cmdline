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
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class DbMigrationSettingsParserTest {

    @Test
    public void testParse() throws Exception {
        DbMigrationSettings settings = DbMigrationSettingsParser.parse("src/test/resources/settings.xml");

        assertEquals(settings.getDatabaseConfigurationList().size(), 2);
        DatabaseConfiguration dc1 = settings.getDatabaseConfigurationList().get("DEV-APP");
        assertEquals(dc1.getName(), "DEV-APP");
        assertFalse(dc1.isProduction());
        assertEquals(dc1.getDriver(), "com.mysql.jdbc.Driver");
        assertEquals(dc1.getUrl(), "jdbc:mysql://localhost/dev_app");
        assertEquals(dc1.getUsername(), "app_user");
        assertEquals(dc1.getPassword(), "app_password");

        DatabaseConfiguration dc2 = settings.getDatabaseConfigurationList().get("INT-APP");
        assertEquals(dc2.getName(), "INT-APP");
        assertTrue(dc2.isProduction());
        assertEquals(dc2.getDriver(), "com.mysql.jdbc.Driver");
        assertEquals(dc2.getUrl(), "jdbc:mysql://dev-linux-mysql5/int_app");
        assertEquals(dc2.getUsername(), "app_user");
        assertEquals(dc2.getPassword(), "app_password");
    }

}
