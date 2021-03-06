package entities;

import com.onyx.persistence.IManagedEntity;
import com.onyx.persistence.annotations.*;

/**
 * Created by cosborn on 12/26/2014.
 */
@Entity
public class EntityWithCallbacks extends AbstractEntity implements IManagedEntity {

    @Identifier(generator=IdentifierGenerator.NONE)
    @Attribute(size=255)
    private String id;

    @Attribute(size=255)
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @PreInsert
    public void beforeInsert(){
        setName(getName() + "_PreInsert");
    }

    @PreUpdate
    public void beforeUpdate(){
        setName(getName() + "_PreUpdate");
    }

    @PrePersist
    public void beforePersist(){
        setName(getName() + "_PrePersist");
    }

    @PreRemove
    public void beforeRemove(){
        setName(getName() + "_PreRemove");
    }

    @PostInsert
    public void afterInsert(){
        setName(getName() + "_PostInsert");
    }

    @PostUpdate
    public void afterUpdate(){
        setName(getName() + "_PostUpdate");
    }

    @PostPersist
    public void afterPersist(){
        setName(getName() + "_PostPersist");
    }

    @PostRemove
    public void afterRemove(){
        setName(getName() + "_PostRemove");
    }

    @PostLoad
    public void afterLoad(){
        setName(getName() + "_PostLoad");
    }

}
