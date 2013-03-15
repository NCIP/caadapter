/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.common.nodeloader;

import java.io.BufferedReader;
import java.util.ArrayList;

/**
 * This class defines functions to load and unload HL7V3 message from and/or to a given resource.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2008-06-09 19:53:51 $
 */
public class HL7V3MessageLoader
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: HL7V3MessageLoader.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/nodeloader/HL7V3MessageLoader.java,v 1.2 2008-06-09 19:53:51 phadkes Exp $";

	public static final String HL7_V3_MESSAGE_DELIMITER = "*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*";

	/**
	 * Load in data from the given reader.
	 * @param reader
	 * @return
	 * @throws Exception
	 */
	public java.util.List<String> loadData(BufferedReader reader) throws Exception
	{
		ArrayList<String> resultList = new ArrayList<String>();
		String line = null;
		StringBuffer buf = new StringBuffer();
		while((line = reader.readLine())!=null)
		{
			//leave the line to preserve those new-line characters, etc.
			String localLine = line.trim();
			if(HL7_V3_MESSAGE_DELIMITER.equals(localLine))
			{
				resultList.add(buf.toString());
				buf = new StringBuffer();
			}
			else
			{
				buf.append(line + "\n");
			}
		}
		String lastOne = buf.toString();
		if(lastOne.length()>0)
		{
            resultList.add(lastOne);
		}
		return resultList;
	}

//	/**
//	 * Write out the message content through the given writer.
//	 * @param writer
//	 * @param messageList
//	 * @throws Exception
//	 */
//	public void unLoadData(BufferedWriter writer, java.util.List<String> messageList) throws Exception
//	{
//		int size = messageList==null ? 0 : messageList.size();
//		for(int i=0; i<size; i++)
//		{
//			String message = messageList.get(i);
//			if(i>0)
//			{
//				writer.write(HL7_V3_MESSAGE_DELIMITER);
//				writer.newLine();
//			}
//			writer.write(message);
//			writer.newLine();
//			writer.flush();
//		}
//	}
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2007/04/03 16:17:13  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.11  2006/08/02 18:44:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.10  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.9  2006/01/03 18:56:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/12/29 23:06:15  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/12/29 15:39:06  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/12/14 21:37:18  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/11/29 16:23:54  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/08/12 18:38:16  jiangsc
 * HISTORY      : Enable HL7 V3 Message to be saved in multiple XML file.
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/08/10 16:10:52  jiangsc
 * HISTORY      : Now multiple V3 Message could be saved and retrieved from the single file.
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/08/03 16:56:18  jiangsc
 * HISTORY      : Further consolidation of context sensitive management.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/08/02 22:28:56  jiangsc
 * HISTORY      : Newly enhanced context-sensitive menus and toolbar.
 * HISTORY      :
 */
