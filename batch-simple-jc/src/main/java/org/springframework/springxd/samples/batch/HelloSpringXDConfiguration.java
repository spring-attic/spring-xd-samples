/*
 * Copyright 2015 wz07.
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

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Java Configuration for Batch Job
 * @author Sathish Kumar Thiyagarajan
 */
@Configuration
@EnableBatchProcessing
public class HelloSpringXDConfiguration {

    @Autowired
    private HelloSpringXDTasklet helloSpringXDTasklet;

    @Bean
    public Job job(JobBuilderFactory jobs, StepBuilderFactory stepBuilderFactory) {
        Step step1 = stepBuilderFactory.get("helloSpringXDStep")
                .tasklet(helloSpringXDTasklet)
                .build();
        return jobs.get("helloSpringXDJob")
                .incrementer(new RunIdIncrementer())
                .flow(step1)
                .end()
                .build();
    }

}
