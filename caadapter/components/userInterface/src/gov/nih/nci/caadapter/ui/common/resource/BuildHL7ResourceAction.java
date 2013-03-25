/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */
package gov.nih.nci.caadapter.ui.common.resource;

import gov.nih.nci.caadapter.hl7.mif.v1.BuildResourceUtil;
import gov.nih.nci.caadapter.ui.common.AbstractMainFrame;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.actions.AbstractContextAction;
import gov.nih.nci.caadapter.ui.main.HL7AuthorizationDialog;
import gov.nih.nci.caadapter.ui.mapping.V2V3.V2MetaCollectorDialog;
import gov.nih.nci.caadapter.ui.mapping.V2V3.V2MetaBasicInstallDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class BuildHL7ResourceAction extends AbstractContextAction {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public static final String COMMAND_BUILD_V3 = "Load HL7 v3 Normative Edition Processable Artifacts";
    public static final String COMMAND_BUILD_V2 = "Load HL7 v2 Processable Artifacts";
    public static final String COMMAND_BUILD_V2_CORE ="Load HL7 v2 Core Artifacts";
    public static final String COMMAND_BUILD_V_MESSAGE = "Load HL7 v2 Message Artifacts";

    private AbstractMainFrame mainFrame;

    private static final String RESOURCE_NAME_V3="resource.zip";
    private static final String RESOURCE_NAME_V2="resourceV2.zip";


    /**
     * Defines an <code>Action</code> object with the specified
     * description string and a default icon.
     */
    public BuildHL7ResourceAction(String name, AbstractMainFrame mainFrame) {
        this(name, null, mainFrame);
    }

    /**
     * Defines an <code>Action</code> object with the specified
     * description string and a the specified icon.
     */
    public BuildHL7ResourceAction(String name, Icon icon, AbstractMainFrame mainFrame) {
        super(name, icon);
        this.mainFrame = mainFrame;
        setActionCommandType(DESKTOP_ACTION_TYPE);
    }

    /**
     * The abstract function that descendant classes must be overridden to
     * provide customsized handling.
     *
     * @param e
     * @return true if the action is finished successfully; otherwise,
     *         return false.
     */
    protected boolean doAction(ActionEvent e) throws Exception {

    	String resourceName="";
    	if (getName().equals(COMMAND_BUILD_V2))
    		resourceName=RESOURCE_NAME_V2;
    	else if (getName().equals(COMMAND_BUILD_V3))
    		resourceName=RESOURCE_NAME_V3;

    	String resorcePath=BuildResourceUtil.findResourceFile(resourceName);
    	boolean toContinue=false;

    	if (resorcePath==null||resorcePath.equals(""))
    	{
    		toContinue=true;
    		resorcePath=System.getProperty("user.dir")+java.io.File.separator
    		+"lib"+java.io.File.separator
    		+resourceName;
    	}
    	else
    	{
    		String msgConfirm="Your resource is found :"+resorcePath+".\nIt will replaced if you continue ?";

    		int userReply=JOptionPane.showConfirmDialog(mainFrame, msgConfirm, getName(),JOptionPane.YES_OPTION);
    		if (userReply==JOptionPane.YES_OPTION)
    			toContinue=true;
    	}
    	if (toContinue)
    	{
    		if (getName().equals( COMMAND_BUILD_V3))
    			new BuildHL7ResourceDialog(mainFrame,getName(),true, resorcePath).setVisible(true);
    		else if (getName().equals( COMMAND_BUILD_V2))
    		{
//		        V2MetaCollectorDialog dialog = new V2MetaCollectorDialog(mainFrame);
    			HL7AuthorizationDialog dialog=	new HL7AuthorizationDialog (mainFrame,"Notice: Loading HL7 V2 Specification"
    					,HL7AuthorizationDialog.HL7_V2_WARNING_CONTEXT_FILE_PATH);

    		}
            else if (getName().equals( COMMAND_BUILD_V2_CORE))
    		{
    			V2MetaBasicInstallDialog dialog = new V2MetaBasicInstallDialog(mainFrame);
    			DefaultSettings.centerWindow(dialog);
    			//dialog.setViewOnly(true);
		        dialog.setVisible(true);

    		}
            else if (getName().equals( COMMAND_BUILD_V_MESSAGE))
    		{
                V2MetaCollectorDialog dialog = new V2MetaCollectorDialog(mainFrame);
//    			    			HL7AuthorizationDialog dialog=	new HL7AuthorizationDialog (mainFrame,"Notice: Loading HL7 V3 Specification"
//    					,HL7AuthorizationDialog.HL7_V2_WARNING_CONTEXT_FILE_PATH);
    			DefaultSettings.centerWindow(dialog);
    			//dialog.setViewOnly(true);
		        dialog.setVisible(true);

    		}
        }

        /*
        SelectionCollectingMethodDialog selectDialog = new SelectionCollectingMethodDialog(mainFrame);

        DefaultSettings.centerWindow(selectDialog);
        selectDialog.setVisible(true);

        int selected = selectDialog.getSelected();

        if (selected == 0)
        {
            V2MetaCollectorDialog dialog = new V2MetaCollectorDialog(mainFrame);
            DefaultSettings.centerWindow(dialog);
            dialog.setVisible(true);
            return true;
        }
        else if (selected == 1) {}
        else if (selected == 2) {}
        else return false;

        String mtFile = "";

        dialog = new V2MetaCollectorForBatchDialog(mainFrame);

        DefaultSettings.centerWindow(dialog);
        dialog.setVisible(true);

        if (!dialog.wasFinished()) return true;

        int res = JOptionPane.showConfirmDialog(mainFrame, "Do you want to install the collected meta data?", "Install Meta data?", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
        if (res != JOptionPane.YES_OPTION)
        {
            return true;
        }

        mtFile = dialog.getMessageTypeMetaFilePath();


        File file = new File(mtFile);
        if ((!file.exists())||(!file.isFile()))
        {
            JOptionPane.showMessageDialog(mainFrame, "Invalid Message Type File : " + mtFile, "Invalid Message Type File", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        String mtData = FileUtil.readFileIntoString(mtFile);
        String fName = file.getName();
        int idx = fName.indexOf(".");
        if (idx <= 0)
        {
            JOptionPane.showMessageDialog(mainFrame, "Invalid File Name (1) : " + fName, "Invalid File Name", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String parDir = file.getParentFile().getAbsolutePath();
        if (!parDir.endsWith(File.separator)) parDir = parDir + File.separator;

        String mtName = fName.substring(0, idx);
        String mtVersion = fName.substring(idx+1);

        boolean cTag = false;

        CheckVersionAndItem check = new  CheckVersionAndItem();
        for(String ver:check.getVersionTo()) if (mtVersion.equals(ver)) cTag = true;
        if (!cTag)
        {
            JOptionPane.showMessageDialog(mainFrame, "Invalid File Name (2) : " + fName, "Invalid File Name", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        cTag = false;
        GroupingMetaInstance group = new GroupingMetaInstance(mtVersion, check.getItemTo()[2]);
        for (String nam:group.getOutList()) if (mtName.equals(nam)) cTag = true;
        if (!cTag)
        {
            JOptionPane.showMessageDialog(mainFrame, "Invalid File Name (3) : " + fName, "Invalid File Name", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        File segDir = new File(parDir + mtName);
        if ((!segDir.exists())||(!segDir.isDirectory()))
        {
            JOptionPane.showMessageDialog(mainFrame, "Segment directory is not exist : " + parDir + mtName, "No Segment directory", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        CompareInstance compare = new CompareInstance(path, mtVersion, check.getItemTo()[2]);
        */

        return true;

    }

    /**
     * Return the associated UI component.
     *
     * @return the associated UI component.
     */
    protected Component getAssociatedUIComponent() {
        return mainFrame;
    }
}

