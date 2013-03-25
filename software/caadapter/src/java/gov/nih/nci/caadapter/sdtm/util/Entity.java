/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.sdtm.util;

/**
 * This class represents a entity
 *
 * @author OWNER: Harsha Jayanna
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v4.0 revision
 *          $Revision: 1.4 $
 *          $Date: 2008-06-09 19:53:51 $
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
 * Revision 1.3  2008/06/06 18:55:19  phadkes
 * Changes for License Text
 *
 * Revision 1.2  2007/08/16 19:04:58  jayannah
 * Reformatted and added the Comments and the log tags for all the files
 *
 */
