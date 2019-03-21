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

import org.springframework.util.Assert;


/**
 * A line item.
 * @author Oliver Gierke
 */

public class LineItem {


	protected Product product;

	protected BigDecimal price;

	protected int amount;


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof LineItem)) return false;

		LineItem lineItem = (LineItem) o;

		if (amount != lineItem.amount) return false;
		if (price != null ? !price.equals(lineItem.price) : lineItem.price != null) return false;
		if (product != null ? !product.equals(lineItem.product) : lineItem.product != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = product != null ? product.hashCode() : 0;
		result = 31 * result + (price != null ? price.hashCode() : 0);
		result = 31 * result + amount;
		return result;
	}

	/**
	 * Creates a new {@link LineItem} for the given {@link Product} and amount.
	 * @param product must not be {@literal null}.
	 * @param amount
	 */
	public LineItem(Product product, int amount) {

		Assert.notNull(product, "The given Product must not be null!");
		Assert.isTrue(amount > 0, "The amount of Products to be bought must be greater than 0!");

		this.product = product;
		this.amount = amount;
		this.price = product.getPrice();
	}

	/**
	 * Returns the {@link Product} the {@link LineItem} refers to.
	 * @return
	 */
	public Product getProduct() {
		return product;
	}

	/**
	 * Returns the amount of {@link Product}s to be ordered.
	 * @return
	 */
	public int getAmount() {
		return amount;
	}

	/**
	 * Returns the price a single unit of the {@link LineItem}'s product.
	 * @return the price
	 */
	public BigDecimal getUnitPrice() {
		return price;
	}

	/**
	 * Returns the total for the {@link LineItem}.
	 * @return
	 */
	public BigDecimal getTotal() {
		return price.multiply(BigDecimal.valueOf(amount));
	}
}
