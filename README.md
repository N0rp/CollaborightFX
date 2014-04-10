CollaborightFX
==============

Collaborate right through JavaFX.

Install
--------

- run maven (create Eclipse project through mvn eclipse:eclipse)
- import into eclipse
- change java library to JDK 7
	- Project Properties
	- Java Build Path
	- Remove JDK 6
	- Add Library->JRE System Library->Alternate JRE->JDK 7
- add javafx RT 
	- Project Properties
	- Java Build Path
	- Add external JARs->"Java/Java Virtual Machines/jdk1.7.../Contents/Home/jre/lib/jfxrt.jar"
- Set Compiler Level to Java 7
    - Project Properties
    - Java Compiler
    - Compiler Compliance Level: 1.7
- should fix any errors

Java 8
----------

Java Version on the JAVA\_HOME has to be 8. Check that the correct version is referenced by using: $ echo $JAVA_HOME . On Mac Java can usually be found in /Library/Java/JavaVirtualMachines on Windows that would be C:\Program Files\Java\ . $ echo $JAVA_HOME  should result in /Library/Java/JavaVirtualMachines/jdk1.8.0.jdk/Contents/Home or similar. If not you have to change it:

	$ vim .bash_profile 
	export JAVA_HOME=$(/usr/libexec/java_home -v 1.8)
	$ source .bash_profile
	$ echo $JAVA_HOME
	/Library/Java/JavaVirtualMachines/jdk1.8.0.jdk/Contents/Home
 

Dependencies
--------------

Project uses several libaries. 

- JDK 7
- JavaFX (included in JDK 7 install)
- Smack 3 (smack, smackx, smackx-debug, smackx-jingle)
- ControlsFx
- SimpleXml