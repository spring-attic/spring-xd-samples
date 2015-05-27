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
import com.esotericsoftware.kryo.serializers.CollectionSerializer;
import data.media.Image;
import data.media.Media;
import data.media.MediaContent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mark Pollack
 */
public class MediaContentSerializer extends com.esotericsoftware.kryo.Serializer<MediaContent> {
    private CollectionSerializer _imagesSerializer;

    public MediaContentSerializer () {
        _imagesSerializer = new CollectionSerializer();
        _imagesSerializer.setElementsCanBeNull(false);
    }

    public MediaContent read (com.esotericsoftware.kryo.Kryo kryo, Input input, Class<MediaContent> type) {
        final Media media = kryo.readObject(input, Media.class);
        @SuppressWarnings("unchecked")
        final List<Image> images = (List<Image>)kryo.readObject(input, ArrayList.class, _imagesSerializer);
        return new MediaContent(media, images);
    }

    public void write (com.esotericsoftware.kryo.Kryo kryo, Output output, MediaContent obj) {
        kryo.writeObject(output, obj.media);
        kryo.writeObject(output, obj.images, _imagesSerializer);
    }
}
