package gov.nih.nci.caadapter.sdtm.meta;

import gov.nih.nci.caadapter.common.MetaObjectImpl;
import gov.nih.nci.caadapter.common.SDKMetaData;

/**
 * Created by IntelliJ IDEA.
 * User: hjayanna
 * Date: Jul 13, 2007
 * Time: 2:30:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class QBTableMetaData
{

    String type;

    String name;

    public QBTableMetaData(String _type, String _name)
    {
        this.type = _type;
        this.name = _name;
    }

    public String getName()
    {
        return name;
    }

    public String getType()
    {
        return type;
    }

    public String toString()
    {
        return name;
    }
}
