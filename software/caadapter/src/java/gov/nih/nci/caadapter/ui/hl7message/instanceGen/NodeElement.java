/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */
package gov.nih.nci.caadapter.ui.hl7message.instanceGen;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.3 $
 *          date        Mar 19, 2008
 *          Time:       2:18:15 PM $
 */
public class NodeElement
{

    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = ": NodeElement.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = ": /share/content/cvsroot/hl7sdk/src/gov/nih/nci/hl7/common/standard/impl/NodeElement.java,v 1.00 Mar 19, 2008 2:18:15 PM umkis Exp $";


        private String mode;
        private String level;
        private String name;
        private String uuid;
        private String uuidLinked = "NONE";
        private NodeElement upper;
        private NodeElement right;
        private NodeElement lower;

        NodeElement(String dMode, NodeElement node)
        {
            mode = dMode;
            upper = node;
        }
        NodeElement(String dMode)
        {
            mode = dMode;
        }
        NodeElement(String dMode, String dLevel, NodeElement node)
        {
            mode = dMode;
            upper = node;
            level = dLevel;
        }
        NodeElement(String dMode, String dLevel, String dName, NodeElement node)
        {
            mode = dMode;
            upper = node;
            level = dLevel;
            name = dName;
        }
        NodeElement(String dMode, String dLevel, String dName, String uid, NodeElement node)
        {
            mode = dMode;
            upper = node;
            level = dLevel;
            name = dName;
            uuid = uid;
        }
        public String getMode() { return mode; }
        public String getLevel() { return level; }
        public String getName() { return name; }
        public String getXmlPath() { return uuid; }
        public String getLinkedUUID() { return uuidLinked; }
        public NodeElement getUpper() { return upper; }
        public NodeElement getRight() { return right; }
        public NodeElement getLower() { return lower; }

        public void setMode(String dt) { mode = dt; }
        public void setLevel(String dt) { level = dt; }
        public void setName(String dt) { name = dt; }
        public void setUUID(String dt) { uuid = dt; }
        public void setLinkedUUID(String dt) { uuidLinked = dt; }
        public void setUpper(NodeElement dt) { upper = dt; }
        public void setRight(NodeElement dt) { right = dt; }
        public void setLower(NodeElement dt) { lower = dt; }

        public String getXPath()
        {
            String xpath = "";
            NodeElement temp = this;
            while(temp != null)
            {
                String nodeName = temp.getName();
                int serial = -1;
                int count = 0;
                if (temp.getUpper() != null)
                {
                    NodeElement temp2 = temp.getUpper().getLower();

                    boolean cTag = false;
                    while(temp2 != null)
                    {
                        String node2Name = temp2.getName();
                        if (node2Name.equals(nodeName))
                        {
                            count++;
                            if (!cTag) serial++;
                        }
                        if (temp == temp2) cTag = true;
                        temp2 = temp2.getRight();
                    }
                }
                String tag = "";
                if (count > 1)
                {
                    if (serial < 10) tag = "0" + serial;
                    else tag = "" + serial;
                }
                xpath = nodeName + tag + "." + xpath;
                temp = temp.getUpper();
            }
            return xpath.substring(0, xpath.length()-1);
        }

        public boolean isHead()
        {
            if (upper == null) return true;
            else return false;
        }
        public boolean isEndRight()
        {
            if (right == null) return true;
            else return false;
        }
        public boolean isLeafNode()
        {
            if (lower == null) return true;
            else return false;
        }
    }


/**
 * HISTORY      :$Log: not supported by cvs2svn $
 */
