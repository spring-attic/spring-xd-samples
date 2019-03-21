/*
 * Copyright 2012 the original author or authors.
 *
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
package org.springframework.xd.samples.domain.baseline;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.util.Assert;


/**
 * An order.
 * @author Oliver Gierke
 */

public class Order {

	protected Customer customer;

	protected Address billingAddress;

	protected Address shippingAddress;

	protected Set<LineItem> lineItems = new HashSet<LineItem>();

	/**
	 * Creates a new {@link Order} for the given customer, shipping and billing {@link Address}.
	 * @param customer must not be {@literal null}.
	 * @param shippingAddress must not be {@literal null}.
	 * @param billingAddress can be {@@iteral null}.
	 */
	public Order(Customer customer, Address shippingAddress, Address billingAddress) {

		Assert.notNull(customer);
		Assert.notNull(shippingAddress);

		this.customer = customer;
		this.shippingAddress = shippingAddress;
		this.billingAddress = billingAddress == null ? null : billingAddress;
	}


	/**
	 * Adds the given {@link LineItem} to the {@link Order}.
	 * @param lineItem
	 */
	public void add(LineItem lineItem) {
		this.lineItems.add(lineItem);
	}

	/**
	 * Returns the {@link Customer} who placed the {@link Order}.
	 * @return
	 */
	public Customer getCustomer() {
		return customer;
	}

	/**
	 * Returns the billing {@link Address} for this order.
	 * @return
	 */
	public Address getBillingAddress() {
		return billingAddress != null ? billingAddress : shippingAddress;
	}

	/**
	 * Returns the shipping {@link Address} for this order;
	 * @return
	 */
	public Address getShippingAddress() {
		return shippingAddress;
	}

	/**
	 * Returns all {@link LineItem}s currently belonging to the {@link Order}.
	 * @return
	 */
	public Set<LineItem> getLineItems() {
		return Collections.unmodifiableSet(lineItems);
	}

	/**
	 * Returns the total of the {@link Order}.
	 * @return
	 */
	public BigDecimal getTotal() {

		BigDecimal total = BigDecimal.ZERO;

		for (LineItem item : lineItems) {
			total = total.add(item.getTotal());
		}

		return total;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Order)) return false;

		Order order = (Order) o;

		if (billingAddress != null ? !billingAddress.equals(order.billingAddress) : order.billingAddress != null)
			return false;
		if (customer != null ? !customer.equals(order.customer) : order.customer != null) return false;
		if (lineItems != null ? !lineItems.equals(order.lineItems) : order.lineItems != null) return false;
		if (shippingAddress != null ? !shippingAddress.equals(order.shippingAddress) : order.shippingAddress != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = customer != null ? customer.hashCode() : 0;
		result = 31 * result + (billingAddress != null ? billingAddress.hashCode() : 0);
		result = 31 * result + (shippingAddress != null ? shippingAddress.hashCode() : 0);
		result = 31 * result + (lineItems != null ? lineItems.hashCode() : 0);
		return result;
	}
}
