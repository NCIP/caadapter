/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.hl7.v2v3.tools;

import gov.nih.nci.caadapter.common.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: kium
 * Date: Jan 13, 2009
 * Time: 10:32:26 AM
 * To change this template use File | Settings | File Templates.
 */
public class CharacterCorrectionForHL7Specification
{
    CharacterCorrectionForHL7Specification(String filename)
    {
        process(filename);
    }

    private void process(String filename)
    {
        if (filename==null) return;
        filename = filename.trim();
        if (filename.equals("")) return;
        File file = new File(filename);
        if (!file.exists()) return;
        if (!file.isDirectory()) return;

        File[] files = file.listFiles();
        for(File fil:files)
        {
            String filname = fil.getAbsolutePath().trim();
            if (fil.isDirectory()) process(filname);

            if (!fil.isFile()) continue;
            if (!((filname.toLowerCase().endsWith(".htm"))||(filname.toLowerCase().endsWith(".html")))) continue;
            correctFile(filname);
        }
    }

    private void correctFile(String filename)
    {
        if ((filename.indexOf("UV") > 0)||
            (filename.indexOf("_DM") > 0)||
            (filename.indexOf("_RM") > 0)||
            (filename.indexOf("_HD") > 0)||
            (filename.indexOf("_IN") > 0)||
            (filename.indexOf("_MT") > 0))
        {
            System.out.println("Skip correction : " + filename + " => Skipped");
            return;
        }
        else System.out.print("Search correction : " + filename);
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

        List<String> list2 = new ArrayList<String>();
        boolean changed = false;
        for(String str:list)
        {
            String line = "";
            /*
            char ch0 = ' ';
            char chE = ' ';
            int in0 = -1;
            int in1 = -1;
            for (char ch1:str.toCharArray())
            {
                in1 = (int) ch1;
                chE = ch1;
                if (in0 >= 0)
                {
                    //in0 = in1;
                    //ch0 = ch1;
                    //continue;

                    if ((in0 == 160)&&(in1 == 60))
                    {
                        changed = true;
                        ch0 = (char)32;
                    }
                    line = line + ch0;
                }
                in0 = in1;
                ch0 = ch1;
            }
            line = line + chE;
            */
            for (char ch:str.toCharArray())
            {
                int in = (int) ch;

                if ((in > 127)&&(in < 256))
                {
                    changed = true;
                    ch = (char)32;
                }
                line = line + ch;

            }
            list2.add(line);
        }

        if (changed)
        {

            FileWriter fw = null;
            System.out.print(" Saving the Corrections ");
            try
            {
                fw = new FileWriter(filename);
                for(String str:list2) fw.write(str + "\r\n");

                fw.close();
                System.out.println(" => Finished");
            }
            catch(Exception ie)
            {
                System.out.println("\nFile Writing Error(" + filename + ") : " + ie.getMessage());
            }

        }
        else System.out.println(" => Not corrected");
    }

    public static void main(String[] args)
    {
        String file = "C:\\hl7\\tempTT";
        new CharacterCorrectionForHL7Specification(file);
    }
}
