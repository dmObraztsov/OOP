@echo off
setlocal enabledelayedexpansion

if exist out  rmdir /s /q out
if exist docs rmdir /s /q docs
if exist dist rmdir /s /q dist
mkdir out
mkdir docs
mkdir dist

if exist out\sources.txt del /q out\sources.txt
for /R src\main\java %%f in (*.java) do @echo %%f>> out\sources.txt
javac -d out @out\sources.txt || goto :e

javadoc -d docs -quiet -sourcepath src\main\java heapsort || goto :e

> out\MANIFEST.MF echo Manifest-Version: 1.0
>> out\MANIFEST.MF echo Main-Class: heapsort.App
jar cfm dist\heapsort.jar out\MANIFEST.MF -C out . || goto :e


java -jar dist\heapsort.jar %* || goto :e
goto :q

:e
echo Build or run failed. Make sure java/javac/jar are on PATH.
exit /b 1

:q
endlocal
