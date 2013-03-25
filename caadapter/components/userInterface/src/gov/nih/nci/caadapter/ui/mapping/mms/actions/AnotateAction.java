/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.mapping.mms.actions;


import gov.nih.nci.caadapter.mms.generator.CumulativeMappingGenerator;
import gov.nih.nci.caadapter.mms.generator.XMIGenerator;
import gov.nih.nci.caadapter.ui.mapping.AbstractMappingPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * This class defines a concrete "Save As" action.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2008-06-09 19:54:06 $
 */
public class AnotateAction extends SaveAsObjectToDbMapAction
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: AnotateAction.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/mms/actions/AnotateAction.java,v 1.2 2008-06-09 19:54:06 phadkes Exp $";

	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public AnotateAction(AbstractMappingPanel mappingPanel)
	{
		this(DefaultAnotateAction.COMMAND_NAME, mappingPanel);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a default icon.
	 */
	public AnotateAction(String name, AbstractMappingPanel mappingPanel)
	{
		this(name, DefaultAnotateAction.IMAGE_ICON, mappingPanel);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a the specified icon.
	 */
	public AnotateAction(String name, Icon icon, AbstractMappingPanel mappingPanel)
	{
		super(name, icon, null);
		this.mappingPanel = mappingPanel;
//		setAdditionalAttributes();
	}

	/**
	 * Invoked when an action occurs.
	 */
	protected boolean doAction(ActionEvent e) throws Exception
	{
		boolean results;
		File fileFromPanel = mappingPanel.getSaveFile();

		if(fileFromPanel==null)
		{
			results =  super.doAction(e);
		}
		else
		{
			results = processSaveFile(fileFromPanel);
		}
		try {
			fileFromPanel = mappingPanel.getSaveFile();
			String xmiFile = CumulativeMappingGenerator.getXmiFileName();
			XMIGenerator generator = new XMIGenerator(fileFromPanel.getAbsolutePath(),xmiFile);
			generator.annotateXMI();
		}catch (Exception ex) {
			ex.printStackTrace();
		}

		return results;
	}

}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
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
