/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.common;




import java.io.*;

/**
 * Base class to build a meta XML file
 *
 * @author OWNER: Eric Chen  Date: Aug 4, 2005
 * @author LAST UPDATE: $Author: phadkes $
 * @version $Revision: 1.2 $
 * @date $$Date: 2008-06-09 19:53:49 $
 * @since caAdapter v1.2
 */


abstract public class MetaBuilderBase implements MetaBuilder
{
    public void build(File file, MetaObject metaObject) throws MetaException
    {
        File tempFile = null;
        BufferedOutputStream tempbw = null;

        BufferedOutputStream filebw = null;

        BufferedInputStream in = null;

        try
        {
            tempFile = new File(file.getAbsolutePath() + "~");
            tempbw = new BufferedOutputStream(new FileOutputStream(tempFile));
            build(tempbw, metaObject);

            //copy the file
            in = new BufferedInputStream(new FileInputStream(tempFile));
            filebw = new BufferedOutputStream(new FileOutputStream(file));

            byte[] buff = new byte[32 * 1024];
            int len;
            while ((len = in.read(buff)) > 0)
                filebw.write(buff, 0, len);
        }
        catch (FileNotFoundException e)
        {
            Log.logException(this, e);
			//do not swallow the e's message presumptively
			throw new MetaException(e.getMessage(), e);
//            throw new MetaException("File is not founded: " + file.getAbsolutePath(), e);
        }
        catch (IOException e)
        {
            Log.logException(this, e);
//            throw new MetaException("IO Exception", e);
			//do not swallow the e's message presumptively
			throw new MetaException(e.getMessage(), e);
		}
        finally
        {
            try
            {
                //close buffered writer will automatically close enclosed file writer.
                if (tempbw != null) tempbw.close();
                if (filebw != null) filebw.close();
                if (in != null) in.close();
                // delelte the temporary file
                if (tempFile != null) tempFile.delete();

            }
            catch (Exception e)
            {//intentionally ignored.
            }
        }


    }

}


/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2007/04/03 16:02:37  wangeug
 * HISTORY      : initial loading of common module
 * HISTORY      :
 * HISTORY      : Revision 1.10  2006/08/02 18:44:20  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.9  2006/01/03 19:16:51  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.8  2006/01/03 18:27:13  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/12/29 23:06:13  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/12/28 21:50:46  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/10/26 16:21:50  jiangsc
 * HISTORY      : Preserved the exception message rather than swallowing it presumptively
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/08/17 22:48:40  chene
 * HISTORY      : Refactor MetaBuilder to be singleton
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/08/17 21:27:50  chene
 * HISTORY      : Refactor MetaBuilder to be singleton
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/08/04 22:46:36  chene
 * HISTORY      : Fix the no such method exception
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/08/04 21:41:14  chene
 * HISTORY      : Support temporaly file saving
 * HISTORY      :
 */
