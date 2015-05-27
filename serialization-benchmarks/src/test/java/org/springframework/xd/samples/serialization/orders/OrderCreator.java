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

import java.math.BigDecimal;
import java.util.Iterator;

import org.springframework.xd.samples.domain.baseline.Address;
import org.springframework.xd.samples.domain.baseline.Customer;
import org.springframework.xd.samples.domain.baseline.EmailAddress;
import org.springframework.xd.samples.domain.baseline.LineItem;
import org.springframework.xd.samples.domain.baseline.Order;
import org.springframework.xd.samples.domain.baseline.Product;
import org.springframework.xd.samples.domain.serializable.SerializableAddress;
import org.springframework.xd.samples.domain.serializable.SerializableCustomer;
import org.springframework.xd.samples.domain.serializable.SerializableEmailAddress;
import org.springframework.xd.samples.domain.serializable.SerializableLineItem;
import org.springframework.xd.samples.domain.serializable.SerializableOrder;
import org.springframework.xd.samples.domain.serializable.SerializableProduct;

/**
 * Creates an Order with populated field values.
 *
 * @author David Turanski
 */
class OrderCreator {

	// Change these values to show the effect of object size on serialization time.
	static final int LINE_ITEMS = 1;

	static final int ADDRESSES = 2; // 2 is the minimum

	static final int ATTRIBUTES = 0;

	static Order getInstance(boolean serializable) {
		String first = "Firstname";
		String last = "Lastname";

		Customer customer = serializable ?
				new SerializableCustomer(first, last) :
				new Customer(first, last);

		for (int i = 0; i < ADDRESSES; i++) {
			int number = 999 + i;
			String streetName = "Some Street";
			String city = "City";
			String country = "US";
			Address address = serializable ? new SerializableAddress(String.format("%d %s Street", number, streetName),
					city, country) :
					new Address(String.format("%d %s Street", number, streetName), city, country);

			customer.add(address);

			String email = String.format("%s@%s.com", "somebody", "email");

			EmailAddress emailAddress = serializable ? new SerializableEmailAddress(email) : new EmailAddress(email);

			customer.setEmailAddress(emailAddress);
		}


		Iterator<Address> it = customer.getAddresses().iterator();

		Order order = serializable ? new SerializableOrder(customer, it.next(), it.next()) :
				new Order(customer, it.next(), it.next());
		for (int i = 0; i < LINE_ITEMS; i++) {
			String name = "product" + i;
			BigDecimal price = new BigDecimal(99.99);
			String description = String.format("product description:%s", "A product description");

			Product product = serializable ? new SerializableProduct(name, price, description) :
					new Product(name, price, description);

			for (int j = 0; j < ATTRIBUTES; j++) {
				product.setAttribute("attribute" + j, "value" + j);
			}
			LineItem lineItem = serializable ? new SerializableLineItem(product, 1) :
					new LineItem(product, 1);
			order.add(lineItem);
		}
		return order;
	}
}
