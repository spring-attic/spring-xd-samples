Spring XD Batch Simple Sample
=================================

The purpose of this sample is to show how to deploy a simple batch process in Spring XD, without having to compile any code or install any jars. This example batch job will read a csv file, replace the commas with spaces and output the result to another file.

## Requirements

In order for the sample to run you will need to have installed:

* Spring XD ([Instructions](https://github.com/SpringSource/spring-xd/wiki/Getting-Started))

## Running the Sample

In the batch-simple directory

		$mkdir -p $XD_HOME/custom-modules/job/simple_example/config
        $ cp simple_example.xml $XD_HOME/modules/job/simple_example/config
        $ cp sample.txt /tmp

Now your Sample is ready to be executed. Start your *Spring XD* singlenode server:

        xd/bin>$ ./xd-singlenode

Now start the *Spring XD Shell* in a separate window:

        shell/bin>$ ./xd-shell

You will now create a new Batch Job Stream using the *Spring XD Shell* (and deploy it immediately):

        xd:>job create --name myBatchJob --definition "simple_example" --deploy

and launch it using the command:

        xd:>job launch myBatchJob

You should see a message:

        Successfully created and deployed job 'myBatchJob'

## Verify the result

The simple_example batch job reads the contents `/tmp/sample.txt` and outputs its result to `/tmp/sample1out.txt`.

So lets look at the contents `/tmp/sample.txt` (input) file:

         h,e,l,l,o, ,w,o,r,l,d

The `/tmp/sample1out.txt` (output) file will look like:

         h e l l o   w o r l d
