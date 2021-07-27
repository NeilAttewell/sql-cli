## About The Project

Yet another CLI for working with SQL databases.  

Here's why:
* When running queries from the command line on a remote Oracle Database, the output is difficult to read.  
  The purpose of this project is to have a Oracle Database cli tool that prints data like the MySQL cli.

### Current Features

1. Load queries from stdin
2. Load queries from file 
3. Load queries from console (in interactive mode)
4. (Console) Multi-line - cursor can be positioned with up, down, left, right arrows for query editing.
5. (Console) Search through History - Ctrl+R
6. Write output to stdout
7. Write output to file
8. Write output to console (in interactive mode)
9. Format query results as a table.
10. Format query results as a pivot table (column name and value - one per line)
11. Format query results as JSON Array
12. List aliases
13. Execute above alias.  eg:  When connecting to oracle, "show tables" lists all tables in the database.
14. Add/Edit aliases
15. Support variables  

### Future Features

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

## Notes
This project is still very new, so far, I've only spent a couple hours working on it.  
I've tested it and it works.  
There is little to no documentation just yet, I need to put in the effort to add this.  
I need to add unit tests to test things like command line argument parsing + parsing and executing queries/commands
