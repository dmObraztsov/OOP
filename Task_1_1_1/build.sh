#!/bin/bash
set -e

rm -rf build docs dist app.jar
mkdir -p build docs dist

# компиляция исходников
javac -d build \
  src/main/java/HeapSort.java \
  src/main/java/HeapUtils.java

# генерация Javadoc
javadoc -d docs -Xdoclint:none \
  src/main/java/HeapSort.java \
  src/main/java/HeapUtils.java

# создаём JAR
jar cf dist/heapsort-1.0.0.jar -C build .

