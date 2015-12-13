#pmp-excel-ingestor
PMP Service that's responsible for ingesting contents of HFN Excel Spreadsheets into our DB

#Environment Setup
PMP is built on Spring Boot which makes it very easy to setup and build Java/spring based applications. All we need is JDK (Oracle JDK 8) and Maven (v 3.3). PMP uses MySQL (version 5.6) for database.

JDK can be downloaded from [Oracle download site](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
Maven can be downloaded from [Maven site](https://maven.apache.org/download.html)
For MySQL - Mac users can use MAMP and Windows users can download WAMP or XAMPP - these packages will provide few more useful tools.
MAPM for Mac can be downloaded from [here](https://www.mamp.info/en/downloads/) 
WAMP for Window can be downloaded from [here](http://www.wampserver.com/en/#download-wrapper) 

#Database setup
Verify that you are able to connect to mysql - Enter the password
>> mysql -uroot -p

All the commands below use implicit host (127.0.0.1) and port (8889 for Mac, 3306 for Windows).
If you want to login to a specific host and port please use the following style to login to mysql
>>mysql -h127.0.0.1 -P 8889 -uroot -p

Create the user and db by running the command:
>>mysql -uroot -p < src/main/resources/db/create-db-users.sql

Verify that you can login using the new credentials
>>mysql -upmpuser -pheartfulness -Dpmp

Load the schema into mysql
>> mysql -upmpuser -pheartfulness -Dpmp < src/main/resources/schema.sql

#Build using maven
>> mvn clean package (-DskipTests to skip tests)

#To run PMP locally
>> java -jar target/pmp-excel-ingestor-0.0.1-SNAPSHOT.war
This one command runs the PMP application within an embedded tomcat server that's included within Spring Boot

#Access the site
>> http://localhost:8080/ingest/inputForm (select the valid excel file in src/test/resources/v21ValidEventDate.xlsm)
When prompted for User/Password on accessing the above link: use user/pmp

#To deploy war file to an existing tomcat installation
We can also copy over the target/pmp.war file to the webapps folder in tomcat. The URL will be http://localhost:8080/pmp/ingest/inputForm 

#Troubleshooting
###Problem while logging into MySQL as pmpuser 
If you are unable login to MySQL as pmpuser it probably means that there is a conflicting rule in the user table that needs to be removed
---Start of user login resolution----
Type the following commands
>> mysql -uroot -proot #To login t DB
>> use mysql; #To connect to the mysql metadata db
>> delete from user where User=''; #delete the entry from the table
>> quit; #Quit database
----end resolution------

Flush the tables
>> mysqladmin -uroot -proot flush-tables;
>> mysqladmin -uroot -proot reload;
### Problem in running build (mvn clean package) 
Sometimes you may face errors while doing the build, please ensure the ports are not blocked by any other processes. Also you can use the skip tests option in mvn command by appending -DskipTest (mvn clean package -DskipTests)
