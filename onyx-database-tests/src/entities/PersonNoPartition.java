package entities;

import com.onyx.persistence.ManagedEntity;
import com.onyx.persistence.annotations.*;

/**
 * Created by tosborn1 on 3/17/17.
 */
@Entity
public class PersonNoPartition extends ManagedEntity{
    public PersonNoPartition() {

    }
    @Identifier(generator = IdentifierGenerator.SEQUENCE)
    @Attribute
    public Long id;

    @Attribute(nullable = false)
    public String firstName;

    @Attribute(nullable = false)
    public String lastName;

    @Relationship(type = RelationshipType.MANY_TO_ONE, inverseClass = AddressNoPartition.class, inverse = "occupants", cascadePolicy = CascadePolicy.ALL)
    public AddressNoPartition address;
}
