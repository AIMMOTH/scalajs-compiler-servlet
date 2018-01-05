Scala JS Compiler
=================
Compiles list of Strings with Scala JS code to a JavaScript. Use this by reading your Scala JS source code and let backend respond with a JavaScript.

Using it Live
-------------
If this compiler is used live, use it as a dependency and make sure you find the dependency JAR files with a relative path. Usually it's something like "/WEB-INF/lib/". Check out the "web-demo" branch for a live demo. Make sure all dependencies are either with Scala source code or compiled with Scala JS.

Container
---------
This Scala JS Compiler uses ServletContext to load classes.


Installation
------------
Install Java 8, Scala 2.11 and Maven 3.3.


Run and Deploy
--------------

Use maven to build with $ mvn clean package install 