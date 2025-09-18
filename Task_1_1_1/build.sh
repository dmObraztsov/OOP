#!/bin/bash
set -e

rm -rf build docs dist app.jar
mkdir -p build docs dist

# компиляция исходников
javac -d build \
  src/main/java/HeapSort.java \
  src/main/java/HeapUtils.java \
  src/main/java/Main.java


# генерация Javadoc
javadoc -d docs -Xdoclint:none \
  src/main/java/HeapSort.java \
  src/main/java/HeapUtils.java \
  src/main/java/Main.java

# создаём JAR
jar cf Main -C build .

# запуск
java -jar app.jar "$@"

