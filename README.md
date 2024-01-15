Scala JS Compiler
=================
Compiles list of Strings with Scala JS code to a JavaScript. Use this by reading your Scala JS source code and let backend respond with a JavaScript.


Using it Live
-------------
If this compiler is used live, use it as a dependency and make sure you find the dependency JAR files with a relative path. Usually it's something like "/WEB-INF/lib/". Check out the "web-demo" branch for a live demo. Make sure all dependencies are either with Scala source code or compiled with Scala JS.


Container
---------
This Scala JS Compiler uses ServletContext to load classes.


Scala Version 2.11
------------------

Lot's of compiler classes are deprecated in 2.12 and cannot be updated.


Installation
------------
For the scala 2 version, install Java 8, Scala 2.11 and sbt.
For the scala 3 version (3.3.1) any java version will work.
