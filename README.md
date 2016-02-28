# Java Interface for the CLP Linear Solver

Java interface for the CLP linear solver, optimized towards fast model building and fast resolves. The interface is a vital part of the [QUASAR stochastic optimizer](https://github.com/quantego/quasar) and serves as the default linear solver. QUASAR typically solves millions of small linear programs and relies on a fast and light-weight interface.

## Overview

The interface provides a number of useful features for Java programmers who want to build either large models or apply decomposition strategies that require a lot of resolves. The interface has undergone several rounds of testing and profiling outside of unit tests.

#### Compact model creation using builders

Decision variables as well as their bounds and objective coefficients can be created in a single line. For example, ![x \in [-3,3]](https://latex.codecogs.com/gif.latex?x%20%5Cin%20%5B-3%2C3%5D) and ![y \in \mathbb{R}](https://latex.codecogs.com/gif.latex?y%20%5Cin%20%5Cmathbb%7BR%7D), would become
```
CLPVariable x = model.addVariable().lb(-3).ub(3);
CLPVariable y = model.addVariable().free();
```
We can alos write constraints as a series of *add* statements as opposed to using getters and setters. For example, ![3x + 4y \leq 10](https://latex.codecogs.com/gif.latex?3x&plus;4y%20%5Cleq%2010) can be expressed as
```
model.createExpression().add(3,x).add(4,y).leq(10);
```
Of course, this also works for an array of decision variables. For example

![\sum_{i=1}}^n 2x_i = 0](https://latex.codecogs.com/gif.latex?%5Csum_%7B%7Di%5En%202x_i%20%3D%200)

becomes
```
DecisionVariable[] X = model.addVariables().ub(10).build();
Expression e = model.createExpression();
e.add(2,X).eq(0);
```
In this way, models become more compact and readible.

#### Fast model building

Chunks of a model are buffered in heap for model building before being sent to the native lib. The size of the buffer can be set by the user. This helps to formulate models in a  row-by-row fashion, without bothering about possible performance bottlenecks through slow JNI access. Models with millions of constraints can be generated rather quickly.

#### Direct memory access

Once a model is build it only lives in native memory. To update model coefficients, the model is accessed directly in native memory via direct byte buffers that are provided by the [https://github.com/nativelibs4java/BridJ](BridJ) native interface. When the model gets gc'ed, native memory will be released automatically.

## Installation

#### Deployment via Maven

Simply add the following dependency to your `pom.xml`
```
TODO
```

#### All-in-one jar file

[Download here](https://github.com/loehndorf/clp-java/target)

The Java jar file contains the native libs for the most important host systems (Mac, Win 64, Linux 64), so there won't be ÙnsatisfiedLinkeError`messages and there is no messing around with setting build paths. A copy of the native libs will be copied to a temporary directory runtime. Simply import the jar.

## Changelog

## License

Software License Agreement (BSD License)

Copyright (c) -2016, Nils Loehndorf
All rights reserved.

Redistribution and use of this software in source and binary forms,
with or without modification, are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above
  copyright notice, this list of conditions and the
  following disclaimer.

* Neither the name of Nils Loehndorf nor the names of its
  contributors may be used to endorse or promote products
  derived from this software without specific prior
  written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
