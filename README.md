#pmp-excel-ingestor
PMP Service that's responsible for ingesting contents of HFN Excel Spreadsheets into our DB

#Environment Setup
PMP is built on Spring Boot which makes it very easy to setup and build Java/spring based applications. All we need is JDK (Oracle JDK 8) and Maven (v 3.3). PMP uses MySQL (version 5.6) for database.

 * JDK can be downloaded from [Oracle download site](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
 * Maven can be downloaded from [Maven site](https://maven.apache.org/download.html)
 * For MySQL - Mac users can use MAMP and Windows users can download WAMP or XAMPP - these packages will provide few more useful tools.
   * MAPM for Mac can be downloaded from [here](https://www.mamp.info/en/downloads/) 
   * WAMP for Window can be downloaded from [here](http://www.wampserver.com/en/#download-wrapper) 

#Database setup
Verify that you are able to connect to mysql - Enter the password when prompted
 >> $ mysql -uroot -p

All the commands below use implicit host (127.0.0.1) and port (8889 for Mac, 3306 for Windows).
If you want to login to a specific host and port please use the following style to login to mysql
 >> $ mysql -h127.0.0.1 -P 8889 -uroot -p

Create the user and db by running the command:
  >> mysql -uroot -p < src/main/resources/db/create-db-users.sql

Verify that you can login using the new credentials
  >> mysql -upmpuser -pheartfulness -Dpmp

Load the schema into mysql
  >> mysql -upmpuser -pheartfulness -Dpmp < src/main/resources/schema.sql
  
#Database Migrations & Versions
Flyway updates the database from one version to the next using migrations.

The migrations are scripts in the form V<VERSION>__<NAME>.sql (with <VERSION> an underscore-separated version, e.g. ‘V1_1_description.sql’ or ‘V2_1_description.sql’).
It will scan the filesystem or your classpath for available migrations. It will compare them to the migrations that have been applied to the database. If any difference is found, it will migrate the database to close the gap.

Migrate should preferably be executed on application startup to avoid any incompatibilities between the database and the expectations of the code.

  *Example 1: We have migrations available up to version 9, and the database is at version 5.
     Migrate will apply the migrations 6, 7, 8 and 9 in order.
  *Example 2: We have migrations available up to version 9, and the database is at version 9.
     Migrate does nothing.

By default they live in a folder classpath:db/migration but we can modify that using flyway.locations property in application.properties.  

#Build using maven
 >>  mvn clean package (-DskipTests to skip tests)

#To run PMP locally
 >> java -jar target/pmp-excel-ingestor-0.0.1-SNAPSHOT.war
This one command runs the PMP application within an embedded tomcat server that's included within Spring Boot

#Access the site
 * http://localhost:8080/ingest/inputForm (select the valid excel file in src/test/resources/v21ValidEventDate.xlsm)
When prompted for User/Password on accessing the above link: use user/pmp

#To deploy war file to an existing tomcat installation
We can also copy over the target/pmp.war file to the webapps folder in tomcat. The URL will be http://localhost:8080/pmp/ingest/inputForm 

#Troubleshooting
###Problem while logging into MySQL as pmpuser 
If you are unable login to MySQL as pmpuser it probably means that there is a conflicting rule in the user table that needs to be removed
---Start of user login resolution----
Type the following commands
*  mysql -uroot -proot #To login t DB
*  use mysql; #To connect to the mysql metadata db
*  delete from user where User=''; #delete the entry from the table
*  quit; #Quit database
----end resolution------
Flush the tables
*  mysqladmin -uroot -proot flush-tables;
*  mysqladmin -uroot -proot reload;

### Problem in running build (mvn clean package) 
Sometimes you may face errors while doing the build, please ensure the ports are not blocked by any other processes. Also you can use the skip tests option in mvn command by appending -DskipTest (mvn clean package -DskipTests)

# Setting up PMP on Server machine (Amazon Linux/RHEL/CentOS)
##Install Java

```
  $ wget --no-cookies --no-check-certificate --header "Cookie: gpw_e24=http%3A%2F%2Fwww.oracle.com%2F; oraclelicense=accept-securebackup-cookie" "http://download.oracle.com/otn-pub/java/jdk/8u60-b27/jdk-8u60-linux-x64.rpm"
  $ sudo yum localinstall jdk-8u60-linux-x64.rpm 
  $ java -version
  $ export $JAVA_HOME=/usr/java/jdk1.8.0_60
```
##Install Maven
```
  $ wget http://www.eu.apache.org/dist/maven/maven-3/3.3.3/binaries/apache-maven-3.3.3-bin.tar.gz
  $ unzip apache-maven-3.3.3-bin.tar.gz 
  $ tar -xvzf apache-maven-3.3.3-bin.tar.gz 
  $ sudo mv apache-maven-3.3.3 /opt/maven
  $ sudo ln -s /opt/maven/bin/mvn /usr/bin/mvn
```

##Install tomcat7
```
  $ sudo yum install tomcat7
  $ sudo /etc/init.d/tomcat7 start 
```
  (Webapp folder will be /usr/share/tomcat7/webapps; config folder /etc/tomcat7/conf

##Install and configure Git
```
  $ sudo yum install git
  $ git config --global user.name "User Name"
  $ git config --global user.email user.email@email.com
```
  
##Checkout code and build
  * $ git clone https://github.com/hpoliset/pmp-excel-ingestor.git pmp-excel-ingestor
  * Update application properties to point to mysql database 
  * $ mvn clean package
  
##Deploy
  *  Copy pmp.war from target folder to tomcat webapp folder
  *  Access with application at https://hostname.org:8443/pmp

#PMP host - Regular Deployment process to be followed 

##Goto the deploy working directory
```
  $ cd /pmp/pmp-excel-ingestor/
  $ sudo su
```
  
##Get the latest from Git
```
  $ git pull
```
  
##Maven build command
```
  $ mvn clean package -DskipTests
```

##Stop the tomcat server
```
  $ /etc/init.d/tomcat7 stop
  $ /etc/init.d/tomcat7 status
```

##Backup existing war and copy new war file to tomcat webapps folder 
```
  $ mv /usr/share/tomcat7/webapps/pmp.war /tmp/pmp.war.DDMMYYYY
  $ cp target/pmp.war /usr/share/tomcat7/webapps
```

##Start the tomcat server
```
  $ /etc/init.d/tomcat7 status
```
  
##Validate server up and changes applied 
* URL should be accessible - https://pmp.heartfulness.org:8443/pmp and if possible, validate new changes are applied
