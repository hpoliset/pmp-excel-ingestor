#pmp-excel-ingestor
PMP Service that's responsible for ingesting contents of HFN Excel Spreadsheets into our DB

#Environment Setup
Download and install Mysql server on your laptop/desktop.
Mac users can try MAMP and Windows users well you are on your own.
You can try searching for lamp for windows on Google. There are a number of options
you can install either WinLAMP or Wamp or XAMPP whichever suits your liking

Setup and run Mysql from the application of your choice.
Verify that you are able to connect to mysql
Command using Explicit host and port
>> mysql -uroot -proot

All the commands below use implicit host (127.0.0.1) and port (8889).
If you want to login to a specific host and port please use the following style to login to mysql
>>mysql -h127.0.0.1 -P 8889 -uroot -proot

Create the user and db by running the command:
>>mysql -uroot -p < src/main/resources/db/create-db-user.sql

Verify that you can login using the new credentials
>>mysql -upmpuser -pheartfulness -Dpmp

If you are unable login as pmpuser it probably means that there is a conflicting rule in the user table that needs to be removed
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

Load the schema into mysql
>> mysql -upmpuser -pheartfulness -Dpmp < src/main/resources/schema.sql

Build using maven
>> mvn clean package (-DskipTests to skip tests)

Run using the command
>> java -jar target/pmp-excel-ingestor-0.0.1-SNAPSHOT.war

Access the site using
>> http://localhost:8080/ingest/inputForm (select the valid excel file in src/test/resources/v21ValidEventDate.xlsm)

If prompted for User/Password on accessing the above link: use user/pmp

