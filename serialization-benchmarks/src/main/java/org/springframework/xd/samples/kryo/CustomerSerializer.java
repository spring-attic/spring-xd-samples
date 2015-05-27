/*
 * Copyright 2014 the original author or authors.
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

package org.springframework.xd.samples.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import org.springframework.xd.samples.domain.baseline.Address;
import org.springframework.xd.samples.domain.baseline.Customer;
import org.springframework.xd.samples.domain.baseline.EmailAddress;

/**
 * @author David Turanski
 */
public class CustomerSerializer extends Serializer<Customer> {
	private final EmailAddressSerializer emailAddressSerializer;

	private final AddressSerializer addressSerializer;

	public CustomerSerializer() {
		this.emailAddressSerializer = new EmailAddressSerializer();
		this.addressSerializer = new AddressSerializer();

	}
	@Override
	public void write(Kryo kryo, Output output, Customer customer) {
		output.writeString(customer.getFirstname());
		output.writeString(customer.getLastname());
		kryo.writeObjectOrNull(output, customer.getEmailAddress(), emailAddressSerializer);
		output.writeInt(customer.getAddresses().size());
		for (Address address: customer.getAddresses()) {
			kryo.writeObject(output, address, addressSerializer);
		}

	}

	@Override
	public Customer read(Kryo kryo, Input input, Class<Customer> type) {
		Customer customer = new Customer(input.readString(), input.readString());
		customer.setEmailAddress(kryo.readObjectOrNull(input, EmailAddress.class, emailAddressSerializer));
		int numAddresses = input.readInt();
		for (int i=0; i< numAddresses; i++) {
			customer.add(kryo.readObject(input, Address.class, addressSerializer));
		}
		return customer;
	}
}
