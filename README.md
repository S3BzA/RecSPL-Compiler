# RecSPL Compiler

## Introduction

We made our compiler in Java. We completed phase 1, 2, 3 and 4 of the project and did not get to 5a or 5b.

As such, we only expect an arg for the input that is a file path such as `path/to/input.txt`. Further instructions follow.

When run, we output to the terminal:
1. The input program.
2. The Tokens listed out.
3. The Concrete syntax tree.
4. The Scope Tree and Symbol Tables
5. If type checks were passed

We did not, as previously mentioned, get to part 5 and thus no output files are generated.

## Assumptions of RecSPL Input

1. We assume the assignment token `< input` to have a space between the `<` and the `input`.
2. We would hope that the files could be formatted similar to:
```plaintext
main
num V_number,

begin
V_number = add(0.03 , 2 );  
end
num F_test(V_first, V_second, V_third){
text V_t1,
text V_t2,
text V_t3,

begin
return 0;
end

}
end
```
## Java Version

### Linux

openjdk 17.0.12 2024-07-16
OpenJDK Runtime Environment (build 17.0.12+7-Ubuntu-1ubuntu222.04)
OpenJDK 64-Bit Server VM (build 17.0.12+7-Ubuntu-1ubuntu222.04, mixed mode, sharing)

### Windows

openjdk 17.0.12 2024-07-16
OpenJDK Runtime Environment Temurin-17.0.12+7 (build 17.0.12+7)
OpenJDK 64-Bit Server VM Temurin-17.0.12+7 (build 17.0.12+7, mixed mode, sharing)

### Summary

Any Java 17 should work.

## Usage

The .jar can be run with the following command (linux):

```bash
java -jar path/to/compiler.jar path/to/input.txt
```

Should you want to use the makefile commands to run from the source code:
```bash
make all ARGS="path/to/input.txt"
```
