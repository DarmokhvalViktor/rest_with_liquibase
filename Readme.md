## REST service with CRUD and some additional operations on entities "Car" and "Owner"

### To start this application, you need to:
1. Open maven->package. Or use command mvn clean package inside project. That will create directory backend, where file "app.jar" is located. This file is used to create Docker image.
2. Open Docker 
3. Use command docker build . | docker compose up

#### Additional information:
- To test one can use files "car_data.json"(25%+- valid records), "car_data2.json"(invalid file structure, will throw 500 error), "car_data3.json"(2 valid and 2 invalid records) in the root of the project. Or one can use RandomFileWithCarsGenerator class to generate file with custom amount of records. Example how to use this class in CarService class. File should be created in "resources" folder.
- Postman collection lies in the root as well, name "rest_with_liquibase.postman_collection".
- There are two liquibase scripts 000_drop_all.sql that deletes all tables from database and 001_initial_setup.sql that creates DB schema and populates with some additional data. In db.changelog-master.yaml one can change. For test purposes both scripts were executed always. 
- Application expects valid .json file, even if they don't match expected entities. Examples:
- #### Invalid structure:  
[{
  "accessoriesIds": [
  3,
  11
  ] (invalid, missing comma ",")
  "yearOfRelease": 1997,
  "modelId": 19,
  "wasInAccident": false,
  "brandId": 15,
  "ownerId": 2,
  "mileage": 166065
  }]
- #### Valid structure
  [{
  "accessoriesIds": [
  3,
  11
  ],
  "yearOfRelease": 1997,
  "Non_existing_field_in_entity":"some_value"
  "modelId": 19,
  "wasInAccident": false,
  "brandId": 15,
  "ownerId": 2,
  "mileage": 166065
  },]
