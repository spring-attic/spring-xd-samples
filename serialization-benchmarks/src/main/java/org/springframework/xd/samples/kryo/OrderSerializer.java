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
import org.springframework.xd.samples.domain.baseline.LineItem;
import org.springframework.xd.samples.domain.baseline.Order;

/**
 * @author David Turanski
 */
public class OrderSerializer extends Serializer<Order> {

	private final CustomerSerializer customerSerializer;

	private final LineItemSerializer lineItemSerializer;

	private final AddressSerializer addressSerializer;

	public OrderSerializer() {
		this.customerSerializer = new CustomerSerializer();
		this.lineItemSerializer = new LineItemSerializer();
		this.addressSerializer = new AddressSerializer();
		
	}
	@Override
	public void write(Kryo kryo, Output output, Order order) {
		kryo.writeObject(output,order.getCustomer(),customerSerializer);
		kryo.writeObject(output, order.getShippingAddress(), addressSerializer);
		kryo.writeObject(output, order.getBillingAddress(), addressSerializer);
		output.writeInt(order.getLineItems().size());
		for (LineItem lineItem: order.getLineItems()) {
			kryo.writeObject(output, lineItem, lineItemSerializer);
		}

	}

	@Override
	public Order read(Kryo kryo, Input input, Class<Order> type) {
		Order order = new Order(kryo.readObject(input, Customer.class, customerSerializer), kryo.readObject(input, 
				Address.class, addressSerializer),kryo.readObject(input, Address.class, addressSerializer));
		int numLineItems = input.readInt();
		for (int i=0; i< numLineItems; i++) {
			order.add(kryo.readObject(input, LineItem.class, lineItemSerializer));
		}
		return order;
	}
}
