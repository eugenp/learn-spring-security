# About this Project

This is the codebase of the Learn Spring Security course Reference Project. <br/><br/>
This includes all the functionality from M1- M9 and M12 as well. 
 <br/> <br/>
 
#### How to Run and access the project:
The Project can be run as a Spring Boot app from an IDE, or from a command line using: mvn spring-boot:run
Once the Spring Boot app is started, the project can be accessed at: http://localhost:8081
 
#### Note about Authentication Providers:
By default, the DAOAuthenticationProvider is enabled, which uses the database driven mechanism to authenticate users.
To enable the CustomAuthenticationProvider instead, set the property google.auth.enabled=true. This custom provider uses 2 Factor Authentication using Google Authenticator App.

If the Custom 2-factor authentication provider is enabled, then newly registered users will need to provide a Google Authenticator code, in addition to the username and password to login.

#### Default Users and their credentials:
There are 2 default users defined: <br>
Default User with Admin Role: test@email.com/pass<br>
Default User with User Role: user@email.com/pass<br>
 
The default users do not need a Google Authenticator Code to login, regardless of which provider is enabled.

#### Persistence
This project uses _**in-memory**_ HSQLDB by default. This is also used while executing integration tests<br>
To enable MySQL DB just uncomment the properties relevant to MySQL in the **application.properties** file.<br>
and comment out the HSQLDB related properties.<br>

**_schema-mysql.sql_** - This can be used to create database schema if you want to **_manually_** create schema.<br>
**_data-mysql.sql_** - This can be used to initialize database with default data if you want to **_manually_** initialize schema.<br>
To use MySQL _**manually**_, following line must be commented out or change the value to **_none_** instead of **_always_** -  <br>
  `spring.datasource.initialization-mode=always`
 
   
 