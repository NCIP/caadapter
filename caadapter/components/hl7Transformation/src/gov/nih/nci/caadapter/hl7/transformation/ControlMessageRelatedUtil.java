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
import gov.nih.nci.caadapter.hl7.validation.complement.ReorganizingForValidating;
import gov.nih.nci.caadapter.hl7.validation.complement.XSDValidationTree;
import gov.nih.nci.caadapter.hl7.validation.XMLValidator;
import gov.nih.nci.caadapter.hl7.mif.MIFClass;
import gov.nih.nci.caadapter.hl7.transformation.data.XMLElement;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.ObjectOutputStream;
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

        if (mifClass == null) return null;
        String mifName = mifClass.getName();
        if ((mifName == null)||(mifName.trim().equals(""))) return null;
        String mifType = mifClass.getMessageType();
        if ((mifType == null)||(mifType.trim().equals(""))) return null;
        mifName = mifName.trim();
        mifType = mifType.trim();

        String dirS = (new SchemaDirUtil()).getV3XsdFilePath(mifClass.getCopyrightYears());
        if (dirS == null) return null;

        dirS = dirS + File.separator + Config.V3_XSD_MULTI_CACHE_SCHEMAS_DIRECTORY_NAME;

        File dir = new File(dirS);
        if ((!dir.exists())||(!dir.isDirectory())) return null;
        else dirS = dir.getAbsolutePath();

        if (!dirS.endsWith(File.separator)) dirS = dirS + File.separator;

        if ((controlFile != null)&&(!controlFile.trim().equals("")))
        {
            controlMessageTemplate = searchControlMessageFile(mifName, mifType, (new File(controlFile)), dirS);
            if (controlMessageTemplate == null)
            {
                System.err.println("Invalid Control message File : " + controlFile);
                return null;
            }
            else return controlMessageTemplate;
        }

        String dirName = FileUtil.searchProperty("ControlMessageTemplateDirectory");

        if (dirName == null) dirName = FileUtil.getWorkingDirPath() + "\\demo\\contTemplate";

//        if (dirName == null) dirName = "";
//        else dirName = dirName.trim();
        if (dirName.equals("")) return null;

        File f = new File(dirName);
        if ((!f.exists())||(!f.isDirectory())) return null;

        File[] fList = f.listFiles();

        for (File ff:fList)
        {
            XmlReorganizingTree xrt = searchControlMessageFile(mifName, mifType, ff, dirS);
            if (xrt != null)
            {
                controlMessageTemplate = xrt;
                break;
            }
        }
        //if (controlMessageTemplate == null) System.err.println("Invalid Control message File : " + controlFile);

        return controlMessageTemplate;
    }

    private static XmlReorganizingTree searchControlMessageFile(String mifName, String mifType, File ff, String dirS)
    {

            if (ff == null) return null;
            if ((!ff.exists())||(!ff.isFile())) return null;
            List<String> lines = null;
            try
            {
                lines = FileUtil.readFileIntoList(ff.getAbsolutePath());
            }
            catch(IOException ie)
            {
                lines = null;
            }
            if ((lines == null)||(lines.size() == 0)) return null;

            String type = null;
            boolean found = false;
            for (int i=(lines.size()-1);i>=0;i--)
            {
                String line = lines.get(i);
                int idx1 = line.indexOf("</");
                if (idx1 < 0) continue;
                int idx2 = line.indexOf(">");
                if (idx2 < 0) continue;
                if (idx1 >= idx2) continue;
                if (type == null) type = line.substring(idx1+2, idx2).trim();
                if (type.equals("")) type = null;
                line = line.toLowerCase();
                int idx3 = line.indexOf(mifName.toLowerCase());
                if (idx3 > 0) found = true;
                if ((found)&&(type != null)) break;
            }
            if ((!found)||(type == null)) return null;

            String schemaF = dirS + type + ".xsd";

            File f2 = new File(schemaF);
            if ((!f2.exists())||(!f2.isFile())) return null;

            ReorganizingForValidating rfv = null;
            String schemaFileNameL = f2.getAbsolutePath();
            try
            {
                rfv = new ReorganizingForValidating(ff.getAbsolutePath(), schemaFileNameL);
            }
            catch(ApplicationException ae)
            {
                System.out.println("ApplicationException in ReorganizingForValidating object during TransformationService : " + ae.getMessage());
                return null;
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
                //System.out.println("FFFFF : nodeName=" + nodeName + ", typeName=" + typeName + ", found2=" + found2);
                if (found2) break;
            }

            if (!found2) return null;
            XmlReorganizingTree controlMessageTemplate = rfv.getXMLTree();
            try
            {
                controlMessageTemplate.setSchemaFileName(schemaFileNameL);
            }
            catch(ApplicationException ae)
            {
                System.err.println("controlMessageTemplate ERROR at the last stage (controlMessageTemplate.setSchemaFileName(schemaFileNameL)) : " + ae.getMessage());
                return null;
            }
            return controlMessageTemplate;
    }

    public static String excuteXSDValidationForTransformationService(ValidatorResults validatorsToShow, int messageCount, int i, String v3Message, ZipOutputStream zipOut, OutputStreamWriter writer, String schemaFileName, MIFClass mifClass) throws IOException
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

            String dirS = (new SchemaDirUtil()).getV3XsdFilePath(mifClass.getCopyrightYears());
            if (dirS == null)
            {
                validatorsToShow = GeneralUtilities.addValidatorMessage(validatorsToShow, errM + "No xml schema directroy");
                break;
            }
            dirS = dirS + File.separator + Config.V3_XSD_MULTI_CACHE_SCHEMAS_DIRECTORY_NAME;

            File dir = new File(dirS);
            if ((!dir.exists())||(!dir.isDirectory()))
            {
                validatorsToShow = GeneralUtilities.addValidatorMessage(validatorsToShow, errM + "Not found this xml schema directroy : " + dirS);
                break;
            }
            else dirS = dir.getAbsolutePath();

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

            break;
        }
        String infoMsg = "";
        if (isReorganizedMssageGenerated) infoMsg = ", Reorganized v3 message (" +(messageCount+i)+"_Reorganized.xml)";
        validatorsToShow = GeneralUtilities.addValidatorMessageInfo(validatorsToShow, "Direct message ("+(messageCount+i)+".xml)"+infoMsg+" and validation message object ("+(messageCount+i)+".ser) are successfully generated.");

        return v3Message;
    }

    public static boolean insertV3IntoControlMessage(XmlReorganizingTree controlMessageTemplate, String v3Message, MIFClass mifClass, ValidatorResults validatorsToShow, int i)
    {
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
