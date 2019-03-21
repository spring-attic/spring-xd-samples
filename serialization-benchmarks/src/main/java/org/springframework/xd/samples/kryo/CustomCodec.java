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

package org.springframework.xd.samples.kryo;

import com.esotericsoftware.kryo.Kryo;

import org.springframework.xd.dirt.integration.bus.serializer.kryo.PojoCodec;
import org.springframework.xd.samples.domain.baseline.Address;
import org.springframework.xd.samples.domain.baseline.Customer;
import org.springframework.xd.samples.domain.baseline.EmailAddress;
import org.springframework.xd.samples.domain.baseline.LineItem;
import org.springframework.xd.samples.domain.baseline.Order;
import org.springframework.xd.samples.domain.baseline.Product;

/**
 * @author David Turanski
 */
public class CustomCodec extends PojoCodec {
	OrderSerializer orderSerializer = new OrderSerializer();
//	CustomerSerializer customerSerializer = new CustomerSerializer();
//	EmailAddressSerializer emailAddressSerializer = new EmailAddressSerializer();
//	AddressSerializer addressSerializer = new AddressSerializer();
//	ProductSerializer productSerializer = new ProductSerializer();
//	LineItemSerializer lineItemSerializer = new LineItemSerializer();
	
	@Override
	protected void configureKryoInstance(Kryo kryo) {
		kryo.register(Order.class, orderSerializer);
		kryo.register(Customer.class);
		kryo.register(Product.class);
		kryo.register(Address.class);
		kryo.register(LineItem.class);
		kryo.register(EmailAddress.class);
	}
}
