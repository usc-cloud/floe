
set CONTAINER_HOME=%~dps0..
set _JAVACMD="%JAVA_HOME%\bin\java.exe"
cd %CONTAINER_HOME%
echo "Starting Floe/Java ..."
echo Using CONTAINER_HOME:        %CONTAINER_HOME%
echo Using JAVA_HOME:       %JAVA_HOME%
echo USING params Manager host : %1 Manager Port :%2
%_JAVACMD% -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=4000,suspend=n -cp .;%CONTAINER_HOME%\lib\*;%CONTAINER_HOME%\lib\sigar-bin\lib\* edu.usc.pgroup.floe.startup.Container %CONTAINER_HOME%\conf\Container.properties





