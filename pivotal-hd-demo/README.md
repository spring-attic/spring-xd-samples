# Demo using Spring XD with Pivotal HD

_(Using Pivotal HD 2.0 and Spring XD 1.0.0.RC1)_

## Preparing the VM

### Step 1: Download, install and start Pivotal HD 2.0 VM

The "Pivotal HD 2.0 Single Node VM" can be downloded from: 
https://network.gopivotal.com/products/pivotal-hd

### Step 2: Download Spring XD 1.0 RC1 release

From the Pivotal HD VM download Spring XD 1.0 RC1 release using this link: 
http://repo.spring.io/simple/libs-milestone-local/org/springframework/xd/spring-xd/1.0.0.RC1/spring-xd-1.0.0.RC1-dist.zip

### Step 3: Unzip, configure and start Spring XD 1.0 RC1 release

Unzip the `spring-xd-1.0.0.RC1.zip` file into the home directory of the gpadmin user. This should create 
a `/home/gpadmin/spring-xd-1.0.0.RC1` directory.

We need to modify the connection properties for HDFS which are specified in `config/servers.yml`. We can edit that file using this command:

    gedit /home/gpadmin/spring-xd-1.0.0.RC1/xd/config/servers.yml

Find the Hadoop properties and uncomment that section and change the content to the following:

```
---
# Hadoop properties
spring:
  hadoop:
   fsUri: hdfs://pivhdsne:8020
```

Open a command prompt and enter the following commands:

    cd /home/gpadmin/spring-xd-1.0.0.RC1/xd
    ./bin/xd-singlenode --hadoopDistro phd20

We need to specify the hadoop distro as phd20 since we are running against Pivotal HD 2.0.

### Step 4: Start the Spring XD shell

Open another command prompt and enter the following command:

    /home/gpadmin/spring-xd-1.0.0.RC1/shell/bin/xd-shell --hadoopDistro phd20
    
Once the shell starts up we need to need to set the hdfs configuration so we can run Hadoop fs commands from the shell:

    xd:> hadoop config fs --namenode hdfs://pivhdsne:8020
    

## The Demos

### Demo1 - Twitter search using HAWQ external table in hdfs

*This is a Spring XD "twittersearch | transform | hdfs" stream.*

In order to query the Twitter data with an HAWQ external table we need to provide the Twitter data in a 
tab-delimited format with one line per row. For this demo we will be querying the hash tags so we will create one 
line per hash tag, so data will be de-normalized since tweets with multiple hash tags will have additional rows. 
The easiest way to do this reformatting is is to provide a transformer script. We have written one in Groovy that can 
be viewed or downloaded here: 
[tweets-delim.groovy](https://raw.github.com/spring-projects/spring-xd-samples/master/pivotal-hd-demo/modules/processor/scripts/tweets-delim.groovy)

To use this script in our XD stream we need to copy it to the Spring XD `modules/processor/scripts` directory. We can do that 
by opening another command prompt and entering the following commwnd:

    wget -O /home/gpadmin/spring-xd-1.0.0.RC1/xd/modules/processor/scripts/tweets-delim.groovy https://raw.github.com/spring-projects/spring-xd-samples/master/pivotal-hd-demo/modules/processor/scripts/tweets-delim.groovy --no-check-certificate

Last config task is to add your Twitter consumerKey and consumerSecret to `config/mdules/modules.yml`. We can edit this file using:

    gedit /home/gpadmin/spring-xd-1.0.0.RC1/xd/config/modules/modules.yml
    
Uncomment the following lines and add your consumer key and secret to this file:

```
twitter:
  consumerKey: {your-consumer-key}
  consumerSecret: {your-consumer-secret}
  accessToken: {your-access-token}
  accessTokenSecret: {your-access-token-secret}
```

See the [Spring XD docs](https://github.com/spring-projects/spring-xd/wiki/Sources#wiki-twitter-search) for more details.

We are now ready to create and deploy the stream, so we switch back to the Spring XD shell:

    xd:> stream create --name tweets --definition "twittersearch --query='hadoop' --outputType=application/json | transform --script=tweets-delim.groovy | hdfs --rollover=10000" --deploy

We should see the stream get created in the Spring XD admin window. From the shell we can list the streams using:

    xd:> stream list
    
We let this stream run for a little while to collect some tweets. We can check that we actually have a data file created
in hdfs by entering the following command in the Spring XDshell:

    xd:> hadoop fs ls /xd/tweets

if the `tweets-0.txt.tmp` file has 0 size it just means that our rollover limit has not been reached yet.

We can stop and remove the stream to flush all the data using:

    xd:> stream destroy --name tweets
    
Now we can look at the content in the file in hdfs using:

    xd:> hadoop fs cat /xd/tweets/tweets-0.txt
    
So far so good. Next step is to import this data into HAWQ using the PXF External Tables feature. We open up a new command window
and enter `psql` to start a PostgreSQL client shell. We are automatically logged in as gpadmin. Create the external table using the 
following command:

     CREATE EXTERNAL TABLE tweets(
       id BIGINT, from_user VARCHAR(255), created_at TIMESTAMP, hash_tag VARCHAR(255), 
       followers INTEGER, language_code VARCHAR(10), retweet_count INTEGER, retweet BOOLEAN) 
     LOCATION ('pxf://pivhdsne:50070/xd/tweets/*.txt?PROFILE=HdfsTextSimple') 
     FORMAT 'TEXT' (DELIMITER = E'\t');

Once the table is created we can query it:

    select count(*) from tweets;
     count
    -------
        85
    (1 row)
     
We can also run the following query to get the hash tags that were used most often:

    select lower(hash_tag) as hash_tag, count(*) 
    from tweets where hash_tag != '-' 
    group by lower(hash_tag) 
    order by count(*) desc limit 10;


### Demo2 - Twitter search storing data directly into HAWQ using JDBC

*This is a Spring XD "twittersearch | jdbc" stream.*

For this demo we will be querying Twitter for tweets matching our query and then storing some of the fields in a table in
HAWQ. We will insert one row per tweet using the Spring XD JDBC sink.

We need to modify the connection properties for JDBC which are specified in `config/modules/sink/jdbc/jdbc.properties`. We can edit that file 
using this command:

    gedit /home/gpadmin/spring-xd-1.0.0.RC1/xd/config/modules/sink/jdbc/jdbc.properties

Then change the content of the file to the following:

```
url = jdbc:postgresql:gpadmin
driverClassName = org.postgresql.Driver
username = gpadmin
password =
```

Next step is to create the table in HAWQ. We open up a new command window and enter `psql` to start a PostgreSQL client 
shell. We are automatically logged in as gpadmin. Create the table using the following command:

     CREATE TABLE jdbc_tweets(
       id BIGINT, created_at VARCHAR(30), text VARCHAR(255), 
       lang VARCHAR(10), retweet_count INTEGER, retweeted CHAR(5)); 

We don't do any conversion of the incoming data so we need to limit the datatypes used to ones that don't require an explicit cast based on the data 
available in the JSON document we get back from the Twitter search.

If you didn't do this as part of Demo 1, then the last config task is to add your Twitter consumerKey and consumerSecret to `config/mdules/modules.yml`. We can edit this file using:

    gedit /home/gpadmin/spring-xd-1.0.0.RC1/xd/config/modules/modules.yml
    
Uncomment the following lines and add your consumer key and secret to this file:

```
twitter:
  consumerKey: {your-consumer-key}
  consumerSecret: {your-consumer-secret}
  accessToken: {your-access-token}
  accessTokenSecret: {your-access-token-secret}
```
    
See the [Spring XD docs](https://github.com/spring-projects/spring-xd/wiki/Sources#wiki-twitter-search) for more details.

We are now ready to create and deploy the stream, so we switch back to the Spring XD shell:

    xd:> stream create --name jdbc_tweets --definition "twittersearch --query='hadoop' --outputType=application/json | jdbc --columns='id, created_at, text, lang, retweet_count, retweeted'" --deploy

We should see the stream get created in the Spring XD admin window. From the shell we can list the streams using:

    xd:> stream list
    
We let this stream run for a little while to collect some tweets. The columns we specified will be matched with the keys in the JSON document
returned from the twittersearch. We can now check that we actually have a some data in the table by opening up a new 
command window and entering `psql` to start a PostgreSQL client shell. Run the following command to see the data in 
the table:

    select * from jdbc_tweets;
     
We can stop and remove the stream from the Spring XD Shell using:

    xd:> stream destroy --name jdbc_tweets

