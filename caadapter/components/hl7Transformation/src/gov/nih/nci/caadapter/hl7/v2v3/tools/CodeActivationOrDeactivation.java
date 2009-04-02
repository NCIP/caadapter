package gov.nih.nci.caadapter.hl7.v2v3.tools;

import gov.nih.nci.caadapter.common.function.DateFunction;
import gov.nih.nci.caadapter.common.util.FileUtil;

import java.io.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Apr 1, 2009
 * Time: 11:51:05 AM
 * To change this template use File | Settings | File Templates.
 */
public class CodeActivationOrDeactivation
{
    private String PROTECT_TAG_FILE_NAME = "cloned_caAdapter_by_umkis.txt";
    private String CLONED_CAADAPTER_DIR_NAME = "caAdapter_cloned_";

    private String CODE_TAG_SOURCE_DEACTIVATE = "//&umkis";
    private String CODE_TAG_TARGET_ACTIVATE =   "/*&umk*/";
    private String CODE_TAG_SOURCE_ACTIVATE =   "/*#umk*/";
    private String CODE_TAG_TARGET_DEACTIVATE = "//#umkis";

    private boolean simpleTag = false;
    CodeActivationOrDeactivation(String dirS)
    {
        doMain(dirS);
    }
    CodeActivationOrDeactivation(String dirS, boolean simpleTag)
    {
        this.simpleTag = simpleTag;
        doMain(dirS);
    }
    private void doMain(String dirS)
    {
        if ((dirS == null)||(dirS.trim().equals("")))
        {
            System.out.println("Directory is null");
            return;
        }
        dirS = dirS.trim();
        File dir = new File(dirS);
        if ((!dir.exists())||(!dir.isDirectory()))
        {
            System.out.println("Directory is not exist. : " + dirS);
            return;
        }
        String dirName = dir.getAbsolutePath();
        if (!dirName.endsWith(File.separator)) dirName = dirName + File.separator;

        File tagFile = new File(PROTECT_TAG_FILE_NAME);
        if ((tagFile.exists())&&(tagFile.isFile()))
        {
            System.out.println("Source caAdapter dir is already cloned. : ");
            return;
        }
        DateFunction df = new DateFunction();
        String currTime = df.getCurrentTime();
        dirName = dirName + CLONED_CAADAPTER_DIR_NAME + currTime.substring(0,12);
        File dirNew = new File(dirName);
        if ((dirNew.exists())&&(dirNew.isDirectory()))
        {
            System.out.println("This Directory is already exist. : " + dirName);
            return;
        }
        if (!dirNew.mkdir())
        {
            System.out.println("Directory creation Failure! : " + dirName);
            return;
        }
        dirName = dirName + File.separator;

        try
        {
            saveStringIntoFile(dirName + PROTECT_TAG_FILE_NAME, "Cloned on " + currTime + " from " + FileUtil.getWorkingDirPath());
        }
        catch(IOException ie)
        {
            System.out.println("Protect tag file writing error! : " + ie.getMessage());
            return;
        }

        try
        {
            cloneDirectory(new File(FileUtil.getWorkingDirPath()), dirNew);
        }
        catch(Exception ie)
        {
            System.out.println("Error! : " + ie.getMessage());
            return;
        }
    }

    private void cloneDirectory(File source, File target) throws IOException
    {
        if (source == null) throw new IOException("Source dir is null");
        if (target == null) throw new IOException("Target dir is null");
        if ((!source.exists())||(!source.isDirectory())) throw new IOException("Source dir is not exist.");
        if ((!target.exists())||(!target.isDirectory())) throw new IOException("Target dir is not exist.");

        File[] list = source.listFiles();
        String targetDirName = target.getAbsolutePath();
        if (!targetDirName.endsWith(File.separator)) targetDirName = targetDirName + File.separator;

        for (File file:list)
        {
            if (file.isFile())
            {
                copyFile(file, targetDirName);
                continue;
            }
            else if (!file.isDirectory()) continue;

            String dirName = file.getName();
            if (dirName.equalsIgnoreCase("cvs")) continue;
            if (dirName.equalsIgnoreCase("build")) continue;
            if (dirName.equalsIgnoreCase("dist")) continue;
            if (dirName.equalsIgnoreCase("classes")) continue;
            if (dirName.equalsIgnoreCase("gencode")) continue;
            if (dirName.equalsIgnoreCase("log")) continue;
            if ((simpleTag)&&(dirName.equalsIgnoreCase("docs"))) continue;
            if ((simpleTag)&&(dirName.equalsIgnoreCase("demo"))) continue;

            File newDir = new File(targetDirName + dirName);
            if (!newDir.mkdir()) throw new IOException("Sub-Directory creation Failure! : " + targetDirName + dirName);

            cloneDirectory(file, newDir);
        }
    }

    private void copyFile(File file, String targetDirName) throws IOException
    {

        String fileName = file.getName();
        if (fileName.equalsIgnoreCase("CodeActivationOrDeactivation.java")) return;
        if ((fileName.toLowerCase().startsWith("caadapter_hl7_src"))&&(fileName.toLowerCase().endsWith(".zip"))) return;

        boolean textTag = false;

        if (fileName.toLowerCase().endsWith(".java")) textTag = true;
        if (fileName.toLowerCase().endsWith(".txt")) textTag = true;
        if (fileName.toLowerCase().endsWith(".xml")) textTag = true;
        if (fileName.toLowerCase().endsWith(".iml")) textTag = true;
        if (fileName.toLowerCase().endsWith(".ipr")) textTag = true;
        if (fileName.toLowerCase().endsWith(".iws")) textTag = true;
        if (fileName.toLowerCase().endsWith(".properties")) textTag = true;
        if (fileName.toLowerCase().endsWith(".property")) textTag = true;
        if (fileName.toLowerCase().endsWith(".spp")) textTag = true;
        if (fileName.toLowerCase().endsWith(".bat")) textTag = true;
        if (fileName.toLowerCase().endsWith(".htm")) textTag = true;
        if (fileName.toLowerCase().endsWith(".html")) textTag = true;
        if (fileName.toLowerCase().endsWith(".xsd")) textTag = true;
        if (fileName.toLowerCase().endsWith(".fls")) textTag = true;
        if (fileName.toLowerCase().endsWith(".scs")) textTag = true;
        if (fileName.toLowerCase().endsWith(".h3s")) textTag = true;
        if (fileName.toLowerCase().endsWith(".map")) textTag = true;
        if (fileName.toLowerCase().endsWith(".xmi")) textTag = true;
        if (fileName.toLowerCase().endsWith(".vom")) textTag = true;
        if (fileName.toLowerCase().endsWith(".dtd")) textTag = true;
        if (fileName.toLowerCase().endsWith(".uml")) textTag = true;
        if (fileName.toLowerCase().endsWith(".mif")) textTag = true;
        if (fileName.toLowerCase().endsWith(".hmd")) textTag = true;
        if (fileName.toLowerCase().endsWith(".jsp")) textTag = true;
        if (fileName.toLowerCase().endsWith(".csv")) textTag = true;
        if (fileName.toLowerCase().endsWith(".js")) textTag = true;
        if (fileName.toLowerCase().endsWith(".policy")) textTag = true;
        if (fileName.toLowerCase().endsWith(".hl7")) textTag = true;


        if (!textTag)
        {

            copyBinaryFile(file, targetDirName);
            //copyBinaryFileWithURI(file, targetDirName);
            return;
        }
        System.out.println("Copy file (text)   : " + targetDirName + fileName);
        saveStringIntoFile(targetDirName + fileName, FileUtil.readFileIntoList(file.getAbsolutePath()));
    }
    private void copyBinaryFileWithURI(File file, String targetDirName) throws IOException
    {
        String uri = file.toURI().toString();
        String url = file.toURI().toURL().toString();
        //System.out.println("FFF : " + file.getAbsolutePath() + " : " + uri + " : " + url);

        if (url.toLowerCase().startsWith("file:///")) {}
        else if (url.toLowerCase().startsWith("file://")) url = url.replace("file://", "file:///");
        else if (url.toLowerCase().startsWith("file:/")) url = url.replace("file:/", "file:///");
        System.out.println("Copy file (binary) : " + url);
        String name = FileUtil.downloadFromURLtoTempFile(url);
        File r = new File(name);
        r.renameTo(new File(targetDirName + file.getName()));
    }
    private void copyBinaryFile(File file, String targetDirName) throws IOException
    {

        String fileName = file.getName();
        System.out.print("Copy file (binary) : " + targetDirName + fileName);
    	//DataInputStream distr = null;
		FileInputStream fis = null;

        FileOutputStream fos = null;
		//DataOutputStream dos2 = null;
        try
		{
		    fis = new FileInputStream(file);
			//distr = new DataInputStream(fis);

            fos = new FileOutputStream(targetDirName + fileName);
			//dos2 = new DataOutputStream(fos);

			byte nn = 0;
			boolean endSig = false;
            byte[] bytes = new byte[fis.available()];
            int n = 0;
            while(true)
			{
                int q = -1;


                q = fis.read(bytes);

                if (q != bytes.length)
                {
                    System.out.println(" : ERROR => Different length : " + q + " : " + bytes.length);
                    break;
                }
                else System.out.println(" : *** GOOD => File length : " + q);
                fos.write(bytes);
                break;
            }
		}
		catch(IOException cse)
		{
		    throw new IOException("Binary File Input failure... (IOException) " + cse.getMessage());
		}
		catch(Exception ne)
		{
            throw new IOException("Binary File Input failure... (Excep) " + ne.getMessage());
        }
        finally
        {
            if (fis != null) fis.close();
			//if (distr != null) distr.close();
			if (fos != null) fos.close();
			//if (dos2 != null) dos2.close();
        }
    }

    private void copyBinaryFileWithDataStream(File file, String targetDirName) throws IOException
    {

        String fileName = file.getName();
        System.out.println("Copy file (binary) : " + targetDirName + fileName);
    	DataInputStream distr = null;
		FileInputStream fis = null;

        FileOutputStream fos = null;
		DataOutputStream dos2 = null;
        try
		{
		    fis = new FileInputStream(file);
			distr = new DataInputStream(fis);

            fos = new FileOutputStream(targetDirName + fileName);
			dos2 = new DataOutputStream(fos);

			byte nn = 0;
			boolean endSig = false;
            byte[] bytes = new byte[fis.available()];
            int n = 0;
            while(true)
			{
                int q = -1;
                for(int i=0;i<bytes.length;i++)
                {
                    try
                    {

                        bytes[i] = distr.readByte();
                    }
                    catch(EOFException ie)
                    {
                        endSig = true;
                    }

                    if (endSig) break;
                    q = i;
                }
				if (q == (bytes.length - 1)) dos2.write(bytes);
                else for(int i=0;i<q;i++) dos2.writeByte(bytes[i]);

                //dos2.writeByte(nn);
                if (endSig) break;
            }
		}
		catch(IOException cse)
		{
		    throw new IOException("Binary File Input failure... (IOException) " + cse.getMessage());
		}
		catch(Exception ne)
		{
            throw new IOException("Binary File Input failure... (Excep) " + ne.getMessage());
        }
        finally
        {
            if (fis != null) fis.close();
			if (distr != null) distr.close();
			if (fos != null) fos.close();
			if (dos2 != null) dos2.close();
        }
    }
    private void saveStringIntoFile(String fileName, String string) throws IOException
    {
        List<String> list = new ArrayList<String>();
        list.add(string);
        saveStringIntoFile(fileName, list);
    }
    private void saveStringIntoFile(String fileName, List<String> string) throws IOException
    {
        FileWriter fw = null;

        try
        {
            fw = new FileWriter(fileName);
            for (int i=0;i<string.size();i++)
            {
                String line = string.get(i);
                if (fileName.toLowerCase().endsWith(".java"))
                {
                    line = replaceDeactivateTag(line, CODE_TAG_SOURCE_DEACTIVATE);
                    line = replaceActivateTag(line, CODE_TAG_SOURCE_ACTIVATE, CODE_TAG_TARGET_DEACTIVATE);
                }
                fw.write(line + "\r\n");
            }
        }
        catch(Exception ie)
        {
            throw new IOException("File Writing Error(" + fileName + ") : " + ie.getMessage() + ", value : " + string);
        }
        finally
        {
            if (fw != null) fw.close();
        }
    }

    private String replaceActivateTag(String line, String from, String to)
    {
        int idx = line.toLowerCase().indexOf(from);

        if (idx < 0) return line;
        else if (idx == 0) return to + line.substring(from.length());

        return line.substring(0, idx) + to + line.substring(idx + from.length());
    }
    private String replaceDeactivateTag(String line, String from)
    {
        int idx = line.toLowerCase().indexOf(from);

        if (idx < 0) return line;
        else if (idx == 0) return line.substring(from.length()) + "     " +from;

        return line;
    }

    public static void main(String[] arg)
    {
        new CodeActivationOrDeactivation("c:\\clone_caAdapter");
    }

}
