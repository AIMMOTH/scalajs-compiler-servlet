Scala JS Compiler
=================
With ServletContext it loads libraries and compiles list of Strings with Scala JS code to a JavaScript. Use this by reading your Scala JS source code and let backend respond with a JavaScript.


Using it Live
-------------
If this compiler is used live, use it as a dependency and make sure you find the dependency JAR files with a relative path. Usually it's something like "/WEB-INF/lib/". Check out the "web-demo" branch for a live demo. Make sure all dependencies are either with Scala source code or compiled with Scala JS.


Container
---------
This Scala JS Compiler uses ServletContext to load classes.


Scala Version 3.3.1 and Java 21
-------------------------------

Thanks to @VirtusLab for upgrading this library.


Run and Deploy
--------------

Use SBT to build. Use `> sbt publishM2` to create Maven library