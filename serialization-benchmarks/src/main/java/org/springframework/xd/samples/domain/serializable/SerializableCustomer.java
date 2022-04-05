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

import java.util.HashSet;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import org.springframework.xd.samples.domain.baseline.Address;
import org.springframework.xd.samples.domain.baseline.Customer;

/**
 * A customer.
 * 
 * @author Oliver Gierke
 */
public class SerializableCustomer extends Customer implements KryoSerializable {

	public SerializableCustomer(String firstname, String lastname) {
		super(firstname, lastname);
	}

	@Override
	public void write(Kryo kryo, Output output) {
		output.writeString(this.firstname);
		output.writeString(this.lastname);
		kryo.writeObjectOrNull(output, this.emailAddress, SerializableEmailAddress.class);
		output.writeInt(this.addresses.size());
		for (Address address: this.addresses) {
			kryo.writeObject(output, address);
		}
	}

	@Override
	public void read(Kryo kryo, Input input) {
		this.firstname = input.readString();
		this.lastname = input.readString();
		this.emailAddress = kryo.readObjectOrNull(input,SerializableEmailAddress.class);
		this.addresses = new HashSet<Address>();
		int numAddresses = input.readInt();
		for (int i=0; i< numAddresses; i++) {
			this.add(kryo.readObject(input, SerializableAddress.class));
		}
	}
}
