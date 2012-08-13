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


import java.util.List;

import static org.helyx.dbmigration.model.ActionType.CHECK;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

public class ExecutionConfiguration {

    private String sqlFilesPath;
    private ActionType action = CHECK;
    private List<String> targets = newArrayList();

    public String getSqlFilesPath() {
        return sqlFilesPath;
    }

    public ActionType getAction() {
        return action;
    }

    public List<String> getTargets() {
        return targets;
    }

    public static class Builder {

        private ExecutionConfiguration ec = new ExecutionConfiguration();

        public Builder withSqlFilesPath(String sqlFilesPath) {
            checkNotNull(sqlFilesPath);
            ec.sqlFilesPath = sqlFilesPath;
            return this;
        }

        public Builder withAction(ActionType action) {
            checkNotNull(action);
            ec.action = action;
            return this;
        }

        public Builder withTarget(String target) {
            checkNotNull(target);
            ec.targets.add(target);
            return this;
        }

        public Builder withTargets(List<String> targets) {
            checkNotNull(targets);
            ec.targets.addAll(targets);
            return this;
        }

        public ExecutionConfiguration build() {
            return ec;
        }

    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return "ExecutionConfiguration{" +
                "sqlFilesPath='" + sqlFilesPath + '\'' +
                ", action=" + action +
                ", targets=" + targets +
                '}';
    }

}
