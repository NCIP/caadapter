package gov.nih.nci.cbiit.cdms.formula.gui.view;

import java.awt.Rectangle;

import gov.nih.nci.cbiit.cdms.formula.core.TermMeta;
import gov.nih.nci.cbiit.cdms.formula.core.TermType;

import javax.swing.JLabel;



public class TermUiComponent extends JLabel {
	private TermMeta viewMeta;

	public TermUiComponent (String text)
	{
		super(text);
		inintUI(null);
	}
	public TermUiComponent (TermMeta meta)
	{
		super();
		inintUI(meta);
	}
	
	private void inintUI ( TermMeta meta)
	{
		String text="";
		viewMeta=meta;
		if (meta!=null)
		{
			if (meta.getType().equals(TermType.UNKNOWN))
				text=meta.getName();
			else
				text=meta.getValue();
			if (meta.getUnit()!=null)
				text=text+"("+meta.getUnit()+")";
			this.setText(text);
		}
		setHorizontalAlignment(JLabel.CENTER);
		setBounds(new Rectangle(text.length()*TermView.VIEW_CHARACTER_WEIDTH+TermView.VIEW_COMPONENT_PADDING, TermView.VIEW_COMPONENT_HEIGHT));
		this.setVerticalTextPosition(JLabel.TOP);
	}

	public TermMeta getViewMeta() {
		return viewMeta;
	}
	
	public void setViewMeta(TermMeta viewMeta) {
		this.viewMeta = viewMeta;
	}
}
