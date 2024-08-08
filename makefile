all: build run

#make .jar file 
jar:
	javac -d build/ src/*.java
	jar cfe build/Assignment1.jar Main -C build/ .
	java -jar build/Assignment1.jar

build:
	javac -d build/ src/*.java

run:
	java -cp build Main

clean:
	rm -rf build
	mkdir build

.PHONY: build run clean jar