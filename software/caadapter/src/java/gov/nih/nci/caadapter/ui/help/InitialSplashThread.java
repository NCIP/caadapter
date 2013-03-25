/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.help;

import gov.nih.nci.caadapter.ui.common.DefaultSettings;

/**
 * This class defines splash window thread.
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: linc $
 * @since caAdapter v1.2
 * @version    $Revision: 1.3 $
 * @date       $Date: 2008-09-26 20:35:27 $
 */




public class InitialSplashThread implements Runnable
{
  /**
   * Logging constant used to identify source of log entry, that could be later used to create
   * logging mechanism to uniquely identify the logged class.
   */
  private static final String LOGID = "$RCSfile: InitialSplashThread.java,v $";
  /**
   * String that identifies the class version and solves the serial version UID problem.
   * This String is for informational purposes only and MUST not be made final.
   *
   * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
   */
  public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/help/InitialSplashThread.java,v 1.3 2008-09-26 20:35:27 linc Exp $";

  //private String tagFileName;
  private boolean tagOFSignal;
  private int finalCount;
  private int semiFinalCount;

  public InitialSplashThread()
    {
      tagOFSignal = true;
      finalCount = 50;          // Maximum Splash Window Displaying time in default is 5.0 sec.
      semiFinalCount = 20;      // Minimum Splash Window Displaying time in default is 2.0 sec.
    }
  public void setSignal(boolean sig)
    {
      tagOFSignal = sig;
    }

  public void run()
    {
      InitialSplashWindow isw = new InitialSplashWindow();
      isw.setAlwaysOnTop(true);
      DefaultSettings.centerWindow(isw);
      isw.setVisible(true);
      int i = 0;

      try
        {
          while(true)
            {
              i++;

              Thread.sleep(100);
              int n = 0;

              if (!tagOFSignal)
                {
                  if (i<semiFinalCount) finalCount = semiFinalCount;
                }

              if (i==finalCount)
                {
                  break;
                }
            }
        }
      catch(InterruptedException e)
        {

        }
      isw.dispose();
    }

}


/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.2  2008/06/09 19:53:52  phadkes
 * HISTORY      : New license text replaced for all .java files.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:17:14  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.12  2006/08/02 18:44:21  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.11  2006/01/19 00:46:58  umkis
 * HISTORY      : minimum displaying secont was changed to 2.0 sec.
 * HISTORY      :
 * HISTORY      : Revision 1.10  2006/01/17 19:39:53  umkis
 * HISTORY      : change minimum displaying time 2.3 => 3.0
 * HISTORY      :
 * HISTORY      : Revision 1.9  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.8  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/12/29 23:06:12  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/12/14 21:37:16  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/11/29 16:23:55  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/11/02 21:12:18  umkis
 * HISTORY      : code re-organized
 * HISTORY      :
 */
