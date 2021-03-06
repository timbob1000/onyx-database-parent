package entities;

import com.onyx.persistence.IManagedEntity;
import com.onyx.persistence.annotations.*;

/**
 * Created by timothy.osborn on 11/15/14.
 */
@Entity
public class OneToManyChildFetchEntity extends AbstractInheritedAttributes implements IManagedEntity
{
    @Attribute
    @Identifier
    public String id;

    @Relationship(type = RelationshipType.MANY_TO_ONE, inverseClass = OneToOneFetchEntity.class, inverse = "children")
    public OneToOneFetchEntity parents;

}
