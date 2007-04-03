/*
 *  $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/jgraph/actions/VacabularyMappingEditAction.java,v 1.1 2007-04-03 16:17:57 wangeug Exp $
 *
 * ******************************************************************
 * COPYRIGHT NOTICE
 * ******************************************************************
 *
 *	The HL7 SDK Software License, Version 1.0
 *
 *	Copyright 2001 SAIC. This software was developed in conjunction with the National Cancer
 *	Institute, and so to the extent government employees are co-authors, any rights in such works
 *	shall be subject to Title 17 of the United States Code, section 105.
 *
 *	Redistribution and use in source and binary forms, with or without modification, are permitted
 *	provided that the following conditions are met:
 *
 *	1. Redistributions of source code must retain the above copyright notice, this list of conditions
 *	and the disclaimer of Article 3, below.  Redistributions in binary form must reproduce the above
 *	copyright notice, this list of conditions and the following disclaimer in the documentation and/or
 *	other materials provided with the distribution.
 *
 *	2.  The end-user documentation included with the redistribution, if any, must include the
 *	following acknowledgment:
 *
 *	"This product includes software developed by the SAIC and the National Cancer
 *	Institute."
 *
 *	If no such end-user documentation is to be included, this acknowledgment shall appear in the
 *	software itself, wherever such third-party acknowledgments normally appear.
 *
 *	3. The names "The National Cancer Institute", "NCI" and "SAIC" must not be used to endorse or
 *	promote products derived from this software.
 *
 *	4. This license does not authorize the incorporation of this software into any proprietary
 *	programs.  This license does not authorize the recipient to use any trademarks owned by either
 *	NCI or SAIC-Frederick.
 *
 *	5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED
 *	WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 *	MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE
 *	DISCLAIMED.  IN NO EVENT SHALL THE NATIONAL CANCER INSTITUTE, SAIC, OR
 *	THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *	EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *	PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *	PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
 *	OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *	NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *	SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *
 * ******************************************************************
 */

package gov.nih.nci.caadapter.ui.mapping.jgraph.actions;

import gov.nih.nci.caadapter.common.function.FunctionException;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.util.FileUtil;
import gov.nih.nci.caadapter.hl7.map.FunctionVocabularyMapping;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.functions.FunctionBoxCell;
import gov.nih.nci.caadapter.ui.common.functions.FunctionBoxMutableViewInterface;
import gov.nih.nci.caadapter.ui.common.functions.FunctionVocabularyMappingDefinitionDialog;
import gov.nih.nci.caadapter.ui.mapping.MappingMiddlePanel;
import gov.nih.nci.caadapter.ui.mapping.jgraph.MiddlePanelJGraphController;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.*;
import org.jgraph.JGraph;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: wangeug $
 * @version Since HL7 SDK v1.2
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2007-04-03 16:17:57 $
 */
public class VacabularyMappingEditAction extends DefaultAbstractJgraphAction
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: VacabularyMappingEditAction.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/jgraph/actions/VacabularyMappingEditAction.java,v 1.1 2007-04-03 16:17:57 wangeug Exp $";

	private static final String COMMAND_NAME = "Edit Vocabulary Mapping...";
	private static final Character COMMAND_MNEMONIC = new Character('V');
//	private static final KeyStroke ACCELERATOR_KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, false);

	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public VacabularyMappingEditAction(MappingMiddlePanel middlePanel, MiddlePanelJGraphController controller)
	{
		super(COMMAND_NAME, middlePanel, controller);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a default icon.
	 */
	public VacabularyMappingEditAction(String name, MappingMiddlePanel middlePanel, MiddlePanelJGraphController controller)
	{
		this(name, null, middlePanel, controller);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a the specified icon.
	 */
	public VacabularyMappingEditAction(String name, Icon icon, MappingMiddlePanel middlePanel, MiddlePanelJGraphController controller)
	{
		super(name, icon, middlePanel, controller);
		setMnemonic(COMMAND_MNEMONIC);
		//		setAcceleratorKey(ACCELERATOR_KEY_STROKE);
		setActionCommandType(DOCUMENT_ACTION_TYPE);
	}

	/**
	 * The abstract function that descendant classes must be overridden to provide customsized handling.
	 *
	 * @param e
	 * @return true if the action is finished successfully; otherwise, return false.
	 */
	protected boolean doAction(ActionEvent e)
	{
		JGraph graph = getController().getGraph();
		FunctionVocabularyMapping vocabularyMapping = null;
		if (!graph.isSelectionEmpty())
		{//Find the constant metadata information to edit
			Object selectedObj = graph.getSelectionCell();
			if (selectedObj instanceof FunctionBoxCell)
			{
				Object userObject = ((FunctionBoxCell) selectedObj).getUserObject();
				if (userObject instanceof FunctionBoxMutableViewInterface)
				{
					vocabularyMapping = ((FunctionBoxMutableViewInterface) userObject).getFunctionVocabularyMapping();
				}
			}
		}

		if (vocabularyMapping == null)
		{//no constant is actually selected.
			setSuccessfullyPerformed(false);
			return false;
		}
        String mapSource = vocabularyMapping.getValue();

        String type = vocabularyMapping.getType();
        String msg = "";
        int answer = 0;

        if ((type.equals(vocabularyMapping.getTypeNamePossibleList()[0]))||  // Local_FileName
            (type.equals(vocabularyMapping.getTypeNamePossibleList()[2])))   // File_URL
        {
            String fileName = "";
            String domain = "";
            if (mapSource.startsWith(Config.CAADAPTER_HOME_DIR_TAG)) mapSource = mapSource.replace(Config.CAADAPTER_HOME_DIR_TAG, FileUtil.getWorkingDirPath());
            if (mapSource.indexOf(Config.VOCABULARY_MAP_FILE_NAME_DOMAIN_SEPARATOR) > 0)
            {
                int idx = mapSource.indexOf(Config.VOCABULARY_MAP_FILE_NAME_DOMAIN_SEPARATOR);
                fileName = mapSource.substring(0, idx);
                domain = mapSource.substring(idx + Config.VOCABULARY_MAP_FILE_NAME_DOMAIN_SEPARATOR.length());
                answer = JOptionPane.showConfirmDialog(getAssociatedUIComponent(),
                                                       "Source File Name is '" + fileName + "' and domain is '" + domain + "'\n Do you want to change this file or domain?",
                                                       "Vocabulary Mapping Source File or domain Change", JOptionPane.OK_CANCEL_OPTION);
            }
            else
            {
                fileName = mapSource;
                answer = JOptionPane.showConfirmDialog(getAssociatedUIComponent(),
                                       "Source File Name is '" + fileName + "' with no domain \n Do you want to change this file or domain?",
                                       "Vocabulary Mapping Source File or domain Change", JOptionPane.OK_CANCEL_OPTION);
            }
        }
        else if (type.equals(vocabularyMapping.getTypeNamePossibleList()[1])) // Service_URL
        {
            answer = JOptionPane.showConfirmDialog(getAssociatedUIComponent(),
                                       "Source URL is " + mapSource + "\n Do you want to change this URL?",
                                       "Vocabulary Mapping Source URL Change", JOptionPane.OK_CANCEL_OPTION);
        }
        if (answer != JOptionPane.OK_OPTION)
        {
			setSuccessfullyPerformed(false);
			return false;
		}

        FunctionVocabularyMappingDefinitionDialog dialog = new FunctionVocabularyMappingDefinitionDialog(new JFrame(), vocabularyMapping.getInverseTag());
		dialog.setTitle("Edit Vocabulary Mapping...");
		if (!dialog.setMappingTypeClass(vocabularyMapping.getType()))
        {
            JOptionPane.showMessageDialog(getAssociatedUIComponent(), "Invalid Mapping Type Class : " + vocabularyMapping.getType(), "FunctionException (29)", JOptionPane.WARNING_MESSAGE);
            setSuccessfullyPerformed(false);
			return false;
        }
        dialog.setMappingValue(vocabularyMapping.getValue());
		DefaultSettings.centerWindow(dialog);
		dialog.setVisible(true);
		if (dialog.isOkButtonClicked())
		{
			String typeValue = dialog.getMappingTypeClass();
            try
            {
                vocabularyMapping.setType(typeValue);
			    vocabularyMapping.setValue(dialog.getMappingValue());
            }
            catch(FunctionException fe)
            {
                JOptionPane.showMessageDialog(getAssociatedUIComponent(), fe.getMessage(), "FunctionException (29)", JOptionPane.WARNING_MESSAGE);
                setSuccessfullyPerformed(false);
			    return false;
            }
        }
		setSuccessfullyPerformed(true);
		return isSuccessfullyPerformed();
	}

	/**
	 * Return the associated UI component.
	 *
	 * @return the associated UI component.
	 */
	protected Component getAssociatedUIComponent()
	{
		return getMiddlePanel();
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.8  2006/11/15 05:48:17  umkis
 * HISTORY      : Fixing Bugs item #3420
 * HISTORY      :
 * HISTORY      : Revision 1.7  2006/11/02 18:38:05  umkis
 * HISTORY      : XML format vom file must be validated before recorded into a map file with the xml schema file which is directed by Config.VOCABULARY_MAP_XML_FILE_DEFINITION_FILE_LOCATION.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2006/11/01 02:06:39  umkis
 * HISTORY      : Extending function of vocabulary mapping : URL XML vom file can use.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2006/10/11 18:37:13  umkis
 * HISTORY      : protect inputting 'URL' type when inverse mapping.
 * HISTORY      :
 * HISTORY      : Revision 1.4  2006/10/02 18:05:08  umkis
 * HISTORY      : Vocabulary mapping function upgrade which allow to mapping through a URL and domained .vom file.
 * HISTORY      :
 * HISTORY      : Revision 1.3  2006/09/06 22:14:17  umkis
 * HISTORY      : Before Editing, show the current source file name and ask a question whether the user wants to change to another
 * HISTORY      :
 * HISTORY      : Revision 1.2  2006/09/06 18:58:50  umkis
 * HISTORY      : remove ".vom" into Config.VOCABULARY_MAPPING_FILE_EXTENSION
 * HISTORY      :
 * HISTORY      : Revision 1.1  2006/09/06 18:23:38  umkis
 * HISTORY      : The new implement of Vocabulary Mapping function.
 * HISTORY      :
 */
