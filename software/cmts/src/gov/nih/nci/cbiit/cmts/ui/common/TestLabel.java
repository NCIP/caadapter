/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */
package gov.nih.nci.cbiit.cmts.ui.common;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.Icon;
import javax.swing.JLabel;

/**
 * A Testing Label component for testing the layout capability
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMPS v1.0
 * @version    $Revision: 1.1 $
 * @date       $Date: 2008-12-03 20:46:14 $
 *
 */
public class TestLabel extends JLabel {

	String initialText = "<html>\n" +
	"Color and font test:\n" +
	"<ul>\n" +
	"<li><font color=red>red</font>\n" +
	"<li><font color=blue>blue</font>\n" +
	"<li><font color=green>green</font>\n" +
	"<li><font size=-2>small</font>\n" +
	"<li><font size=+2>large</font>\n" +
	"<li><i>italic</i>\n" +
	"<li><b>bold</b>\n" +
	"</ul>\n";

	/**
	 * 
	 */
	public TestLabel() {
		this.setText(initialText);
		this.addSizeShower();
	}

	/**
	 * @param text
	 */
	public TestLabel(String text) {
		super(text);
		this.addSizeShower();
	}

	/**
	 * @param image
	 */
	public TestLabel(Icon image) {
		super(image);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param text
	 * @param horizontalAlignment
	 */
	public TestLabel(String text, int horizontalAlignment) {
		super(text, horizontalAlignment);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param image
	 * @param horizontalAlignment
	 */
	public TestLabel(Icon image, int horizontalAlignment) {
		super(image, horizontalAlignment);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param text
	 * @param icon
	 * @param horizontalAlignment
	 */
	public TestLabel(String text, Icon icon, int horizontalAlignment) {
		super(text, icon, horizontalAlignment);
		// TODO Auto-generated constructor stub
	}

	public void addSizeShower(){
		this.addComponentListener(new ComponentListener(){

			public void componentHidden(ComponentEvent e) {
				// TODO Auto-generated method stub
				
			}

			public void componentMoved(ComponentEvent e) {
				// TODO Auto-generated method stub
				
			}

			public void componentResized(ComponentEvent e) {
				// TODO Auto-generated method stub
				System.out.println("componentResized:"+e.paramString());
				setText(initialText+"<hr>"+e.paramString()+
						"<hr>"+getBounds());
			}

			public void componentShown(ComponentEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
	
}

/**
 * HISTORY: $Log: not supported by cvs2svn $
 */

