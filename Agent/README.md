# Build and Run

Steps to build:
  1. Go into the project's root folder "../hawk-agentj/"
  2. Open the console
  3. Type in "gradlew build" (or "gradlew build -x test" if you want to exclude tests)
    - Gradle will be downloaded, unzipped and installed on your machine
    - The project will be compiled
    - A fat JAR named "hawk-agentj-all.jar" will be created inside the folder "../build/libs/"
    - All tests will be run (if you have not excluded them)

Steps to run:
  1. Go to the location of your "hawk-agentj-all.jar"
  2. Open the console
  3. Type in "java -jar hawk-agentj-all.jar"
