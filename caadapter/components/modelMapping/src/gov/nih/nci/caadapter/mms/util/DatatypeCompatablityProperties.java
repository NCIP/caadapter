/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





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
