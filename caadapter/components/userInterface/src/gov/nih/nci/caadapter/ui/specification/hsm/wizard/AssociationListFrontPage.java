/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/hsm/wizard/AssociationListFrontPage.java,v 1.2 2007-07-03 20:19:34 wangeug Exp $
 *
 * ******************************************************************
 * COPYRIGHT NOTICE
 * ******************************************************************
 *
 * The caAdapter Software License, Version 1.3
 * Copyright Notice.
 * 
 * Copyright 2006 SAIC. This software was developed in conjunction with the National Cancer Institute. To the extent government employees are co-authors, any rights in such works are subject to Title 17 of the United States Code, section 105. 
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the Copyright Notice above, this list of conditions, and the disclaimer of Article 3, below. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 
 * 
 * 2. The end-user documentation included with the redistribution, if any, must include the following acknowledgment:
 * 
 * 
 * "This product includes software developed by the SAIC and the National Cancer Institute."
 * 
 * 
 * If no such end-user documentation is to be included, this acknowledgment shall appear in the software itself, wherever such third-party acknowledgments normally appear. 
 * 
 * 3. The names "The National Cancer Institute", "NCI" and "SAIC" must not be used to endorse or promote products derived from this software. 
 * 
 * 4. This license does not authorize the incorporation of this software into any third party proprietary programs. This license does not authorize the recipient to use any trademarks owned by either NCI or SAIC-Frederick. 
 * 
 * 5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO EVENT SHALL THE UNITED STATES GOVERNMENT, THE NATIONAL CANCER INSTITUTE, SAIC, OR THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * <!-- LICENSE_TEXT_END -->
 */


package gov.nih.nci.caadapter.ui.specification.hsm.wizard;

//import org.hl7.meta.Association;
//import gov.nih.nci.caadapter.hl7.mif.MIFAssociation;
import gov.nih.nci.caadapter.hl7.datatype.DatatypeBaseObject;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Define clone list in the check box format to allow user to pick.
 *
 * @author OWNER: Eric Chen
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2007-07-03 20:19:34 $
 */
public class AssociationListFrontPage extends JPanel
{
    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: AssociationListFrontPage.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/hsm/wizard/AssociationListFrontPage.java,v 1.2 2007-07-03 20:19:34 wangeug Exp $";

    private java.util.List<JCheckBox> checkBoxes = new ArrayList<JCheckBox>();
    private java.util.List<DatatypeBaseObject> enableSelectedAssociation;

    private JList list;
    private DefaultListModel listModel;

    boolean singleSelection = false;

    /**
     * Creates a new <code>JPanel</code> with a double buffer
     * and a flow layout.
     */
    public AssociationListFrontPage(java.util.List<DatatypeBaseObject> enableSelectedAssociation, boolean singleSelection)
    {
        initialize(enableSelectedAssociation, false);
    }

    private void initialize(java.util.List<DatatypeBaseObject> enableSelectedAssociation, boolean singleSelection)
    {
        this.enableSelectedAssociation = enableSelectedAssociation;
        this.setLayout(new BorderLayout());

        JPanel northPanel = new JPanel(new BorderLayout());
        JLabel noteArea = new JLabel("Select one or more items from the following: ");
//        noteArea.setEditable(false);
//        noteArea.setBackground(northPanel.getBackground());
        northPanel.add(noteArea, BorderLayout.CENTER);
        this.add(northPanel, BorderLayout.NORTH);

//        JPanel centerPanel = new JPanel(new GridLayout(0, 1));
//        centerPanel.setBorder(BorderFactory.createTitledBorder(this.getBorder(), "Select ..."));

// check box solution
//        if ((enableSelectedAssociation != null) || (enableSelectedAssociation.size() > 0))
//        {
//            for (int i = 0; i < enableSelectedAssociation.size(); i++)
//            {
//                Association association = enableSelectedAssociation.get(i);
//                JCheckBox checkBox = new JCheckBox(association.getName());
//                checkBoxes.add(checkBox);
//                centerPanel.add(checkBox);
//            }
//        }

// JList solution
        if ((enableSelectedAssociation != null) || (enableSelectedAssociation.size() > 0))
        {
            listModel = new DefaultListModel();
            for (int i = 0; i < enableSelectedAssociation.size(); i++)
            {
            	DatatypeBaseObject association = enableSelectedAssociation.get(i);
                listModel.addElement(association.getName());
            }
        }
        list = new JList(listModel);
        if (singleSelection)
        {
            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        }
        list.setVisibleRowCount(10);

        JScrollPane centerPane = new JScrollPane(list);
//        centerPane.getViewport().setView(centerPanel);
        this.add(centerPane, BorderLayout.CENTER);
    }

//    Check box solution
//    public java.util.List<Association> getUserSelectedAssociations() throws Exception
//    {
//        java.util.List<Association> selectedAssociations = new ArrayList<Association>();
//        for (int i = 0; i < checkBoxes.size(); i++)
//        {
//            JCheckBox checkBox = checkBoxes.get(i);
//            if (checkBox.isSelected())
//            {
//                for (int j = 0; j < enableSelectedAssociation.size(); j++)
//                {
//                    Association association = enableSelectedAssociation.get(j);
//                    if (association.getName().equals(checkBox.getText()))
//                    {
//                        selectedAssociations.add(association);
//                        break;
//                    }
//                }
//            }
//        }
//        return selectedAssociations;
//    }

// List Solution
    public java.util.List<DatatypeBaseObject> getUserSelectedAssociations() throws Exception
    {
        java.util.List<DatatypeBaseObject> selectedAssociations = new ArrayList<DatatypeBaseObject>();
        final Object[] selectedValues = list.getSelectedValues();

        for (int i = 0; i < selectedValues.length; i++)
        {
            for (int j = 0; j < enableSelectedAssociation.size(); j++)
            {
            	DatatypeBaseObject association = enableSelectedAssociation.get(j);
                if (association.getName().equals(selectedValues[i]))
                {
                    selectedAssociations.add(association);
                    break;
                }
            }
        }
        return selectedAssociations;
    }

}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2007/04/03 16:18:15  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.13  2006/08/02 18:44:22  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.12  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.11  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/12/29 23:06:13  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/12/14 21:37:17  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/11/29 16:23:55  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/10/18 13:35:26  umkis
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/10/05 20:50:28  jiangsc
 * HISTORY      : GUI Enhancement.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/10/05 20:39:52  jiangsc
 * HISTORY      : GUI Enhancement.
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/09/15 17:09:44  chene
 * HISTORY      : SelectChoice GUI/Backend Support
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/09/15 16:01:38  chene
 * HISTORY      : SelectChoice GUI/Backend Support
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/09/14 18:14:14  chene
 * HISTORY      : Add/Remove Optional Clone support
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/09/09 22:41:54  chene
 * HISTORY      : Saved Point
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/09/08 19:37:02  chene
 * HISTORY      : Saved point
 * HISTORY      :
 */
