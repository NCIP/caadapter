package gov.nih.nci.caadapter.sdtm.util;

/**
 * Created by IntelliJ IDEA.
 * User: hjayanna
 * Date: Mar 23, 2007
 * Time: 11:16:54 AM
 * To change this template use File | Settings | File Templates.
 */
/**
 *
 * @author gtoffoli
 */
public class Entity {

    private String schema = "";
    private String entityName = "";
    /** Creates a new instance of Entity */
    public Entity(String schema, String entityName) {

        this.setSchema(schema);
        this.setEntityName(entityName);
    }

    public String toString()
    {
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
