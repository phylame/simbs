@echo off
REM This file is part of SIMBS (The Simple Information Management of Book Store).
REM Start the manager tool of HyperSQL database
REM Use database bookdb

REM Get the home directory of SIMBS
set SIMBS_HOME=%~dp0..

javaw -classpath "%SIMBS_HOME%\lib\hsqldb.jar" org.hsqldb.util.DatabaseManagerSwing
