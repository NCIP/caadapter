/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.hl7.validation.complement;

import java.util.List;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: kium
 * Date: Jan 30, 2009
 * Time: 1:26:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class XSDValidationTreeNode
{
    private int role = -1;
    private String name = null;
    private String value = null;
    private String[] roleKind = new String[] {"Head", "mainHead","includeHead" , "E:", "A:", "L:"};
    private String xsdFileName = null;
    //private List<String> includeList = null;
    //private XSDValidationTreeNode head = null;

    public XSDValidationTreeNode(int rolE, String namE, String valuE)
    {
        role = rolE;
        name = namE;
        value = valuE;
    }
    public XSDValidationTreeNode()
    {

    }
    public void setRole(int rolE)
    {
        role = rolE;
    }
    public void setName(String namE)
    {
        name = namE;
    }
    public void setValue(String valuE)
    {
        value = valuE;
    }
    public int getRole()
    {
        return role;
    }
    public String getName()
    {
        return name;
    }
    public String getValue()
    {
        return value;
    }
    public String[] getRoleKind()
    {
        return roleKind;
    }

    public boolean setXSDFileName(String fileName)
    {
        if (!((role == 1)||(role == 2))) return false;
        if (fileName == null) return false;
        fileName = fileName.trim();
        if (fileName.equals("")) return false;

        xsdFileName = fileName;
        return true;
    }
    public String getXSDFileName()
    {
        return xsdFileName;
    }
    public String getXSDMessageType_DEPRE()
    {
        if (xsdFileName == null) return null;
        File file = new File(xsdFileName);
        if (!file.exists()) return null;
        if (!file.isFile()) return null;
        if (!xsdFileName.toLowerCase().endsWith(".xsd")) return null;
        String name = file.getName();
        return name.substring(0, name.length()-4);
    }

    public String toString()
    {
        String s;
        if (value == null) s = "";
        else s = "="+value;
        String t;
        if (xsdFileName == null) t = "";
        else t = ",file="+xsdFileName;
        return roleKind[role]+":"+name+s+t;
    }
}
