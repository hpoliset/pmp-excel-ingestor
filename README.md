# pmp-excel-ingestor
PMP Service that's responsible for ingesting contents of HFN Excel Spreadsheets into our DB

Create the use and db by running the command:
mysql -uroot -p < src/main/resources/create-db-users.ql

Create the schema
mysql -upmpuser -pheartfulness -Dpmp < src/main/resources/schema.sql

Build using maven
mvn clean package (-DskipTests to skip tests)

Run using the command
java -jar target/pmp-excel-ingestor-0.0.1-SNAPSHOT.war

Access the site using
http://localhost:8080/ingest/inputForm (select the valid excel file in src/test/resources/v21ValidEventDate.xlsm)

If prompted for User/Password on accessing the above link: use user/pmp

