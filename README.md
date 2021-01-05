## About The Project

Yet another CLI for working with SQL databases.  

Here's why:
* When running queries from the command line on a remote Oracle Database, the output is difficult to read.  
  The purpose of this project is to have a Oracle Database cli tool that prints data like the MySQL cli.



### Built With

* [Spring Boot](https://spring.io/projects/spring-boot)
* [Maven](https://maven.apache.org/)
* [JLine3](https://github.com/jline/jline3)


### Requirements 

1. Java 10+
2. JDBC Connector for the database you connecting to.


### Supported Databases:

1. Oracle Database
2. MySQL

### Installation

1. Clone and compile application:
```sh
	git clone https://github.com/NeilAttewell/sql-client.git
	cd sql-client
	mvn clean install
  # Copy sql-client-cli/target/sql-client-cli-1.0.0.jar to desired location
   ```
2. Copy run script to /user/bin/
   ```sh
   cp run-script.sh /usr/bin/sql-client
   chmod +x /usr/bin/sql-client
   ```
3. Edit run script.  
  * Set path to JAVA HOME
  * Set path to where sql-client-cli-1.0.0.jar was saved
  * Set path to JDBC connectors

4. Execute program
```sh
sql-client --help
```
