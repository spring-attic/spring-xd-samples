Spring XD Simple Batch Sample
=============================

This is the quintessential *Hello World* example for *Spring XD*. It consists of only one [Tasklet](http://static.springsource.org/spring-batch/apidocs/org/springframework/batch/core/step/tasklet/Tasklet.html) which prints out `Hello Spring XD!`.

## Requirements

In order for the sample to run you will need to have installed:

* Spring XD ([Instructions](https://github.com/SpringSource/spring-xd/wiki/Getting-Started))

## Building



	$ mvn clean assembly:assembly

As a result, you will see the following files and directories created under `target/springxd-batch-simple-1.0.0.BUILD-SNAPSHOT-bin/`:

```
|-- springxd-batch-simple-1.0.0.BUILD-SNAPSHOT-bin
|   |-- lib
|   |   `-- springxd-batch-simple-1.0.0.BUILD-SNAPSHOT.jar
|   `-- modules
|       `-- job
|           `-- myjob.xml
```

## Running the Sample

In the `batch-simple` directory run the shell script copy-files.sh.  

  
	$ ./copy-files.sh


This will move the build artifacts into the modules/job and lib directories to the right locations under $XD_HOME

Now your Sample is ready to be executed. Start your *Spring XD* admin server (If it was already running, you must restart it):

	xd/bin>$ ./xd-singlenode

Now start the *Spring XD Shell* in a separate window:

	shell/bin>$ ./xd-shell

You will now create a new Batch Job Stream using the *Spring XD Shell*:

	xd:>job create --name helloSpringXD --definition "myjob" --deploy false

The UI located on the machine where xd-singlenode is running, will show you the jobs that can be deployed.  The UI is located at http://localhost:9393/admin-ui

Alternatively, you can deploy it using the command line

	xd:>job deploy helloSpringXD

And then launch the job

	xd:>job launch helloSpringXD



You should see a message:

	Hello Spring XD!

