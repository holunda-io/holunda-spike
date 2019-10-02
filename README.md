# holunda-spike

Small, transient examples and demonstrations that do not justify an own repo.

Run 
```bash
./mvnw clean install 
```
to build everything

## Maven Profiles

There are two profiles controlling the selection of Camunda (CE or EE). You will need the 
Enterprise access in your maven settings to retrieve the EE version.

Run with
``` 
./mvn -Pcamunda-ee <your-targets>
```
to activate the Camunda EE profile (or activate it in your IDE).
  
