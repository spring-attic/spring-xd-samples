/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.xd.samples.kryo;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import data.media.Image;

/**
 * @author Mark Pollack
 */
public class ImageSerializer extends com.esotericsoftware.kryo.Serializer<Image> {
    public Image read (com.esotericsoftware.kryo.Kryo kryo, Input input, Class<Image> type) {
        return new Image(input.readString(), input.readString(), input.readInt(true), input.readInt(true), kryo.readObject(
                input, Image.Size.class));
    }

    public void write (com.esotericsoftware.kryo.Kryo kryo, Output output, Image obj) {
        output.writeString(obj.uri);
        output.writeString(obj.title);
        output.writeInt(obj.width, true);
        output.writeInt(obj.height, true);
        kryo.writeObject(output, obj.size);
    }
}