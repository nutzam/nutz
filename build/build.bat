@echo off
IF EXIST temp del /F /S /Q temp
IF EXIST temp rmdir temp

REM ant -buildfile build.xml -propertyfile build.properties
ant -buildfile build.xml
@echo on