/*
 * Copyright 2014 the original author or authors.
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
package org.springframework.xd.samples.domain.serializable;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import org.springframework.xd.samples.domain.baseline.Address;

/**
 * An address.
 * @author Oliver Gierke
 */
public class SerializableAddress extends Address implements KryoSerializable {


	public SerializableAddress(String street, String city, String country) {
		super(street, city, country);

	}

	@Override
	public void write(Kryo kryo, Output output) {
		output.writeString(this.street);
		output.writeString(this.city);
		output.writeString(this.country);
	}

	@Override
	public void read(Kryo kryo, Input input) {
		this.street = input.readString();
		this.city = input.readString();
		this.country = input.readString();
	}
}
