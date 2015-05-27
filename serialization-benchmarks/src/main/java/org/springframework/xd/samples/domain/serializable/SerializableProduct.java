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
import java.util.HashMap;
import java.util.Map;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import org.springframework.xd.samples.domain.baseline.Product;

/**
 * A product.
 * @author Oliver Gierke
 */
public class SerializableProduct extends Product implements KryoSerializable {

	public SerializableProduct(String name, BigDecimal price, String description) {
		super(name, price, description);
	}

	@Override
	public void write(Kryo kryo, Output output) {
		output.writeString(this.name);
		output.writeString(this.description);
		output.writeDouble(this.price.doubleValue());
		output.writeInt(attributes.size());
		for (Map.Entry<String, String> attribute : attributes.entrySet()) {
			output.writeString(attribute.getKey());
			output.writeString(attribute.getValue());
		}
	}

	@Override
	public void read(Kryo kryo, Input input) {
		this.name = input.readString();
		this.description = input.readString();
		this.price = new BigDecimal(input.readDouble());
		this.attributes = new HashMap<String, String>();
		int attributesSize = input.readInt();
		for (int i = 0; i < attributesSize; i++) {
			this.setAttribute(input.readString(), input.readString());
		}
	}
}
