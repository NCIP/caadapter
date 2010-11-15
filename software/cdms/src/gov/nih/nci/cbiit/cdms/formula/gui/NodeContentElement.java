package gov.nih.nci.cbiit.cdms.formula.gui;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Nov 13, 2010
 * Time: 6:00:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class NodeContentElement
{
  public static String[] TYPES = new String[] {"Formula", "Term" ,"Expression", "Variable", "Number"};
    public static String[] OPERATION_NAMES = new String[]
           {"Addition",
            "Subtraction",
            "Multiplication",
            "Division",
            "Mode",
            "Square Root",
            "Radical",
            "Power",
            "Log10"       ,
            "Exponential",
            "Sine",
            "Cosine",
            "Tangent"};
    public static String[] OPERATIONS = new String[]
               {"+",
                "-",
                "*",
                "/",
                "%",
                "sqrt",
                "rad",
                "pow",
                "log10",       
                "exp",
                "sin",
                "con",
                "tan"};


  private String nodeType;
  private String nodeName;
  private String nodeValue;
  private String nodeDescription;
    private String nodeOperator;


  NodeContentElement()
    {
      nodeType = "";
      nodeDescription = "";
      //setKeyWords("");
    }
  NodeContentElement(String type)
    {
      nodeType = type;
      nodeDescription = "";
      //setKeyWords("");
    }
  NodeContentElement(String type, String name)
    {
      nodeType = type;
      nodeName = name;
      //setKeyWords("");
    }
  NodeContentElement(String type, String name, String cont)
    {
      nodeType = type;
      nodeName = name;
      if (getNodeType().equals(NodeContentElement.TYPES[2])) nodeOperator = cont;
      else if (getNodeType().equals(NodeContentElement.TYPES[4])) nodeValue = cont;
      else nodeDescription = cont;
      //setKeyWords(keyWd);
    }
    NodeContentElement(int type)
    {
      nodeType = NodeContentElement.TYPES[type];
      nodeDescription = "";
      //setKeyWords("");
    }
  NodeContentElement(int type, String name)
    {
      nodeType = NodeContentElement.TYPES[type];
      nodeName = name;
      //setKeyWords("");
    }
  NodeContentElement(int type, String name, String cont)
    {
      nodeType = NodeContentElement.TYPES[type];
      nodeName = name;
        if (getNodeType().equals(NodeContentElement.TYPES[2])) nodeOperator = cont;
        else if (getNodeType().equals(NodeContentElement.TYPES[4])) nodeValue = cont;
        else nodeDescription = cont;
      //setKeyWords(keyWd);
    }
    public void setNodeOperator(String oper)
    {
        nodeOperator = oper;
    }
    public String getOperator()
    {
        if (!getNodeType().equals(NodeContentElement.TYPES[2])) return null;
        String content = nodeOperator;

        for (String oper:OPERATIONS) if (content.equals(oper)) return oper;

        for (int i=0;i<OPERATION_NAMES.length;i++)
        {
            String opName = OPERATION_NAMES[i];
            if (content.equals(opName)) return OPERATIONS[i];
        }
        return null;
    }
    public String getOperatorName()
    {
        if (!getNodeType().equals(NodeContentElement.TYPES[2])) return null;
        String content = nodeOperator;

        for (String oper:OPERATION_NAMES) if (content.equals(oper)) return oper;

        for (int i=0;i<OPERATIONS.length;i++)
        {
            String oper = OPERATIONS[i];
            if (content.equals(oper)) return OPERATION_NAMES[i];
        }
        return null;
    }
  public String getNodeType()
    {
        if (nodeType == null) return "";
      return nodeType.trim();
    }
  public String getNodeDescription()
    {
        if (nodeDescription == null) return "";
      return nodeDescription.trim();
    }
    public String getNodeName()
    {
        if (nodeName == null) return "";
      return nodeName.trim();
    }
  public String getNodeValue()
    {
        if (nodeValue == null) return "";
      return nodeValue.trim();
    }

  public void setNodeType(String title)
    {
      nodeType = title;
    }
  public void setNodeName(String name)
    {
      nodeName = name;
    }
    public void setNodeValue(String value)
    {
      nodeValue = value;
    }
  public void setNodeDescription(String cont)
    {
      nodeDescription = cont;
    }

    public boolean isValid()
    {
        if (getNodeType() == null) return false;
        if (getNodeType().equals(NodeContentElement.TYPES[0]))
        {

        }
        else if (getNodeType().equals(NodeContentElement.TYPES[1]))
        {

        }
        else if (getNodeType().equals(NodeContentElement.TYPES[2]))
        {
            String oper = this.getOperator();
            if (oper == null) return false;
        }
        else if (getNodeType().equals(NodeContentElement.TYPES[3]))
        {
            if (nodeName == null) return false;
            if (nodeName.trim().equals("")) return false;
        }
        else if (getNodeType().equals(NodeContentElement.TYPES[4]))
        {
            if (nodeValue == null) return false;
            try
            {
                Double.parseDouble(nodeValue);
            }
            catch(NumberFormatException ne)
            {
                return false;
            }
        }
        else return false;
        return true;
    }
  public String toString()
    {
        String type = getNodeType();
        if (type == null) type = "";
        else type = type.trim();
      if ((getNodeType()).equals("")) return "** Empty Node";
      if (type.equals(NodeContentElement.TYPES[0]))  // Formula
      {
          return "Formula:" + this.getNodeName();
      }
      else if (type.equals(NodeContentElement.TYPES[1]))  //Term
      {
          if (this.getNodeName().equals("")) return "()";
          return "():" + this.getNodeName();
      }
      else if (type.equals(NodeContentElement.TYPES[2]))  //Expression
      {
          return this.getOperator();
      }
      else if (type.equals(NodeContentElement.TYPES[3]))  // Variable
      {
          return "Var:" + this.getNodeName() + "(" +this.getNodeDescription()+ ")";
      }
      else if (type.equals(NodeContentElement.TYPES[4]))  // Number
      {
          return "Num:" + this.getNodeValue();
      }
      else return "** Invlaid Type:" + type;
    }
}

