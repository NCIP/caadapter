/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.hl7.v2v3.tools;

import gov.nih.nci.caadapter.common.util.FileUtil;

import java.util.List;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: kium
 * Date: Jan 12, 2009
 * Time: 1:54:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class CheckWeirdCharacter
{
    CheckWeirdCharacter(String filename)
    {
        //int limit = 30000;
        List<String> list = null;
        try
        {
            list = FileUtil.readFileIntoList(filename);
        }
        catch(IOException ie)
        {
            System.out.println("IOException : " + ie.getMessage());
            return;
        }

        //int n = 0;

        char chr = ' ';
        int in = -1;
        //int n = 0;
        for(String str:list)
        {
            str = str+"\n";
            for (char ch:str.toCharArray())
            {
                //n++;
                //System.out.print("" + ch);
                in = (int) ch;
                boolean cTag = false;
                if ((in <32)||(in>127))
                {
                    if ((in==10)||(in==13)) {}
                    else
                    {
                        chr = ch;
                        cTag = true;
                        //break;
                    }
                }
                if (cTag)
                {
                    char cc = ("\t").toCharArray()[0];
                    int cc1 = (int) cc;
                    if (in == cc1) System.out.print("<<TAB>>");
                    else System.out.print("<<" + in + ">>");
                }
                else System.out.print("" + ch);
                //if (n > 30000) break;
            }
        }
        //if (cTag) System.out.println("Not normal : " + chr + "; " + in);
        //else  System.out.println("Normal");
    }

    public static void main(String[] args)
    {
        //String file = "C:\\hl7\\V3_2008\\disk1\\Edition2008\\help\\backbone\\backbone.htm";
        //String file = "C:\\hl7\\tempTT\\disk1\\Edition2008\\infrastructure\\datatypes\\datatypes.htm";
        String file1 = "C:\\project\\v2v3Instance\\V2Meta_to_V3\\ADT_A03.hl7";

        String file2 = "C:\\project\\v2v3Instance\\V2Meta_to_V3\\ADT_A03_02_2.hl7";
        new CheckWeirdCharacter(file2);
        new CheckWeirdCharacter(file1);

        String str1 = FileUtil.readFileIntoString(file1);
        String str2 = FileUtil.readFileIntoString(file2);
        char[] chr2 = str1.toCharArray();
        char[] chr1 = str2.toCharArray();

        int n = 0;
        for(char ch1:chr1)
        {
            int in1 = (int) ch1;
            int in2 = (int) chr2[n];
            System.out.print("" + ch1);
            if (in1 != in2) System.out.print("<<Here:" + chr2[n] + ":" + in1 + ":" + in2 + ">>");
            n++;
        }
    }

}
