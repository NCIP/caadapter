/*
 *  : /share/content/cvsroot/hl7sdk/src/gov/nih/nci/hl7/common/standard/impl/NodeElement.java,v 1.00 Mar 19, 2008 2:18:15 PM umkis Exp $
 *
 * ******************************************************************
 * COPYRIGHT NOTICE  
 * ******************************************************************
 *
 *	The caAdapter Software License, Version 1.0
 *
 *	Copyright 2001 SAIC. This software was developed in conjunction with the National Cancer
 *	Institute, and so to the extent government employees are co-authors, any rights in such works
 *	shall be subject to Title 17 of the United States Code, section 105.
 *
 *	Redistribution and use in source and binary forms, with or without modification, are permitted
 *	provided that the following conditions are met:
 *
 *	1. Redistributions of source code must retain the above copyright notice, this list of conditions
 *	and the disclaimer of Article 3, below.  Redistributions in binary form must reproduce the above
 *	copyright notice, this list of conditions and the following disclaimer in the documentation and/or
 *	other materials provided with the distribution.
 *
 *	2.  The end-user documentation included with the redistribution, if any, must include the
 *	following acknowledgment:
 *
 *	"This product includes software developed by the SAIC and the National Cancer
 *	Institute."
 *
 *	If no such end-user documentation is to be included, this acknowledgment shall appear in the
 *	software itself, wherever such third-party acknowledgments normally appear.
 *
 *	3. The names "The National Cancer Institute", "NCI" and "SAIC" must not be used to endorse or
 *	promote products derived from this software.
 *
 *	4. This license does not authorize the incorporation of this software into any proprietary
 *	programs.  This license does not authorize the recipient to use any trademarks owned by either
 *	NCI or SAIC-Frederick.
 *
 *	5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED
 *	WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 *	MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE
 *	DISCLAIMED.  IN NO EVENT SHALL THE NATIONAL CANCER INSTITUTE, SAIC, OR
 *	THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *	EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *	PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *	PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
 *	OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *	NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *	SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *
 * ******************************************************************
 */

package gov.nih.nci.caadapter.ui.hl7message.instanceGen;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: umkis $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.1 $
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
 * HISTORY      : : NodeElement.java,v $
 */
