This is the readme for the test project for JIDE Common Layer release

Contents
===============
	README.txt											this file 
	build.properties
	build.xml
	libs
	pom.xml													the config file for Maven
	src															the source code

Test
===============
We suggest to verify a release of JIDE Common Layer by Maven. You just switch to sub directory src-test-project,
modify the version number of JIDE Common Layer you want to test in pom.xml, you can find the version number from
http://search.maven.org by searching jide-oss. Finally you just type the command "mvn test". You should find messages 
of downloading jide-oss release, test cases succeed.

note
---------------
if you have tried to deploy a jide-oss release before you commit mvn test, you should clean the specific version
files in local repository, which is located in ~/.m2/repository/com/jidesoft/jide-oss/, for example
rm -rf ~/.m2/repository/com/jidesoft/jide-oss/3.4.0