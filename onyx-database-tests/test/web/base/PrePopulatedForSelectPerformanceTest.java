package web.base;

import com.onyx.exception.EntityException;
import com.onyx.exception.InitializationException;
import com.onyx.persistence.IManagedEntity;
import entities.PerformanceEntity;
import entities.PerformanceEntityChild;
import org.junit.After;
import org.junit.Before;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by timothy.osborn on 1/14/15.
 */
public class PrePopulatedForSelectPerformanceTest extends BaseTest
{
    @After
    public void after() throws IOException
    {
        shutdown();
    }

    @Before
    public void load100kRecords() throws InitializationException
    {
        initialize();

        List<IManagedEntity> entityList = new ArrayList<>();
        for(int i = 0; i < 100000; i++)
        {
            PerformanceEntity entity = new PerformanceEntity();
            entity.stringValue = getRandomString();
            entity.dateValue = new Date();

            if((i %2) == 0)
            {
                entity.booleanValue = true;
                entity.booleanPrimitive = false;
            }
            else
            {
                entity.booleanPrimitive = true;
                entity.booleanValue = false;
            }

            entity.intPrimitive = getRandomInteger();
            entity.longPrimitive = getRandomInteger();
            entity.doublePrimitive = getRandomInteger() * .001;
            entity.longValue = Long.valueOf(getRandomInteger());
            entity.longPrimitive = getRandomInteger();

            entity.child = new PerformanceEntityChild();
            entity.child.someOtherField = getRandomString();


            entityList.add(entity);
        }

        try
        {
            manager.saveEntities(entityList);
        } catch (EntityException e)
        {
            e.printStackTrace();
        }
    }

}
