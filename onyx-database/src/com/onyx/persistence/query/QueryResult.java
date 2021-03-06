package com.onyx.persistence.query;

import com.onyx.buffer.BufferStream;
import com.onyx.buffer.BufferStreamable;
import com.onyx.exception.BufferingException;

import java.io.*;

/**
 * Created by tosborn1 on 3/13/16.
 *
 * The purpose of this class is to be a wrapper for the results since we need the result count
 * and the results.  Using the embedded, the reference of resultCount is used to update the query object
 * but since we do not have that luxury when dealing with a remote server we need this wrapper.
 */
public class QueryResult implements Serializable, Externalizable, BufferStreamable
{
    @SuppressWarnings("WeakerAccess")
    protected Query query;
    @SuppressWarnings("WeakerAccess")
    protected Object results;

    /**
     * Default Constructor for serialization
     */
    @SuppressWarnings("unused")
    public QueryResult()
    {

    }

    /**
     * Default Constructor
     *
     * @param query Original Query Object
     * @param results Actual list of results
     */
    public QueryResult(Query query, Object results)
    {
        this.query = query;
        this.results = results;
    }

    /**
     * Custom Write Serialization
     * @param out Object Output buffer
     *
     * @throws IOException Exception when writing to output stream
     */
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(this.query);
        out.writeObject(this.results);
    }

    /**
     * Custom De-Serialization
     * @param in Object Input Buffer
     *
     */
    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.query = (Query)in.readObject();
        this.results = in.readObject();
    }

    @SuppressWarnings("unused")
    public Query getQuery() {
        return query;
    }

    @SuppressWarnings("unused")
    public void setQuery(Query query) {
        this.query = query;
    }

    @SuppressWarnings("unused")
    public Object getResults() {
        return results;
    }

    @SuppressWarnings("unused")
    public void setResults(Object results) {
        this.results = results;
    }

    @Override
    public void read(BufferStream buffer) throws BufferingException {
        query = (Query)buffer.getObject();
        results = buffer.getObject();
    }

    @Override
    public void write(BufferStream buffer) throws BufferingException {
        buffer.putObject(query);
        buffer.putObject(results);
    }
}
