package com.onyx.buffer;

import com.onyx.exception.BufferingException;

/**
 * Created by tosborn1 on 7/30/16.
 *
 * This interface is intended to enable an object for expandableByteBuffer io.  It works much like the Externalizable interface
 * except without using input and output streams it uses a ByteBuffer.  The IO is wrapped within the BufferStream.
 */
public interface BufferStreamable {

    /**
     * Read from the expandableByteBuffer expandableByteBuffer to get the objects.
     *
     * @param buffer Buffer Stream to read from
     * @throws BufferingException Generic IO Exception from the expandableByteBuffer
     * @since 1.1.0
     */
    void read(BufferStream buffer) throws BufferingException;

    /**
     * Write to the expandableByteBuffer expandableByteBuffer
     *
     * @param buffer Buffer IO Stream to write to
     * @throws BufferingException Generic IO Exception from the expandableByteBuffer
     * @since 1.1.0
     */
    void write(BufferStream buffer) throws BufferingException;

}
