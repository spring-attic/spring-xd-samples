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

import java.math.BigDecimal;
import java.util.Map;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import org.springframework.xd.samples.domain.baseline.Product;

/**
 * @author David Turanski
 */
public class ProductSerializer extends Serializer<Product> {
	@Override
	public void write(Kryo kryo, Output output, Product product) {
		output.writeString(product.getName());
		output.writeDouble(product.getPrice().doubleValue());
		output.writeString(product.getDescription());
		output.writeInt(product.getAttributes().size());
		for (Map.Entry<String, String> attribute :product.getAttributes().entrySet()) {
			output.writeString(attribute.getKey());
			output.writeString(attribute.getValue());
		}

	}

	@Override
	public Product read(Kryo kryo, Input input, Class<Product> type) {
		Product product = new Product(input.readString(), new BigDecimal(input.readDouble()),input.readString());
		int attributesSize = input.readInt();
		for (int i=0; i< attributesSize; i++) {
			product.setAttribute(input.readString(), input.readString());
		}
		return product;
	}
}
