package gov.nih.nci.caadapter.common.map;


import java.awt.*;

/**
 * Interface for graphical view information.
 *
 * @author OWNER: Matthew Giordano
 * @author LAST UPDATE $Author: wangeug $
 * @since     caAdapter v1.2
 * @version    $Revision: 1.2 $
 * @date        $Date: 2007-07-18 20:36:17 $
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
