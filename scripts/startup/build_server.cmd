@echo off
cd backend
call mvnw.cmd package
java -jar .\target\eduhub_server-1.0-SNAPSHOT.jar