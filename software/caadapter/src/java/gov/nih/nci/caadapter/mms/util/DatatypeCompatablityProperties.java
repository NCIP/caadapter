/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.mms.util;

import java.util.Properties;

/**
 * Class for data type compatibility properties
 *
 * @author OWNER: zengje
 * @author LAST UPDATE $Author: linc $
 * @since     caAdatper v4.0
 * @version    $Revision: 1.4 $
 * @date       $Date: 2008-09-26 20:35:27 $ *
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
/**
 * HISTORY: $Log: not supported by cvs2svn $
 */
