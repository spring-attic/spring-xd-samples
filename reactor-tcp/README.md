# Reactor TCP Spring XD sample

This is a sample application demonstrating the use of [Reactor](https://github.com/reactor/reactor) in *Spring XD*.

## Requirements

To run this sample you'll need:

* Spring XD ([Instructions](https://github.com/SpringSource/spring-xd/wiki/Getting-Started))
* Ruby
* `sendfile` ruby Gem (`gem install sendfile`)

## Create a Stream

You'll need to create a `reactor-tcp` source in *Spring XD* using the following example as a template:

		stream create --name reactortcp --definition "reactor-tcp --port=3000 | throughput-sampler" --deploy

You can change the port to which the source is bound by adjusting the `--port` parameter. The default is 3000 and that's what the Ruby script included with this sample will try to connect to, but you can easily change that, of course.

## Throughput Sampling 

The `throughput-sampler` sink is a simple NOOP sink that looks for a "start" message which, by default is the String `START`. It will count the messages it receives after that point. When it receives an "end" message, which defaults to the not-surprising String value `END`, it will calculate the throughput for the messages it has seen to that point and log that value to the XD log. It's a simple way to measure overall throughput at a fairly coarse level.

## Send the Data

We've found in microbenchmarking network servers that it's very difficult to create a simple client that streams test data fast enough to not interfere with the benchmark. Even using a tool like `netcat`, which you would assume would be about the fastest way to send data to a server, is surprisingly inefficient. These inefficiencies add up when sending many thousands of messages.

The fastest way we've found to send a lot of data to a server very quickly and efficiently (in the hopes you can achieve saturation) is to use the *NIX kernel function "sendfile". How this is done varies based on whether you're using Linux, UNIX, FreeBSD, or Mac OS X. But this test uses the Ruby GEM `sendfile` which has a native extension in it to call this kernel-level function and stream data directly from disk to the network stack, bypassing user space entirely. To do this, it first generates a file of test data that starts with a `START`, contains many thousands of `Hello World!` messages, and is truncated by an `END`.

Each time your run the `senddata.rb` script, it will generate a file of test data and stream that to the TCP source you created in XD with the above command. The throughput for that run will be logged to the XD log file if you're using the included `throughput-sampler` sink.
