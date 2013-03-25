/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */


package gov.nih.nci.caadapter.common.map;


import java.awt.*;

/**
 * Interface for graphical view information.
 *
 * @author OWNER: Matthew Giordano
 * @author LAST UPDATE $Author: phadkes $
 * @since     caAdapter v1.2
 * @version    $Revision: 1.4 $
 * @date        $Date: 2008-09-25 19:15:46 $
 */

public interface View {
    public boolean isVisible();
    public int getX();
    public int getY();
    public int getHeight();
    public int getWidth();
    public Color getColor();
	public String getComponentId() ;
	public void setComponentId(String componentId);
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
*/
