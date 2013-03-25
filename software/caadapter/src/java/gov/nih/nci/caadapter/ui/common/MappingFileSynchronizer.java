/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.common;

import gov.nih.nci.caadapter.common.util.GeneralUtilities;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This class defines an add-on mapping file synchronization manager to help monitor any
 * update on the associated mapping files.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2008-06-09 19:53:51 $
 */
public class MappingFileSynchronizer //implements ChangeListener
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: MappingFileSynchronizer.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/MappingFileSynchronizer.java,v 1.2 2008-06-09 19:53:51 phadkes Exp $";

	public static enum FILE_TYPE
	{
		Source_File, Target_File, Mapping_File
	}

	private JPanel ownerPanel;
	private Map<FILE_TYPE, File> fileMap;
	private Map<FILE_TYPE, Long> fileTimestampMap;

	public MappingFileSynchronizer(JPanel ownerPanel)
	{
		//this.ownerPanel = ownerPanel;
		fileMap = (Map<FILE_TYPE, File>)Collections.synchronizedMap(new HashMap<FILE_TYPE, File>());
		fileTimestampMap = (Map<FILE_TYPE, Long>)Collections.synchronizedMap(new HashMap<FILE_TYPE, Long>());
	}

	public void registerFile(FILE_TYPE fileType, File file)
	{
		if(file!=null && file.exists())
		{
			fileMap.put(fileType, file);
			fileTimestampMap.put(fileType, new Long(file.lastModified()));
		}
	}

	public void removeFile(FILE_TYPE fileType)
	{
		if (fileType == null)
		{
			return;
		}
		fileMap.remove(fileType);
		fileTimestampMap.remove(fileType);
	}

	public void removeFile(File file)
	{
		if (file == null)
		{
			return;
		}
		Iterator it = fileMap.keySet().iterator();
		while(it.hasNext())
		{
			Object key = it.next();
			File existingFile = fileMap.get(key);
			if(GeneralUtilities.areEqual(existingFile, file))
			{
				fileMap.remove(key);
				fileTimestampMap.remove(key);
			}
		}
	}

	public Map<FILE_TYPE, File> doSynchronizationCheck(boolean displayMessageIfFileChanged)
	{
		Iterator it = fileMap.keySet().iterator();
		Map<FILE_TYPE, File> changedFileMap = new HashMap<FILE_TYPE, File>();
		while(it.hasNext())
		{
			FILE_TYPE key = (FILE_TYPE) it.next();
			File file = fileMap.get(key);
			File newFile = new File(file.getAbsolutePath());
			//ignore if either file does not exist anymore
			if(!file.exists() || !newFile.exists())
			{
				continue;
			}
			Long oldTimeStampLong = fileTimestampMap.get(key);
			long oldTimeStamp = (oldTimeStampLong==null) ? 0 : oldTimeStampLong.longValue();
//			System.out.println("oldTimeStamp: '" + oldTimeStamp + "'");
			long newTimeStamp = newFile.lastModified();
//			System.out.println("newTimeStamp: '" + newTimeStamp + "'");
			if(oldTimeStamp!=newTimeStamp)
			{
				changedFileMap.put(key, file);
			}
		}
		if(changedFileMap.size()>0 && displayMessageIfFileChanged)
		{
			notifyChanges(changedFileMap);
		}
		return changedFileMap;
	}

	protected void notifyChanges(Map<FILE_TYPE, File> changedFileMap)
	{
		int size = changedFileMap.size();
		int offset = 3;
		Object[] messageArray = new Object[size + offset];
		messageArray[0] = "<html><b>Following files are updated outside of the Mapping panel.</b></html>";
		messageArray[1] = "<html>It is recommended to <b>save</b> your changes and <b>refresh</b> the Mapping panel with the latest changes.</html>";
		messageArray[2] = "Files:\n";
		Iterator it = changedFileMap.keySet().iterator();
		int i=0;
		while(it.hasNext())
		{
			Object key = it.next();
			messageArray[i+offset] = "<html><ul>" + changedFileMap.get(key).getName() + "</ul></html>";
			i++;
		}
		JOptionPane.showMessageDialog(ownerPanel, messageArray, "Information", JOptionPane.INFORMATION_MESSAGE);
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2007/04/03 16:17:14  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.6  2006/08/02 18:44:23  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.5  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.4  2006/01/03 18:56:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/12/29 23:06:14  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/12/14 21:37:17  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/12/02 23:02:57  jiangsc
 * HISTORY      : Save point
 * HISTORY      :
 */
