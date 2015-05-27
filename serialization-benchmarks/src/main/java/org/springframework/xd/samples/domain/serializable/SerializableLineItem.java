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
package org.springframework.xd.samples.domain.serializable;

import java.math.BigDecimal;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import org.springframework.xd.samples.domain.baseline.LineItem;
import org.springframework.xd.samples.domain.baseline.Product;


/**
 * A line item.
 * @author Oliver Gierke
 */

public class SerializableLineItem extends LineItem implements KryoSerializable {


	public SerializableLineItem(Product product, int amount) {
		super(product, amount);
	}

	@Override
	public void write(Kryo kryo, Output output) {
		kryo.writeObject(output, this.product);
		output.writeDouble(this.price.doubleValue());
		output.writeInt(this.amount);
	}

	@Override
	public void read(Kryo kryo, Input input) {
		this.product = kryo.readObject(input, SerializableProduct.class);
		this.price = new BigDecimal(input.readDouble());
		this.amount = input.readInt();
;	}
}
