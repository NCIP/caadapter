/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */

package gov.nih.nci.caadapter.sdtm.meta;

import gov.nih.nci.caadapter.common.MetaObjectImpl;
import gov.nih.nci.caadapter.common.SDKMetaData;
import gov.nih.nci.caadapter.common.util.PropertiesResult;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

public class QueryBuilderMeta extends MetaObjectImpl implements SDKMetaData
{

    String columnName;

    boolean nullable;

    String ordinalPosition;

    public QueryBuilderMeta(String _columnName, String _typeName, String _columnSize, boolean _nullable, String _ordinalPosition)
    {
        this.columnName = _columnName;
        this.typeName = _typeName;
        this.columnSize = _columnSize;
        this.nullable = _nullable;
        this.ordinalPosition = _ordinalPosition;
    }

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

    public PropertiesResult getPropertyDescriptors() throws Exception
    {
        Class beanClass = this.getClass();
        PropertyDescriptor _columnName = new PropertyDescriptor("Column Name", beanClass, "getColumnName", null);
        PropertyDescriptor _typeName = new PropertyDescriptor("Type Name", beanClass, "getTypeName", null);
        PropertyDescriptor _columnSize = new PropertyDescriptor("Column Size", beanClass, "getColumnSize", null);
        PropertyDescriptor _nullabel = new PropertyDescriptor("Nullable", beanClass, "isNullable", null);
        PropertyDescriptor _ordPosition = new PropertyDescriptor("Ordinal Position", beanClass, "getOrdinalPosition", null);
        List<PropertyDescriptor> propList = new ArrayList<PropertyDescriptor>();
        propList.add(_columnName);
        propList.add(_typeName);
        propList.add(_columnSize);
        propList.add(_nullabel);
        propList.add(_ordPosition);
        PropertiesResult result = new PropertiesResult();
        result.addPropertyDescriptors(this, propList);
        return result;
    }

    /**
     * @param name
     */
    public QueryBuilderMeta(String name)
    {
        this.name = name;
    }

    public String getColumnSize()
    {
        return columnSize;
    }

    public boolean isNullable()
    {
        return nullable;
    }

    public String getOrdinalPosition()
    {
        return ordinalPosition;
    }

    public String getTypeName()
    {
        return typeName;
    }

    String typeName;

    String columnSize;

    public String getColumnName()
    {
        return columnName;
    }

    public String toString()
    {
        return columnName;
    }
}
