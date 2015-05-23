package com.fancypants.common.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.objenesis.strategy.StdInstantiatorStrategy;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class SerializationUtils {
	
    public static byte[] serialize(final Object obj) {
        final Kryo kryo = new Kryo();
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        final Output output = new Output(os);

        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
        kryo.writeClassAndObject(output, obj);

        output.flush();
        return os.toByteArray();
    }

    public static Object deserialize(final byte[] ser) {
        final Kryo kryo = new Kryo();
        final Input input = new Input(new ByteArrayInputStream(ser));

        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
        return kryo.readClassAndObject(input);
    }
}
