Spring XD Batch Simple Sample
=================================

The purpose of this sample is to show how to deploy a simple batch process in Spring XD, without having to compile any code or install
any jars.  
This example batch job will read a csv file, replace the commas with spaces and output the result to another file.

## Requirements

In order for the sample to run you will need to have installed:

* Spring XD ([Instructions](https://github.com/SpringSource/spring-xd/wiki/Getting-Started))
   
## Running the Sample

In the batch-simple directory

        $ cp simple_example.xml $XD_HOME/modules/job
        $ cp sample.txt /tmp

Now your Sample is ready to be executed. Start your *Spring XD* admin server (If it was already running, you must restart it):

        xd/bin>$ ./xd-singlenode

Now start the *Spring XD Shell* in a separate window:

        shell/bin>$ ./xd-shell

You will now create a new Batch Job Stream using the *Spring XD Shell*:

        xd:>job create --name myBatchJob --definition "simple_example"

You should see a message:

        Successfully created and deployed job 'myBatchJob'

Now execute the batch job by creating a stream that will trigger the job.

	xd:>stream create myBatchJobStream --definition "trigger > job:myBatchJob"

You should see a message:

Created new stream 'myBatchJobStream'

## Verify the result
The simple_example batch job reads the contents /tmp/sample.txt and outputs its result to /tmp/sample1out.txt.

So lets look at the contents sample.txt (input) file:

         h,e,l,l,o, ,w,o,r,l,d

The sample1out.txt (output) file will look like:

         h e l l o   w o r l d
