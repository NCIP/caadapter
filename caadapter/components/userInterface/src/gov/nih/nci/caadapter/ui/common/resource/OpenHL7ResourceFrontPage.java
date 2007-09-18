/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/resource/OpenHL7ResourceFrontPage.java,v 1.1 2007-09-18 15:25:11 wangeug Exp $
 *
 * ******************************************************************
 * COPYRIGHT NOTICE
 * ******************************************************************
 *
 * The caAdapter Software License, Version 1.3
 * Copyright Notice.
 * 
 * Copyright 2006 SAIC. This software was developed in conjunction with the National Cancer Institute. To the extent government employees are co-authors, any rights in such works are subject to Title 17 of the United States Code, section 105. 
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the Copyright Notice above, this list of conditions, and the disclaimer of Article 3, below. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 
 * 
 * 2. The end-user documentation included with the redistribution, if any, must include the following acknowledgment:
 * 
 * 
 * "This product includes software developed by the SAIC and the National Cancer Institute."
 * 
 * 
 * If no such end-user documentation is to be included, this acknowledgment shall appear in the software itself, wherever such third-party acknowledgments normally appear. 
 * 
 * 3. The names "The National Cancer Institute", "NCI" and "SAIC" must not be used to endorse or promote products derived from this software. 
 * 
 * 4. This license does not authorize the incorporation of this software into any third party proprietary programs. This license does not authorize the recipient to use any trademarks owned by either NCI or SAIC-Frederick. 
 * 
 * 5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO EVENT SHALL THE UNITED STATES GOVERNMENT, THE NATIONAL CANCER INSTITUTE, SAIC, OR THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * <!-- LICENSE_TEXT_END -->
 */


package gov.nih.nci.caadapter.ui.common.resource;

import javax.swing.*;

import java.awt.*;
import java.io.File;

/**
 * The front page of open HL7 Message panel.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2007-09-18 15:25:11 $
 */
public class OpenHL7ResourceFrontPage extends JPanel
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: OpenHL7ResourceFrontPage.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/resource/OpenHL7ResourceFrontPage.java,v 1.1 2007-09-18 15:25:11 wangeug Exp $";

	public static final String HL7_RESOURCE_SITE = "Target Location:";
	private JTextField dataFileInputField;
	private String openWizardType;
	private String targetSite;
	/**
	 * Creates a new <code>JPanel</code> with a double buffer
	 * and a flow layout.
	 */
	public OpenHL7ResourceFrontPage(JFrame frame,String title, String resourceSite)
	{
		openWizardType=title;
		targetSite=resourceSite;
		initialize();
	}

	private void initialize()
	{
		this.setLayout(new BorderLayout());//new FlowLayout(FlowLayout.LEADING));
		JPanel centerPanel = new JPanel(new GridBagLayout());
		Insets insets = new Insets(5, 5, 5, 5);
		String resourcHome="";
		if (openWizardType.equals(BuildHL7ResourceAction.COMMAND_BUILD_V3))
			resourcHome="HL7 Normative Home:";
		else
			resourcHome="HL7 V2 Resource Home:";
			
		JLabel dataFileLabel = new JLabel(resourcHome);
		centerPanel.add(dataFileLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
		dataFileInputField = new JTextField();
		dataFileInputField.setPreferredSize(new Dimension(350, 25));
		centerPanel.add(dataFileInputField, new GridBagConstraints(1, 0, 2, 1, 1.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		JButton dataFileBrowseButton =new JButton(new BrowseHL7ResourceAction(this, "Browser"));

		centerPanel.add(dataFileBrowseButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.NONE, insets, 0, 0));

		JLabel mapFileLabel = new JLabel(HL7_RESOURCE_SITE);
		centerPanel.add(mapFileLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
		JLabel targetSit = new JLabel(targetSite);
		targetSit.setPreferredSize(new Dimension(350, 25));
		centerPanel.add(targetSit, new GridBagConstraints(1, 1, 2, 1, 1.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		this.add(centerPanel, BorderLayout.CENTER);
	}

	public void setUserSelectionFile(File file, String browseMode)
	{
//		System.out.println("OpenHL7ResourceFrontPage.setUserSelectionFile()..selected file:"+file);
		dataFileInputField.setText(file.getAbsolutePath());
	}
	
	public String getSelectFileHome() {
		return dataFileInputField.getText();
	}
 

	public String getTargetSite() {
		return targetSite;
	}
}


