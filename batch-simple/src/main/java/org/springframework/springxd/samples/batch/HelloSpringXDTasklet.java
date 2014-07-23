/*
 * Copyright 2013-2014 the original author or authors.
 *
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
package org.springframework.springxd.samples.batch;


import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;

/**
 * Sample tasklet.
 *
 * @author Gunnar Hillert
 * @author Ilayaperumal Gopinathan
 */
public class HelloSpringXDTasklet implements Tasklet, StepExecutionListener {

	private volatile AtomicInteger counter = new AtomicInteger(0);

	/**
	 *
	 */
	public HelloSpringXDTasklet() {
		super();
	}

	public RepeatStatus execute(StepContribution contribution,
			ChunkContext chunkContext) throws Exception {

		final JobParameters jobParameters = chunkContext.getStepContext().getStepExecution().getJobParameters();
		final ExecutionContext stepExecutionContext = chunkContext.getStepContext().getStepExecution().getExecutionContext();

		System.out.println("Hello Spring XD!");

		if (jobParameters != null && !jobParameters.isEmpty()) {

			final Set<Entry<String, JobParameter>> parameterEntries = jobParameters.getParameters().entrySet();

			System.out.println(String.format("The following %s Job Parameter(s) is/are present:", parameterEntries.size()));

			for (Entry<String, JobParameter> jobParameterEntry : parameterEntries) {
				System.out.println(String.format(
						"Parameter name: %s; isIdentifying: %s; type: %s; value: %s",
						jobParameterEntry.getKey(),
						jobParameterEntry.getValue().isIdentifying(),
						jobParameterEntry.getValue().getType().toString(),
						jobParameterEntry.getValue().getValue()));

				if (jobParameterEntry.getKey().startsWith("context")) {
					stepExecutionContext.put(jobParameterEntry.getKey(), jobParameterEntry.getValue().getValue());
				}
			}

			if (jobParameters.getString("throwError") != null
					&& Boolean.TRUE.toString().equalsIgnoreCase(jobParameters.getString("throwError"))) {

				if (this.counter.compareAndSet(3, 0)) {
					System.out.println("Counter reset to 0. Execution will succeed.");
				}
				else {
					this.counter.incrementAndGet();
					throw new IllegalStateException("Exception triggered by user.");
				}

			}
		}
		return RepeatStatus.FINISHED;
	}

	@Override
	public void beforeStep(StepExecution stepExecution) {
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		// To make the job execution fail, set the step execution to fail
		// and return failed ExitStatus
		// stepExecution.setStatus(BatchStatus.FAILED);
		// return ExitStatus.FAILED;
		return ExitStatus.COMPLETED;
	}
}