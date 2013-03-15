/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.codegen.core.ant;

import gov.nih.nci.codegen.core.util.LicenseUpdater;
import java.lang.StringBuffer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.types.FileSet;
import org.apache.log4j.*;

/**





 */
/**
 * @author caBIO Team
 * @version 1.0
 */


public class LicenseUpdaterTask extends Task {

	private static Logger log = Logger.getLogger(LicenseUpdaterTask.class);

	private LicenseUpdater _updater;

	private String _licenseFileName;

	private Collection _fileSets = new ArrayList();

	private String _outputDirName;

	/**
	 * Creates a LicenseUpdaterTask instance
	 */
	public LicenseUpdaterTask() {
		_updater = new LicenseUpdater();
	}

	public void setBeginPattern(String p_beginPattern) {
		_updater.setBeginPattern(p_beginPattern);
	}

	public void setEndPattern(String p_endPattern) {
		_updater.setEndPattern(p_endPattern);
	}

	public void setOutputDir(String p_outputDirName) {
		_outputDirName = p_outputDirName;
	}

	public void setLicenseFile(String p_licenseFileName) {
		_licenseFileName = p_licenseFileName;
	}

	public void setLogFile(String p_logFileName) {
		_updater.setLogFileName(p_logFileName);
	}

	public void addFileSet(FileSet p_fileSet) {
		_fileSets.add(p_fileSet);
	}

	public void init() throws BuildException {

	}

	public void execute() throws BuildException {

		if (_licenseFileName == null) {
			log.error("License file not specified");
			throw new BuildException("License file not specified");
		}
		File f = new File(_licenseFileName);
		if (!f.exists()) {
			log.error("License file doesn't exist");
			throw new BuildException("License file doesn't exist");
		}
		try {
			StringBuffer sb = new StringBuffer();
			BufferedReader br = new BufferedReader(new FileReader(f));
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}
			_updater.setText(sb.toString());
		} catch (Exception ex) {
			log.error("Error getting license text: "+ ex.getMessage());
			throw new BuildException("Error getting license text: "
					+ ex.getMessage(), ex);
		}

		try {
			_updater.validate();
		} catch (Exception ex) {
			log.error("Task not configured correctly: " + ex.getMessage());
			throw new BuildException("Task not configured correctly: "
					+ ex.getMessage(), ex);
		}
		if (_fileSets.size() == 0) {
			log.error("A nested fileset element is required");
			throw new BuildException("A nested fileset element is required");
		}
		for (Iterator i = _fileSets.iterator(); i.hasNext();) {
			FileSet fileSet = (FileSet)i.next();
			DirectoryScanner ds = null;
			if (_outputDirName == null) {
				ds = fileSet.getDirectoryScanner(project);
			} else {
				File outputDir = new File(_outputDirName);
				if (!outputDir.exists()) {
					outputDir.mkdirs();
				}

				Copy copy = new Copy();
				copy.setProject(project);
				copy.addFileset(fileSet);
				copy.setTodir(outputDir);
				copy.setVerbose(false);
				copy.init();
				copy.execute();
				fileSet.setDir(outputDir);
				ds = fileSet.getDirectoryScanner(project);
			}

			String[] fileNames = ds.getIncludedFiles();
			String path = ds.getBasedir().getAbsolutePath();
			for (int j = 0; j < fileNames.length; j++) {
				_updater.getFiles().add(new File(path + "/" + fileNames[j]));
			}
		}

		try {
			_updater.run();
		} catch (Exception ex) {
			log.error("Error executing license updater: " + ex.getMessage());
			ex.printStackTrace();
			throw new BuildException("Error executing license updater: "
					+ ex.getMessage(), ex);
		}
	}
}
