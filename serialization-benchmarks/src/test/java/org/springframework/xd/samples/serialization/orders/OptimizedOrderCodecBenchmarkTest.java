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

package org.springframework.xd.samples.serialization.orders;

import java.util.ArrayList;
import java.util.List;

import com.esotericsoftware.kryo.Registration;

import org.springframework.xd.dirt.integration.bus.serializer.kryo.KryoRegistrationRegistrar;
import org.springframework.xd.dirt.integration.bus.serializer.kryo.PojoCodec;
import org.springframework.xd.samples.domain.baseline.Address;
import org.springframework.xd.samples.domain.baseline.Customer;
import org.springframework.xd.samples.domain.baseline.LineItem;
import org.springframework.xd.samples.domain.baseline.Order;
import org.springframework.xd.samples.domain.baseline.Product;
import org.springframework.xd.samples.kryo.AddressSerializer;
import org.springframework.xd.samples.kryo.CustomerSerializer;
import org.springframework.xd.samples.kryo.LineItemSerializer;
import org.springframework.xd.samples.kryo.OrderSerializer;
import org.springframework.xd.samples.kryo.ProductSerializer;
import org.springframework.xd.samples.serialization.AbstractCodecBenchmarkTest;

/**
 * Sample Benchmark for Order domain registering custom Serializers.
 *
 * @author David Turanski
 */
public class OptimizedOrderCodecBenchmarkTest extends AbstractCodecBenchmarkTest {
	@Override
	protected PojoCodec getPojoCodec() {
		// Register serializers for all custom types
		List<Registration> registrationList = new ArrayList<Registration>();
		registrationList.add(new Registration(Address.class, new AddressSerializer(), 20));
		registrationList.add(new Registration(Customer.class, new CustomerSerializer(), 21));
		registrationList.add(new Registration(Product.class, new ProductSerializer(), 22));
		registrationList.add(new Registration(Order.class, new OrderSerializer(), 23));
		registrationList.add(new Registration(LineItem.class, new LineItemSerializer(), 24));
		// Set use references to 'false'
		return new PojoCodec(new KryoRegistrationRegistrar(registrationList), false);
	}

	@Override
	protected Object getObjectToSerialize() {
		// Return baseline instances (do not implement serializable)
		return OrderCreator.getInstance(false);
	}
}
