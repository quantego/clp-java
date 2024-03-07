# Linear Programming in Java

Java interface for the [CLP linear solver](https://projects.coin-or.org/Clp), optimized towards fast model building and fast resolves.

## Overview

The interface provides a number of useful features for Java programmers who want to build either large models or apply decomposition strategies that require a lot of resolves. The interface has undergone several rounds of testing and profiling outside of unit tests.

#### Compact model creation using builders

Variables, constraints, and linear expressions are available as Java objects to make model building as easy as possible.

Decision variables as well as their bounds and objective coefficients can be created in a single line. For example, ![x \in [-3,3]](https://latex.codecogs.com/gif.latex?x%20%5Cin%20%5B-3%2C3%5D) and ![y \in \mathbb{R}](https://latex.codecogs.com/gif.latex?y%20%5Cin%20%5Cmathbb%7BR%7D), would become
```
CLPVariable x = model.addVariable().lb(-3).ub(3);
CLPVariable y = model.addVariable().free();
```
Since there is no operator overloading in Java, algebraic formulations are not possible. To avoid excessive use of setters, constraints can be built by using a series of *add* statements. For example, ![3x + 4y \leq 10](https://latex.codecogs.com/gif.latex?3x&plus;4y%20%5Cleq%2010) can be expressed as
```
model.createExpression().add(3,x).add(4,y).leq(10);
```
The *add* statements also accepts vectors of variables and coefficients. See the [javadoc](http://quantego.github.io/clp-java) for further reference.

#### Fast model building

Chunks of a model are buffered in heap for model building before being sent to the native lib. The size of the buffer can be set by the user. Models can be formulated in a row-by-row fashion, without bothering about possible performance bottlenecks. Models with millions of constraints can be generated quickly.

#### Direct memory access

Once a model is build it only lives in native memory, with the exception of referenced objects of variables and constraints which remain in heap. To update model coefficients, the model is accessed directly in native memory via direct byte buffers which speeds up resolves. When the model gets gc'ed, native memory will be released automatically.

## Installation

#### Maven
The project is available at the central repository. Simply add the following dependency to your pom file
```
<dependency>
  <groupId>com.quantego</groupId>
  <artifactId>clp-java</artifactId>
  <version>1.16.15</version>
</dependency>
```

#### All-in-one jar file
Download the [latest build](https://github.com/quantego/clp-java/releases/latest) of the jar from the release page.

The jar file contains the native libs for the most important host systems (Intel Mac, Apple Silcon, Win 64, Linux ARM and x86), so there won't be `UnsatisfiedLinkError`messages and there is no messing around with setting build paths. A copy of the native libs will be created in a temporary directory at runtime. Simply import the jar and you're done.

## Requirements

* Java JDK 8
* 64-bit Linux, Mac OS, or Windows

## Documentation

See the [javadoc](http://quantego.github.io/clp-java) for a full class reference.

## Author
Nils Löhndorf


