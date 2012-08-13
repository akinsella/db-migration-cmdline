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

import static java.lang.String.format;

public enum ActionType {

    MIGRATE("Apply all pending migrations"),
    CHECK("Check for pending migrations, fail the build if the db is not up to date");

    private String description;

    ActionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return format("ActionType{ description='%1$s'}", description);
    }

}
