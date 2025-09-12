#!/bin/sh
set -e

rm -rf out docs dist
mkdir -p out docs dist

find src/main/java -name "*.java" > out/sources.txt
javac -d out @out/sources.txt

javadoc -d docs -quiet -sourcepath src/main/java heapsort

printf 'Manifest-Version: 1.0\nMain-Class: heapsort.App\n' > out/MANIFEST.MF
jar cfm dist/heapsort.jar out/MANIFEST.MF -C out .

java -jar dist/heapsort.jar "$@"
