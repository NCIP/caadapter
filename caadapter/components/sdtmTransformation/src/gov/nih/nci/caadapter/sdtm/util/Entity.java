package gov.nih.nci.caadapter.sdtm.util;

/**
 * This class represents a entity
 *
 * @author OWNER: Harsha Jayanna
 * @author LAST UPDATE $Author: jayannah $
 * @version Since caAdapter v4.0 revision
 *          $Revision: 1.2 $
 *          $Date: 2007-08-16 19:04:58 $
 */
public class Entity {
    private String schema = null;
    private String entityName = null;

    /**
     * Creates a new instance of Entity
     */
    public Entity(String schema, String entityName) {
        this.setSchema(schema);
        this.setEntityName(entityName);
    }

    public String toString() {
        return getEntityName();
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }
}
/**
 * Change History
 * $Log: not supported by cvs2svn $
 */