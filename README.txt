These instructions assume Windows is being used

Install git for windows, setup ssh keys and clone repository 'git clone git@bitbucket.org:hedgehog89/mad_project.git'

Install Eclipse IDE for Java EE Developers - http://www.eclipse.org/downloads/

In Eclipse install the ADT plugin - http://developer.android.com/sdk/installing/installing-adt.html
	-Once installed add the Android 2.3.3 package. Window -> Android SDK Manager
		-Under Android 2.3.3 (API 10) select:
			-SDK Platform
			-Samples for SDK
			-Google APIs
	
Install Maven 3.0.4 - http://maven.apache.org/download.html
	-Download and extract the binary zip to somewhere on your computer.
	-Create the MAVEN_HOME environment variable
	-add %MAVEN_HOME%\bin to the PATH
	
Install Andoid-m2e 
	-In eclipse help -> Eclipse MarketPlace
	-Search for 'android m2e' full name should be 'Android Configurator for M2E'
	
Setup the Android Maven Plugin
	-no download needed
	-full instructions http://code.google.com/p/maven-android-plugin/wiki/GettingStarted
	-Create an ANDROID_HOME Environment variable which should point to location of the 
	-Add %ANDROID_HOME%\platform-tools and %ANDROID_HOME%\tools to the PATH
	
Import the project into Eclipse
	-In Eclipse File -> Import -> Maven -> Existing maven Projects
	-Browse to the the GeoCaching folder in the Checked out source code.
	-Import into Eclipse

WARNING: When launching the application either for run or debug make sure the active file is a Java file and not XML or it will fail to launch
	