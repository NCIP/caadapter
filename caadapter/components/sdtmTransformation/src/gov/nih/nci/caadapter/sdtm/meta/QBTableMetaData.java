/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */

package gov.nih.nci.caadapter.sdtm.meta;

/**
 * This class represents a Table meta data
 *
 * @author OWNER: Harsha Jayanna
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v4.0 revision
 *          $Revision: 1.3 $
 *          $Date: 2008-06-06 18:55:19 $
 */
public class QBTableMetaData {
    String type=null;
    String name=null;

    public QBTableMetaData(String _type, String _name) {
        this.type = _type;
        this.name = _name;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String toString() {
        return name;
    }
}
/**
 * Change History
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2007/08/16 19:04:58  jayannah
 * Reformatted and added the Comments and the log tags for all the files
 *
 */
