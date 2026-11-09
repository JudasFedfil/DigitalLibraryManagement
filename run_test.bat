@echo off
chcp 65001 > nul
if not exist bin mkdir bin
dir /s /B src\*.java > sources.txt
javac -encoding UTF-8 -d bin @sources.txt
del sources.txt
java -Dfile.encoding=UTF-8 -cp bin test.TestRunner
pause
