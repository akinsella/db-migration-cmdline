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


import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class DatabaseConfiguration {

    private String name;

    private String driver;
    private String url;
    private String username;
    private String password;
    private boolean production;


    public String getName() {
        return name;
    }

    public String getDriver() {
        return driver;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isProduction() {
        return production;
    }


    public static class Builder {

        private DatabaseConfiguration dc = new DatabaseConfiguration();

        public Builder withName(String name) {
            checkArgument(Pattern.compile("[a-zA-Z0-9\\-]*").matcher(name).matches(), "Database configuration name has to match pattern: [a-zA-Z0-9\\-]*");
            dc.name = name;
            return this;
        }

        public Builder withDriver(String driver) {
            checkNotNull(driver);
            dc.driver = driver;
            return this;
        }

        public Builder withUrl(String url) {
            checkNotNull(url);
            dc.url = url;
            return this;
        }

        public Builder withUsername(String username) {
            checkNotNull(username);
            dc.username = username;
            return this;
        }

        public Builder withPassword(String password) {
            checkNotNull(password);
            dc.password = password;
            return this;
        }

        public Builder isProductionDatabase() {
            dc.production = true;
            return this;
        }

        public DatabaseConfiguration build() {
            return dc;
        }

    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return "DatabaseConfiguration{" +
                "driver='" + driver + '\'' +
                ", url='" + url + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", production='" + production + '\'' +
                '}';
    }

}
