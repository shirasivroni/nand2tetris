# Nand2Tetris Projects

This repository contains my solutions to selected projects from the [Nand2Tetris](https://www.nand2tetris.org) course.

The course walks through the complete construction of a modern computer system, from first principles: building hardware from logic gates, creating a virtual machine, developing a compiler, and implementing a simple operating system.

All projects were implemented in **Java**.

---

## Projects Overview

### Project 6: Assembler
**Goal:**  
Implement a two-pass assembler that translates Hack assembly language (`.asm` files) into Hack binary machine code (`.hack` files).

**Details:**
- Parses A-instructions (`@value`) and C-instructions (`dest=comp;jump`).
- Handles labels and symbols.
- Outputs correct 16-bit binary instructions.

📁 Code located under `06/`

---

### Project 7: Basic VM Translator
**Goal:**  
Implement a basic VM Translator that translates simple stack-based VM commands into Hack assembly code.

**Supported commands:**
- Arithmetic/Logical commands (`add`, `sub`, `neg`, `eq`, `gt`, `lt`, `and`, `or`, `not`)
- `push` and `pop` operations for the `constant`, `local`, `argument`, `this`, `that`, `temp`, and `pointer` segments.

📁 Code located under `07/`

---

### Project 8: Full VM Translator
**Goal:**  
Extend the VM Translator to handle:
- Branching commands (`label`, `goto`, `if-goto`)
- Function calling commands (`function`, `call`, `return`)
- Multi-file translation, generating bootstrap code (`Sys.init`)

📁 Code located under `08/`

---

### Project 10: Jack Syntax Analyzer
**Goal:**  
Implement a syntax analyzer for the Jack programming language.

**Details:**
- Parses `.jack` files according to the Jack grammar.
- Outputs structured XML files representing the syntactic structure of the code.
- Assumes the input Jack programs are error-free.

📁 Code located under `10/`

---

### Project 11: Jack Compiler
**Goal:**  
Extend the syntax analyzer to a full Jack-to-VM compiler.

**Details:**
- Implements a symbol table to track variable/function declarations.
- Generates executable VM code from Jack source programs.
- Supports class variables, subroutines, memory allocation, function calls, control flow, and more.

📁 Code located under `11/`

---

## How to Run

Each project contains a `Main.java` entry point.  
You can run each project by compiling and executing `Main.java`, passing as an argument a `.asm`, `.vm`, or `.jack` file or directory, depending on the project.

Example:

```bash
javac Main.java
java Main path/to/input
```

Make sure to have Java installed on your machine.

---

## Tools

For full testing, you can use the CPU Emulator, VM Emulator, and Hardware Simulator tools provided by the [Nand2Tetris website](https://www.nand2tetris.org/software).

---

## Notes
- This repository assumes that input files are syntactically correct, as per the project specifications.
- XML outputs may differ in whitespace, but match structural requirements exactly.

---

## Disclaimer

This repository contains my personal solutions to selected projects from the Nand2Tetris course, developed as part of my studies at Reichman University with Professor Shimon Schocken.  
All rights to the original course material, specifications, and tools belong to their respective authors: Professor Shimon Schocken and Professor Noam Nisan.
