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
- should fix any errors

Dependencies
--------------

Project uses several libaries. 

- JDK 7
- JavaFX (included in JDK 7 install)
- Smack 3