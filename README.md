## MICRONAUT  DATA EXAMPLE


The primary objective of this article is to help creating an end-to-end micronaut application with JPA /hibernate support connecting to a database. And for this we will be using Micronaut Data which is similar to Spring Data in the Spring-boot world .
So let’s get into the action !

### Step -1 : We need a SQL Database !!!
For this part we are going to use a readymade PostgreSQL database that can be run a docker container. To see how this is done please visit my earlier article on the same
https://hareeshgopidas.medium.com/postgresql-database-as-a-docker-container-66359167833a
PostgreSQL database as a docker container
Any technology enthusiast who love to explore new developments in the technology landscape would have come across…
hareeshgopidas.medium.com
So once that’s done you will have a database running on your machine on
http://localhost:5432/airport-db with credentials docker/docker
### Step 2 : Scaffolding a Micronaut project
Our next step is to create micronaut project from the scratch . Micronaut now provides two options for the same. You can either create it directly from the micronaut website ( Yes ! like spring initializer) and download the code onto your system .
The second option is through Micronaut CLI . For which you need to download the micronaut cli from this location ( https://micronaut.io/download.html ) .If you are a fan of using SDKman , you have that option available. For those who like the trivial method , download the cli from this location and then configure the path upto ‘/bin’ in your PATH variable .
Once you have successfully installed micronaut-cli you will be able to use the cli using the command “mn”
Check the “ mn help “ command and you can see the common commands available .
```
mn --help
Application generation commands are:
* create-app NAME
* create-cli-app NAME
* create-function-app NAME
* create-grpc-app NAME
* create-messaging-app NAME
Options:
 -h, — help Show this help message and exit.
 -v, — verbose Create verbose output.
 -V, — version Print version information and exit.
 -x, — stacktrace Show full stack trace when exceptions occur.
Commands:
 create-app Creates an application
 create-cli-app Creates a CLI application
 create-function-app Creates a Cloud Function
 create-grpc-app Creates a gRPC application
 create-messaging-app Creates a messaging application
 ```
So for scaffolding the project you need to use the create-app command
```
mn create-app com.hkg.micronaut.airport.airport-service --build maven
```
As you can see ,you can give the package structure you need for the application as a prefix to the project name . Also you can pass the build tool that you want to use as a parameter . Here in this case I have used the build tool maven . The default build tool will be gradle . i.e if you don’t give the build parameter , the source code will be generated with gradle support .
If you open the project structure you can see a Micronaut application has been created in the project in the package com.hkg.micronaut.airport with the classname “ Application.java” with the content given below
```
package com.hkg.micronaut.airport;
import io.micronaut.runtime.Micronaut;
public class Application {
public static void main(String[] args) {
Micronaut.run(Application.class, args);
}
}
```
### Step-3 Adding the dependencies for Micronaut Data
As the first step we need to add the support for Micronaut Data and JPA .
Open the pom.xml and add the dependency for micronaut-data-processor to annotation processor section
```
<annotationProcessorPaths>
   ……
   ……
  <path>
  <groupId>io.micronaut.data</groupId>
  <artifactId>micronaut-data-processor</artifactId>
  <version>2.0.1</version>
  </path>
 </annotationProcessorPaths>
The next step is to add the dependency for JPA/Hibernate support
<dependency>
  <groupId>io.micronaut.data</groupId>
  <artifactId>micronaut-data-hibernate-jpa</artifactId>        <version>2.0.1</version>
</dependency>
```
### Step-4 Code for Entity and Micronaut Data Repository
After this we can start writing the code for Entity and the Repository .
Airport Entity
```
package com.hkg.micronaut.airport.entity;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
@Entity
@Table(name = “AIRPORT”)
public class Airport implements Serializable {
/**
 *
 */
 private static final long serialVersionUID = 1L;
@Id
 @Column(name=”id”)
 @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = “apt_id_seq”)
 @SequenceGenerator(name = “apt_id_seq”, sequenceName = “apt_id_seq”, allocationSize=1)
 private Long id ;
@Column(name=”apt_code”)
 private String airportCode;
@Column(name=”apt_name”)
 private String airportName;
@Column(name=”city_name”)
 private String cityName;
@Column(name=”country”)
 private String countryName;
public Long getId() {
 return id;
 }
public void setId(Long id) {
 this.id = id;
 }
public String getAirportCode() {
 return airportCode;
 }
public void setAirportCode(String airportCode) {
 this.airportCode = airportCode;
 }
public String getAirportName() {
 return airportName;
 }
public void setAirportName(String airportName) {
 this.airportName = airportName;
 }
public String getCityName() {
 return cityName;
 }
public void setCityName(String cityName) {
 this.cityName = cityName;
 }
public String getCountryName() {
 return countryName;
 }
public void setCountryName(String countryName) {
 this.countryName = countryName;
 }
}
```
That’s a pure java persistence based entity class .
The next step is creating a Repository class. There are many built in Repository interfaces built in by Micronaut data like Spring Data provides a variety of repository classes. The one we are selecting here is CrudRepository which supports basic CRUD operations . Check the link given below to see the different options Micronaut provides for selecting a Repository interface
Micronaut Data
Data Repository Support for Micronaut Version: Micronaut Data is a database access toolkit that uses Ahead of Time…
micronaut-projects.github.io
AirportRepository.java
```
package com.hkg.micronaut.airport.repository;
import com.hkg.micronaut.airport.entity.Airport;
import io.micronaut.context.annotation.Executable;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;
@Repository
public interface AirportRepository extends CrudRepository<Airport, Long>{
 
 @Executable
 Airport findByAirportCode(String airportCode);
 
}
```
In the repository , I have defined a custom find method that helps to find the Airport object based on the airportCode field ( Please note if the Entity doesn’t have a field named AirportCode , we will get an compile time error ) .
### Step-5 Writing the Controller
Now that we have the Repository and Entity done, let’s go ahead and create the controller.
However I follow a practice to call the Repository through a Service instead of directly calling from Controller. This helps to handle any additional processing of requests or the retrieved data in a separate layer other than the controller and repository. So let’s start with AirportService .

```
AirportService.java
package com.hkg.micronaut.airport.service;
import javax.inject.Inject;
import javax.inject.Singleton;
import com.hkg.micronaut.airport.entity.Airport;
import com.hkg.micronaut.airport.repository.AirportRepository;
@Singleton
public class AirportService {
@Inject
 AirportRepository airportRepository;
/**
  * 
  * @return
  */
 public Iterable<Airport> getAllAirports() {
  return airportRepository.findAll();
 }
/**
  * 
  */
 public Airport getAirportByCode(String airportCode) {
  return airportRepository.findByAirportCode(airportCode);
 }
/**
  * 
  * @param airport
  * @return
  */
 public Airport saveOrupdateAirport(Airport airport) {
Airport airportSaved = null;
if (airport.getId()!=null &&   airportRepository.existsById(airport.getId())) {
   airportSaved = airportRepository.update(airport);
  } else {
   airportSaved = airportRepository.save(airport);
  }
return airportSaved;
}
/**
  * 
  * @param airportId
  */
 public void deleteAirport(Long airportId) {
  airportRepository.deleteById(airportId);
 }
}
```
Two important annotations to be noticed here are highlighted in bold . These are @Singleton and @Inject
The default scope of beans in Micronaut is Prototype so we have specifically marked the bean as a @Singleton .
@Inject is similiar to @Autowired in spring .
The next step is to create the Controller class — AirportController.java
```
package com.hkg.mircronaut.controller;
import javax.inject.Inject;
import com.hkg.micronaut.airport.entity.Airport;
import com.hkg.micronaut.airport.service.AirportService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.Put;
@Controller(“/airport”)
public class AirportController {
 
 /**
 * 
 */
 @Inject
 AirportService airportService;
 
 /**
 * 
 * @return
 */
 @Get(“/getAll”)
 @Produces(MediaType.APPLICATION_JSON) 
 public Iterable<Airport> getAll() {
 return airportService.getAllAirports();
 }
 
 /**
 * 
 * @param airportCode
 * @return
 */
 @Get(“/find/{airportCode}”) 
 @Produces(MediaType.APPLICATION_JSON) 
 public Airport getAirportByCode(String airportCode) {
 return airportService.getAirportByCode(airportCode);
 }
 
 /**
 * 
 * @param airport
 * @return
 */
 @Post(“/saveAirport”)
 @Produces(MediaType.APPLICATION_JSON) 
 public Airport saveOrUpdate(@Body Airport airport) {
 return airportService.saveOrupdateAirport(airport);
 
 }
 
 /**
 * 
 * @param airportId
 * @return
 */
 @Delete(“delete/{airportId}”)
 public HttpResponse delete(Long airportId) {
 airportService.deleteAirport(airportId);
 return HttpResponse.ok();
 }
 
}
```
As you can see the annotations used for the Controller is very similiar to the the annotations we use for RestControllers in spring-boot .
## Step-6 Configuring the Datasource
The final step before running the application is configuring the datasource to our locally running PostgreSQL database .
Before this add the runtime dependencies for the connection pool and drivers . Add the below given dependencies for HikariCP and PostgresSQL drivers .
```
<dependency>
<groupId>org.postgresql</groupId>
<artifactId>postgresql</artifactId>
<version>42.2.7</version>
</dependency>
<dependency>
<groupId>io.micronaut.sql</groupId>
<artifactId>micronaut-jdbc-hikari</artifactId>
</dependency>
```
Configure the HikariCP connection details and JPA configurations as given below in the application.yml
```
datasources:
  default:
    url: jdbc:postgresql://localhost:5432/airport-db            
    driverClassName: org.postgresql.Driver
    username: docker
    password: docker
jpa:
 default:
   packages-to-scan:
     - ‘com.hkg.micronaut.airport’
   properties:
    hibernate:
    hbm2ddl:
      auto: update
    show_sql: true
```
### Step-7 Let’s run the application.
We are ready now . Let’ build the application using mvn clean install command .
Once it’s successfully built use the below command to run the micronaut application .
mvn mn:run
You will be seeing an output like below if everything goes well .
```
[INFO] Scanning for projects…
[INFO]
[INFO] — — — — — — -< com.hkg.micronaut.airport:airport-service > — — — — — — — 
[INFO] Building airport-service 0.1
[INFO] — — — — — — — — — — — — — — — — [ jar ] — — — — — — — — — — — — — — — — -
[INFO]
[INFO] — — micronaut-maven-plugin:1.0.0:run (default-cli) @ airport-service — -
[INFO] Using ‘UTF-8’ encoding to copy filtered resources.
[INFO] Copying 2 resources
[INFO] Nothing to compile — all classes are up to date
←[36m01:19:47.903←[0;39m ←[1;30m[main]←[0;39m ←[34mINFO ←[0;39m ←[35mcom.zaxxer.hikari.HikariDataSource←[0;39m — HikariPool-1 — Starting…
←[36m01:19:48.026←[0;39m ←[1;30m[main]←[0;39m ←[34mINFO ←[0;39m ←[35mcom.zaxxer.hikari.HikariDataSource←[0;39m — HikariPool-1 — Start completed.
←[36m01:19:48.034←[0;39m ←[1;30m[main]←[0;39m ←[34mINFO ←[0;39m ←[35morg.hibernate.Version←[0;39m — HHH000412: Hibernate ORM core version 5.4.21.Final
←[36m01:19:48.327←[0;39m ←[1;30m[main]←[0;39m ←[34mINFO ←[0;39m ←[35mo.h.annotations.common.Version←[0;39m — HCANN000001: Hibernate Commons Annotations {5.1.0.Final}
←[36m01:19:48.528←[0;39m ←[1;30m[main]←[0;39m ←[34mINFO ←[0;39m ←[35morg.hibernate.dialect.Dialect←[0;39m — HHH000400: Using dialect: org.hibernate.dialect.PostgreSQL10Dialect
←[36m01:19:50.371←[0;39m ←[1;30m[main]←[0;39m ←[34mINFO ←[0;39m ←[35mio.micronaut.runtime.Micronaut←[0;39m — Startup completed in 4075ms. Server Running: http://localhost:8080
```

You can check http://localhost:8080/aiport/getAll to see the list of airports already created in the PostgreSQL database and verify if the application is working .
So let’s start exploring the world of micronaut .