package gov.nih.nci.caadapter.hl7.transformation;

import gov.nih.nci.caadapter.common.util.FileUtil;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.util.GeneralUtilities;
import gov.nih.nci.caadapter.common.ApplicationException;
import gov.nih.nci.caadapter.common.MessageResources;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.common.validation.ValidatorResult;
import gov.nih.nci.caadapter.hl7.v2v3.tools.SchemaDirUtil;
import gov.nih.nci.caadapter.hl7.v2v3.tools.XmlReorganizingTree;
import gov.nih.nci.caadapter.hl7.v2v3.tools.XmlTreeBrowsingNode;
import gov.nih.nci.caadapter.hl7.v2v3.tools.ZipUtil;
import gov.nih.nci.caadapter.hl7.validation.complement.ReorganizingForValidating;
import gov.nih.nci.caadapter.hl7.validation.complement.XSDValidationTree;
import gov.nih.nci.caadapter.hl7.validation.XMLValidator;
import gov.nih.nci.caadapter.hl7.mif.MIFClass;
import gov.nih.nci.caadapter.hl7.transformation.data.XMLElement;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipEntry;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Sep 9, 2009
 * Time: 2:58:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class ControlMessageRelatedUtil
{
    public static XmlReorganizingTree searchControlMessage(MIFClass mifClass, String controlFile)
    {
        XmlReorganizingTree controlMessageTemplate = null;

        //ZipUtil zipUtil = null;

        if (mifClass == null) return null;
        String mifName = mifClass.getName();
        if ((mifName == null)||(mifName.trim().equals(""))) return null;
        String mifType = mifClass.getMessageType();
        if ((mifType == null)||(mifType.trim().equals(""))) return null;
        mifName = mifName.trim();
        mifType = mifType.trim();

        //System.out.println("WWWW-1 : " + controlFile);
        String dirS = (new SchemaDirUtil()).getV3XsdFilePath(mifClass.getCopyrightYears());
        if (dirS == null) return null;
        if (!dirS.endsWith(File.separator)) dirS = dirS + File.separator;
        dirS = dirS + "schemas" + File.separator + Config.V3_XSD_MULTI_CACHE_SCHEMAS_DIRECTORY_NAME;

        File dir = new File(dirS);
        if ((!dir.exists())||(!dir.isDirectory()))
        {
            String dirS2 = null;
            String dirStr = dirS;
            while(true)
            {
                //System.out.println("WWWW-1 -01 : " + dirStr);
                if ((dirStr.endsWith("/"))||(dirStr.endsWith(File.separator))) dirStr = dirStr.substring(0,dirStr.length()-1);
                int idx = -1;
                for(int k=dirStr.length();k>0;k--)
                {
                    String achar = dirStr.substring(k-1, k);
                    if ((achar.equals("/"))||(achar.equals(File.separator)))
                    {
                        idx = k;
                        break;
                    }
                }
                if (idx > 0) dirStr = dirStr.substring(0, idx);
                else break;
                File dirQ = new File(dirStr);
                if ((!dirQ.exists())||(!dirQ.isDirectory())) continue;
                File[] fList = dirQ.listFiles();
                for(File ff:fList)
                {

                    if (!ff.isFile()) continue;
                    if ((ff.getName().toLowerCase().trim().startsWith("schema"))&&(ff.getName().toLowerCase().trim().endsWith(".zip")))
                    {
                        dirS2 = ff.getAbsolutePath();
                        break;
                    }
//                    if (!ff.getName().toLowerCase().trim().endsWith(".zip")) continue;
//                    //ZipUtil zipUtil = null;
//                    try
//                    {
//                        zipUtil = new ZipUtil(ff.getAbsolutePath());
//                    }
//                    catch(IOException ie)
//                    {
//                        continue;
//                    }
//                    List<ZipEntry> entries = zipUtil.searchEntryWithNameAsPart(mifClass.getMessageType(), ".xsd");
//
//                    System.out.println("WWWW 02 : " + entries.size());
//                    if ((entries != null)&&(entries.size() == 1))
//                    {
//                        //schemaZipFileName = ff.getAbsolutePath();
//                        //break;
//                    }
//                    else continue;
//
//                    String copiedSchemaFile = null;
//                    try
//                    {
//                        copiedSchemaFile = zipUtil.copyIncludedFiles(entries.get(0));
//                     }
//                    catch(IOException ie)
//                    {
//                        System.out.println("WWWW 03 : IOException : " + ie.getMessage());
//                        continue;
//                    }
//                    if ((copiedSchemaFile == null)||(copiedSchemaFile.trim().equals(""))) continue;
//                    File pDir = (new File(copiedSchemaFile)).getParentFile();
//
//                    dirS2 = pDir.getAbsolutePath();

                }
                if (dirS2 != null) break;
            }

            if (dirS2 == null)
            {
                //System.out.println("WWWW 03-1");
                return null;
            }
            dirS = dirS2;
        }
        else dirS = dir.getAbsolutePath();

        //System.out.println("WWWW 04 : " + dirS);

        if (!dirS.endsWith(File.separator)) dirS = dirS + File.separator;

        if ((controlFile != null)&&(!controlFile.trim().equals("")))
        {
            //if (schemaZipFileName != null)
            //    controlMessageTemplate = searchControlMessageFile(mifName, mifType, (new File(controlFile)), schemaZipFileName);
            //else
                controlMessageTemplate = searchControlMessageFile(mifName, mifType, (new File(controlFile)), dirS);

            if (controlMessageTemplate == null)
            {
                System.err.println("Invalid Control message File : " + controlFile);
                return null;
            }
            else
            {
                //if (zipUtil != null) controlMessageTemplate.setZipUtil(zipUtil);
                return controlMessageTemplate;
            }
        }

        String dirName = FileUtil.searchProperty("ControlMessageTemplateDirectory");

        if ((dirName == null)||(dirName.trim().equals("")))
        {
            //dirName = FileUtil.getWorkingDirPath() + "\\demo\\contTemplate";
            return null;
        }

        File f = new File(dirName);
        if ((!f.exists())||(!f.isDirectory())) return null;

        File[] fList = f.listFiles();

        for (File ff:fList)
        {
            XmlReorganizingTree xrt = null;

            //System.out.println("WWWW 05 : " + ff.getAbsolutePath());

            //if (schemaZipFileName != null)
            //    xrt = searchControlMessageFile(mifName, mifType, (new File(controlFile)), schemaZipFileName);
            //else
                xrt = searchControlMessageFile(mifName, mifType, ff, dirS);

            if (xrt != null)
            {
                //System.out.println("WWWW 06-1 : " + dirS);
                controlMessageTemplate = xrt;
                break;
            }
            //else System.out.println("WWWW 06-2 : " + dirS);
        }
        if (controlMessageTemplate == null)
        {
            System.err.println("Invalid Control message File : " + controlFile);
            //if (zipUtil != null) zipUtil.deleteDirectory();
            return null;
        }
        //if (zipUtil != null) controlMessageTemplate.setZipUtil(zipUtil);
        return controlMessageTemplate;
    }

    private static XmlReorganizingTree searchControlMessageFile(String mifName, String mifType, File ff, String dirS)
    {
        ZipUtil zipUtil = null;
        ZipEntry zipEntry = null;

        File fileZ = new File(dirS);

        if (!fileZ.exists()) return null;
        if (fileZ.isFile())
        {
            if ((fileZ.isFile())||(dirS.toLowerCase().endsWith(".zip")))
            {
                try
                {
                    zipUtil = new ZipUtil(fileZ.getAbsolutePath());
                }
                catch(IOException ie)
                {
                    return null;
                }
                //List<ZipEntry> list = zipUtil.searchEntryWithNameAsPart(mifType, ".xsd");
                //if ((list == null)||(list.size() != 1)) return null;
                //zipEntry = list.get(0);
            }
            else return null;
        }
        else dirS = fileZ.getAbsolutePath();
        //System.out.println("WWWW 07 : " + dirS);
        if (!dirS.endsWith(File.separator)) dirS = dirS + File.separator;
        boolean wasFound = false;
        XmlReorganizingTree controlMessageTemplate = null;
        String type = null;
        while(true)
        {
            if (ff == null) break;
            if ((!ff.exists())||(!ff.isFile())) break;

            try
            {
                XmlReorganizingTree xrt = new XmlReorganizingTree(ff.getAbsolutePath());
                DefaultMutableTreeNode dNode = xrt.getHeadNode();
                if (dNode == null) break;
                String head = xrt.getNodeName(dNode);
                if ((head == null)||(head.trim().equals(""))) break;
                type = head;
            }
            catch(ApplicationException ae)
            {
                break;
            }

            //System.out.println("WWWW 09 : Type : " + type);

            String schemaF = null;
            if (zipUtil != null)
            {
                List<ZipEntry> list = zipUtil.searchEntryWithNameAsPart(type, ".xsd");
                if ((list == null)||(list.size() != 1)) break;
                zipEntry = list.get(0);
                try
                {
                    schemaF = zipUtil.copyIncludedFiles(zipEntry);
                }
                catch(IOException ie)
                {
                    //System.out.println("WWWW 10-1 : " + ie.getMessage());
                    break;
                }
            }
            else schemaF = dirS + type + ".xsd";
            //System.out.println("WWWW 11 : " + schemaF);

            File f2 = new File(schemaF);
            if ((!f2.exists())||(!f2.isFile())) break;
            //else System.out.println("WWWW 11-1 : " + schemaF);

            ReorganizingForValidating rfv = null;
            String schemaFileNameL = f2.getAbsolutePath();
            try
            {
                rfv = new ReorganizingForValidating(ff.getAbsolutePath(), schemaFileNameL);
            }
            catch(ApplicationException ae)
            {
                System.out.println("ApplicationException in ReorganizingForValidating object during TransformationService : " + ae.getMessage());
                break;
            }

            XSDValidationTree xsdTree = rfv.getXSDTree();

            DefaultMutableTreeNode sNode = xsdTree.getHeadNode();

            boolean found2 = false;
            while(true)
            {
                sNode = sNode.getNextNode();
                if (sNode == null) break;

                String nodeName = xsdTree.getAttributeValueWithName(sNode);
                String typeName = xsdTree.getAttributeValueWithType(sNode);

                if (nodeName == null) continue;

                if (nodeName.equalsIgnoreCase(mifName))
                {
                    if ((typeName.indexOf(mifType) >= 0)&&(typeName.toLowerCase().endsWith("." + nodeName.toLowerCase()))) found2 = true;
                }
                //System.out.println("WWWW 12 : nodeName=" + nodeName + ", typeName=" + typeName + ", found2=" + found2);
                if (found2) break;
            }

            if (!found2) break;

            controlMessageTemplate = rfv.getXMLTree();
            try
            {
                controlMessageTemplate.setSchemaFileName(schemaFileNameL);
            }
            catch(ApplicationException ae)
            {
                System.err.println("controlMessageTemplate ERROR at the last stage (controlMessageTemplate.setSchemaFileName(schemaFileNameL)) : " + ae.getMessage());
                break;
            }
            //System.out.println("WWWW 13 : ");
            wasFound = true;
            break;
        }
        if (!wasFound) return null;
        if (zipUtil != null)
        {
            if (zipEntry == null) return null;
//            {
//                List<ZipEntry> list = zipUtil.searchEntryWithNameAsPart(mifType, ".xsd");
//                if ((list == null)||(list.size() != 1)) return null;
//                zipEntry = list.get(0);
//                try
//                {
//                    zipUtil.copyIncludedFiles(zipEntry);
//                }
//                catch(IOException ie)
//                {
//                    System.out.println("WWWW 14 : " + ie.getMessage());
//                    return null;
//                }
//            }
            //System.out.println("WWWW 15 : ");
            controlMessageTemplate.setZipUtil(zipUtil);
        }
        return controlMessageTemplate;
    }

    public static Object[] excuteXSDValidationForTransformationService(ValidatorResults validatorsToShow, int messageCount, int i, String v3Message, ZipOutputStream zipOut, OutputStreamWriter writer, String schemaFileName, MIFClass mifClass, XmlReorganizingTree controlMessageTemplate) throws IOException
    {
        //delete unnecessary message.
        ValidatorResults newResults = new ValidatorResults();
        for (ValidatorResult.Level lvl:validatorsToShow.getLevels())
        {
            if (!lvl.toString().equalsIgnoreCase("ALL")) continue;
            for (ValidatorResult result:validatorsToShow.getValidationResult(lvl))
            {
                String msgS = result.getMessage().toString().trim();
                if (!msgS.equals(MessageResources.getMessage("XML4", new Object[]{""}).toString().trim()))
                    newResults.addValidatorResult(result);
            }
        }
        validatorsToShow.removeAll();
        validatorsToShow.addValidatorResults(newResults);

        boolean isReorganizedMssageGenerated = false;

        while(true)
        {
            String errM = "Not generating " + (messageCount+i)+"_Reorganized.xml : ";
            String schemaFileNameL = null;
            //String schemaZipFileName = null;

            ZipUtil zipUtil = null;
            ZipEntry entry = null;

            String dirS = null;
            if (controlMessageTemplate != null)
            {
                zipUtil = controlMessageTemplate.getZipUtil();
                if (zipUtil != null)
                {
                    String fileI = zipUtil.getInitialFile();
                    if ((fileI != null)||(fileI.trim().equals("")))
                    {
                        File file2 = new File(fileI);
                        if ((file2.exists())&&(file2.isFile()))
                        {
                            dirS = file2.getParentFile().getAbsolutePath();
                        }
                    }
                }
            }
            if (dirS == null)
            {
                //System.out.println("WWWW 12 -----2");
                dirS = (new SchemaDirUtil()).getV3XsdFilePath(mifClass.getCopyrightYears());
                if (dirS == null)
                {
                    validatorsToShow = GeneralUtilities.addValidatorMessage(validatorsToShow, errM + "No xml schema directroy");
                    break;
                }
                //dirS = dirS + File.separator + Config.V3_XSD_MULTI_CACHE_SCHEMAS_DIRECTORY_NAME;
                if (!dirS.endsWith(File.separator)) dirS = dirS + File.separator;
                dirS = dirS + "schemas" + File.separator + Config.V3_XSD_MULTI_CACHE_SCHEMAS_DIRECTORY_NAME;

            }

            File dir = new File(dirS);
            if ((!dir.exists())||(!dir.isDirectory()))
            {
                String dirS2 = null;
                String dirStr = dirS;// + File.separator + Config.V3_XSD_MULTI_CACHE_SCHEMAS_DIRECTORY_NAME;

                while(true)
                {
                    int idx = -1;
                    if (dirStr.endsWith(File.separator)) dirStr = dirStr.substring(0, dirStr.length()-1);
                    //System.out.println("WWWW 14 : dirStr : " + dirStr);
                    for(int k=dirStr.length();k>0;k--)
                    {
                        String achar = dirStr.substring(k-1, k);
                        if ((achar.equals("/"))||(achar.equals(File.separator)))
                        {
                            idx = k;
                            break;
                        }
                    }
                    if (idx > 0) dirStr = dirStr.substring(0, idx);
                    else break;
                    File dirQ = new File(dirStr);
                    if ((!dirQ.exists())||(!dirQ.isDirectory())) continue;
                    File[] fList = dirQ.listFiles();

                    for(File ff:fList)
                    {
                        if (!ff.isFile()) continue;
                        if ((ff.getName().toLowerCase().trim().startsWith("schema"))&&(ff.getName().toLowerCase().trim().endsWith(".zip")))
                        {
                            dirS2 = ff.getAbsolutePath();
                        }
                        else continue;
                        //if (!ff.isFile()) continue;
                        //if (!ff.getName().toLowerCase().trim().endsWith(".zip")) continue;

                        try
                        {
                            zipUtil = new ZipUtil(dirS2);
                        }
                        catch(IOException ie)
                        {
                            continue;
                        }
                        List<ZipEntry> entries = zipUtil.searchEntryWithNameAsPart(mifClass.getMessageType(), ".xsd");
                        if ((entries != null)&&(entries.size() == 1))
                        {
                            entry = entries.get(0);
                            break;
                        }
                    }
                    if (entry != null) break;
                }
                if (entry == null)
                {
                    validatorsToShow = GeneralUtilities.addValidatorMessage(validatorsToShow, errM + "Not found this xml schema directroy : " + dirS);
                    break;
                }
                else
                {
                    String xsdFileS = zipUtil.copyIncludedFiles(entry, schemaFileName);
                    File file2 = new File(xsdFileS);
                    if ((file2.exists())&&(file2.isFile()))
                    {
                        dirS = file2.getParentFile().getAbsolutePath();
                    }
                    else
                    {
                        validatorsToShow = GeneralUtilities.addValidatorMessage(validatorsToShow, errM + "Xsd file saving failure : " + xsdFileS);
                        break;
                    }
                }
            }
            else dirS = dir.getAbsolutePath();
            //XMLValidator v = null;

            if (zipUtil == null)
            {
                if (schemaFileName == null)
                {
                    File[] files = dir.listFiles();
                    List<File> listFile = new ArrayList<File>();
                    for(File file:files) if (file.getName().trim().toLowerCase().endsWith(".xsd")) listFile.add(file);
                    if (listFile.size() == 0)
                    {
                        validatorsToShow = GeneralUtilities.addValidatorMessage(validatorsToShow, errM + "No schema file in this directroy : " + dirS);
                        break;
                    }

                    String messageType = mifClass.getMessageType();
                    if ((messageType == null)||(messageType.trim().equals("")))
                    {
                        validatorsToShow = GeneralUtilities.addValidatorMessage(validatorsToShow, errM + "V3 Message type is not specified.");
                        break;
                    }
                    messageType = messageType.trim();

                    //String schemaFileName = null;
                    for (File file:listFile)
                    {
                        String fileName = file.getName();
                        if (fileName.toLowerCase().indexOf(messageType.toLowerCase()) >= 0)
                        {
                            schemaFileNameL = file.getAbsolutePath();
                            break;
                        }
                    }
                    if (schemaFileNameL == null)
                    {
                        validatorsToShow = GeneralUtilities.addValidatorMessage(validatorsToShow, errM + "No schema file for the V3 Message type. : " + messageType);
                        break;
                    }
                }
                else
                {
                    if (schemaFileName.startsWith(dirS)) schemaFileNameL = schemaFileName;
                    else
                    {
                        if (!dirS.endsWith(File.separator)) dirS = dirS + File.separator;
                        String xsdFileNameS = (new File(schemaFileName)).getName();

                        String xsdC = FileUtil.readFileIntoString(schemaFileName);
                        if ((xsdC == null)||(xsdC.trim().equals("")))
                        {
                            validatorsToShow = GeneralUtilities.addValidatorMessage(validatorsToShow, errM + "Null or empty xsd file. : " + schemaFileName);
                            break;
                        }
                        String tempFileName = dirS + "Temp_" + FileUtil.getRandomNumber(5) + "_" + xsdFileNameS;

                        try
                        {
                            FileUtil.saveStringIntoTemporaryFile(tempFileName , xsdC);
                        }
                        catch(IOException ie)
                        {
                            validatorsToShow = GeneralUtilities.addValidatorMessage(validatorsToShow, errM + "IOException during xsd file copying. : " + schemaFileName);
                            break;
                        }
                        schemaFileNameL = tempFileName;
                    }
                }
            }
            else
            {
                String dd = zipUtil.getInitialFile();
                File ddF = new File(dd);
                if ((!ddF.exists())||(!ddF.isFile()))
                {
                    validatorsToShow = GeneralUtilities.addValidatorMessage(validatorsToShow, errM + "This initial File is invalid. : " + dd);
                    break;
                }
                File ddP = ddF.getParentFile();
                File schemaF = null;
                File[] files = ddP.listFiles();
                for(File ff:files)
                {
                    if (!ff.isFile()) continue;
                    String name = ff.getName();
                    if ((name.startsWith(mifClass.getMessageType()))&&(name.toLowerCase().endsWith(".xsd")))
                    {
                        schemaF = ff;
                        break;
                    }
                }
                if (schemaF == null)
                {
                    validatorsToShow = GeneralUtilities.addValidatorMessage(validatorsToShow, errM + "'"+mifClass.getMessageType()+"' message type schema was not found in this directory : " + ddP.getAbsolutePath());
                    break;
                }
                schemaFileNameL = schemaF.getAbsolutePath();
            }

            XMLValidator v = new XMLValidator(v3Message, schemaFileNameL, true);

            ValidatorResults results = v.validate();
            String reorganizedV3FileName = v.getTempReorganizedV3File();

            if (reorganizedV3FileName != null)
            {
                zipOut.putNextEntry(new ZipEntry(String.valueOf(messageCount+i)+"_Reorganized.xml"));
                v3Message = FileUtil.readFileIntoString(reorganizedV3FileName);
                writer.write(v3Message);
                writer.flush();

                isReorganizedMssageGenerated = true;
            }
            else validatorsToShow = GeneralUtilities.addValidatorMessage(validatorsToShow, "The reorganized message ("+(messageCount+i)+"_Reorganized.xml) cannot be generated because of a FATAL or very serious error. Please check the other messages.");

            validatorsToShow.addValidatorResults(results);

            if ((controlMessageTemplate == null)&&(zipUtil != null))
            {
                controlMessageTemplate = new XmlReorganizingTree();
                controlMessageTemplate.setZipUtil(zipUtil);
            }
            //System.out.println("WWWW 15 : " + controlMessageTemplate);
            break;
        }
        String infoMsg = "";
        if (isReorganizedMssageGenerated) infoMsg = ", Reorganized v3 message (" +(messageCount+i)+"_Reorganized.xml)";
        validatorsToShow = GeneralUtilities.addValidatorMessageInfo(validatorsToShow, "Direct message ("+(messageCount+i)+".xml)"+infoMsg+" and validation message object ("+(messageCount+i)+".ser) are successfully generated.");

        return new Object[] {v3Message, controlMessageTemplate, validatorsToShow};
    }

    public static boolean insertV3IntoControlMessage(XmlReorganizingTree controlMessageTemplate, String v3Message, MIFClass mifClass, ValidatorResults validatorsToShow, int i)
    {
        if (controlMessageTemplate.getHeadNode() == null) return false;
        if (validatorsToShow == null) validatorsToShow = new ValidatorResults();
        if (controlMessageTemplate == null)
        {
            validatorsToShow = GeneralUtilities.addValidatorMessage(validatorsToShow, "XmlReorganizingTree object is null. The '"+i+".xml' message cannot be wrapped with the control message");
            return false;
        }

        DefaultMutableTreeNode current = controlMessageTemplate.getCurrentNode();

        XmlReorganizingTree xmlTree = null;
        try
        {
            xmlTree = new XmlReorganizingTree(v3Message);
        }
        catch(ApplicationException ae)
        {
            validatorsToShow = GeneralUtilities.addValidatorMessage(validatorsToShow, "The payload message cannot be parsed. The '"+i+".xml' message cannot be wrapped with the control message");
            return false;
        }
        DefaultMutableTreeNode head = controlMessageTemplate.getHeadNode();

        if (current == null)
        {
            DefaultMutableTreeNode sNode = head;
            while(true)
            {
                sNode = sNode.getNextNode();
                if (sNode == null) break;
                DefaultMutableTreeNode parent = (DefaultMutableTreeNode) sNode.getParent();
                if (parent == null) continue;
                XmlTreeBrowsingNode xNode = (XmlTreeBrowsingNode) sNode.getUserObject();
                String name = xNode.getName();
                String role = xNode.getRole();
                if ((role.equals(xNode.getRoleKind()[0]))&&(name.equalsIgnoreCase(mifClass.getName())))
                {
                    current = parent;
                    break;
                }
            }
        }
        else
        {
            DefaultMutableTreeNode tNode = (DefaultMutableTreeNode) current.getParent();
            if (tNode == null)
            {
                validatorsToShow = GeneralUtilities.addValidatorMessage(validatorsToShow, "A Null parent node of the current node. The '"+i+".xml' message cannot be wrapped with the control message");
                return false;
            }
            DefaultMutableTreeNode lNode = (DefaultMutableTreeNode) tNode.getLastChild();
            XmlTreeBrowsingNode xNode = (XmlTreeBrowsingNode) lNode.getUserObject();
            XmlTreeBrowsingNode newXnode = new XmlTreeBrowsingNode(xNode.getRole(), xNode.getName(), xNode.getValue());
            DefaultMutableTreeNode newDnode = new DefaultMutableTreeNode(newXnode);
            tNode.add(newDnode);
            for(int j=0;j<lNode.getChildCount();j++)
            {
                DefaultMutableTreeNode cNode = (DefaultMutableTreeNode) lNode.getChildAt(j);
                XmlTreeBrowsingNode x1Node = (XmlTreeBrowsingNode) cNode.getUserObject();
                XmlTreeBrowsingNode newX1node = new XmlTreeBrowsingNode(x1Node.getRole(), x1Node.getName(), x1Node.getValue());
                DefaultMutableTreeNode newD1node = new DefaultMutableTreeNode(newX1node);
                newDnode.add(newD1node);
            }
            current = newDnode;
        }
        if (current == null)
        {
            validatorsToShow = GeneralUtilities.addValidatorMessage(validatorsToShow, "The payload element pointer is null. The '"+i+".xml' message cannot be wrapped with the control message");
            return false;
        }
        controlMessageTemplate.setCurrentNode(current);

        for(int j=0;j<current.getChildCount();j++)
        {
            DefaultMutableTreeNode cNode = (DefaultMutableTreeNode) current.getChildAt(j);
            XmlTreeBrowsingNode xNode = (XmlTreeBrowsingNode) cNode.getUserObject();
            String name = xNode.getName();
            String role = xNode.getRole();
            if ((role.equals(xNode.getRoleKind()[0]))&&(name.equalsIgnoreCase(mifClass.getName())))
            {
                current.remove(cNode);
                DefaultMutableTreeNode xmlHeadNode = xmlTree.getHeadNode();
                ((XmlTreeBrowsingNode) xmlHeadNode.getUserObject()).setName(name);
                current.add(xmlHeadNode);
            }
        }

        return true;
    }
}
