package de.example.soe.demo.infrastructure;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.JsonObject;

public abstract class AbstractCodec<T> implements MessageCodec<T,T> {

    @Override
    public void encodeToWire(final Buffer buffer, final T t) {
        String json =  JsonObject.mapFrom(t).encode();
        buffer.appendInt(json.getBytes().length);
        buffer.appendString(json);
    }

    @Override
    public T decodeFromWire(final int position, final Buffer buffer) {
        int length = buffer.getInt(position);
        String jsonStr = buffer.getString(position+4, position+4+length);
        return (T) new JsonObject(jsonStr).mapTo(getEncodedClass());
    }

    @Override
    public T transform(final T t) {
        return t;
    }

    @Override
    public String name() {
        return getEncodedClass().getSimpleName();
    }

    /**
     * according to http://vertx.io/docs/apidocs/io/vertx/core/eventbus/MessageCodec.html#systemCodecID--,
     * this is supposed to always return -1 for user implemented codecs
     * @return -1
     */

    @Override
    public byte systemCodecID() {
        return -1;
    }

    public abstract Class getEncodedClass();
}
