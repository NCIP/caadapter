/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cdms.formula.gui.view;

import java.awt.Rectangle;

import gov.nih.nci.cbiit.cdms.formula.core.TermType;

import javax.swing.JLabel;



public class TermUiComponent extends JLabel
{
	private TermView viewMeta;

	public TermUiComponent (String text)
	{
		super(text);
		inintUI(null);
	}
	public TermUiComponent (TermView meta)
	{
		super();
		inintUI(meta);
	}
	
	private void inintUI ( TermView views)
	{
		String text="";
		viewMeta=views;
		if (views!=null)
		{
			if (views.getTerm().getType().equals(TermType.UNKNOWN))
				text=views.getTerm().getName();
			else
				text=views.getTerm().getValue();
			if (views.getTerm().getUnit()!=null&&views.getTerm().getUnit().trim().length()>0)
				text=text+"("+views.getTerm().getUnit()+")";
			this.setText(text);
		}
		setHorizontalAlignment(JLabel.CENTER);
		int txtLength=0;
		if (this.getText()!=null)
			txtLength=this.getText().length();
		setBounds(new Rectangle(txtLength*TermView.VIEW_CHARACTER_WEIDTH+TermView.VIEW_COMPONENT_PADDING, TermView.VIEW_COMPONENT_HEIGHT));
	}

	public TermView getViewMeta() {
		return viewMeta;
	}
	
	public void setViewMeta(TermView view) {
			viewMeta = view;
	}
}
