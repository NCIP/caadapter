/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */
 
package gov.nih.nci.caadapter.common.map;


//import gov.nih.nci.caadapter.hl7.map.View;

import java.awt.*;

/**
 * Implementation for graphical view information.
 *
 * @author OWNER: Matthew Giordano
 * @author LAST UPDATE $Author: phadkes $
 * @since     caAdapter v1.2
 * @version    $Revision: 1.3 $
 * @date        $Date: 2008-06-06 18:54:28 $
 */
public class ViewImpl implements View{
    private static final String LOGID = "$RCSfile: ViewImpl.java,v $";
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/map/ViewImpl.java,v 1.3 2008-06-06 18:54:28 phadkes Exp $";

	private boolean visible;
	private int x;
	private int y;
	private int height;
	private int width;
	private Color color;
	private String componentId;
	private static int idCount=0;
	private ViewImpl()
	{
		super();
		this.setComponentId(idCount+"");
		idCount++;
		
	}
	public static ViewImpl getViewImpl(){
		return new  ViewImpl();
	}

    public static ViewImpl getViewImpl(boolean visible, int x, int y, int height, int width, Color color) {
    	ViewImpl rtnView=new ViewImpl();
    	rtnView.setVisible(visible);
    	rtnView.setX(x);
    	rtnView.setY(y);
    	rtnView.setHeight(height);
    	rtnView.setWidth(width);
    	rtnView.setColor(color);
    	return rtnView;
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
	public String getComponentId() {
		return componentId;
	}
	public void setComponentId(String componentId) {
		this.componentId = componentId;
	}
}
