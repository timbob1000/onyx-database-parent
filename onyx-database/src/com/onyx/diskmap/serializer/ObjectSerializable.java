package com.onyx.diskmap.serializer;

import java.io.IOException;
import java.io.Serializable;

/**
 * Created by timothy.osborn on 3/25/15.
 *
 * Interface that defines custom serialization behavior with the ObjectBuffer
 */
public interface ObjectSerializable extends Serializable
{
    /**
     * Write Object to the object buffer
     * @param buffer Object buffer to write from
     * @throws IOException Exception while writing.
     */
    void writeObject(ObjectBuffer buffer) throws IOException;

    /**
     * Read an object from the buffer
     *
     * @param buffer Buffer to read from
     * @throws IOException Exception while reading
     */
    void readObject(ObjectBuffer buffer) throws IOException;

    /**
     * Read an object from the object buffer an check the checksum to ensure the record was de-serialized propertly.
     *
     * @param buffer Buffer to read from
     * @param position position to verify the checksum with
     * @throws IOException Exception while reading
     */
    default void readObject(ObjectBuffer buffer, long position) throws IOException{
        readObject(buffer);
    }

    /**
     * Read an object from the object buffer an check the checksum to ensure the record was de-serialized propertly.  Also
     * use a specific serializer id to determine how to de-serialize object.
     *
     * @param buffer Buffer to read from
     * @param position position to verify the checksum with
     * @throws IOException Exception while reading
     */
    default void readObject(ObjectBuffer buffer, long position, int serializerId) throws IOException{
        readObject(buffer, position);
    }
}
