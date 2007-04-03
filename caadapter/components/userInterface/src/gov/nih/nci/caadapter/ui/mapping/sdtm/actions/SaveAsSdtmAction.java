package gov.nih.nci.caadapter.ui.mapping.sdtm.actions;

import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.sdtm.SDTMMappingGenerator;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.actions.DefaultSaveAsAction;
import gov.nih.nci.caadapter.ui.mapping.AbstractMappingPanel;
//import gov.nih.nci.caadapter.ui.hl7.map.HL7MappingPanel;
//import gov.nih.nci.caadapter.ui.sdtm.view.Database2SDTMMappingPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.StringTokenizer;

public class SaveAsSdtmAction extends DefaultSaveAsAction
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: SaveAsSdtmAction.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem. This String is for informational purposes only and MUST not be made
	 * final.
	 * 
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/sdtm/actions/SaveAsSdtmAction.java,v 1.1 2007-04-03 16:17:57 wangeug Exp $";

	protected AbstractMappingPanel mappingPanel;

	public SDTMMappingGenerator sdtmMappingGenerator;

	/**
	 * Defines an <code>Action</code> object with a default description string and default icon.
	 */
	public SaveAsSdtmAction(AbstractMappingPanel mappingPanel, SDTMMappingGenerator _sdtmMappingGenerator) {
		this(COMMAND_NAME, mappingPanel);
		this.sdtmMappingGenerator = _sdtmMappingGenerator;
	}

	public SaveAsSdtmAction(String command, AbstractMappingPanel mappingPanel, SDTMMappingGenerator _sdtmMappingGenerator) {
		this(command, mappingPanel);
		this.sdtmMappingGenerator = _sdtmMappingGenerator;
	}

	/**
	 * Defines an <code>Action</code> object with the specified description string and a default icon.
	 */
	public SaveAsSdtmAction(String name, AbstractMappingPanel mappingPanel) {
		this(name, null, mappingPanel);
	}

	/**
	 * Defines an <code>Action</code> object with the specified description string and a the specified icon.
	 */
	public SaveAsSdtmAction(String name, Icon icon, AbstractMappingPanel mappingPanel) {
		super(name, icon, null);
		this.mappingPanel = mappingPanel;
		// setAdditionalAttributes();
	}

	/**
	 * Invoked when an action occurs.
	 */
	protected boolean doAction(ActionEvent e) throws Exception
	{
		if (this.mappingPanel != null) {
			if (!mappingPanel.isSourceTreePopulated() || !mappingPanel.isTargetTreePopulated()) {
				String msg = "Enter both source and target information before saving the map specification.";
				JOptionPane.showMessageDialog(mappingPanel, msg, "Error", JOptionPane.ERROR_MESSAGE);
				setSuccessfullyPerformed(false);
				return false;
			}
		}
		File file = DefaultSettings.getUserInputOfFileFromGUI(this.mappingPanel, Config.MAP_FILE_DEFAULT_EXTENTION, "Save As...", true, true);
		if (file != null) {
			System.out.println("SaveSdtmAction ------ " + mappingPanel.getMappingDataManager().retrieveMappingData(false));
			// setSuccessfullyPerformed(processSaveFile(file));
			processSaveFile(file);
		}
		return isSuccessfullyPerformed();
	}

	protected boolean processSaveFile(File file) throws Exception
	{
		preActionPerformed(mappingPanel);
		BufferedOutputStream bw = null;
		boolean oldChangeValue = mappingPanel.isChanged();
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			out.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
			out.write("<mapping>\n");
			out.write("  <components>\n");
			out.write("  \t<component kind=\"SCS\" location=\"" + sdtmMappingGenerator.getScsSDTMFile() + "\"/>\n");
			out.write("  \t<component kind=\"XML\" location=\"" + sdtmMappingGenerator.getScsDefineXMLFIle() + "\"/>\n");
			out.write("  </components>\n");
			for (int i = 0; i < sdtmMappingGenerator.results.size(); i++) {
				out.write("  <link>\n");
				StringTokenizer strTk = new StringTokenizer(sdtmMappingGenerator.results.get(i), "~");
				out.write("  \t<source>");
				out.write(strTk.nextToken());
				out.write("</source>\n");
				out.write("  \t<target>");
				out.write(strTk.nextToken());
				out.write("</target>\n");
				out.write("</link>\n");
			}
			out.write("</mapping>");
			out.close();
			// clear the change flag.
			mappingPanel.setChanged(false);
			// try to notify affected panels
			postActionPerformed(mappingPanel);
			JOptionPane.showMessageDialog(mappingPanel.getParent(), "Mapping data has been saved successfully.", "Save Complete", JOptionPane.INFORMATION_MESSAGE);
			return true;
		} catch (Throwable e) {
			// restore the change value since something occurred and believe
			// the save process is aborted.
			mappingPanel.setChanged(oldChangeValue);
			// rethrow the exeception
			throw new Exception(e);
			// return false;
		} finally {
			try {
				// close buffered writer will automatically close enclosed file
				// writer.
				if (bw != null) {
					bw.close();
					// the output stream will flush and assign the timestamp
					// upon closure.
					// moved the setSaveFile() call here so as to record the
					// right timestamp of last modified.
				}
				mappingPanel.setSaveFile(file);
			} catch (Throwable e) {// intentionally ignored.
			}
		}
	}
}