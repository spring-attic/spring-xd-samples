/*
 * Copyright 2015 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.xd.samples.rt.firehose;

import rx.Subscriber;

import org.springframework.context.SmartLifecycle;
import org.springframework.integration.core.MessageProducer;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.xd.samples.rt.event.Event;

/**
 * @author David Turanski
 */
public class FirehoseSimulatorDriver extends Subscriber<Event> implements MessageProducer, SmartLifecycle {
	private MessageChannel outputChannel;

	private final FirehoseSimulator firehoseSimulator;

	private boolean running;

	public FirehoseSimulatorDriver(FirehoseSimulator firehoseSimulator) {
		this.firehoseSimulator = firehoseSimulator;
	}

	@Override
	public void onCompleted() {
	}

	@Override
	public void onError(Throwable e) {
	}

	@Override
	public void onNext(Event event) {
		Message<?> message = new GenericMessage<>(event);
		this.outputChannel.send(message);
	}

	@Override
	public void setOutputChannel(MessageChannel outputChannel) {
		this.outputChannel = outputChannel;
	}

	@Override
	public boolean isAutoStartup() {
		return false;
	}

	@Override
	public void stop(Runnable callback) {
		callback.run();
		stop();
	}

	@Override
	public synchronized void start() {
		new Thread(() -> firehoseSimulator.generator().subscribe(FirehoseSimulatorDriver.this)).start();
		this.running = true;
	}

	@Override
	public synchronized void stop() {
		firehoseSimulator.stop();
		this.running = false;
	}

	@Override
	public boolean isRunning() {
		return this.running;
	}

	@Override
	public int getPhase() {
		return 0;
	}

	protected FirehoseSimulator getFirehoseSimulator() {
		return this.firehoseSimulator;
	}
}
