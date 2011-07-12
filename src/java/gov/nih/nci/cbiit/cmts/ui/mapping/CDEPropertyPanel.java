package gov.nih.nci.cbiit.cmts.ui.mapping;

import gov.nih.nci.cbiit.cmts.core.AttributeMeta;
import gov.nih.nci.cbiit.cmts.core.ElementMeta;
import gov.nih.nci.cbiit.cmts.util.CdeBrowserLauncher;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class CDEPropertyPanel extends JPanel implements ActionListener {

	private ElementMeta meta;
	private JLabel label1;
	private JLabel label2;
	private JLabel metaPublicId;
	private JLabel metaVersion;
	private JButton cdeElementLink;
	
	public CDEPropertyPanel ()
	{
		super(new GridBagLayout());
		initUI();
	}
	
	private void initUI()
	{
		Insets insets = new Insets(5, 5, 5, 5);
		label1=new JLabel("Public ID");
		metaPublicId=new JLabel("");
		add(label1,new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0) );

		add(metaPublicId, new GridBagConstraints(1, 0, 2, 1, 1.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		
		label2=new JLabel("Version");
		metaVersion=new JLabel("");
		add(label2,new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0) );

		add(metaVersion, new GridBagConstraints(1, 1, 2, 1, 1.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		cdeElementLink=new JButton("<html><a href=\"https://cdebrowser.nci.nih.gov/CDEBrowser/\">caDSR Element Details</a></html>");
		cdeElementLink.addActionListener(this);
		cdeElementLink.setEnabled(false);
		cdeElementLink.setBorder(null);
		cdeElementLink.setHorizontalTextPosition(AbstractButton.LEADING);//.LEADING); 
		cdeElementLink.setVisible(false);
		label1.setVisible(false);
		label2.setVisible(false);
//		add(cdeLinkLabel,new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
//				GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0) );

		add(cdeElementLink, new GridBagConstraints(0, 2, 3, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
 	}
	public ElementMeta getMeta() {
		return meta;
	}

	public void setMeta(ElementMeta meta) {
		if (meta==null)
			return;
		cdeElementLink.setVisible(true);
		label1.setVisible(true);
		label2.setVisible(true);
		cdeElementLink.setEnabled(true);
		for (AttributeMeta attrMeta:meta.getAttrData())
		{
			if (attrMeta.getName().equals("PUBLICID"))
				metaPublicId.setText(attrMeta.getFixedValue());
			else if (attrMeta.getName().equals("VERSION"))
				metaVersion.setText(attrMeta.getFixedValue());
			this.repaint();
		}
	}

	public void updateSelection(Object selectedItem)
	{
		metaPublicId.setText("");
		metaVersion.setText(null);
		cdeElementLink.setVisible(false);
		label1.setVisible(false);
		label2.setVisible(false);
		cdeElementLink.setEnabled(false);
		if (selectedItem==null)
			return;
		
		if(selectedItem instanceof ElementMeta)
			setMeta((ElementMeta)selectedItem);
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		CdeBrowserLauncher.BrowseCDE(metaPublicId.getText(), metaVersion.getText());
	}

	
}
