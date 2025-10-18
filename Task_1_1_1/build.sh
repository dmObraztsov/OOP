#!/bin/bash
set -e

rm -rf build docs app.jar
mkdir -p build docs

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
jar cfe app.jar Main -C build .

# запуск
java -jar app.jar "$@"

