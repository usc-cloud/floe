
pushd manager\target\flow-manager-1.0.0-SNAPSHOT\bin
start manager.bat
popd

ping 127.0.0.1

pushd coordinator\target\flow-coordinator-1.0.0-SNAPSHOT\bin
start coordinator.bat
popd

ping 127.0.0.1

pushd container\target\flow-container-1.0.0-SNAPSHOT\bin
start container.bat
popd




