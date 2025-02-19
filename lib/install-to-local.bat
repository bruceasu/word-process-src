@echo off
set PATH=%PATH%;C:\green\apache-maven-3.6.0\bin
set APP_HOME=%~dp0
mvn install:install-file ^
  -Dfile="%APP_HOME%/asu-commands-1.0.0-SNAPSHOT.jar" ^
  -DgroupId=me.asu ^
  -DartifactId=asu-commands ^
  -Dversion=1.0.0-SNAPSHOT ^
  -Dpackaging=jar
  :: -DlocalRepositoryPath=path-to-specific-local-repo

mvn install:install-file ^
  -Dfile="%APP_HOME%/asu-util-1.0.3-SNAPSHOT.jar" ^
  -DgroupId=me.asu ^
  -DartifactId=asu-util ^
  -Dversion=1.0.3-SNAPSHOT ^
  -Dpackaging=jar
