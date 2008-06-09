/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */

package gov.nih.nci.caadapter.mms.util;

import java.util.Properties;

/**
 * @author zengje
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DatatypeCompatablityProperties {

	private static DatatypeCompatablityProperties instance;

	private Properties datatypeCompatabilityProp;
	/**
	 *
	 */
	private DatatypeCompatablityProperties() throws Exception {

		datatypeCompatabilityProp=new Properties();
		datatypeCompatabilityProp.load(this.getClass().getClassLoader().getResourceAsStream("datatypecompatability.properties"));
	}

	public synchronized static DatatypeCompatablityProperties getInstance() throws Exception
	{
		if (instance == null)
		{
			instance = new DatatypeCompatablityProperties();
		}
		return instance;
	}

	public Properties getProperties()
	{
		return datatypeCompatabilityProp;
	}

}
