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
package org.helyx.dbmigration;

import org.helyx.dbmigration.model.ActionType;
import org.helyx.dbmigration.model.ExecutionConfiguration;
import org.helyx.dbmigration.service.DBMLogger;
import org.helyx.dbmigration.service.DbMigrator;
import com.google.common.base.Splitter;
import org.apache.commons.cli.*;

import static org.helyx.dbmigration.model.ActionType.CHECK;
import static org.helyx.dbmigration.model.ActionType.MIGRATE;
import static com.google.common.base.Joiner.on;
import static java.lang.String.format;
import static java.lang.System.exit;

public class Main {

    public static void main(String[] args) {
        try {
            Options clOptions = getCommandLineOptions();
            CommandLine cl = parseCommandLine(args, clOptions);

            if( !requiredOptionsSetted(cl) || helpOptionSetted(cl) ) {
                showHelpAndExit(clOptions);
            }

            ExecutionConfiguration.Builder ecBuilder = ExecutionConfiguration.newBuilder();

            if (cl.hasOption("c")) {
                ecBuilder.withAction(ActionType.valueOf(cl.getOptionValue("c")));
            }

            if (cl.hasOption("f")) {
                ecBuilder.withSqlFilesPath(cl.getOptionValue("f"));
            }

            if (cl.hasOption("t")) {
                for (String target : Splitter.on(",").split(cl.getOptionValue("t"))) {
                    ecBuilder.withTarget(target);
                }
            }

            ExecutionConfiguration ec = ecBuilder.build();
            DbMigrator.execute(ec);

            DBMLogger.info("Migration ended with success !!");

            exit(0);
        } catch (Exception e) {
            DBMLogger.error(e.getMessage());
            exit(1);
        }
    }

    private static boolean helpOptionSetted(CommandLine cl) {
        return cl.hasOption("h");
    }

    private static boolean requiredOptionsSetted(CommandLine cl) {
        return cl.hasOption("c") && cl.hasOption("f") && cl.hasOption("t");
    }

    private static CommandLine parseCommandLine(String[] args, Options clOptions) throws ParseException {
        CommandLineParser parser = new GnuParser();
        return parser.parse(clOptions, args );
    }

    private static void showHelpAndExit(Options clOptions) {
        // automatically generate the help statement
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "db-migration", clOptions );
        exit(0);
    }

    private static Options getCommandLineOptions() {
        String availableActionsFormatted = on(", ").join(MIGRATE.name(), CHECK.name());

        Options options = new Options();
        options.addOption( "f", "file", true, "Path of the zip file or folder containing the SQL files to execute" );
        options.addOption( "t", "target", true, "List of configuration target separated by comma" );
        options.addOption( "c", "command", true, format("(Command(s) to execute [%1$s]" , availableActionsFormatted));
        options.addOption( "h", "help", false, "Show the current help message");
        return options;
    }

}
