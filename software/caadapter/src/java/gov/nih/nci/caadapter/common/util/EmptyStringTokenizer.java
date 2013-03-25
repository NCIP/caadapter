/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */


package gov.nih.nci.caadapter.common.util;

import java.util.ArrayList;

/**
 * This class provides empty tokens if the string has empty commas and toArray() and getTokenAt() implementations.
 *
 * @author OWNER: Harsha Jayanna
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v3.2 revision $Revision: 1.6 $ date $Date: 2008-06-09 19:53:50 $
 */
public class EmptyStringTokenizer
{

    private ArrayList<String> tokens_;

    private int current_;

    public EmptyStringTokenizer(String string, String delimiter)
    {
        tokens_ = new ArrayList<String>();
        current_ = 0;
        java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(string, delimiter, true);
        boolean wasDelimiter = true;
        boolean isDelimiter = false;
        while (tokenizer.hasMoreTokens())
        {
            String token = tokenizer.nextToken();
            isDelimiter = token.equals(delimiter);
            if (wasDelimiter)
                tokens_.add(isDelimiter ? "" : token);
            else if (!isDelimiter)
                tokens_.add(token);
            wasDelimiter = isDelimiter;
        }
        if (isDelimiter)
            tokens_.add("");
    }

    public int countTokens()
    {
        return tokens_.size();
    }

    public boolean hasMoreTokens()
    {
        return current_ < tokens_.size();
    }

    public boolean hasMoreElements()
    {
        return hasMoreTokens();
    }

    public Object nextElement()
    {
        return nextToken();
    }

    public String nextToken()
    {
        String token = (String) tokens_.get(current_);
        current_++;
        return token;
    }

    public void deleteTokenAt(int _tokenIndex)
    {
        tokens_.remove(_tokenIndex);
    }

     public String deleteTokenAt(int _tokenIndex, boolean returnVal)
    {
        String ret = tokens_.get(_tokenIndex);
        tokens_.remove(_tokenIndex);
        return ret;
    }

    public String toStringAndRemoveLastSlash()
    {
        StringBuffer _tmpBuf = new StringBuffer();
        for (int i = 0; i < tokens_.size(); i++)
        {
            try
            {
                tokens_.get(i + 1).toString();
            } catch (Exception e)
            {
                _tmpBuf.append(tokens_.get(i).toString());
                return _tmpBuf.toString();
            }
            _tmpBuf.append(tokens_.get(i).toString() + "\\");
        }
        return _tmpBuf.toString();
    }

    public String toString()
    {
        StringBuffer _tmpBuf = new StringBuffer();
        for (int i = 0; i < tokens_.size(); i++)
        {
            _tmpBuf.append(tokens_.get(i).toString() + "\\");
        }
        return _tmpBuf.toString();
    }

    public String toSameString()
    {
        StringBuffer _tmpBuf = new StringBuffer();
        for (int i = 0; i < tokens_.size(); i++)
        {
            _tmpBuf.append(tokens_.get(i).toString() + ",");
        }
        return _tmpBuf.toString();
    }

    public String toReportString()
    {
        StringBuffer _tmpBuf = new StringBuffer();
        for (int i = 0; i < tokens_.size(); i++)
        {
            _tmpBuf.append(tokens_.get(i).toString() + ".");
        }
        return _tmpBuf.toString();
    }

    public String[] getStringArray(String toBeTokenized, String delims)
    {
        EmptyStringTokenizer st = new EmptyStringTokenizer(toBeTokenized, delims);
        String[] tokenArray = new String[st.countTokens()];
        int i = 0;
        while (st.hasMoreTokens())
        {
            tokenArray[i++] = st.nextToken();
        }
        return tokenArray;
    }

    public String getTokenAt(int _position)
    {
        return tokens_.get(_position);
    }

    public void updateToken(String token, int position)
    {
        tokens_.set(position, token);
    }

    /**
     * Testing this class.
     *
     * @param args Not used.
     */
    public static void main(String args[])
    {
        EmptyStringTokenizer t = new EmptyStringTokenizer("MSH|^~\\&|_DSMC|MISC^Medical Information Sharing Center|KNUMC|KNUMC^Kyungpook University Medical Center|20030809153902||ADT^A03^ADT_A03|KNU_DSMS20030303194529ADT_AO3-11122211|P^A|2.4|||||KOR|8859/1|^KOREAN", "|");
        int i = 1;
        while (t.hasMoreTokens())
        {
            String token = t.nextToken();
            //System.out.println("times" + i + " - " + token);
            i++;
        }
    }
}
/**
 * HISTORY : $Log: not supported by cvs2svn $
 * HISTORY : Revision 1.5  2008/06/06 18:54:28  phadkes
 * HISTORY : Changes for License Text
 * HISTORY :
 * HISTORY : Revision 1.4  2007/10/10 19:47:04  jayannah
 * HISTORY : commented a System.out
 * HISTORY :
 * HISTORY : Revision 1.3  2007/07/19 18:33:32  jayannah
 * HISTORY : Changes for 4.0 release
 * HISTORY :
 * HISTORY : Revision 1.2  2007/05/09 19:44:17  jayannah
 * HISTORY : added update method
 * HISTORY :
 * HISTORY : Revision 1.1  2007/04/03 16:02:37  wangeug
 * HISTORY : initial loading of common module
 * HISTORY :
 * HISTORY : Revision 1.5  2006/11/29 18:25:33  jayannah
 * HISTORY : toReportString was missing during check in. Added the method and checked in
 * HISTORY :
 * HISTORY : Revision 1.4  2006/11/27 21:20:38  jayannah
 * HISTORY : *** empty log message ***
 * HISTORY :
 * HISTORY : Revision 1.3  2006/11/17 20:04:54  jayannah
 * HISTORY : Added util methods to the EmptyStringTokenizer
 * HISTORY : HISTORY : Revision 1.2 2006/10/03 15:14:29 jayannah HISTORY : changed the package names HISTORY : HISTORY :
 * Revision 1.1 2006/10/03 14:59:57 jayannah HISTORY : Created the files HISTORY :
 */
