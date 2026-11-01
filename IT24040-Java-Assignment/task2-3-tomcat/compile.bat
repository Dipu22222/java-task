@echo off
REM Edit these two paths for your computer
set TOMCAT_HOME=C:\Program Files\Apache Software Foundation\Tomcat 11.0
set JAVAC=javac
"%JAVAC%" --release 17 -cp "%TOMCAT_HOME%\lib\servlet-api.jar" -d myapp\WEB-INF\classes src\*.java
echo Done.
pause
