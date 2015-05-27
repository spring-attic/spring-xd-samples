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

package org.springframework.xd.samples.serialization.media;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.esotericsoftware.kryo.Registration;
import com.esotericsoftware.kryo.serializers.CollectionSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers;
import com.fasterxml.jackson.databind.ObjectMapper;
import data.media.Image;
import data.media.Media;
import data.media.MediaContent;

import org.springframework.xd.dirt.integration.bus.serializer.kryo.KryoRegistrationRegistrar;
import org.springframework.xd.dirt.integration.bus.serializer.kryo.PojoCodec;
import org.springframework.xd.samples.kryo.ImageSerializer;
import org.springframework.xd.samples.kryo.MediaContentSerializer;
import org.springframework.xd.samples.kryo.MediaSerializer;
import org.springframework.xd.samples.serialization.AbstractCodecBenchmarkTest;

import static junit.framework.TestCase.fail;


/**
 * This example is intended to replicate published results for kryo from https://github.com/eishay/jvm-serializers/wiki using
 * the same data and optimizations.  
 *  
 * @author David Turanski
 */
public class MediaContentCodecBenchmarkTest extends AbstractCodecBenchmarkTest {

	@Override
	protected Object getObjectToSerialize() {
		ObjectMapper objectMapper = new ObjectMapper();
		Object obj = null;
		try {
			obj = objectMapper.readValue(getClass().getResourceAsStream("/media.1.json"),
					MediaContent.class);
		}
		catch (IOException e) {
			fail("cannot create object instance");
		}
		return obj;
	}

	@Override
	protected PojoCodec getPojoCodec() {
		// Register serializers for all custom types
		List<Registration> registrationList = new ArrayList<Registration>();
		registrationList.add(new Registration(ArrayList.class, new CollectionSerializer(), 20));
		registrationList.add(new Registration(MediaContent.class, new MediaContentSerializer(), 21));
		registrationList.add(new Registration(Image.class, new ImageSerializer(), 22));
		registrationList.add(new Registration(Media.class, new MediaSerializer(), 23));
		registrationList.add(new Registration(Media.Player.class, new DefaultSerializers.EnumSerializer(Media.Player
				.class), 24));
		registrationList.add(new Registration(Image.Size.class, new DefaultSerializers.EnumSerializer(Image.Size
				.class), 25));
		// Set use references to 'false'
		return new PojoCodec(new KryoRegistrationRegistrar(registrationList), false);
	}
}
