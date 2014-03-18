#!/usr/bin/env ruby

require 'rubygems'
require 'socket'
require 'sendfile'

PORT = 3000
MSGCNT = 100000

puts "Create an XD stream using the following command:\n\n\tstream create --name reactor --definition \"reactor-tcp --port=#{PORT} | throughput-sampler\""

# generate test data
File.open('.testdata', 'w') do |f|
	f.puts 'START'
	(1..MSGCNT).each do |i|
		f.puts 'Hello World!'
	end
	f.puts 'END'
end

puts "\nHit [ENTER] when ready to send the data..."
gets 

# use sendfile to efficient send the data
outs = TCPSocket.new 'localhost', PORT
File.open('.testdata', 'r') do |f| 
	outs.sendfile f
end
outs.close
