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
package org.helyx.dbmigration.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.newHashMap;
import static java.lang.String.format;

public class DbMigrationSettings {

    private Map<String, DatabaseConfiguration> databaseConfigurations = newHashMap();

    public Map<String, DatabaseConfiguration> getDatabaseConfigurationList() {
        return Collections.unmodifiableMap(databaseConfigurations);
    }

    public DatabaseConfiguration getDatabaseConfiguration(String name) {
        return databaseConfigurations.get(name);
    }

    public boolean hasDatabaseConfiguration(String name) {
        return databaseConfigurations.containsKey(name);
    }

    public static class Builder {

        private DbMigrationSettings dmc = new DbMigrationSettings();

        public Builder withDatabaseConfiguration(DatabaseConfiguration dc) {
            checkNotNull(dc);
            checkArgument(!dmc.databaseConfigurations.containsKey(dc.getName()), format("Database name already used: %1$s", dc.getName()));
            dmc.databaseConfigurations.put(dc.getName(), dc);
            return this;
        }

        public Builder withDatabaseConfigurations(List<DatabaseConfiguration> dcs) {
            checkNotNull(dcs);
            for (DatabaseConfiguration dc : dcs) {
                withDatabaseConfiguration(dc);
            }

            return this;
        }

        public DbMigrationSettings build() {
            return dmc;
        }

    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return "DbMigrationSettings{" +
                "databaseConfigurations=" + databaseConfigurations +
                '}';
    }

}
