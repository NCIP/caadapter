/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cdms.formula.test;

import gov.nih.nci.cbiit.cdms.formula.gui.view.TermUiComponent;

import javax.swing.JLabel;
import javax.xml.bind.JAXBException;

import org.junit.Test;

public class GuiTest {

	/**
	 * test loading formula store
	 * @throws JAXBException 
	 */
	@Test
	public void testLabelSize()
	{
		JLabel stLabel=new TermUiComponent("test");
		double dValue=Double.valueOf("1.2")/3.5;
		JLabel dLabel=new TermUiComponent(String.valueOf(dValue));
		System.out.println("GuiTest.testLabelSize()..double:"+dValue);
		System.out.println("GuiTest.testLabelSize()..:"+stLabel.getText() +"...size(w,h)"+"..("+stLabel.getSize().getWidth()+", "+stLabel.getSize().getHeight()+")"
					+"..bound(w,h)..("+stLabel.getBounds().getWidth()+", "+stLabel.getBounds().getHeight()+")");
		System.out.println("GuiTest.testLabelSize()"+stLabel.getFont().getSize());
	}
}
