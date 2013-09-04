
set MANAGER_HOME=%~dps0..
set _JAVACMD="%JAVA_HOME%\bin\java.exe"
cd %MANAGER_HOME%
echo "Starting Floe/Java ..."
echo Using MANAGER_HOME:        %MANAGER_HOME%
echo Using JAVA_HOME:       %JAVA_HOME%

%_JAVACMD% -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=4001,suspend=n -cp .;%MANAGER_HOME%\lib\* edu.usc.pgroup.floe.startup.Manager %MANAGER_HOME%\conf\InstanceTypes.properties %MANAGER_HOME%\conf\Eucalyptus.properties%


