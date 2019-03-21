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
import org.springframework.xd.samples.domain.baseline.LineItem;
import org.springframework.xd.samples.domain.baseline.Order;


/**
 * An order.
 * @author Oliver Gierke
 */

public class SerializableOrder extends Order implements KryoSerializable {

	public SerializableOrder(Customer customer, Address shippingAddress, Address billingAddress) {
		super(customer, shippingAddress, billingAddress);
	}

	@Override
	public void write(Kryo kryo, Output output) {
		kryo.writeObject(output,this.customer);
		kryo.writeObject(output, this.billingAddress);
		kryo.writeObject(output, this.shippingAddress);
		output.writeInt(lineItems.size());
		for (LineItem lineItem: lineItems) {
			kryo.writeObject(output, lineItem);
		}
	}

	@Override
	public void read(Kryo kryo, Input input) {
		this.customer = kryo.readObject(input, SerializableCustomer.class);
		this.billingAddress = kryo.readObject(input, SerializableAddress.class);
		this.shippingAddress = kryo.readObject(input, SerializableAddress.class);
		this.lineItems = new HashSet<LineItem>();
		int numLineItems = input.readInt();
		for (int i=0; i< numLineItems; i++) {
			this.add(kryo.readObject(input, SerializableLineItem.class));
		}
	}
}
