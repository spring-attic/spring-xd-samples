/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.springxd.samples.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This class contains the configuration for both a parent job (job) and a child job
 * (childJob).  The childJob executes a single {@link Tasklet} step that prints out
 * "Hello, Spring XD!"  The parent job contains a single step that executes the child job.
 *
 * @author Michael Minella
 */
@Configuration
public class NestedJobsConfiguration {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1")
				.tasklet(new Tasklet() {
					@Override
					public RepeatStatus execute(StepContribution contribution,
							ChunkContext chunkContext) throws Exception {
						System.out.println("Hello Spring XD!");
						return RepeatStatus.FINISHED;
					}
				}).build();
	}

	@Bean
	public Job childJob() {
		return jobBuilderFactory.get("childJob")
				.start(step1())
				.build();
	}

	@Bean
	public Step jobStep() {
		return stepBuilderFactory.get("jobStep")
				.job(childJob())
				.build();
	}

	@Bean
	public Job job() {
		return jobBuilderFactory.get("job")
				.start(jobStep())
				.build();
	}
}
