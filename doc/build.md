## Build

- For backend...
  - Provide necessary database connection values in `pok-backend-springboot`
  - Run `mvn install` 
  - Run `java -jar target/podsofkon.jar` for the Java backend

- For frontend...
  - Load `pok-frontend-unity` into Unity editor and simply select the platform to build to. 
  - Note the `Assets\PodsOfKon\Scripts\EnvProperties.cs` file that has two attributes
    - `bool isOffline` which is set to true by default so the game can be played offline
    - `string bankendAddress` which is the address where the `pok-backend-springboot` is running