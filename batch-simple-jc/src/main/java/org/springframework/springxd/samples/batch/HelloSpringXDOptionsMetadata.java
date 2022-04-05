/*
 * Copyright 2015 WZ07.
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

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

import org.springframework.xd.module.options.spi.ModuleOption;

/**
 * An example class to describe and validate module options. 
 *
 * Note the @ModuleOption annotation on the setters and the javax.validation
 * annotations on the getters.
 * @author Sathish Kumar Thiyagarajan
 */
public class HelloSpringXDOptionsMetadata {

    private String message;

    @NotNull
    public String getMessage() {
        return message;
    }

    @ModuleOption("the message")
    public void setMessage(String message) {
        this.message = message;
    }

    @AssertTrue(message = "message cannot be empty")
    public boolean messageCannotBeTheEmpty() {
        return (message != null && message.trim().length() !=0);
    }
}
