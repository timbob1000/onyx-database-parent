package com.onyx.structure.node;

import com.onyx.structure.serializer.ObjectBuffer;
import com.onyx.structure.serializer.ObjectSerializable;

import java.io.IOException;

/**
 * Created by timothy.osborn on 3/25/15.
 */
public class Record implements ObjectSerializable
{
    public Object key;
    public Object value;

    public RecordReference reference;
    public int keySize;
    public int valueSize;

    public int getSize()
    {
        return keySize + valueSize;
    }

    @Override
    public void writeObject(ObjectBuffer buffer) throws IOException
    {
        keySize = buffer.writeObject(key);
        valueSize = buffer.writeObject(value);
    }

    @Override
    public void readObject(ObjectBuffer buffer) throws IOException
    {
        key = buffer.readObject();
        value = buffer.readObject();
    }

    @Override
    public void readObject(ObjectBuffer buffer, long checksum) throws IOException
    {
        readObject(buffer);
    }

    @Override
    public void readObject(ObjectBuffer buffer, long position, int serializerId) throws IOException {

    }
}
