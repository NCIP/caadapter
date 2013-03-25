/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.hl7.map.impl;

import gov.nih.nci.caadapter.common.map.BaseComponent;
import gov.nih.nci.caadapter.hl7.map.FunctionComponent;
import gov.nih.nci.caadapter.hl7.map.Map;
import gov.nih.nci.caadapter.hl7.map.Mapping;

import java.util.ArrayList;
import java.util.List;

/**
 * The primary class which contains all Mapping information.
 *
 * @author OWNER: Matthew Giordano
 * @author LAST UPDATE $Author: wangeug $
 * @version $Revision: 1.7 $
 * @date $Date: 2008-11-21 16:17:33 $
 * @since caAdapter v1.2
 */

public class MappingImpl implements Mapping
{
	private static final String LOGID = "$RCSfile: MappingImpl.java,v $";
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/hl7Transformation/src/gov/nih/nci/caadapter/hl7/map/impl/MappingImpl.java,v 1.7 2008-11-21 16:17:33 wangeug Exp $";
	private String mappingType="";
	private List<Map> maps = new ArrayList<Map>();
	private List<FunctionComponent> functionComponents = new ArrayList<FunctionComponent>();
	private BaseComponent sourceComponent = null;
	private BaseComponent targetComponent = null;

	/**
	 * Constructors.
	 */
	public MappingImpl()
	{
	}

	public MappingImpl(List<Map> maps, List<FunctionComponent> functionComponents,
					   BaseComponent sourceComponent, BaseComponent targetComponent)
	{
		this.maps = maps;
		this.functionComponents = functionComponents;
		this.sourceComponent = sourceComponent;
		this.targetComponent = targetComponent;
	}

	/**
	 * Setters and Getters.
	 */
	public List<Map> getMaps()
	{
		return maps;
	}

	public void setMaps(List<Map> maps)
	{
		this.maps = maps;
	}

	/**
	 * Return a list of function component in this mapping.
	 * This function never return null. If nothing exists, will return an empty list.
	 * @return
	 */
	public List<FunctionComponent> getFunctionComponent()
	{
		if(functionComponents==null)
		{
			functionComponents = new ArrayList<FunctionComponent>();
		}
		return functionComponents;
	}

	public void setFunctionComponent(List<FunctionComponent> functionComponents)
	{
		this.functionComponents = functionComponents;
		if (this.functionComponents == null)
		{
			this.functionComponents = new ArrayList<FunctionComponent>();
		}
	}

	public BaseComponent getSourceComponent()
	{
		return sourceComponent;
	}

	public void setSourceComponent(BaseComponent sourceComponent)
	{
		this.sourceComponent = sourceComponent;
	}

	public BaseComponent getTargetComponent()
	{
		return targetComponent;
	}

	public void setTargetComponent(BaseComponent targetComponent)
	{
		this.targetComponent = targetComponent;
	}

	/**
	 * Convenience Methods.
	 */
	public void addMap(Map m)
	{
		this.maps.add(m);
	}

	public void removeMap(Map m)
	{
		this.maps.remove(m);
	}

	public void removeAllMaps()
	{
		this.maps.clear();
	}

	public void addFunctionComponent(FunctionComponent f)
	{
		this.functionComponents.add(f);
	}

	public void removeFunctionComponent(FunctionComponent f)
	{
		this.functionComponents.remove(f);
	}

	public void removeAllFunctionComponents()
	{
		this.functionComponents.clear();
	}

    public FunctionComponent getFunctionComponent(String uuid){
        List<FunctionComponent> fc = getFunctionComponent();
        for (int i = 0; i < fc.size(); i++) {
            FunctionComponent functionComponent =  fc.get(i);
            if(functionComponent.getXmlPath().equalsIgnoreCase(uuid)){
                return functionComponent;
            }
        }
        return null;
    }

	public String getMappingType() {
		// TODO Auto-generated method stub
		return mappingType;
	}

	public void setMappingType(String newType) {
		// TODO Auto-generated method stub
		mappingType=newType;
	}
}
/**
 * HISTORY :$Log: not supported by cvs2svn $
 * HISTORY :Revision 1.6  2008/11/17 20:08:52  wangeug
 * HISTORY :Move FunctionComponent and VocabularyMap from HL7 module to common module
 * HISTORY :
 * HISTORY :Revision 1.5  2008/09/29 15:45:56  wangeug
 * HISTORY :enforce code standard: license file, file description, changing history
 * HISTORY :
 */