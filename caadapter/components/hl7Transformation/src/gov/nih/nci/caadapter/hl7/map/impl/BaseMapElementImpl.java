/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/hl7Transformation/src/gov/nih/nci/caadapter/hl7/map/impl/BaseMapElementImpl.java,v 1.1 2007-07-03 18:26:44 wangeug Exp $
 *
 * ******************************************************************
 * COPYRIGHT NOTICE
 * ******************************************************************
 *
 * The caAdapter Software License, Version 1.3
 * Copyright Notice.
 * 
 * Copyright 2006 SAIC. This software was developed in conjunction with the National Cancer Institute. To the extent government employees are co-authors, any rights in such works are subject to Title 17 of the United States Code, section 105. 
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the Copyright Notice above, this list of conditions, and the disclaimer of Article 3, below. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 
 * 
 * 2. The end-user documentation included with the redistribution, if any, must include the following acknowledgment:
 * 
 * 
 * "This product includes software developed by the SAIC and the National Cancer Institute."
 * 
 * 
 * If no such end-user documentation is to be included, this acknowledgment shall appear in the software itself, wherever such third-party acknowledgments normally appear. 
 * 
 * 3. The names "The National Cancer Institute", "NCI" and "SAIC" must not be used to endorse or promote products derived from this software. 
 * 
 * 4. This license does not authorize the incorporation of this software into any third party proprietary programs. This license does not authorize the recipient to use any trademarks owned by either NCI or SAIC-Frederick. 
 * 
 * 5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO EVENT SHALL THE UNITED STATES GOVERNMENT, THE NATIONAL CANCER INSTITUTE, SAIC, OR THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * <!-- LICENSE_TEXT_END -->
 */


package gov.nih.nci.caadapter.hl7.map.impl;

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
 * @author LAST UPDATE $Author: wangeug $
 * @since     caAdapter v1.2
 * @version    $Revision: 1.1 $
 */
public class BaseMapElementImpl implements BaseMapElement{
    private static final String LOGID = "$RCSfile: BaseMapElementImpl.java,v $";
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/hl7Transformation/src/gov/nih/nci/caadapter/hl7/map/impl/BaseMapElementImpl.java,v 1.1 2007-07-03 18:26:44 wangeug Exp $";

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
    public String getDatauuid() {
    	if(metaObject!=null)
    	{
   			return metaObject.getUUID();
    	}
    	else
    		return getMappedObjectXmlPath();
    }

    public String getMappedObjectXmlPath() {
		return mappedObjectXmlPath;
	}

	public void setMappedObjectXmlPath(String datatypeBaseObjectXmlPath) {
		mappedObjectXmlPath = datatypeBaseObjectXmlPath;
	}

	public String getComponentuuid() {
        return component.getUUID();
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

}
