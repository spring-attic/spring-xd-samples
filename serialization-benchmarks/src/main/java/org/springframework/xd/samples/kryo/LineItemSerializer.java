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
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import org.springframework.xd.samples.domain.baseline.LineItem;
import org.springframework.xd.samples.domain.baseline.Product;

/**
 * @author David Turanski
 */
public class LineItemSerializer extends Serializer<LineItem> {
	ProductSerializer productSerializer;

	public LineItemSerializer() {
		this.productSerializer = new ProductSerializer();
	}

	@Override
	public void write(Kryo kryo, Output output, LineItem lineItem) {
		kryo.writeObject(output, lineItem.getProduct(),productSerializer);
		output.writeInt(lineItem.getAmount());

	}

	@Override
	public LineItem read(Kryo kryo, Input input, Class<LineItem> type) {
		return new LineItem(kryo.readObject(input, Product.class, productSerializer),input.readInt());
	}
}
