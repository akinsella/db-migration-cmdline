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

import com.carbonfive.db.migration.DriverManagerMigrationManager;
import com.carbonfive.db.migration.Migration;
import com.carbonfive.db.migration.MigrationManager;
import com.carbonfive.db.migration.ResourceMigrationResolver;
import org.helyx.dbmigration.model.DatabaseConfiguration;
import org.helyx.dbmigration.model.DbMigrationSettings;
import org.helyx.dbmigration.model.ExecutionConfiguration;
import org.helyx.dbmigration.utils.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.SystemUtils;

import java.io.Console;
import java.io.File;

import static org.helyx.dbmigration.service.DBMLogger.debug;
import static org.helyx.dbmigration.service.DBMLogger.info;
import static org.helyx.dbmigration.utils.FileUtils.deleteDirectoryOnExit;
import static org.helyx.dbmigration.utils.ZipUtils.unzipArchive;
import static java.lang.String.format;
import static org.slf4j.LoggerFactory.getLogger;

public class DbMigrator {

    private static final String DB_MIGRATION_DIR = ".db-migration";

    private ExecutionConfiguration ec = new ExecutionConfiguration();

    private DbMigrator(ExecutionConfiguration ec) {
        this.ec = ec;
    }

    public static void execute(ExecutionConfiguration ec) {

        DbMigrator dbMigrator = new DbMigrator(ec);

        dbMigrator.init();

        dbMigrator.execute();
    }

    private void init() {
        File dbMigrationDir = getApplicationDirectoryFile();
        if (!dbMigrationDir.exists()) {
            boolean created = dbMigrationDir.mkdirs();
            if (!created) {
                throw new IORuntimeException(format("Could not create application directory with path: '%1$s'", dbMigrationDir.getAbsolutePath()));
            }
        }
        else if (!dbMigrationDir.isDirectory()) {
            throw new IORuntimeException(format("Application directory Path is not a directy: '%1$s'", dbMigrationDir.getAbsolutePath()));
        }
        else if (!dbMigrationDir.canRead()) {
            throw new IORuntimeException(format("Application directory is not readable: '%1$s'", dbMigrationDir.getAbsolutePath()));
        }
    }

    private File getApplicationDirectoryFile() {
        return new File(SystemUtils.getUserHome(), DB_MIGRATION_DIR);
    }

    public void execute() {
        info(format("[ Action: %1$s ]", ec.getAction().name()));

        DbMigrationSettings settings = DbMigrationSettingsParser.parse(getSettingsFile().getAbsolutePath());

        checkDatabaseConfigurationsAvailability(settings, ec);

        File sqlFilesResourceToCheck = new File(ec.getSqlFilesPath()).getAbsoluteFile();
        File sqlFilesDir;

        if (sqlFilesResourceToCheck.exists() && sqlFilesResourceToCheck.isDirectory() && sqlFilesResourceToCheck.canRead()) {
            if (sqlFilesResourceToCheck.list() == null || sqlFilesResourceToCheck.list().length == 0) {
                throw new IllegalArgumentException(format("Directory '%1$s' has no sql files to migrate!", sqlFilesResourceToCheck.getAbsolutePath()));
            }
            sqlFilesDir = sqlFilesResourceToCheck;
        }
        else if (sqlFilesResourceToCheck.exists() && sqlFilesResourceToCheck.isFile() && sqlFilesResourceToCheck.canRead() && sqlFilesResourceToCheck.getName().toLowerCase().endsWith(".zip")) {
            sqlFilesDir = unzipSqlFiles(sqlFilesResourceToCheck);
        }
        else {
            throw new IllegalArgumentException(format("Path '%1$s' cannot be processed. Neither a directory nor a zip file", sqlFilesResourceToCheck.getAbsolutePath()));
        }

        for (String target : ec.getTargets()) {
            info(format("  [ Target: '%1$s' ]", target));

            DatabaseConfiguration dc = settings.getDatabaseConfiguration(target);

            info(format("    -> [ Db - Host: '%1$s', User: '%2$s']", dc.getUrl(), dc.getUsername()));

            DriverManagerMigrationManager migrationManager = new DriverManagerMigrationManager(dc.getDriver(), dc.getUrl(), dc.getUsername(), dc.getPassword());
            ResourceMigrationResolver resourceMigrationResolver = new ResourceMigrationResolver(format("file:%1$s", sqlFilesDir.getAbsolutePath()));
            migrationManager.setMigrationResolver(resourceMigrationResolver);

            switch (ec.getAction()) {
                case CHECK:
                    validate(target, migrationManager);
                    break;
                case MIGRATE:
                    validate(target, migrationManager);
                    confirmIfProductionTarget(target, dc);
                    migrate(target, migrationManager);
                    break;
            }
        }
    }

    private File unzipSqlFiles(File sqlFileArchive) {

        if (!sqlFileArchive.exists()) {
            throw new IORuntimeException(format("Sql archive not found: %1$s", sqlFileArchive.getAbsolutePath()));
        }
        if (!sqlFileArchive.isFile()) {
            throw new IORuntimeException(format("Sql archive path is not a file: %1$s", sqlFileArchive.getAbsolutePath()));
        }
        if (!sqlFileArchive.canRead()) {
            throw new IORuntimeException(format("Sql archive path is not a filenot readable: %1$s", sqlFileArchive.getAbsolutePath()));
        }

        final File sqlFileDirectory = FileUtils.createTempDirectory();

        debug(format("Path of unzipped directory: %1$s", sqlFileDirectory.getAbsolutePath()));
        deleteDirectoryOnExit(sqlFileDirectory);
        unzipArchive(sqlFileArchive, sqlFileDirectory);

        return sqlFileDirectory;
    }



    private File getSettingsFile() {
        return new File(getApplicationDirectoryFile(), "settings.xml");
    }

    private void validate(String target, MigrationManager migrationManager) {

        if (migrationManager.pendingMigrations().isEmpty()) {
            info(format("         No migration need to be applied - Database is up to date"));
        }
        else {
            info(format("         Migrations available:"));
            for (Migration migration : migrationManager.pendingMigrations()) {
                info(format("           * [%1$s] '%2$s']", migration.getVersion(), migration.getFilename()));
            }

            if (migrationManager.validate()) {
                throw new MigrationValidationException(format("Validation failed for database configuration : '%1$s'", target));
            }
        }
    }

    private void confirmIfProductionTarget(String target, DatabaseConfiguration dc) {
        Console console = System.console();

        if (dc.isProduction()) {
            String generatedCode = RandomStringUtils.randomNumeric(4);
            info(format("           Target Database : '%1$s' is a production database.", target));

            if (console != null) {
                info(format("Please confirm action by typing the following code: %2$s", target, generatedCode));
                System.out.println("Enter the code: ");
                String typedCode = System.console().readLine().trim();
                if (!generatedCode.equals(typedCode)) {
                    throw new WrongCodeException(format("Wrong code ! You typed: '%1$s'. Expected code was: '%2$s'", typedCode, generatedCode));
                }
            }
       }
        else {
            if (console != null) {
                info("Type enter to continue...");
                System.console().readLine();
            }
        }
    }

    private void migrate(String target, MigrationManager migrationManager) {
        try {
            migrationManager.migrate();
        } catch (Exception e) {
            throw new MigrationValidationException(format("Migration failed for database configuration : '%1$s'", target));
        }
    }

    private void checkDatabaseConfigurationsAvailability(DbMigrationSettings settings, ExecutionConfiguration ec) {
        for (String target : ec.getTargets()) {
            checkDatabaseConfigurationAvailability(settings, target);
        }
    }

    private void checkDatabaseConfigurationAvailability(DbMigrationSettings settings, String target) {
        if (!settings.hasDatabaseConfiguration(target)) {
            throw new IllegalArgumentException(format("Targeted configuration not found in settings for database name: '%1$s'", target));
        }
    }

}
