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
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.Assert;

/**
 * A product.
 * @author Oliver Gierke
 */
public class Product {
	protected String name;

	protected String description;

	protected BigDecimal price;

	protected Map<String, String> attributes = new HashMap<String, String>();
	
	/**
	 * Creates a new {@link Product} from the given name and description.
	 * @param name must not be {@literal null} or empty.
	 * @param price must not be {@literal null} or less than or equal to zero.
	 * @param description
	 */
	public Product(String name, BigDecimal price, String description) {

		Assert.hasText(name, "Name must not be null or empty!");
		Assert.isTrue(BigDecimal.ZERO.compareTo(price) < 0, "Price must be greater than zero!");

		this.name = name;
		this.price = price;
		this.description = description;
	}

	/**
	 * Sets the attribute with the given name to the given value.
	 * @param name must not be {@literal null} or empty.
	 * @param value
	 */
	public void setAttribute(String name, String value) {

		Assert.hasText(name);

		if (value == null) {
			this.attributes.remove(value);
		}
		else {
			this.attributes.put(name, value);
		}
	}

	/**
	 * Returns the {@link Product}'s name.
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the {@link Product}'s description.
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Returns all the custom attributes of the {@link Product}.
	 * @return
	 */
	public Map<String, String> getAttributes() {
		return Collections.unmodifiableMap(attributes);
	}

	/**
	 * Returns the price of the {@link Product}.
	 * @return
	 */
	public BigDecimal getPrice() {
		return price;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Product)) return false;

		Product product = (Product) o;

		if (attributes != null ? !attributes.equals(product.attributes) : product.attributes != null) return false;
		if (description != null ? !description.equals(product.description) : product.description != null) return false;
		if (name != null ? !name.equals(product.name) : product.name != null) return false;
		if (price != null ? !price.equals(product.price) : product.price != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = name != null ? name.hashCode() : 0;
		result = 31 * result + (description != null ? description.hashCode() : 0);
		result = 31 * result + (price != null ? price.hashCode() : 0);
		result = 31 * result + (attributes != null ? attributes.hashCode() : 0);
		return result;
	}
}
