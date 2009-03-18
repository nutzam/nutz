@echo off
REM IF EXIST temp del /F /S /Q temp
REM IF EXIST temp rmdir temp
REM ant -buildfile build.xml -propertyfile build.properties
ant -f %1
@echo on