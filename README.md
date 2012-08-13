#Download

  * Version 1.0.0: [db-migration-cmdline-1.0.0.jar](http://maven.helyx.org/repository/release/org/helyx/db-migration-cmdline/db-migration-cmdline/1.0.0/db-migration-cmdline-1.0.0.jar)


#Install

  * Create a directory in you user home: ~/.db-migration
  * Add file: settings.xml
  * Add confgiurations to your: settings.xml

        <?xml version="1.0" encoding="UTF-8" ?>
        <dbmigration>
            <databases>
                <database name="DEV-APP">
                    <driver>com.mysql.jdbc.Driver</driver>
                    <url>jdbc:mysql://localhost/dev_app</url>
                    <username>app_user</username>
                    <password>app_password</password>
                </database>
                <database name="INT-APP" prod="true">
                    <driver>com.mysql.jdbc.Driver</driver>
                    <url>jdbc:mysql://dev-linux-mysql5/int_app</url>
                    <username>app_user</username>
                    <password>app_password</password>
                </database>
            </databases>
        </dbmigration>


# Execute

 Command line for a sql files directory:

   * java -jar db-migration-cmdline-1.0.0-SNAPSHOT.jar -c MIGRATE -f sql-files.zip -t DEV-APP,INT-APP

 Or for a sql files zip:

   * java -jar db-migration-cmdline-1.0.0-SNAPSHOT.jar -c MIGRATE -f sql-files -t DEV-APP,INT-APP



#Release a version

  * mvn --batch-mode release:clean release:prepare release:perform -DreleaseVersion=1.0.0 -DdevelopmentVersion=1.0.1-SNAPSHOT -Dtag=1.0.0

#Remove a existing tag after a release failure:

  * git tag -d '1.0.0'
  * git push origin :refs/tags/1.0.0
