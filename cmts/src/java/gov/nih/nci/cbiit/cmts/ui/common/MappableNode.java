/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */
package gov.nih.nci.cbiit.cmts.ui.common;

/**
 * An interface to indicate whether the subject has participated in mapping or not.
 * The implementation of this interface will help UI render to present different cues
 * to indicate its (mapping) status.
 * Also the implementation class shall define an instance member variable to remember the status.
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMTS v1.0
 * @version    $Revision: 1.1 $
 * @date       $Date: 2008-10-27 20:06:30 $
 *
 */
public interface MappableNode
{
	/**
	 * Set the map status to new value, which might trigger underline property change.
	 * @param newValue
	 */
	void setMapStatus(boolean newValue);

	/**
	 * Answer if this given node is mapped.
	 * @return
	 */
	boolean isMapped();
}
/**
 * HISTORY: $Log: not supported by cvs2svn $
 */

