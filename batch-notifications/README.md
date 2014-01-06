Spring XD Payments Import with Notifications
============================================

This sample will take an input file containing payment data, and import the data into a database using Spring XD's batch job support. During import various notification events are triggered and printed to the console.

## Requirements

In order for the sample to run you will need to have installed:

* Spring XD ([Instructions](https://github.com/spring-projects/spring-xd/wiki/Getting-Started))

## Building

Build the sample simply by executing:

	$ mvn clean assembly:assembly

As a result, you will see the following files and directories created under `target/batch-payment-import-1.0.0.BUILD-SNAPSHOT-bin/`:

```
|-- batch-notifications-1.0.0.BUILD-SNAPSHOT-bin
|   |-- lib
|   |   `-- batch-notifications-1.0.0.BUILD-SNAPSHOT.jar
|   |-- modules
|   |   `-- job
|   |       `-- payment-import.xml
```

## Running the Sample

In the batch-wordcount directory

	$ cp target/batch-notifications-1.0.0.BUILD-SNAPSHOT-bin/modules/job/* $XD_HOME/modules/job
	$ cp target/batch-notifications-1.0.0.BUILD-SNAPSHOT-bin/lib/* $XD_HOME/lib

Now your Sample is ready to be executed. Start your *Spring XD* admin server (If it was already running, you must restart it):

	xd/bin>$ ./xd-singlenode

Now start the *Spring XD Shell* in a separate window:

	shell/bin>$ ./xd-shell

## Setup the process

In this example, the job is driven by a stream rather than being launched using a separate command. A job instance is launched by posting http data to it.

	xd:> job create --name payment --definition "payment-import" --makeUnique false 
	xd:> stream create --name paymenthttp --definition "http > job:payment"
	xd:> stream create --name paymenttap --definition ":payment-notifications > log"
	
We also create a separate stream sends notifications from the job to the log. This lets us know when the job completes, along with status information.

## Execute the process

	xd:> http post --data "{"input.file.name":"/path/to/payment.txt"}"

The payment file is located under `/src/main/resources/data/paymentImport/payment.input`
	
## Results

You will see 27 payments being imported. The notifications are printed to the console. If you try to execute the batch job again, you will notice that the batch job will not execute again, as it was already run. In that case you should see an exception such as:

	JobInstanceAlreadyCompleteException:
	A job instance already exists and is complete for parameters={input.file.name=/path/to/payment.txt}.  If you want to run this job again, change the parameters.

If you would leave off the parameter `--makeUnique=false` you could run the job over and over again.

## Trigger error behavior and retry

Let try to import a payment file that contains an error. E.g.:

	...
	1,2,2.0,2010-01-14
	1,2,2.0,2010-01-15
	1,2,2.0,2010xxx-16
	1,2,2.0,2010-01-17
	1,2,2.0,2010-01-18
	...

Executed the process with:

	xd:> http post --data "{"input.file.name":"/path/to/payment_with_error.txt"}"

The import will fail but all the payment records up to erroneous row have been imported successfully. Now lets fix that row save the file and execute again:

	xd:> http post --data "{"input.file.name":"/path/to/payment_with_error.txt"}"
	
This time the import will continue with the previously erroneous row and continues with the successful import of the rest of the file.

