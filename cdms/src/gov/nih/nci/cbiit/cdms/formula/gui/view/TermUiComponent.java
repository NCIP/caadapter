package gov.nih.nci.cbiit.cdms.formula.gui.view;

import gov.nih.nci.cbiit.cdms.formula.core.TermMeta;

import javax.swing.JLabel;



public class TermUiComponent extends JLabel {
	private TermMeta viewMeta;
	public TermUiComponent (String text, TermMeta meta)
	{
		this(text, JLabel.CENTER, meta);
	}
	public TermUiComponent (String text, int horizontalAlignment, TermMeta meta)
	{
		super(text, horizontalAlignment);
		viewMeta=meta;
	}

	public TermMeta getViewMeta() {
		return viewMeta;
	}
}
