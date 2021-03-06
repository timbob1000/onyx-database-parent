package embedded.relationship;

import category.EmbeddedDatabaseTests;
import com.onyx.exception.EntityException;
import com.onyx.persistence.query.*;
import entities.Address;
import entities.Person;
import embedded.base.BaseTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by tosborn1 on 3/13/17.
 */
@Category({EmbeddedDatabaseTests.class})
public class RelationshipPartitionSelectTest extends BaseTest {
    @Before
    public void before() throws EntityException {
        initialize();
    }

    @After
    public void after() throws IOException {
        shutdown();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testInvalidQueryException() throws EntityException {
        for (int i = 0; i < 50; i++) {
            Person person = new Person();
            person.firstName = "Cristian";
            person.lastName = "Vogel" + i;
            person.address = new Address();
            person.address.street = "Sluisvaart";
            person.address.houseNr = 98;
            manager.saveEntity(person);
        }

        Query query = new Query(Person.class, new QueryCriteria("address.street", QueryCriteriaOperator.EQUAL, "Sluisvaart"));
        query.setPartition("ASDF");
        List<Person> addresses = manager.executeQuery(query);
        assert addresses.size() > 0;
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testInsert() throws EntityException {
        for (int i = 0; i < 50; i++) {
            Person person = new Person();
            person.firstName = "Cristian";
            person.lastName = "Vogel" + i;
            person.address = new Address();
            person.address.street = "Sluisvaart";
            person.address.houseNr = 98;
            manager.saveEntity(person);
        }


        QueryCriteria first = new QueryCriteria("firstName", QueryCriteriaOperator.EQUAL, "Cristian");
        QueryCriteria second = new QueryCriteria("address.street", QueryCriteriaOperator.EQUAL, "Sluisvaart");
        Query query = new Query(Person.class, first.and(second));
        query.setPartition(QueryPartitionMode.ALL);
        List<Person> addresses = manager.executeQuery(query);
        assert addresses.size() > 0;
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testQuerySpecificPartition() throws EntityException {
        for (int i = 0; i < 50; i++) {
            Person person = new Person();
            person.firstName = "Cristian";
            person.lastName = "Vogel" + i;
            person.address = new Address();
            person.address.street = "Sluisvaart";
            person.address.houseNr = 98;
            manager.saveEntity(person);
        }


        QueryCriteria first = new QueryCriteria("firstName", QueryCriteriaOperator.EQUAL, "Cristian");
        QueryCriteria second = new QueryCriteria("address.street", QueryCriteriaOperator.EQUAL, "Sluisvaart");
        Query query = new Query(Person.class, first.and(second));
        query.setPartition("ASDF");
        List<Person> addresses = manager.executeQuery(query);
        assert addresses.size() > 0;
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSelectAttribute() throws EntityException {
        for (int i = 0; i < 50; i++) {
            Person person = new Person();
            person.firstName = "Cristian";
            person.lastName = "Vogel" + i;
            person.address = new Address();
            person.address.street = "Sluisvaart";
            person.address.houseNr = 98;
            manager.saveEntity(person);
        }


        QueryCriteria first = new QueryCriteria("firstName", QueryCriteriaOperator.EQUAL, "Cristian");
        QueryCriteria second = new QueryCriteria("address.street", QueryCriteriaOperator.EQUAL, "Sluisvaart");
        Query query = new Query(Person.class, first.and(second));
        query.setSelections(Arrays.asList("firstName", "address.street"));
        query.setPartition("ASDF");
        List<Person> addresses = manager.executeQuery(query);
        assert addresses.size() > 0;
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSelectRelationship() throws EntityException {
        for (int i = 0; i < 50; i++) {
            Person person = new Person();
            person.firstName = "Cristian";
            person.lastName = "Vogel" + i;
            person.address = new Address();
            person.address.street = "Sluisvaart";
            person.address.houseNr = 98;
            manager.saveEntity(person);
        }


        QueryCriteria first = new QueryCriteria("firstName", QueryCriteriaOperator.EQUAL, "Cristian");
        QueryCriteria second = new QueryCriteria("address.street", QueryCriteriaOperator.EQUAL, "Sluisvaart");
        Query query = new Query(Person.class, first.and(second));
        query.setSelections(Arrays.asList("firstName", "address"));
        query.setPartition("ASDF");
        List<Map> addresses = manager.executeQuery(query);
        assert addresses.size() > 0;
        assert addresses.get(0).get("address") instanceof Map;
        assert ((Map) addresses.get(0).get("address")).get("street").equals("Sluisvaart");

    }

    @Test
    @SuppressWarnings("unchecked")
    public void testToManySelectRelationship() throws EntityException {
        manager.executeDelete(new Query(Person.class));
        manager.executeDelete(new Query(Address.class));

        for (int i = 0; i < 50; i++) {
            Person person = new Person();
            person.firstName = "Cristian";
            person.lastName = "Vogel" + i;
            person.address = new Address();
            person.address.street = "Sluisvaart";
            person.address.houseNr = 98;
            manager.saveEntity(person);

            Person person2 = new Person();
            person2.firstName = "Timbob";
            person2.lastName = "Rooski" + i;
            person2.address = new Address();
            person2.address.id = person.address.id;
            person2.address.street = "Sluisvaart";
            person2.address.houseNr = 98;
            manager.saveEntity(person2);

            find(person2.address);
        }


        QueryCriteria first = new QueryCriteria("street", QueryCriteriaOperator.EQUAL, "Sluisvaart");
        QueryCriteria second = new QueryCriteria("occupants.firstName", QueryCriteriaOperator.NOT_EQUAL, "Ti!mbob");
        Query query = new Query(Address.class, first.and(second));
        query.setSelections(Arrays.asList("id", "street", "occupants"));

        List<Map> addresses = manager.executeQuery(query);
        assert addresses.size() > 0;
        assert addresses.get(0).get("occupants") instanceof List;
        assert ((List) addresses.get(0).get("occupants")).get(0) instanceof Map;
        assert ((Map) ((List) addresses.get(0).get("occupants")).get(0)).get("firstName") instanceof String;
        assert ((Map) ((List) addresses.get(0).get("occupants")).get(1)).get("firstName") instanceof String;
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testToManySelectRelationshipNoRelationshipCriteria() throws EntityException {
        manager.executeDelete(new Query(Person.class));
        manager.executeDelete(new Query(Address.class));

        for (int i = 0; i < 50; i++) {
            Person person = new Person();
            person.firstName = "Cristian";
            person.lastName = "Vogel" + i;
            person.address = new Address();
            person.address.street = "Sluisvaart";
            person.address.houseNr = 98;
            manager.saveEntity(person);

            Person person2 = new Person();
            person2.firstName = "Timbob";
            person2.lastName = "Rooski" + i;
            person2.address = new Address();
            person2.address.id = person.address.id;
            person2.address.street = "Sluisvaart";
            person2.address.houseNr = 98;
            manager.saveEntity(person2);

            find(person2.address);
        }


        QueryCriteria first = new QueryCriteria("street", QueryCriteriaOperator.EQUAL, "Sluisvaart");
        Query query = new Query(Address.class, first);
        query.setSelections(Arrays.asList("id", "street", "occupants"));

        List<Map> addresses = manager.executeQuery(query);
        assert addresses.size() > 0;
        assert addresses.get(0).get("occupants") instanceof List;
        assert ((List) addresses.get(0).get("occupants")).get(0) instanceof Map;
        assert ((Map) ((List) addresses.get(0).get("occupants")).get(0)).get("firstName").equals("Cristian");
        assert ((Map) ((List) addresses.get(0).get("occupants")).get(1)).get("firstName").equals("Timbob");
    }


    @Test
    @SuppressWarnings("unchecked")
    public void testQuerySpecificPartitionOrderBy() throws EntityException {
        for (int i = 0; i < 50; i++) {
            Person person = new Person();
            person.firstName = "Cristian";
            person.lastName = "Vogel" + i;
            person.address = new Address();
            person.address.street = "Sluisvaart";
            person.address.houseNr = 98;
            manager.saveEntity(person);
        }


        QueryCriteria first = new QueryCriteria("firstName", QueryCriteriaOperator.EQUAL, "Cristian");
        QueryCriteria second = new QueryCriteria("address.street", QueryCriteriaOperator.EQUAL, "Sluisvaart");
        Query query = new Query(Person.class, first.and(second));
        query.setQueryOrders(Arrays.asList(new QueryOrder("firstName"), new QueryOrder("address.street")));
        query.setPartition("ASDF");
        List<Person> addresses = manager.executeQuery(query);
        assert addresses.size() > 0;
    }


    @Test
    @SuppressWarnings("unchecked")
    public void testSelectAttributeOrderBy() throws EntityException {
        for (int i = 0; i < 50; i++) {
            Person person = new Person();
            person.firstName = "Cristian";
            person.lastName = "Vogel" + i;
            person.address = new Address();
            person.address.street = "Sluisvaart";
            person.address.houseNr = 98;
            manager.saveEntity(person);
        }


        QueryCriteria first = new QueryCriteria("firstName", QueryCriteriaOperator.EQUAL, "Cristian");
        QueryCriteria second = new QueryCriteria("address.street", QueryCriteriaOperator.EQUAL, "Sluisvaart");
        Query query = new Query(Person.class, first.and(second));
        query.setSelections(Arrays.asList("firstName", "address.street"));
        query.setPartition("ASDF");
        query.setQueryOrders(Arrays.asList(new QueryOrder("firstName"), new QueryOrder("address.street")));
        List<Person> addresses = manager.executeQuery(query);
        assert addresses.size() > 0;
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSelectRelationshipOrderBy() throws EntityException {
        for (int i = 0; i < 50; i++) {
            Person person = new Person();
            person.firstName = "Cristian";
            person.lastName = "Vogel" + i;
            person.address = new Address();
            person.address.street = "Sluisvaart";
            person.address.houseNr = 98;
            manager.saveEntity(person);
        }


        QueryCriteria first = new QueryCriteria("firstName", QueryCriteriaOperator.EQUAL, "Cristian");
        QueryCriteria second = new QueryCriteria("address.street", QueryCriteriaOperator.EQUAL, "Sluisvaart");
        Query query = new Query(Person.class, first.and(second));
        query.setSelections(Arrays.asList("firstName", "address"));
        query.setPartition("ASDF");
        query.setQueryOrders(Arrays.asList(new QueryOrder("firstName"), new QueryOrder("address.street")));
        List<Map> addresses = manager.executeQuery(query);
        assert addresses.size() > 0;
        assert addresses.get(0).get("address") instanceof Map;
        assert ((Map) addresses.get(0).get("address")).get("street").equals("Sluisvaart");

    }

    @Test
    @SuppressWarnings("unchecked")
    public void testToManySelectRelationshipOrderBy() throws EntityException {
        manager.executeDelete(new Query(Person.class));
        manager.executeDelete(new Query(Address.class));

        for (int i = 0; i < 50; i++) {
            Person person = new Person();
            person.firstName = "Cristian";
            person.lastName = "Vogel" + i;
            person.address = new Address();
            person.address.street = "Sluisvaart";
            person.address.houseNr = 98;
            manager.saveEntity(person);

            Person person2 = new Person();
            person2.firstName = "Timbob";
            person2.lastName = "Rooski" + i;
            person2.address = new Address();
            person2.address.id = person.address.id;
            person2.address.street = "Sluisvaart";
            person2.address.houseNr = 98;
            manager.saveEntity(person2);

            find(person2.address);
        }


        QueryCriteria first = new QueryCriteria("street", QueryCriteriaOperator.EQUAL, "Sluisvaart");
        QueryCriteria second = new QueryCriteria("occupants.firstName", QueryCriteriaOperator.NOT_EQUAL, "Ti!mbob");
        Query query = new Query(Address.class, first.and(second));
        query.setSelections(Arrays.asList("id", "street", "occupants"));
        query.setQueryOrders(Arrays.asList(new QueryOrder("occupants.firstName")));

        List<Map> addresses = manager.executeQuery(query);
        assert addresses.size() > 0;
        assert addresses.get(0).get("occupants") instanceof List;
        assert ((List) addresses.get(0).get("occupants")).get(0) instanceof Map;
        assert ((Map) ((List) addresses.get(0).get("occupants")).get(1)).get("firstName").equals("Timbob");
        assert ((Map) ((List) addresses.get(0).get("occupants")).get(0)).get("firstName").equals("Cristian");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testToManySelectRelationshipNoRelationshipCriteriaOrderBy() throws EntityException {
        for (int i = 0; i < 50; i++) {
            Person person = new Person();
            person.firstName = "Cristian";
            person.lastName = "Vogel" + i;
            person.address = new Address();
            person.address.street = "Sluisvaart";
            person.address.houseNr = 98;
            manager.saveEntity(person);

            Person person2 = new Person();
            person2.firstName = "Timbob";
            person2.lastName = "Rooski" + i;
            person2.address = new Address();
            person2.address.id = person.address.id;
            person2.address.street = "Sluisvaart";
            person2.address.houseNr = 98;
            manager.saveEntity(person2);

            find(person2.address);
        }


        QueryCriteria first = new QueryCriteria("street", QueryCriteriaOperator.EQUAL, "Sluisvaart");
        Query query = new Query(Address.class, first);
        query.setSelections(Arrays.asList("id", "street", "occupants"));
        query.setQueryOrders(Arrays.asList(new QueryOrder("street")));

        List<Map> addresses = manager.executeQuery(query);
        assert addresses.size() > 0;
        assert addresses.get(0).get("occupants") instanceof List;
        assert ((List) addresses.get(0).get("occupants")).get(0) instanceof Map;
    }
}
