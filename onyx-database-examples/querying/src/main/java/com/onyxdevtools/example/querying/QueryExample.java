package com.onyxdevtools.example.querying;

import com.onyx.exception.EntityException;
import com.onyx.persistence.factory.PersistenceManagerFactory;
import com.onyx.persistence.factory.impl.EmbeddedPersistenceManagerFactory;
import com.onyx.persistence.manager.PersistenceManager;
import com.onyx.persistence.query.Query;
import com.onyx.persistence.query.QueryCriteria;
import com.onyx.persistence.query.QueryCriteriaOperator;
import com.onyx.persistence.query.QueryOrder;
import com.onyxdevtools.example.querying.entities.Player;

import java.io.File;
import java.util.List;


/**
 @author  cosborn
 */
class QueryExample
{
    @SuppressWarnings("unchecked")
    public static void demo() throws EntityException
    {
        // get an instance of the persistenceManager
        final PersistenceManagerFactory factory = new EmbeddedPersistenceManagerFactory();
        factory.setCredentials("onyx-user", "SavingDataIsFun!");

        final String pathToOnyxDB = System.getProperty("user.home") + File.separatorChar + ".onyxdb" + File.separatorChar + "sandbox" +
            File.separatorChar + "querying-db.oxd";
        factory.setDatabaseLocation(pathToOnyxDB);
        factory.initialize();

        final PersistenceManager manager = factory.getPersistenceManager();

        // Create a query and criteria
        final QueryCriteria criteria = new QueryCriteria("position", QueryCriteriaOperator.EQUAL, "QB");
        criteria.and("lastName", QueryCriteriaOperator.STARTS_WITH, "C");

        final Query query = new Query(Player.class, criteria, new QueryOrder("firstName"));
        // query.setCriteria(criteria); or you can set the critiera after construction

        final List<Player> quarterbacks = manager.executeQuery(query);

        for (final Player qb : quarterbacks)
        {
            System.out.println(qb.getFirstName() + " " + qb.getLastName());
        }

        factory.close(); // close the factory so that we can use it again

    }
}
