package gov.nih.nci.caadapter.common.map;


import java.awt.*;

/**
 * Interface for graphical view information.
 *
 * @author OWNER: Matthew Giordano
 * @author LAST UPDATE $Author: wangeug $
 * @since     caAdapter v1.2
 * @version    $Revision: 1.1 $
 * @date        $Date: 2007-05-24 15:03:30 $
 */

public interface View {
    public boolean isVisible();
    public int getX();
    public int getY();
    public int getHeight();
    public int getWidth();
    public Color getColor();
}
