package com.onyx.diskmap.base.hashmap;

import com.onyx.buffer.BufferStream;
import com.onyx.diskmap.base.DiskSkipListMap;
import com.onyx.diskmap.node.Header;
import com.onyx.diskmap.serializer.ObjectBuffer;
import com.onyx.diskmap.store.Store;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class is the base of a hash table.  It will allocate upon being instantiated if the header does not already exist
 * This allocates a fixed amount of space within the store and puts it aside to maintain a hash table.
 *
 * @since 1.2.0
 *
 * @param <K> Key
 * @param <V> Value
 */
abstract class AbstractHashMap<K, V> extends DiskSkipListMap<K,V> {

    @SuppressWarnings("WeakerAccess")
    final AtomicInteger mapCount; // Count of allocated hash table used
    private final int referenceOffset; // Offest of the references
    private final int listReferenceOffset; // Offest of the iteration list reference

    /**
     * Constructor
     *
     * @param fileStore File storage
     * @param header Head of the data structore
     * @param headless Whether it is used in a stateless manner.
     * @param loadFactor Indicates how large to allocate the initial data structure
     *
     * @since 1.2.0
     */
    AbstractHashMap(Store fileStore, Header header, boolean headless, int loadFactor) {
        super(fileStore, header, headless);
        this.loadFactor = (byte) loadFactor;
        int allocation = 1;

        // Figure out how many bytes to allocate
        for (int i = 0; i < loadFactor; i++)
            allocation *= 10;

        // Find the offest
        int numberOfReferenceBytes = allocation * Long.BYTES;
        int numberOfListReferenceBytes = allocation * Integer.BYTES;
        int countBytes = Integer.BYTES;

        // Create the header if it does not exist.  Also allocate the hash table
        if (header.firstNode == 0) {
            forceUpdateHeaderFirstNode(this.header, fileStore.allocate(numberOfReferenceBytes + numberOfListReferenceBytes + countBytes));
            this.mapCount = new AtomicInteger(0);
        } else
        {
            // It already exist.  Get the map count.  It is located within the first 4 bytes of the allocated hash table space.
            long position = header.firstNode;
            final ObjectBuffer objectBuffer = fileStore.read(position, Integer.BYTES);
            mapCount = new AtomicInteger(objectBuffer.readInt());
        }

        referenceOffset = countBytes;
        listReferenceOffset = referenceOffset + numberOfReferenceBytes;
    }

    /**
     * Insert reference into the hash array.
     *
     * @param hash The maximum hash value can only contain as many digits as the size of the loadFactor
     * @param reference Reference of the sub data structure to put it into.
     * @return The reference that was inserted
     *
     * @since 1.2.0
     */
    @SuppressWarnings({"UnusedReturnValue", "WeakerAccess"})
    protected long insertReference(int hash, long reference)
    {
        ByteBuffer buffer = BufferStream.allocate(Long.BYTES);

        // Update count
        int count = incrementMapCount();
        buffer.putInt(count);
        fileStore.write(new ObjectBuffer(buffer, null), header.firstNode);

        buffer.clear();
        buffer.putLong(reference);
        fileStore.write(new ObjectBuffer(buffer, null), (header.firstNode + referenceOffset + (hash*8)));

        addIterationList(buffer, hash, count-1);

        BufferStream.recycle(buffer);
        return reference;
    }

    /**
     * Add iteration list.  This method adds a reference so that the iterator knows what to iterate through without
     * guessing wich element within the hash as a sub data structure reference.
     *
     * @param buffer Byte Buffer to add the hash id to.
     * @param hash Identifier of the sub data structure
     * @param count The current size of the hash table
     *
     * @since 1.2.0
     */
    void addIterationList(ByteBuffer buffer, int hash, int count)
    {
        // Add list reference for iterating
        buffer.clear();
        buffer.putInt(hash);
        fileStore.write(new ObjectBuffer(buffer, null), (header.firstNode + listReferenceOffset + (count * Integer.BYTES)));
    }

    /**
     * Update the reference of the hash.
     *
     * @param hash Identifier of the data structure
     * @param reference Reference of the sub data structure to update to.
     * @since 1.2.0
     * @return The reference that was sent in.
     */
    @SuppressWarnings({"UnusedReturnValue", "WeakerAccess"})
    protected long updateReference(int hash, long reference)
    {
        long position = (header.firstNode + referenceOffset + (hash*8));
        ByteBuffer buffer = BufferStream.allocate(Long.BYTES);
        buffer.putLong(reference);
        fileStore.write(new ObjectBuffer(buffer, null), position);
        BufferStream.recycle(buffer);
        return reference;
    }

    /**
     * Get Map ID within the iteration index
     * @param index Index within the list of maps
     * @return The hash identifier of the sub data structure
     * @since 1.2.0
     */
    @SuppressWarnings("WeakerAccess")
    protected int getMapIdentifier(int index)
    {
        long position = header.firstNode + listReferenceOffset + (index * Integer.BYTES);
        final ObjectBuffer objectBuffer = fileStore.read(position, Integer.BYTES);
        return objectBuffer.readInt();
    }

    /**
     * Get the sub data structure reference for the hash id.
     * @param hash Identifier of the data structure
     * @return Location of the data structure within the volume/store
     *
     * @since 1.2.0
     */
    @SuppressWarnings("WeakerAccess")
    protected long getReference(int hash)
    {
        long position = (hash*8) + referenceOffset + header.firstNode;
        final ObjectBuffer objectBuffer = fileStore.read(position, Long.BYTES);
        return objectBuffer.readLong();
    }

    /**
     * Used to retrieve the amount of sub data structures
     *
     * @return atomic value of map count
     */
    @SuppressWarnings("WeakerAccess")
    protected int getMapCount() {
        return mapCount.get();
    }

    /**
     * Used to increment map count
     *
     * @return Map count value after incrementing
     */
    @SuppressWarnings("WeakerAccess")
    protected int incrementMapCount() {
        return mapCount.addAndGet(1);
    }
}
