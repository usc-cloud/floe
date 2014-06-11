
set COORDINATOR_HOME=%~dps0..
set _JAVACMD="%JAVA_HOME%\bin\java.exe"
cd %COORDINATOR_HOME%
echo "Starting Floe/Java ..."
echo Using COORDINATOR_HOME:        %COORDINATOR_HOME%
echo Using JAVA_HOME:       %JAVA_HOME%

%_JAVACMD% -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=4002,suspend=n -cp .;%COORDINATOR_HOME%\lib\* edu.usc.pgroup.floe.startup.Coordinator


