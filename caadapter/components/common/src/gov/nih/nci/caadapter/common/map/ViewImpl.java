package gov.nih.nci.caadapter.common.map;


//import gov.nih.nci.caadapter.hl7.map.View;

import java.awt.*;

/**
 * Implementation for graphical view information.
 *
 * @author OWNER: Matthew Giordano
 * @author LAST UPDATE $Author: wangeug $
 * @since     caAdapter v1.2
 * @version    $Revision: 1.1 $
 * @date        $Date: 2007-05-24 15:03:30 $
 */
public class ViewImpl implements View{
    private static final String LOGID = "$RCSfile: ViewImpl.java,v $";
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/map/ViewImpl.java,v 1.1 2007-05-24 15:03:30 wangeug Exp $";

	private boolean visible;
	private int x;
	private int y;
	private int height;
	private int width;
	private Color color;

	public ViewImpl(){
	}

    public ViewImpl(boolean visible, int x, int y, int height, int width, Color color) {
        this.visible = visible;
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
        this.color = color;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
