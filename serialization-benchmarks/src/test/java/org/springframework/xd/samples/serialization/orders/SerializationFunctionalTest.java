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

package org.springframework.xd.samples.serialization.orders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import org.springframework.xd.dirt.integration.bus.serializer.kryo.PojoCodec;
import org.springframework.xd.samples.domain.baseline.Address;
import org.springframework.xd.samples.domain.baseline.LineItem;
import org.springframework.xd.samples.domain.baseline.Order;
import org.springframework.xd.samples.kryo.CustomCodec;
import org.springframework.xd.samples.serialization.orders.OrderCreator;

/**
 * Functional tests for Order domain serializers. In this case, two separate PojoCodec instances 
 * are used: One for serialization, and the other for deserialization. This emulates the situation
 * in Spring XD. Serializer and Deserializer run in remote processes and must have identical registrations
 * to function properly.
 *  
 * @author David Turanski
 */
public class SerializationFunctionalTest {

	private PojoCodec serializer;

	private PojoCodec deserializer;

	@Before
	public void setUp() throws IOException {
		serializer = new PojoCodec();
		deserializer = new PojoCodec();
	}

	@Test
	public void testBaseline() throws IOException {
		verify(OrderCreator.getInstance(false));

	}

	@Test
	public void testOptimized() throws IOException {
		verify(OrderCreator.getInstance(true));
	}
	
	@Test 
	public void testCustomSerializers() throws IOException {
		serializer = new CustomCodec();
		deserializer = new CustomCodec();
		verify(OrderCreator.getInstance(false));
	}
	
	private void verify(Order order) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		serializer.serialize(order, bos);
		byte[] bytes = bos.toByteArray();
		Order result = (Order) deserializer.deserialize(bytes, order.getClass());
		
		assertEquals(order, result);
		assertEquals(order.getBillingAddress(),result.getBillingAddress());
		assertEquals(order.getShippingAddress(),result.getShippingAddress());
		
		assertEquals(order.getCustomer().getFirstname(), result.getCustomer().getFirstname());
		assertEquals(order.getCustomer().getLastname(), result.getCustomer().getLastname());
		assertEquals(order.getCustomer().getAddresses().size(), result.getCustomer().getAddresses().size());

		
		for (Iterator<Address> it1 = order.getCustomer().getAddresses().iterator(); it1.hasNext();) {
			Address expected = it1.next();
			assertTrue(order.getCustomer().getAddresses().contains(expected));
		}
		
		for (Iterator<LineItem> it1 = order.getLineItems().iterator(); it1.hasNext();) {
			LineItem expected = it1.next();
			assertTrue(order.getLineItems().contains(expected));
		}
	}
}
