/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.acme;

import static org.springframework.util.ObjectUtils.nullSafeEquals;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

import org.springframework.xd.module.options.spi.ModuleOption;
import org.springframework.xd.module.options.spi.ProfileNamesProvider;
/**
 * An example class to describe and validate module options. This Example
 * Illustrates how to bind an option to a Spring profile. This is not usually
 * required, but may be useful in some situations.
 *  
 * Note the @ModuleOption annotation on the setters and the javax.validation
 * annotations on the getters.
 *
 */
public class ExampleModuleOptionsMetadata implements ProfileNamesProvider {
	private String suffix;

	private String prefix;

	private boolean prefixOnly = false;

	@NotNull
	public String getPrefix() {
		return prefix;
	}

	@ModuleOption("the prefix")
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getSuffix() {
		return suffix;
	}

	@ModuleOption("the suffix")
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public boolean isPrefixOnly() {
		return prefixOnly;
	}

	@ModuleOption("set to true to prepend prefix only")
	public void setPrefixOnly(boolean prefixOnly) {
		this.prefixOnly = prefixOnly;
	}

	@Override
	public String[] profilesToActivate() {
		if (prefixOnly) {
			return new String[] {"use-prefix"};
		}
		return new String[] {"use-both"};
	}

	@AssertTrue(message = "prefix and suffix cannot be the same")
	public boolean getPrefixAndSuffixCannotBeTheSame() {
		return (!nullSafeEquals(prefix, suffix));
	}
}
