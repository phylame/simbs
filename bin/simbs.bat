@echo off
REM This file is part of SIMBS (The Simple Information Management of Book Store).
REM Run SIMBS application
REM Use database bookdb

REM Get the home directory of SIMBS
set SIMBS_HOME=%~dp0..

javaw -jar "%SIMBS_HOME%\lib\simbs.jar" "%@"
