package gov.nih.nci.caadapter.sdtm.meta;

/**
 * This class represents a Table meta data
 *
 * @author OWNER: Harsha Jayanna
 * @author LAST UPDATE $Author: jayannah $
 * @version Since caAdapter v4.0 revision
 *          $Revision: 1.2 $
 *          $Date: 2007-08-16 19:04:58 $
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
 */
