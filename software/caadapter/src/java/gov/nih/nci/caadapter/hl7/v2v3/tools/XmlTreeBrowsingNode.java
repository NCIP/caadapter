/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.hl7.v2v3.tools;

import gov.nih.nci.caadapter.hl7.validation.complement.XSDValidationTreeNode;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Dec 19, 2008
 * Time: 3:27:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class XmlTreeBrowsingNode
{
    private String role = null;
    private String name = null;
    private String value = null;
    private String[] roleKind = new String[] {"E:", "A:", "L:"};
    private DefaultMutableTreeNode xsdNode = null;

    public XmlTreeBrowsingNode(String rolE, String namE, String valuE)
    {
        role = rolE;
        name = namE;
        value = valuE;
    }
    public XmlTreeBrowsingNode()
    {

    }
    public void setRole(String rolE)
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
    public String getRole()
    {
        return role;
    }
    public String getName()
    {
        int idx = name.indexOf(";");
        if (idx > 0) return name.substring(0, idx);
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
    public void setXSDNode(DefaultMutableTreeNode node)
    {
        xsdNode = node;
    }
    public DefaultMutableTreeNode getXSDNode()
    {
        return xsdNode;
    }
    public String toString()
    {
        String s;
        if (value == null) s = "";
        else s = "="+value;
        return role+name+s;
    }
}