package com.onyx.fetch.impl;

import com.onyx.descriptor.EntityDescriptor;
import com.onyx.exception.EntityClassNotFoundException;
import com.onyx.helpers.PartitionHelper;
import com.onyx.persistence.IManagedEntity;
import com.onyx.util.ReflectionUtil;
import com.onyx.util.map.CompatHashMap;
import com.onyx.util.map.CompatMap;
import com.onyx.util.map.SynchronizedMap;
import com.onyx.entity.SystemEntity;
import com.onyx.entity.SystemPartitionEntry;
import com.onyx.exception.EntityException;
import com.onyx.exception.EntityExceptionWrapper;
import com.onyx.fetch.PartitionReference;
import com.onyx.fetch.TableScanner;
import com.onyx.index.IndexController;
import com.onyx.persistence.context.SchemaContext;
import com.onyx.persistence.manager.PersistenceManager;
import com.onyx.persistence.query.Query;
import com.onyx.persistence.query.QueryCriteria;
import com.onyx.persistence.query.QueryCriteriaOperator;
import com.onyx.persistence.query.QueryPartitionMode;
import com.onyx.diskmap.MapBuilder;

import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * Created by timothy.osborn on 2/10/15.
 *
 * Scan a partition for matching index values
 */
public class PartitionIndexScanner extends IndexScanner implements TableScanner {

    private SystemEntity systemEntity = null;

    /**
     * Constructor
     *
     * @param criteria Query Criteria
     * @param classToScan Class type to scan
     * @param descriptor Entity descriptor of entity type to scan
     * @param temporaryDataFile Temproary data file to put results into
     */
    public PartitionIndexScanner(QueryCriteria criteria, Class classToScan, EntityDescriptor descriptor, MapBuilder temporaryDataFile, Query query, SchemaContext context, PersistenceManager persistenceManager) throws EntityException
    {
        super(criteria, classToScan, descriptor, temporaryDataFile, query, context, persistenceManager);

        systemEntity = context.getSystemEntityByName(query.getEntityType().getName());
    }

    /**
     * Full Table Scan
     *
     * @return Matching references for criteria
     * @throws EntityException Cannot scan partition
     */
    @SuppressWarnings("unchecked")
    public Map scan() throws EntityException
    {

        final EntityExceptionWrapper wrapper = new EntityExceptionWrapper();
        CompatMap<PartitionReference, PartitionReference> results = new SynchronizedMap();

        if(query.getPartition() == QueryPartitionMode.ALL)
        {
            Iterator<SystemPartitionEntry> it = systemEntity.getPartition().getEntries().iterator();
            final CountDownLatch countDownLatch = new CountDownLatch(systemEntity.getPartition().getEntries().size());

            while(it.hasNext())
            {
                final SystemPartitionEntry partition = it.next();

                executorService.execute(() -> {
                    try
                    {
                        final EntityDescriptor partitionDescriptor = getContext().getDescriptorForEntity(query.getEntityType(), partition.getValue());
                        final IndexController partitionIndexController = getContext().getIndexController(partitionDescriptor.getIndexes().get(criteria.getAttribute()));
                        Map partitionResults = scanPartition(partitionIndexController, partition.getIndex());
                        results.putAll(partitionResults);
                        countDownLatch.countDown();
                    } catch (EntityException e)
                    {
                        countDownLatch.countDown();
                        wrapper.exception = e;
                    }

                });

            }

            try {
                countDownLatch.await();
            } catch (InterruptedException ignore) {}

            if (wrapper.exception != null)
            {
                throw wrapper.exception;
            }
        }
        else
        {
            IManagedEntity entity;
            try {
                entity = (IManagedEntity) ReflectionUtil.instantiate(query.getEntityType());
            } catch (InstantiationException | IllegalAccessException e) {
                throw new EntityClassNotFoundException(EntityClassNotFoundException.ENTITY_NOT_FOUND, query.getEntityType());
            }

            PartitionHelper.setPartitionValueForEntity(entity, query.getPartition(), getContext());
            long partitionId = getPartitionId(entity);
            if(partitionId < 1)
                return new HashMap();

            final EntityDescriptor partitionDescriptor = getContext().getDescriptorForEntity(query.getEntityType(), query.getPartition());
            final IndexController partitionIndexController = getContext().getIndexController(partitionDescriptor.getIndexes().get(criteria.getAttribute()));
            results.putAll(scanPartition(partitionIndexController, partitionId));
        }

        return results;
    }

    /**
     * Scan indexes
     *
     * @return Matching values meeting criteria
     * @throws EntityException Cannot scan partition
     */
    @SuppressWarnings("unchecked")
    private Map scanPartition(IndexController partitionIndexController, long partitionId) throws EntityException
    {
        final CompatMap returnValue = new CompatHashMap();
        final List<Long> references = new ArrayList<>();

        if(criteria.getValue() instanceof List)
        {
            for(Object idValue : (List<Object>) criteria.getValue())
            {
                if(query.isTerminated())
                    return returnValue;

                references.addAll(partitionIndexController.findAll(idValue).keySet());
            }
        }
        else
        {
            Set values;

            if(QueryCriteriaOperator.GREATER_THAN.equals(criteria.getOperator()))
                values = partitionIndexController.findAllAbove(criteria.getValue(), false);
            else if(QueryCriteriaOperator.GREATER_THAN_EQUAL.equals(criteria.getOperator()))
                values = partitionIndexController.findAllAbove(criteria.getValue(), true);
            else if(QueryCriteriaOperator.LESS_THAN.equals(criteria.getOperator()))
                values = partitionIndexController.findAllBelow(criteria.getValue(), false);
            else if(QueryCriteriaOperator.LESS_THAN_EQUAL.equals(criteria.getOperator()))
                values = partitionIndexController.findAllBelow(criteria.getValue(), true);
            else
                values =  partitionIndexController.findAll(criteria.getValue()).keySet();

            references.addAll(values);
        }

        for(Long val : references)
        {
            returnValue.put(new PartitionReference(partitionId, val), new PartitionReference(partitionId, val));
        }

        return returnValue;
    }

    /**
     * Scan indexes that are within the existing values
     *
     * @param existingValues Existing values to match criteria
     * @return Existing values meeting additional criteria
     * @throws EntityException Cannot scan partition
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map scan(Map existingValues) throws EntityException
    {
        final CompatMap returnValue = new CompatHashMap<>();

        final EntityExceptionWrapper wrapper = new EntityExceptionWrapper();
        CompatMap<PartitionReference, PartitionReference> results = new SynchronizedMap<>();

        if(query.getPartition() == QueryPartitionMode.ALL)
        {
            for(SystemPartitionEntry partition : systemEntity.getPartition().getEntries())
            {
                try {
                    final EntityDescriptor partitionDescriptor = getContext().getDescriptorForEntity(query.getEntityType(), partition.getValue());
                    final IndexController partitionIndexController = getContext().getIndexController(partitionDescriptor.getIndexes().get(criteria.getAttribute()));
                    Map partitionResults = scanPartition(partitionIndexController, partition.getIndex());

                    results.putAll(partitionResults);

                    //noinspection Convert2streamapi
                    for(PartitionReference reference : results.keySet())
                    {
                        if (existingValues.containsKey(reference)) {
                            returnValue.put(reference, reference);
                        }
                    }

                } catch (EntityException e) {
                    wrapper.exception = e;
                }
            }

            if (wrapper.exception != null)
            {
                throw wrapper.exception;
            }
        }
        else
        {

            IManagedEntity entity;
            try {
                entity = (IManagedEntity) ReflectionUtil.instantiate(query.getEntityType());
            } catch (InstantiationException | IllegalAccessException e) {
                throw new EntityClassNotFoundException(EntityClassNotFoundException.ENTITY_NOT_FOUND, query.getEntityType());
            }

            PartitionHelper.setPartitionValueForEntity(entity, query.getPartition(), getContext());
            long partitionId = getPartitionId(entity);
            if(partitionId < 1)
                return new HashMap();

            final EntityDescriptor partitionDescriptor = getContext().getDescriptorForEntity(query.getEntityType(), query.getPartition());
            final IndexController partitionIndexController = getContext().getIndexController(partitionDescriptor.getIndexes().get(criteria.getAttribute()));
            results.putAll(scanPartition(partitionIndexController, partitionId));

            //noinspection Convert2streamapi
            for(PartitionReference reference : results.keySet())
            {
                if (existingValues.containsKey(reference)) {
                    returnValue.put(reference, reference);
                }
            }
        }
        return returnValue;
    }
}
