package gov.nih.nci.caadapter.hl7.v2v3.tools;

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
    public String toString()
    {
        String s;
        if (value == null) s = "";
        else s = "="+value;
        return role+name+s;
    }
}