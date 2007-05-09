package gov.nih.nci.caadapter.sdtm.meta;

import gov.nih.nci.caadapter.common.MetaObjectImpl;
import gov.nih.nci.caadapter.common.SDKMetaData;

public class QueryBuilderMeta extends MetaObjectImpl implements SDKMetaData
{

    /**
     *
     */
    public String name = null;

    /**
     * @return
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @param name
     */
    public QueryBuilderMeta(String name)
    {
        this.name = name;
    }
}
