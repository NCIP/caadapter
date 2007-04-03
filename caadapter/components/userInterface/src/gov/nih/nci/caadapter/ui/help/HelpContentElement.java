/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/help/HelpContentElement.java,v 1.1 2007-04-03 16:17:14 wangeug Exp $
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


package gov.nih.nci.caadapter.ui.help;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * This class defines the content element.
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2007-04-03 16:17:14 $
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
  public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/help/HelpContentElement.java,v 1.1 2007-04-03 16:17:14 wangeug Exp $";


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
