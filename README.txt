(Please enable word-wrap when viewing this file)

This is the readme for the JIDE Common Layer Open Source Project.  

Contents
===============
The contents of the distributions are:

	build.properties
	build.xml
	docs                        the Developer Guide
	examples                    examples for JIDE Common Layer (contains only a README file to redirect you to JIDE product demo)
	libs                        external jars used by JIDE Common Layer
	LICENSE.txt                 the license statement
	oss-test-project            the test project based on Maven
	pom.xml                     the config file for Maven
	properties                  localized properties files
	README.txt                  this file
	src                         the source code
	test                        the test classes  
	JIDE Common Layer.iml       the module file for IntelliJ IDEA
	JIDE Common Layer.ipr       the JIDE Common Layer project for IntelliJ IDEA

Build
=====

We have migrated our code base from java.net to github, and changed our build method to Maven. We suggest you to build JIDE Common Layer project by Maven too. However the ant way to build the project is still available.

Build using Maven
---------------
Assuming you have JDK6+ installed. JDK5 may not work because we used JDK6 only methods in the source code. Please also install Maven from http://maven.apache.org/download.html. After installation you should add a system environment variable M2_HOME which link to where Maven been installed, and add the %M2_HOME%\bin to PATH environment variable as well.

Finally, you come to the JIDE Common Layer directory, use Maven command to compile and package the project. 
mvn compile    compile the source, classes output to directory "target/classes"
mvn clean      clean previous build
mvn package    compile the source and package jars to sub directory "target"

Build using Ant
---------------
You can use your favorite Java IDE to build this project. But to make it easy, we provided ant script (build.xml) to build the project. You can download ant from http://ant.apache.org/. Once you install it, you can run "ant" or "ant dist"

to build the whole source code and produce the jar file you need for your application. 

All available targets in build.xml are:

init
clean
compile
jar
javadoc
source
dist (default)

Configure Your Own POM
======================
If you just want to use JIDE Common Layer in your project which used Maven, you don't need to build the project unless you modified it locally. All you need to do is to add the following dependency to your pom.xml.

  <dependency> 
    <groupId>com.jidesoft</groupId> 
    <artifactId>jide-oss</artifactId> 
    <version>3.5.13</version> 
  </dependency> 

You can specify different versions if you want to use that version. You can find all available version numbers at http://search.maven.org by searching jide-oss. 
