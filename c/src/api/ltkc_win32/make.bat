@echo off

echo LTKC Build Started
echo Running gencode.bat to generate supporting files.

call "gencode.bat"

echo "Starting Visual Studio 2019..."
set configuration="Release"

call "C:\Program Files (x86)\Microsoft Visual Studio\2019\Enterprise\VC\Auxiliary\Build\vcvars64.bat"
goto buildProjects

:buildProjects
echo "Building LTKC Project Started..."
devenv "proj\VS_LTKC.sln" /Clean
devenv "proj\VS_LTKC.sln" /Build %configuration%
goto exitBatch
rem goto copyFilesToMercuryAPISDK

rem :copyFilesToMercuryAPISDK
rem echo "Copying ..."
rem goto exitBatch

:exitBatch
echo "Execution Completed"
@echo on