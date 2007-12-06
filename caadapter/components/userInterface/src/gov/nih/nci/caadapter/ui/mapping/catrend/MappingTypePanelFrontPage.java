/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/catrend/MappingTypePanelFrontPage.java,v 1.1 2007-12-06 20:40:35 wangeug Exp $
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


package gov.nih.nci.caadapter.ui.mapping.catrend;

import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.util.GeneralUtilities;
import gov.nih.nci.caadapter.ui.specification.csv.actions.BrowseCsvAction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * This class defines the first page for NewCSVPanelWizard.
 *
 * @author OWNER: wangeug
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2007-12-06 20:40:35 $
 */
public class MappingTypePanelFrontPage extends JPanel implements ActionListener
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: MappingTypePanelFrontPage.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/catrend/MappingTypePanelFrontPage.java,v 1.1 2007-12-06 20:40:35 wangeug Exp $";

	public static final int OBJECT_SELECTED = 1;
	public static final int DATA_SELECTED = 2;
	
	private static final String DATA_MODEL = "Data Model (XMI)";
	private static final String OBJECT_MODEL = "Object Model (XMI)";
	private int selectionType=1;

	private JRadioButton objectButton;
	private JRadioButton dataButton;

	public MappingTypePanelFrontPage()
	{
		initialize();
	}
		private void initialize()
	{
		this.setLayout(new BorderLayout());
		JPanel northPanel = new JPanel(new BorderLayout());
		JTextArea noteArea = new JTextArea("Select mapping target");
		noteArea.setEditable(false);
		noteArea.setBackground(northPanel.getBackground());
		northPanel.add(noteArea, BorderLayout.CENTER);
		this.add(northPanel, BorderLayout.NORTH);

		JPanel centerPanel = new JPanel(new GridLayout(2, 1));
		this.setBorder(BorderFactory.createTitledBorder(this.getBorder(), "" /*ActionConstants.NEW_CSV_SPEC*/));

		objectButton = new JRadioButton(OBJECT_MODEL);
		objectButton.setMnemonic('B');
		objectButton.addActionListener(this);
		centerPanel.add(objectButton);

		dataButton = new JRadioButton(DATA_MODEL);
		dataButton.setMnemonic('G');
		dataButton.addActionListener(this);
		centerPanel.add(dataButton);

		this.add(centerPanel, BorderLayout.CENTER);
 
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(objectButton);
		buttonGroup.add(dataButton);


		//default selection
		objectButton.setSelected(true);
	}



	/**
	 * Invoked when an action occurs.
	 */
	public void actionPerformed(ActionEvent e)
	{
		//every time clear it unless being set by explicit BLANK_COMMAND

		String command = e.getActionCommand();
		if (OBJECT_MODEL.equals(command))
		{//do nothing
			setSelectionType(OBJECT_SELECTED);

		}
		else if (DATA_MODEL.equals(command))
		{
			setSelectionType(DATA_SELECTED);
		}
		else
		{
			System.err.println("Why do I come here? Command: '" + command + "'.");
		}
	}
	public int getSelectionType() {
		return selectionType;
	}
	public void setSelectionType(int selectionType) {
		this.selectionType = selectionType;
	}



}
