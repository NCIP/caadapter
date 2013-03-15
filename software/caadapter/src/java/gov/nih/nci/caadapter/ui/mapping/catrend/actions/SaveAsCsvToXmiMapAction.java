/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.mapping.catrend.actions;

import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.util.GeneralUtilities;
import gov.nih.nci.caadapter.hl7.map.Mapping;
import gov.nih.nci.caadapter.hl7.map.impl.MapBuilderImpl;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.MappableNode;
import gov.nih.nci.caadapter.ui.common.actions.DefaultSaveAsAction;
import gov.nih.nci.caadapter.ui.common.jgraph.MappingDataManager;
import gov.nih.nci.caadapter.ui.common.tree.MappingBaseTree;
import gov.nih.nci.caadapter.ui.mapping.AbstractMappingPanel;
import gov.nih.nci.caadapter.ui.mapping.catrend.CsvToXmiMappingPanel;
import gov.nih.nci.caadapter.ui.mapping.catrend.CsvToXmiMappingReporter;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * This class defines a concrete "Save As" action.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.9 $
 *          date        $Date: 2008-06-09 19:54:06 $
 */
public class SaveAsCsvToXmiMapAction extends DefaultSaveAsAction
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: SaveAsCsvToXmiMapAction.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/catrend/actions/SaveAsCsvToXmiMapAction.java,v 1.9 2008-06-09 19:54:06 phadkes Exp $";

	protected AbstractMappingPanel mappingPanel;

	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public SaveAsCsvToXmiMapAction(AbstractMappingPanel mappingPanel)
	{
		this(COMMAND_NAME, mappingPanel);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a default icon.
	 */
	public SaveAsCsvToXmiMapAction(String name, AbstractMappingPanel mappingPanel)
	{
		this(name, null, mappingPanel);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a the specified icon.
	 */
	public SaveAsCsvToXmiMapAction(String name, Icon icon, AbstractMappingPanel mappingPanel)
	{
		super(name, icon, null);
		this.mappingPanel = mappingPanel;
	}

	protected boolean doAction(ActionEvent e) throws Exception
	{
		if(this.mappingPanel!=null)
		{
			if(!mappingPanel.isSourceTreePopulated() || !mappingPanel.isTargetTreePopulated())
			{
				String msg = "Enter both source and target information before saving the map specification.";
				JOptionPane.showMessageDialog(mappingPanel, msg, "Error", JOptionPane.ERROR_MESSAGE);
				setSuccessfullyPerformed(false);
				return false;
			}
		}

		//Select file name
		File file = DefaultSettings.getUserInputOfFileFromGUI(this.mappingPanel, Config.MAP_FILE_DEFAULT_EXTENTION, "Save As...", true, true);
		if (file != null)
		{
			setSuccessfullyPerformed(processSaveFile(file));
		}

		return isSuccessfullyPerformed();
	}

	/**
	 *
	 * @param file
	 * @return
	 * @throws Exception
	 */
	protected boolean processSaveFile(File file) throws Exception
	{
		preActionPerformed(mappingPanel);

        MappingDataManager mappingManager = mappingPanel.getMappingDataManager();
		Mapping mappingData = mappingManager.retrieveMappingData(true);
		MapBuilderImpl builder = new MapBuilderImpl();

        FileOutputStream fw = null;
		BufferedOutputStream bw = null;

        CsvToXmiMappingPanel csvToXmi = (CsvToXmiMappingPanel)mappingPanel;
		mappingData.setMappingType(csvToXmi.getMappingTarget());

        boolean oldChangeValue = mappingPanel.isChanged();

        try
		{
			fw = new FileOutputStream(file);
			bw = new BufferedOutputStream(fw);

            builder.build(bw, mappingData);

            if (!GeneralUtilities.areEqual(defaultFile, file))
			{//not equal, change it.
				removeFileUsageListener(defaultFile, mappingPanel);
				defaultFile = file;
			}
			//clear the change flag.
			mappingPanel.setChanged(false);
			//try to notify affected panels
			postActionPerformed(mappingPanel);

            csvToXmi.cvsToXmiGeneration(file.getAbsolutePath());

            //generate mapping report:
            File rptFile =null;
            if (csvToXmi.getReportPanel()!=null)
            	rptFile=csvToXmi.getReportPanel().getSaveFile();

            if (rptFile!=null)
            {
//            	rptFile=DefaultSettings.getUserInputOfFileFromGUI(this.mappingPanel, Config.HL7_V3_MESSAGE_FILE_DEFAULT_EXTENSION, "Save Mapping Report As...", true, true);
	            //regenerate reportfile if it is available
            	MappingBaseTree mappingBaseTree=(MappingBaseTree)csvToXmi.getSourceTree();
	            CsvToXmiMappingReporter reporter=new CsvToXmiMappingReporter((MappableNode)mappingBaseTree.getRootTreeNode(), CsvToXmiMappingReporter.REPORT_UNMAPPED);
	            reporter.setSourceFileName(csvToXmi.getSourceFileName());
	            reporter.setTargetFileName(csvToXmi.getTargetFileName());
	            reporter.generateReportFile(rptFile);
            }
            JOptionPane.showMessageDialog(mappingPanel.getParent(), "Mapping data has been saved successfully.", "Save Complete", JOptionPane.INFORMATION_MESSAGE);
			return true;
		}
		catch(Throwable e)
		{
			//restore the change value since something occurred and believe the save process is aborted.
			mappingPanel.setChanged(oldChangeValue);
			//rethrow the exeception
			e.printStackTrace();
			throw new Exception(e);
		}
		finally
		{
			try
			{
				//close buffered writer will automatically close enclosed file writer.
				if(bw!=null)
				{
					bw.close();
					mappingPanel.setSaveFile(file);
				}
			}
			catch(Throwable e)
			{//intentionally ignored.
			}
		}
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.8  2008/03/04 16:05:29  schroedn
 * HISTORY      : Corrected Save and SaveAs functionality
 * HISTORY      :
 * HISTORY      : Revision 1.7  2007/12/14 16:01:36  wangeug
 * HISTORY      : do not force to save mapping report
 * HISTORY      :
 * HISTORY      : Revision 1.6  2007/12/12 19:54:07  wangeug
 * HISTORY      : support both data model and object model
 * HISTORY      :
 * HISTORY      : Revision 1.5  2007/12/07 16:06:15  wangeug
 * HISTORY      : support both data model and object model
 * HISTORY      :
 * HISTORY      : Revision 1.4  2007/12/06 16:16:28  schroedn
 * HISTORY      : Annotate XMI file in csv to xmi
 * HISTORY      :
 * HISTORY      : Revision 1.3  2007/11/30 20:57:53  schroedn
 * HISTORY      : CSV to XMI mapping
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/11/30 14:40:33  wangeug
 * HISTORY      : create CSV_TO_XMI mapping module
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/11/29 16:47:52  wangeug
 * HISTORY      : create CSV_TO_XMI mapping module
 * HISTORY      :
 * HISTORY      : Revision 1.3  2007/09/20 16:40:14  schroedn
 * HISTORY      : License text
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/06/07 19:02:24  schroedn
 * HISTORY      : Edits to sync with new codebase and java webstart
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:17:57  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.1  2006/09/26 15:48:30  wuye
 * HISTORY      : New actions for object - 2 db mapping
 * HISTORY      :
 * HISTORY      : Revision 1.29  2006/08/02 18:44:23  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.28  2006/06/13 18:12:13  jiangsc
 * HISTORY      : Upgraded to catch Throwable instead of Exception.
 * HISTORY      :
 * HISTORY      : Revision 1.27  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.26  2006/01/03 18:56:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.25  2005/12/29 23:06:15  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.24  2005/12/29 15:39:06  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.23  2005/12/14 21:37:18  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.22  2005/12/01 20:03:39  jiangsc
 * HISTORY      : Save point
 * HISTORY      :
 * HISTORY      : Revision 1.21  2005/11/29 16:23:54  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.20  2005/10/26 18:12:29  jiangsc
 * HISTORY      : replaced printStackTrace() to Log.logException
 * HISTORY      :
 * HISTORY      : Revision 1.19  2005/10/26 16:22:10  jiangsc
 * HISTORY      : Face lift to provide better error report.
 * HISTORY      :
 * HISTORY      : Revision 1.18  2005/10/25 22:00:42  jiangsc
 * HISTORY      : Re-arranged system output strings within UI packages.
 * HISTORY      :
 * HISTORY      : Revision 1.17  2005/10/19 18:51:24  jiangsc
 * HISTORY      : Re-engineered Action calling sequence.
 * HISTORY      :
 * HISTORY      : Revision 1.16  2005/10/18 13:35:26  umkis
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.15  2005/10/13 17:37:41  jiangsc
 * HISTORY      : Enhanced UI reporting on exceptions.
 * HISTORY      :
 * HISTORY      : Revision 1.14  2005/10/10 20:49:01  jiangsc
 * HISTORY      : Enhanced dialog operation.
 * HISTORY      :
 * HISTORY      : Revision 1.13  2005/09/30 20:44:06  jiangsc
 * HISTORY      : Minor update - corrected wording
 * HISTORY      :
 * HISTORY      : Revision 1.12  2005/09/30 20:28:09  jiangsc
 * HISTORY      : Minor update
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/08/30 20:48:17  jiangsc
 * HISTORY      : minor update
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/08/11 22:10:32  jiangsc
 * HISTORY      : Open/Save File Dialog consolidation.
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/08/05 20:35:53  jiangsc
 * HISTORY      : 0)Implemented field sequencing on CSVPanel but needs further rework;
 * HISTORY      : 1)Removed (Yes/No) for questions;
 * HISTORY      : 2)Removed double-checking after Save-As;
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/07/27 22:41:11  jiangsc
 * HISTORY      : Consolidated context sensitive menu implementation.
 * HISTORY      :
 */
