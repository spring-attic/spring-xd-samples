/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
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
import com.esotericsoftware.kryo.serializers.CollectionSerializer;
import data.media.Media;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mark Pollack
 */
public class  MediaSerializer extends com.esotericsoftware.kryo.Serializer<Media> {
    private final CollectionSerializer _personsSerializer;

    public MediaSerializer () {
        _personsSerializer = new CollectionSerializer();
        _personsSerializer.setElementsCanBeNull(false);
    }

    @SuppressWarnings("unchecked")
    public Media read (com.esotericsoftware.kryo.Kryo kryo, Input input, Class<Media> type) {
        return new Media(input.readString(), input.readString(), input.readInt(true), input.readInt(true), input.readString(),
                input.readLong(true), input.readLong(true), input.readInt(true), input.readBoolean(), (List<String>)kryo.readObject(
                input, ArrayList.class, _personsSerializer), kryo.readObject(input, Media.Player.class), input.readString());
    }

    public void write (com.esotericsoftware.kryo.Kryo kryo, Output output, Media obj) {
        output.writeString(obj.uri);
        output.writeString(obj.title);
        output.writeInt(obj.width, true);
        output.writeInt(obj.height, true);
        output.writeString(obj.format);
        output.writeLong(obj.duration, true);
        output.writeLong(obj.size, true);
        output.writeInt(obj.bitrate, true);
        output.writeBoolean(obj.hasBitrate);
        kryo.writeObject(output, obj.persons, _personsSerializer);
        kryo.writeObject(output, obj.player);
        output.writeString(obj.copyright);
    }
}
