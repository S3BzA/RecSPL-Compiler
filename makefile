all: build run

jar:
	javac -d build/ src/*.java
	jar cfe build/Compiler.jar Main -C build/ .
	java -jar build/compiler.jar

build:
	javac -d build/ src/*.java

run:
	java -cp build Main $(ARGS)

clean:
	rm -rf build
	mkdir build

.PHONY: build run clean jar