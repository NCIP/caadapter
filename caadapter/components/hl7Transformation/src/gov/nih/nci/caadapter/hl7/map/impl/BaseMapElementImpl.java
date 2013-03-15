/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.hl7.map.impl;

import gov.nih.nci.caadapter.castor.map.impl.C_component;
import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.common.MetaObject;
import gov.nih.nci.caadapter.common.csv.meta.CSVFieldMeta;
import gov.nih.nci.caadapter.common.function.meta.ParameterMeta;
import gov.nih.nci.caadapter.common.map.BaseComponent;
import gov.nih.nci.caadapter.common.map.BaseMapElement;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.hl7.datatype.DatatypeBaseObject;

/**
 * Half of a map, represents an object that has been mapped to or from.
 *
 * @author OWNER: Matthew Giordano
 * @author LAST UPDATE $Author: phadkes $
 * @since     caAdapter v1.2
 * @version    $Revision: 1.6 $
 */
public class BaseMapElementImpl implements BaseMapElement{
    private static final String LOGID = "$RCSfile: BaseMapElementImpl.java,v $";
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/hl7Transformation/src/gov/nih/nci/caadapter/hl7/map/impl/BaseMapElementImpl.java,v 1.6 2008-06-09 19:53:50 phadkes Exp $";

    private BaseComponent component;
    private MetaObject metaObject;

    private String mappedObjectXmlPath;
    // constuctors.
    public BaseMapElementImpl() {}

    public BaseMapElementImpl(BaseComponent component, Object object) {
        this.component = component;
        if ( object instanceof MetaObject)
        	this.metaObject = (MetaObject)object;
        else if (object instanceof DatatypeBaseObject )
        	mappedObjectXmlPath=((DatatypeBaseObject)object).getXmlPath();
        else if ( object instanceof String)
        	mappedObjectXmlPath= (String)object;
        else
        {
        	String logMsg="Invalid object to create a mapping element: "+object.getClass();
        	Log.logError(this, logMsg);
        }
    }

    // setters and getters.
    public BaseComponent getComponent() {
        return component;
    }

    public void setComponent(BaseComponent component) {
        this.component = component;
    }

    public MetaObject getMetaObject() {
        return metaObject;
    }

    public void setMetaObject(MetaObject metaObject) {
        this.metaObject = metaObject;
    }

    // BaseMapElement methods.
    public String getDataXmlPath() {
    	if(metaObject!=null)
    	{
   			return metaObject.getXmlPath();
    	}
    	else
    		return getXmlPath();
    }

    public String getXmlPath() {
		return mappedObjectXmlPath;
	}

	public void setXmlPath(String datatypeBaseObjectXmlPath) {
		mappedObjectXmlPath = datatypeBaseObjectXmlPath;
	}

    public boolean isSource()
	{
		//@todo: Matt will help realize isSource() and isTarget() functions to be based on this object's position in the MapImpl object that this object is enclosed.
		/**
		 * warning:
		 * 1) The type of enclosed component does not imply whether this map object is source or target in the enclosing map relation (the outer MapImpl object).
		 * --It could, but it is an implicit denotation on the relationship between the component type and the origination of this map element in its mapping relation.
		 * 2) The isSource() and isTarget() methods need better delibration especially esp. when every possible type of meta data could be
		 * used as source component.
		 */
        if(isComponentOfTargetType()){
            return false;
        }else if(isComponentOfSourceType()){
            return true;
        }if(isComponentOfFunctionType()){
            return !((ParameterMeta)metaObject).isInput();
        }
        return false;
    }

	public boolean isTarget()
	{
        return !isSource();
	}



	/**
	 * This utility method will answer if the contained component is of source type.
	 *
	 * @return
	 */
	public boolean isComponentOfSourceType()
	{
		boolean checkStatus = isComponentOfType(Config.MAP_COMPONENT_SOURCE_TYPE);
		return checkStatus;
	}

	/**
	 * This utility method will answer if the contained component is of target type.
	 *
	 * @return
	 */
	public boolean isComponentOfTargetType()
	{
		boolean checkStatus = isComponentOfType(Config.MAP_COMPONENT_TARGET_TYPE);
		return checkStatus;
	}
	/**
	 * This utility method will answer if the contained component is of function type.
	 * @return
	 */
	public boolean isComponentOfFunctionType()
	{
		boolean checkStatus = isComponentOfType(Config.MAP_COMPONENT_FUNCTION_TYPE);
		return checkStatus;
	}

	/**
	 * The type string is currently defined in gov.nih.nci.caadapter.hl7.util.Config class.
	 * @param typeString
	 * @return
	 */
	private boolean isComponentOfType(String typeString)
	{
		boolean checkStatus = component != null;
		checkStatus = checkStatus && (typeString==null ? component.getType()==null : typeString.equals(component.getType()));
		return checkStatus;
	}

    public String toString(){
        if(metaObject instanceof CSVFieldMeta){
            return ((CSVFieldMeta)metaObject).getSegmentName() + "." + ((CSVFieldMeta)metaObject).getName();
        }if(metaObject instanceof ParameterMeta){
            return ((ParameterMeta)metaObject).getFunctionMeta().getFunctionName() +"."+ ((ParameterMeta)metaObject).getParameterName();
        }else{
            return "unknown";
        }
    }
    public static String getCastorComponentID(C_component cc)
    {
    	StringBuffer rtnSb=new StringBuffer(cc.getType());
    	if(!cc.getType().equalsIgnoreCase("function"))
    	{
    		rtnSb.append("."+cc.getKind());

    		if (cc.getName()!=null&&!cc.getName().equals(""))
    			rtnSb.append("."+cc.getName());
    		if (cc.getGroup()!=null&&!cc.getGroup().equals(""))
    			rtnSb.append("."+cc.getGroup());
    	}
   		rtnSb.append("."+cc.getId());
    	return rtnSb.toString();
    }
}
