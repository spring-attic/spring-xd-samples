Spring XD Sqoop Batch Job Example
=================================

This is a brief example showing how to work with the Sqoop batch job that is provided with Spring XD. [Apache Sqoop](https://sqoop.apache.org/) is a tool designed for efficiently transferring bulk data between Apache Hadoop and structured datastores such as relational databases.

## Requirements

You need to have Spring XD 1.1.0 or later installed

In order to follow this example you will need to have the following installed and running:

* Spring XD version 1.1.x ([Instructions](https://docs.spring.io/spring-xd/docs/current/reference/html/#getting-started)) running in `xd-singlenode` mode.
* Apache Hadoop version 2.6.x ([Instructions](https://docs.spring.io/spring-xd/docs/current/reference/html/#installing-hadoop)) running with namenode listening on `hdfs://localhost:8020`.

> This example is developed to be run on a Linux or Mac OS X system.

## Overview

We will download some CSV data and copy that to HDFS. Then we will export the data from HDFS to an SQL database table. Once this is done we'll show an example of how to import the data from the database table to new CSV files in HDFS.

## Setup

All of the paths in this example is based on the directory where the Spring XD Shell is located. The XD Shell is part of the Spring XD installation. To go to the XD Shell directory change to the following directory:

    cd [path-to-Spring-XD]/shell

Depending on your prompt configuration, you should see the word shell in your current directory info. We will run some commands from the OS shell and most commands from the Spring XD Shell. To make it clear what prompt we are currently at, we will use `$ ` to show when we are at the OS shell prompt and `xd:> ` when we are at a Spring XD Shell prompt. 

## Step 1: Get some sample CSV data

Let's grab some snowfall data from NOAA - ftp://ftp.ncdc.noaa.gov/pub/data/surface-snow-products/regional-snowfall-index_c20150206_v2.csv

We will get this file and then remove the first line that contains the column names before we copy the resulting file to HDFS.

From the OS shell run the following commands:

    $ curl ftp://ftp.ncdc.noaa.gov/pub/data/surface-snow-products/regional-snowfall-index_c20150206_v2.csv -o snowfall.csv
    $ sed -i -e '1d' snowfall.csv

## Step2: Create the database table

We will create the table that we can use with the Sqoop jobs we will run. You can use any relational database that Sqoop supports as long as you provide the JDBC driver in the Spring XD `lib` directory. We will for simplicity's sake use the built in HSQLDB database provided by Spring XD in singlenode mode.

The DDL used for HSQLDB:

    CREATE TABLE
        SNOWFALL
        (
            REGION VARCHAR(100),
            START VARCHAR(10),
            END VARCHAR(10),
            RSI DECIMAL(9,3),
            CATEGORY CHAR(1),
            TERM1PCT DECIMAL(9,3),
            TERM2PCT DECIMAL(9,3),
            TERM3PCT DECIMAL(9,3),
            TERM4PCT DECIMAL(9,3),
            AREA0 INT,
            POP0 INT,
            AREA1 INT,
            POP1 INT,
            AREA2 INT,
            POP2 INT,
            AREA3 INT,
            POP3 INT,
            AREA4 INT,
            POP4 INT,
            STORM_ID VARCHAR(20),
            REGION_CODE CHAR(3),
            YEAR VARCHAR(4),
            MONTH VARCHAR(2)
        );

You might have a preferred SQL tool, and if so, you can connect to the Spring XD database using the JDBC URL `jdbc:hsqldb:hsql://localhost:9101/xdjob` and a username of `sa` with no password.

If you don't have an SQL tool we can quickly download the HSQLDB SqlTool and use that. Just run:

```
$ curl -L -O https://search.maven.org/remotecontent?filepath=org/hsqldb/sqltool/2.3.2/sqltool-2.3.2.jar
$ curl -L -O https://search.maven.org/remotecontent?filepath=org/hsqldb/hsqldb/2.3.2/hsqldb-2.3.2.jar
```

To create the database table with the HSQLDB SqlTool we can run the following:

```
$ java -jar sqltool-2.3.2.jar --inlineRc url=jdbc:hsqldb:hsql://localhost:9101/xdjob,user=sa,password= --sql "CREATE TABLE SNOWFALL (REGION VARCHAR(100), START VARCHAR(10), END VARCHAR(10), RSI DECIMAL(9,3), CATEGORY CHAR(1), TERM1PCT DECIMAL(9,3), TERM2PCT DECIMAL(9,3), TERM3PCT DECIMAL(9,3), TERM4PCT DECIMAL(9,3), AREA0 INT, POP0 INT, AREA1 INT, POP1 INT, AREA2 INT, POP2 INT, AREA3 INT, POP3 INT, AREA4 INT, POP4 INT, STORM_ID VARCHAR(20), REGION_CODE CHAR(3), YEAR VARCHAR(4), MONTH VARCHAR(2));"
```

## Step3: copy CSV file to HDFS

First, let's start the Spring XD Shell by running:

    $ ./bin/xd-shell

Now we can configure the URL for the Hadoop namenode and run some commands to copy the sample data to HDFS. (We assume that you already have the /xd directory created in HDFS, if not just create that as well with a `hadoop fs mkdir /xd` command).

```
xd:> hadoop config fs --namenode hdfs://localhost:8020
xd:> hadoop fs mkdir /xd/noaa
xd:> hadoop fs copyFromLocal --from snowfall.csv --to /xd/noaa/snowfall.csv
```
We should now have our data in HDFS, just double check with the command:

```
xd:> hadoop fs ls /xd/noaa
Found 1 items
-rw-r--r--   3 trisberg supergroup     611503 2015-02-20 00:17 /xd/noaa/snowfall.csv
```

## Step4: Sqoop export

We first create the job using the `export` command. In the args parameter we provide all additional Sqoop options we need for our job to run like directory where files to be exported are, table name, connect string, username and password. We also specify batch mode to speed up the process.

```
xd:> job create exp_snowfall --definition "sqoop --command=export --args='--table SNOWFALL --connect jdbc:hsqldb:hsql://localhost:9101/xdjob --username sa --export-dir /xd/noaa --optionally-enclosed-by \" --batch'"
```

Now that we have the job we can deploy and launch it.

```
xd:> job deploy exp_snowfall
xd:> job launch exp_snowfall
```

To check on the status of the job run:

```
xd:> job execution list
```

Check the execution status and once that is COMPLETED we can close the XD Shell to check the results in the database.

```
xd:> exit
```

To see how many rows we exported to the database we can run a simple `SELECT COUNT(*) FROM SNOWFALL` query.

If you downloaded the HSQLDB SqlTool you can use the following command:

```
$ java -jar sqltool-2.3.2.jar --inlineRc url=jdbc:hsqldb:hsql://localhost:9101/xdjob,user=sa,password= --sql "SELECT COUNT(*) FROM SNOWFALL;"
```

The count should be 4179.

## Step5: Sqoop import

Now it's time to copy the data the other way, from the database to a CSV files in HDFS. We start by creating a n XD job for the import.

Start the XD shell again and configure the Hadoop namenode to use:

```
$ ./bin/xd-shell
xd:> hadoop config fs --namenode hdfs://localhost:8020
```

When creating the job, we need to specify the connection string and username along with the table name as previously. We also specify the target directory since the default is to copy the data to our /user/<username> directory in HDFS. Since we don't have a primary key for the table we do need to specify the column to split the data between the mappers or use a single mapper. This is a small table so just using one mapper should be fine.

```
xd:> job create imp_snowfall --definition "sqoop --command=import --args='--table SNOWFALL --connect jdbc:hsqldb:hsql://localhost:9101/xdjob --username sa --target-dir /xd/import --num-mappers 1'"
```

We specified the HDFS target directory as `/xd/import`. If this directory already exists, choose a different name or remove the existing directory using:

```
xd:> hadoop fs rm /xd/import --recursive
```

It's time to deploy and launch or import job.

```
xd:> job deploy imp_snowfall
xd:> job launch imp_snowfall
```

Again, check on the status of the job using:

```
xd:> job execution list
```

Once the job status is COMPLETE, we can check that we actually have some data in the target directory.

```
xd:> hadoop fs ls /xd/import --recursive 
-rw-r--r--   3 trisberg supergroup          0 2015-02-20 10:58 /xd/import/_SUCCESS
-rw-r--r--   3 trisberg supergroup     604619 2015-02-20 10:58 /xd/import/part-m-00000
```

> If any of your jobs fail you can look at the `sqoop.log` that is attached to the step execution context accessible from the Spring UI [Job Executions page](http://localhost:9393/admin-ui/#/jobs/executions).

