# Assumptions
1. Introducing intermediate Drone's states related to unit time of periodic task
2. Battery capacity is changing on 2% per time unit.
3. Total battery dischang for full drone work cycle is 22% (11 time units)
4. From LOADING to LOADED, LOADED to DELIVERING, DELIVERED to RETURNING required one time unit (time unit is predefined and may be configured)
5. From DELIVERING to DELIVERED, RETURNING to IDLE - 4 time units
6. To keep things much simplier there are introduced intermediate states between DELIVERING and DELIVERED, and between RETURNING and IDLE that has allowed considering all moves per one time unit with appropriated logging
7. Battery is charging in IDLE state with the same principle (2% per time unit) 
# Running instructions
1. Download ZIP from GitHub and Unzip
2. Enter folder with unzipped project
4. Run command maven wrapper: ```mvnw package```. All Unit Tests should be performed and as a result there will be created JAR file drones-medications-0.0.1.jar
5. Run command: ```java -jar target/drones-medications-0.0.1.jar```. As a result the application will start on the port 8080
6. By using Postman or any other Restful client there may be performed a sanity integration test according to the API
# API
1. ```/drones``` - POST method, register new drone. Requires serial number (100 characters max) and modelType (Lightweight, Middleweight, Cruiserweight, Heavyweight).
```
{
    "number": "Drone-1",
    "modelType":"Lightweight"
}
```
2. ```/drones/load``` - POST method, load selected drone with medication. Requires serial number and medicationCode.
  ```
{
    "droneNumber": "Drone-1",
    "medicationCode":"MED_3"
}
```
3. ```/drones/items/{droneNumber}``` - GET method, checking loaded medication items for a given drone. Where {droneNumber} is serial number.
4. ```/drones/available``` - GET method, checking available drones for loading.
5. ```/drones/battery/{droneNumber}``` - GET method, checking drone battery level for a given drone. Where {droneNumber} is serial number.
6. ```/drones/amount/items``` - GET method, check how many medication items have been loaded for each drone, ordered bythe amount in the descending order.
7. ```/drones/logs/{droneNumber}``` - GET method, return all logs of the drone with a given number. Where {droneNumber} is serial number.
  
# SQL script for initial DB population
1. [Script for fleet of 10 drones](https://raw.githubusercontent.com/Java50SergeevAleksandr/drones-medications/master/src/main/resources/fleet10.sql). Path: ```src/main/resources/fleet10.sql```
