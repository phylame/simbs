@echo off

REM This file is part of SIMBS (The Simple Information Management of Book Store).
REM Start the HyperSQL server process
REM Use database bookdb

REM Get the home directory of SIMBS
set SIMBS_HOME=%~dp0..

java -cp %SIMBS_HOME%\lib\hsqldb.jar org.hsqldb.Server -database.0 %SIMBS_HOME%\res\data\bookdb;user=book_admin;password=123456 -dbname.0 bookdb
