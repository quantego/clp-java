# Java Interface for the CLP Linear Solver

Java interface for the CLP linear solver, optimized towards fast model building and fast resolves. The interface is a vital part of the [QUASAR stochastic optimizer](https://github.com/quantego/quasar) and serves as the default linear solver. QUASAR typically solves millions of small linear programs and relies on a fast and light-weight interface.

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
The *add* statements also accepts vectors of variables and coefficients.

#### Fast model building

Chunks of a model are buffered in heap for model building before being sent to the native lib. The size of the buffer can be set by the user. Models can be formulated in a row-by-row fashion, without bothering about possible performance bottlenecks. Models with millions of constraints can be generated quickly.

#### Direct memory access

Once a model is build it only lives in native memory, with the exception of referenced objects of variables and constraints which remein in heap. To update model coefficients, the model is accessed directly in native memory via direct byte buffers that are provided by the [https://github.com/nativelibs4java/BridJ](BridJ) native interface. When the model gets gc'ed, native memory will be released automatically.

## Installation

#### Deployment via Maven

I just started working with Maven, so this is still on the todo.

#### All-in-one jar file

The Java jar file contains the native libs for the most important host systems (Mac, Win 64, Linux 64), so there won't be `UnsatisfiedLinkeError`messages and there is no messing around with setting build paths. A copy of the native libs will be copied to a temporary directory runtime. Simply import the jar. See release page to download the latest built.


