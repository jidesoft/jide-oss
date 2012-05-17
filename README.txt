(Please enable word-wrap when viewing this file)

This is the readme for the JIDE Common Layer Open Source Project.  

Contents
===============
The contents of the distributions are:

	build.properties
	build.xml											
	docs													the Developer Guide
	examples											examples for JIDE Common Layer
	JIDE Common Layer.iml
	JIDE Common Layer.ipr					the JIDE Common Layer project for IntelliJ IDEA
	libs
	LICENSE.txt										the license statement
	oss-test-project							the test project based on Maven
	pom.xml												the config file for Maven
	properties									
	README.txt										this file
	src														the source code
	test													
	www														

Build
===============

We have immigrated our code base to GtiHub, and change our build method to Maven. 
We suggest you to build JIDE Common Layer project by Maven too, however we still reserve ant method here.

build by Maven
---------------
First of all, you should have Maven installed. You can find it from http://maven.apache.org/download.html.
 
Then make some configuration. Here is a brief instruction, you should make sure you have JDK 1.5+ installed
before your Maven installation. Then you just unpack Maven package and install it by defautl. After installation 
you should add a system environment variables M2_HOME which link to where Maven been installed, and add the 
%M2_HOME%\bin to PATH environment variables as well.

Finally, you come to the JIDE Common Layer directory, use Maven command to compile, package or deploy the project. 
mvn compile										compile the source code, classes will be in sub directory target/classes
mvn clean											clean previous build
mvn package										compile the source code and package jars to sub directory target
mvn deploy										compile the source code, package jars, and then deploy to Maven
                              You should get deploy keys from JIDESOFT if you want to deploy release.

build by Ant
---------------

You can use your favorite Java IDE to build this project. By default, we provided ant script (build.xml) to build 
the project. You can download ant from http://ant.apache.org/. Once you install it, you can run "ant" or "ant dist"

to build the whole source code and produce the jar file you need for your application. 

All available targets in build.xml are:

init
clean
compile
jar
javadoc
source
dist (default)


Test
===============
We also suggest to verify a release of JIDE Common Layer by Maven. You just switch to sub directory src-test-project,
modify the version number of JIDE Common Layer you want to test in pom.xml, you can find the version number from
http://search.maven.org by searching jide-oss. Finally you just type the command "mvn test". You should find messages 
of downloading jide-oss release, test cases succeed.

note
---------------
if you have tried to deploy a jide-oss release before you commit mvn test, you should clean the specific version
files in local repository, which is located in ~/.m2/repository/com/jidesoft/jide-oss/, for example
rm -rf ~/.m2/repository/com/jidesoft/jide-oss/3.4.0
