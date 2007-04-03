/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/MappingFileSynchronizer.java,v 1.1 2007-04-03 16:17:14 wangeug Exp $
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
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2007-04-03 16:17:14 $
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
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/MappingFileSynchronizer.java,v 1.1 2007-04-03 16:17:14 wangeug Exp $";

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
