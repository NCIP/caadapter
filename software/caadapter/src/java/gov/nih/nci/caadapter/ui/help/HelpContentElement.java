/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.ui.help;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * This class defines the content element.
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: linc $
 * @since caAdapter v1.2
 * @version    $Revision: 1.3 $
 * @date       $Date: 2008-09-26 20:35:27 $
 */


public class HelpContentElement
{
  /**
   * Logging constant used to identify source of log entry, that could be later used to create
   * logging mechanism to uniquely identify the logged class.
   */
  private static final String LOGID = "$RCSfile: HelpContentElement.java,v $";

  /**
   * String that identifies the class version and solves the serial version UID problem.
   * This String is for informational purposes only and MUST not be made final.
   *
   * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
   */
  public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/help/HelpContentElement.java,v 1.3 2008-09-26 20:35:27 linc Exp $";


  private String nodeTitle;
  private String nodeContent;
  private List<String> keyWordsList;
  private String keyWordsDelimiter = ",";

  HelpContentElement()
    {
      nodeTitle = "";
      nodeContent = "";
      setKeyWords("");
    }
  HelpContentElement(String title)
    {
      nodeTitle = title;
      nodeContent = "";
      setKeyWords("");
    }
  HelpContentElement(String title, String cont)
    {
      nodeTitle = title;
      nodeContent = cont;
      setKeyWords("");
    }
  HelpContentElement(String title, String cont, String keyWd)
    {
      nodeTitle = title;
      nodeContent = cont;
      setKeyWords(keyWd);
    }
  public String getNodeTitle()
    {
      return nodeTitle;
    }
  public String getNodeContent()
    {
      return nodeContent;
    }
  public String getKeyWords()
    {
      if (getCountKeyWords() == 0) return "";
      if (getCountKeyWords() == 1) return keyWordsList.get(0);
      String kwds = keyWordsList.get(0);
      for (int i=1;i<getCountKeyWords();i++) kwds = kwds + getkeyWordsDelimiter() + " " + keyWordsList.get(i);
      return kwds;
    }
  public int getCountKeyWords()
    {
      return (getKeyWordsList()).size();
    }
  public String getkeyWordsDelimiter()
    {
      return keyWordsDelimiter;
    }
  public List<String> getKeyWordsList()
    {
      return keyWordsList;
    }

  public void setkeyWordsDelimiter(String deli)
    {
      keyWordsDelimiter = deli;
    }
  public void addKeyWords(String kwds)
    {
      String ds = getKeyWords() + getkeyWordsDelimiter() + " " + kwds;
      setKeyWords(ds);
    }
  public boolean compareKeyWords(String keyW)
    {
      List<String> ls = getKeyWordsList();
      List<String> lp = transformKeyWordsList(keyW);
      if (ls.size() != lp.size()) return false;
      if (ls.size() == 0) return true;
      boolean ck = true;
      for(int i=0;i<ls.size();i++)
        {
          if (!((ls.get(i)).trim()).equalsIgnoreCase((lp.get(i)).trim())) ck = false;
        }
      return ck;
    }
  public void setKeyWords(String keyWordsX)
    {
      keyWordsList = transformKeyWordsList(keyWordsX);
    }

  public List<String> transformKeyWordsList(String keyWordsX)
    {
      if (keyWordsX == null) keyWordsX = "";
      List<String> ls = new ArrayList<String>();

      if (keyWordsX.equals("")) return ls;

      if ((keyWordsX.indexOf(getkeyWordsDelimiter())) < 0)
        {
          ls.add(keyWordsX);
          return ls;
        }
      StringTokenizer st = new StringTokenizer(keyWordsX, getkeyWordsDelimiter());

      int i = -1;
      while(st.hasMoreTokens())
        {
          i++;
          String keyWd = (st.nextToken()).trim();
          if (i == 0)
            {
              ls.add(keyWd);
              continue;
            }

          if (searchKeyWord(ls, keyWd)) continue;
          else ls.add(keyWd);
        }
      return ls;
    }
  public boolean searchKeyWord(String srch)
    {
      return searchKeyWord(getKeyWordsList(), srch);
    }
  public boolean searchKeyWord(List<String> ls, String srch)
    {
      boolean ccx = false;
      if (ls.size() == 0) return false;
      for(int i=0;i<ls.size();i++)
        {
          if (((ls.get(i)).trim()).equalsIgnoreCase(srch.trim())) ccx = true;
        }
      return ccx;
    }

  public void setNodeTitle(String title)
    {
      nodeTitle = title;
    }
  public void setNodeContent(String cont)
    {
      nodeContent = cont;
    }
  public String toString()
    {
      if ((getNodeTitle()).equals("")) return "** Empty Node";
      else return getNodeTitle();
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
 * HISTORY      : Revision 1.11  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.10  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/12/29 23:06:12  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/12/29 15:39:05  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/12/14 21:37:16  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/11/29 16:23:55  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/11/03 14:30:38  umkis
 * HISTORY      : code re-organize
 * HISTORY      :
 */
