/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.hl7.v2v3.tools;

import gov.nih.nci.caadapter.common.function.DateFunction;
import gov.nih.nci.caadapter.common.util.FileUtil;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;

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
    private String SETUP_FILE_NAME = "kisung_seup.properties";
    private String CODE_TAG_SOURCE_DEACTIVATE = "//&umkis";

    private String CODE_TAG_TARGET_ACTIVATE =   "/*&umk*/";
    private String CODE_TAG_SOURCE_ACTIVATE =   "/*#umk*/";
    private String CODE_TAG_TARGET_DEACTIVATE = "//#umkis";

    private List<String> setupLines = null;
    private String downloadURL = null;

    private boolean schemaTag = false;
    CodeActivationOrDeactivation(String dirS)
    {
        doMain(dirS);
    }
    CodeActivationOrDeactivation(String dirS, boolean schemaTag)
    {
        this.schemaTag = schemaTag;
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

        downloadURL = FileUtil.getPropertyFromComponentPropertyFile("conf/v3xsdFilePath.properties","setupDownloadURL");
        if ((downloadURL == null)||(downloadURL.trim().equals("")))
        {
            System.out.println("Download URL cannot be found : ");
            return;
        }

        String res = downloadFileFromURL(SETUP_FILE_NAME);
        if ((res == null)||(res.trim().equals("")))
        {
            System.out.println("Code download server is not working. : " + downloadURL);
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

        //String setupFile = res;

        try
        {
            setupLines = FileUtil.readFileIntoList(res);
        }
        catch(IOException ie)
        {
            System.out.println("Reading "+SETUP_FILE_NAME+" file failure : " + ie.getMessage());
            return;
        }

        if ((setupLines == null)||(setupLines.size() == 0))
        {
            System.out.println(SETUP_FILE_NAME+" file is empty : ");
            return;
        }

        List<String> list = getPropertyList("Directories");

        if (list == null)
        {
            try
            {
                cloneDirectory(new File(FileUtil.getWorkingDirPath()), dirNew);
                String metaInf = FileUtil.searchDir("META-INF", dirNew);
                if (metaInf != null)
                {
                    if (!metaInf.endsWith(File.separator)) metaInf = metaInf + File.separator;
                    File dirNew2 = new File(metaInf + "hl7_home");
                    if (dirNew2.mkdir()) cloneDirectory(new File(FileUtil.searchDir("hl7_home")), dirNew2);
                    File dirNew3 = new File(metaInf + "conf");
                    if (dirNew3.mkdir()) cloneDirectory(new File(FileUtil.searchDir("conf")), dirNew3);
                }
            }
            catch(Exception ie)
            {
                System.out.println("Error! (1) : " + ie.getMessage());
            }
            return;
        }

        File top = new File(FileUtil.getWorkingDirPath());
        String topDirName = list.get(0);
        while(topDirName != null)
        {
            topDirName = topDirName.trim();
            if (topDirName.equals("")) break;
            if (top==null)
            {
                System.out.println("Top directory cannot be found : ");
                return;
            }
            String name = top.getName();
            if (name.equalsIgnoreCase(topDirName)) break;
            top = top.getParentFile();
        }

        File[] dirs = top.listFiles();
        for(File dirP:dirs)
        {
            if (!dirP.isDirectory()) continue;
            boolean isTheDir = false;
            for (int i=1;i<list.size();i++)
            {
                String dirV = list.get(i);
                if (dirV.equals(dirP.getName())) isTheDir = true;
            }
            if (!isTheDir) continue;
            String dirNewName = dirNew.getAbsolutePath();
            if (!dirNewName.endsWith(File.separator)) dirNewName = dirNewName + File.separator;
            dirNewName = dirNewName + dirP.getName();
            File dirNewSub = new File(dirNewName);
            if (!dirNewSub.exists())
            {
                if (!dirNewSub.mkdir())
                {
                    System.out.println("Sub Directroy creation failure : " + dirNewName);
                    return;
                }
            }
            try
            {
                cloneDirectory(dirP, dirNewSub);
                String metaInf = FileUtil.searchDir("META-INF", dirNewSub);
                if (metaInf != null)
                {
                    if (!metaInf.endsWith(File.separator)) metaInf = metaInf + File.separator;
                    File dirNew2 = new File(metaInf + "hl7_home");
                    if (dirNew2.mkdir()) cloneDirectory(new File(FileUtil.searchDir("hl7_home")), dirNew2);
                    File dirNew3 = new File(metaInf + "conf");
                    if (dirNew3.mkdir()) cloneDirectory(new File(FileUtil.searchDir("conf")), dirNew3);
                }
            }
            catch(Exception ie)
            {
                System.out.println("Error! : " + ie.getMessage());
                ie.printStackTrace();
                return;
            }
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

        List<String> listA = getPropertyList("Added");
        for(String str:listA)
        {
            if (str == null) continue;
            StringTokenizer st = new StringTokenizer(str, ",");
            String dirN = null;
            String subN = null;
            String fileN = null;
            int n = 0;
            while(st.hasMoreTokens())
            {
                String tok = st.nextToken();
                //System.out.println("FFF : " + tok);
                if (n == 0) dirN = tok.trim();
                if (n == 1) subN = tok.trim();
                if (n == 2) fileN = tok.trim();
                n++;
            }
            if ((!subN.equals(""))&&(fileN == null))
            {
                fileN = subN;
                subN = "";
            }
            if (dirN.equals("")) continue;
            if (fileN.equals("")) continue;
            String targetDD = targetDirName;

            if (!File.separator.equals("/")) dirN = dirN.replace("/", File.separator);
            if (!dirN.endsWith(File.separator)) dirN = dirN + File.separator;
            if (targetDirName.endsWith(dirN))
            {
                if ((subN!=null)&&(!subN.equals("")))
                {
                    targetDD = targetDirName + subN;
                    File dirNewSub = new File(targetDD);
                    targetDD = targetDD + File.separator;
                    if (!dirNewSub.exists())
                    {
                        if (!dirNewSub.mkdirs()) throw new IOException("Sub Directroy creation failure : " + targetDD);
                        //else System.out.println("FFF Create dir : " + targetDD);
                    }

                }

                File path = new File(targetDD + fileN);
                if ((!path.exists())||(!path.isFile()))
                {
                    copyFile(path, targetDD);
                }
            }

        }

        List<String> avoidDir = getPropertyList("SkipDir");

        for (File file:list)
        {
            if (file.isHidden()) continue;
            if (file.isFile())
            {
                copyFile(file, targetDirName);
                continue;
            }
            else if (!file.isDirectory()) continue;

            String dirName = file.getName();

            char cc = dirName.toCharArray()[0];
            int inFF = (int)(byte) cc;
            if (inFF < 65) continue;
            if (inFF > 122) continue;
            if ((inFF < 97)&&(inFF > 90)) continue;

            boolean avoid = false;
            for (String str:avoidDir)
            {
                if (str == null) continue;
                if (dirName.equalsIgnoreCase(str)) avoid = true;
            }
            if (avoid) continue;

            File newDir = new File(targetDirName + dirName);
            if (!newDir.mkdir()) throw new IOException("Sub-Directory creation Failure! : " + targetDirName + dirName);

            cloneDirectory(file, newDir);
        }
    }
    private void copyFile(File file, String targetDirName) throws IOException
    {

        String fileName = file.getName();
        if (FileUtil.isTemporaryFileName(fileName)) return;
        //if (fileName.equalsIgnoreCase("CodeActivationOrDeactivation.java")) return;
        if ((fileName.toLowerCase().startsWith("caadapter"))&&(fileName.toLowerCase().indexOf("_src") > 0)&&(fileName.toLowerCase().endsWith(".zip"))) return;
        if ((fileName.toLowerCase().startsWith("caadapter"))&&(fileName.toLowerCase().indexOf("_bin") > 0)&&(fileName.toLowerCase().endsWith(".zip"))) return;

        List<String> textExtensions = getPropertyList("TextExtension");
        boolean textTag = false;
        for (String str:textExtensions)
        {
            if (str == null) continue;
            if (fileName.toLowerCase().endsWith(str)) textTag = true;
        }

        boolean downloadTag = false;

        if ((!file.exists())||(!file.isFile()))
        {
            downloadTag = true;
        }
        else
        {

            List<String> replaceFiles = getPropertyList("Replace");
            for (String str:replaceFiles)
            {
                if (str == null) continue;
                int idx = str.indexOf(",");
                String nameF = str;
                String nameD = null;
                if (idx > 0)
                {
                    nameF = str.substring(0, idx).trim();
                    nameD = str.substring(idx+1).trim();
                    if (nameD.equals("")) nameD = null;
                }
                if (nameD == null)
                {
                    if (fileName.equals(nameF)) downloadTag = true;
                }
                else
                {
                    if (fileName.equals(nameF))
                    {
                        if (targetDirName.indexOf(nameD) > 0) downloadTag = true;
                    }
                }
            }
//            if (fileName.equals("MapProcessor.java")) downloadTag = true;
//            if (fileName.equals("DatatypeProcessor.java")) downloadTag = true;
//            if (fileName.equals("XMLElement.java")) downloadTag = true;
//            if (fileName.equals("StringFunction.java")) downloadTag = true;
//            if (fileName.equals("MapProcessorHelper.java")) downloadTag = true;
//            //if (fileName.equals("caAdapterTransformationService.java")) downloadTag = true;
//            if (fileName.equals("mif.zip")) downloadTag = true;
//            if (fileName.equals("Attribute.java"))
//            {
//                if (targetDirName.indexOf("transformation") > 0) downloadTag = true;
//            }
//            if (fileName.equals("web.xml")) downloadTag = true;
//            if (fileName.equals("AddNewScenario.java"))
//            {
//                List<String> list = new ArrayList<String>();
//                String d = targetDirName + "stellar" + File.separator;
//                File dD = new File(d);
//                if ((!dD.exists())||(!dD.isDirectory()))
//                {
//                    if (!dD.mkdirs())
//                    {
//                        System.out.println("##### New Directory creation failure : " + d);
//                        return;
//                    }
//                }
//                list.add("CaAdapterUserWorks.java");
//                list.add("CaadapterWSUtil.java");
//                list.add("DeleteOutputFile.java");
//                list.add("DosFileHandler.java");
//                list.add("FileUploaderWS.java");
//                list.add("GeneralUtilitiesWS.java");
//                list.add("ManageCaadapterWSUser.java");
//                list.add("MultipartRequest.java");
//                list.add("ScenarioFileRegistration.java");
//                list.add("TestIPAddress.java");
//                list.add("TransformationServiceOnWeb.java");
//                list.add("MenuStart.java");
//                list.add("TransformationServiceMain.java");
//                list.add("TransformationServiceWithWSDL.java");
//
//
//                for(String line:list)
//                {
//                    File f = new File(d + line);
//                    copyFile(f, d);
//                }
//            }
        }



        if (!textTag)
        {
            if (downloadTag)
            {
                System.out.print("-- Downloaded Binary File :  : " + fileName);
                String tempFile = downloadFileFromURL(fileName);
                if ((tempFile != null)&&(!tempFile.trim().equals("")))
                {
                    file = new File(tempFile);

                    System.out.println(" => successful");
                }
                else
                {
                    System.out.println("");
                    System.out.println("##### Binary file download failure : " + fileName);
                }
            }

            copyBinaryFile(file, targetDirName, fileName);
            //copyBinaryFileWithURI(file, targetDirName);
            return;
        }

        String oriFile = "";
        if (downloadTag)
        {
            System.out.print("-- Downloaded Text File : " + fileName);
            String tempFile = downloadFileFromURL(fileName);

            if ((tempFile == null)||(tempFile.equals("")))
            {
                System.out.println("");
                System.out.println("##### ERROR Text File Download failure : " + fileName);
                return;
            }
            else
            {
                System.out.println(" => successful");
                oriFile = tempFile;
            }
        }
        else oriFile = file.getAbsolutePath();

        System.out.println("Copy file (text)   : " + targetDirName + fileName);
        saveStringIntoFile(targetDirName + fileName, FileUtil.readFileIntoList(oriFile));
    }
    private String downloadFileFromURL(String fileName)
    {
        //String[] urls = new String[] {"http://10.1.1.61:8080/file_exchange/",
        //                              "http://155.230.210.233:8080/file_exchange/"};
        String[] urls = new String[] {downloadURL};
        String tempFile = null;
        for(int i=0;i<urls.length;i++)
        {
            try
            {
                tempFile = FileUtil.downloadFromURLtoTempFile(urls[i] + fileName);
            }
            catch(IOException ie)
            {
                tempFile = null;
            }
            if ((tempFile != null)&&(!tempFile.trim().equals("")))
            {
                tempFile = tempFile.trim();
                break;
            }
        }
        return tempFile;
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
        copyBinaryFile(file, targetDirName, file.getName());
    }
    private void copyBinaryFile(File file, String targetDirName, String targetFileName) throws IOException
    {

        String fileName = file.getName();
        System.out.print("Copy file (binary) : " + targetDirName + targetFileName);
        //DataInputStream distr = null;
        FileInputStream fis = null;

        FileOutputStream fos = null;
        //DataOutputStream dos2 = null;
        try
        {
            fis = new FileInputStream(file);
            //distr = new DataInputStream(fis);

            fos = new FileOutputStream(targetDirName + targetFileName);
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
        if ((schemaTag)&&(fileName.equalsIgnoreCase("schemas.zip"))) extractSchemaZipFile(file, targetDirName);
    }
    private void extractSchemaZipFile(File file, String targetDirName) throws IOException
    {
        File f = new File(targetDirName + "schemas");
        if (!f.mkdir()) throw new IOException("Schema directory creation failure : " + targetDirName);

        String schemaDir = targetDirName + "schemas" + File.separator;

        ZipFile xsdZipFile=new ZipFile(file);
        if (xsdZipFile == null) throw new IOException("Schema zip file object creation failure : " + targetDirName);

        Enumeration<? extends ZipEntry> enumer = xsdZipFile.entries();

        while(enumer.hasMoreElements())
        {
            ZipEntry entry = enumer.nextElement();
            String entryName = entry.getName();
            if (!File.separator.equals("/"))
            {
                entryName = entryName.replace("/", File.separator);
            }
            String name = schemaDir + entryName;
            if (entry.isDirectory())
            {
                File fl = new File(name);
                if (!fl.mkdirs()) throw new IOException("Schema zip entry directory creation failure : " + schemaDir + entryName);
                else System.out.println("    Success sub directory creation for Schema zip entry 1 : " + schemaDir + entryName);

                continue;
            }

            int idx = name.lastIndexOf(File.separator);
            String par = name.substring(0, idx);

            File fl = new File(par);
            if ((!fl.exists())||(!fl.isDirectory()))
            {
                if (!fl.mkdirs()) throw new IOException("Schema sub directory creation failure : " + par);
                else System.out.println("    Success sub directory creation for Schema zip entry 2 : " + par);
            }


            FileOutputStream fos = null;
            //DataOutputStream dos2 = null;
            int cnt =0;
            try
            {
                //byte[] bytes = new byte[(int)entry.getSize()];
                InputStream stream = xsdZipFile.getInputStream(entry);
                //byte[] bytes = new byte[stream.available()];
                //cnt = stream.read(bytes);
                //if (cnt != bytes.length) throw new IOException("Unmatched entry size ("+cnt+" : "+bytes.length+") : " + name);
                fos = new FileOutputStream(name);
                while(true)
                {
                    int bt = 0;
                    try
                    {
                        bt = stream.read();
                    }
                    catch(java.io.EOFException ee)
                    {
                        break;
                    }
                    if (bt < 0) break;
                    fos.write(bt);
                }
            }
            catch(IOException cse)
            {
                throw new IOException("Schema zip entry write failure... (IOException) " + cse.getMessage());
            }
            catch(Exception ne)
            {
                ne.printStackTrace();
                throw new IOException("Schema zip entry write failure... (Excep) " + ne.getMessage());
            }
            finally
            {
                if (fos != null) fos.close();
            }
            System.out.println("    Success writing Schema zip entry : " + name);

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
                //if (fileName.equals("run.bat"))
                if (fileName.toLowerCase().endsWith("run.bat"))
                {
                    if (line.toLowerCase().startsWith("java ")) line = "java -Xmx150000000" + line.substring(4);
                }
                //if (fileName.equals("build.properties"))
                if (fileName.toLowerCase().endsWith("build.properties"))
                    line = modifyBuildProperties(line);
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

    private String modifyBuildProperties(String line)
    {
        String ln = line.trim();

        if (ln.startsWith("project."))
        {
            if ((ln.indexOf("MTS") > 0)||(ln.indexOf("HL7") > 0)) {}
            else
            {
                if (ln.startsWith("project.release.version")) line = "project.release.version=caadapter_HL7";
                if (ln.startsWith("project.docs.home")) line = "project.docs.home=docs/4.3";
                if (ln.startsWith("project.user.guide")) line = "project.user.guide=caAdapter_HL7_MTS_v4.3_UsersGuide";
                if (ln.startsWith("project.installation.guide")) line = "project.installation.guide=caAdapter HL7 MTS v4.3 Installation Guide.pdf";
                if (ln.startsWith("proejct.online.help")) line = "proejct.online.help=help_4.3.zip";
            }
        }
        if (ln.startsWith("caadapter.release.mms.only")) line = "caadapter.release.mms.only=false";
        if (ln.startsWith("caadapter.release.mms.gme.only")) line = "caadapter.release.mms.gme.only=false";
        if (ln.startsWith("caadapter.release.hl7.only")) line = "caadapter.release.hl7.only=true";
        if (ln.startsWith("caadapter.release.ws.include")) line = "caadapter.release.ws.include=true";
        if (ln.startsWith("caadapter.release.all.modules")) line = "caadapter.release.all.modules=false";

        //System.out.println("XXXX : " + line);
        return line;
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
        String line2 = line.trim();
        int idx = line2.toLowerCase().indexOf(from);

        if (idx < 0) return line;
        else if (idx == 0)
        {
            String tTag = CODE_TAG_SOURCE_DEACTIVATE + ":INSERT=";
            if (line2.startsWith(tTag))
            {
                String res = downloadFileFromURL(line2.substring(tTag.length()));
                if ((res != null)&&(!res.trim().equals("")))
                {
                    String rr = FileUtil.readFileIntoString(res);
                    if ((rr != null)&&(!rr.trim().equals(""))) return rr;
                }
            }
            else return line2.substring(from.length()) + "     " +from;
        }

        return line;
    }

    private List<String> getPropertyList(String key)
    {
        if ((key==null)||(key.trim().equals(""))) return null;
        key = key.trim();
        boolean found = false;
        List<String> list = new ArrayList<String>();
        for(String line:setupLines)
        {
            if ((line==null)||(line.trim().equals(""))) continue;
            line = line.trim();
            if (line.startsWith("//")) continue;
            if (line.startsWith("#")) continue;
            if (line.toLowerCase().startsWith("&start:"+key.toLowerCase()))
            {
                found = true;
                int idx = line.indexOf("=>");
                String prop = null;
                if(idx > 0)
                {
                    String subLine = line.substring(idx + 2);
                    if (!subLine.trim().equals(""))
                    {
                        prop = subLine.trim();
                    }
                }
                list.add(prop);
                continue;
            }
            if (line.toLowerCase().startsWith("&end:"+key.toLowerCase()))
            {
                found = false;
                continue;
            }
            if (found) list.add(line);
        }
        if (list.size() <= 1) return null;
        return list;
    }

    public static void main(String[] arg)
    {
        new CodeActivationOrDeactivation("c:\\clone_caAdapter");
        //new CodeActivationOrDeactivation("c:\\clone_caAdapter", true);
    }

}
