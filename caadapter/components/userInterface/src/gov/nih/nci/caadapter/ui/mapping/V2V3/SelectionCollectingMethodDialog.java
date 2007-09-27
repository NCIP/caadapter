/*
 *  $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/V2V3/SelectionCollectingMethodDialog.java,v 1.1 2007-09-27 19:39:34 umkis Exp $
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

package gov.nih.nci.caadapter.ui.mapping.V2V3;

import gov.nih.nci.caadapter.ui.common.DefaultSettings;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: umkis $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.1 $
 *          date        Sep 27, 2007
 *          Time:       11:04:47 AM $
 */
public class SelectionCollectingMethodDialog extends JDialog implements ActionListener
{

    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: SelectionCollectingMethodDialog.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/V2V3/SelectionCollectingMethodDialog.java,v 1.1 2007-09-27 19:39:34 umkis Exp $";

    private JRadioButton jrDirect;
    private JRadioButton jrMakeFile;
    private JRadioButton jrBatch;

    private JButton jbGoNext;
    private JButton jbCancel;

    private int select = -1;

    public SelectionCollectingMethodDialog(JDialog dialog)
    {
        super(dialog, "Choose a Method of V2 Meta Data Collecting", true);
        initialize();
    }
    public SelectionCollectingMethodDialog(JFrame frame)
    {
        super(frame, "Choose a Method of V2 Meta Data Collecting", true);
        initialize();
    }

    private void initialize()
    {
        Object[] ob1 = setupRadioButtonPanel(jrDirect, "Meta data Direct Install (Cut and Paste from the manual files)", "Direct", false,
                                             jrMakeFile, "Make TSV files (Cut and Paste from the manual files)", "File", true,
                                             jrBatch, "Meta data Install from TSV files", "Install", false, true);
        select = 1;
        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add((JPanel)ob1[0], BorderLayout.CENTER);
        this.getContentPane().add(generateButtonPanel(), BorderLayout.SOUTH);

        this.setSize(300, 170);
        
    }

    private JPanel generateButtonPanel()
      {
          jbGoNext = new JButton("Go Next");
          jbGoNext.setActionCommand("Next");
          jbGoNext.addActionListener(this);
          jbGoNext.setEnabled(true);
          jbCancel = new JButton("Cancel");
          jbCancel.setActionCommand("Cancel");
          jbCancel.addActionListener(this);
          jbCancel.setEnabled(true);

          JPanel east = new JPanel(new BorderLayout());

          east.add(jbGoNext, BorderLayout.WEST);
          east.add(jbCancel, BorderLayout.EAST);
          east.add(new JLabel(" "), BorderLayout.CENTER);

          JPanel south = new JPanel(new BorderLayout());
          south.add(new JLabel(" "), BorderLayout.CENTER);

          south.add(east, BorderLayout.EAST);
          south.add(new JLabel(" "), BorderLayout.WEST);

          return south;
      }

    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == jbCancel)
        {
            select = -1;
            this.dispose();
        }
        if (e.getSource() == jbGoNext)
        {
            this.dispose();
        }
        if (e.getSource() == jrDirect)
        {
            select = 0;
        }
        if (e.getSource() == jrMakeFile)
        {
            select = 1;
        }
        if (e.getSource() == jrBatch)
        {
            select = 2;
        }
    }

    public int getSelected()
    {
        return select;
    }

    private Object[] setupRadioButtonPanel(JRadioButton radioButton01, String buttonLabel01, String buttonCommand01, boolean selected01,
                                           JRadioButton radioButton02, String buttonLabel02, String buttonCommand02, boolean selected02,
                                           JRadioButton radioButton03, String buttonLabel03, String buttonCommand03, boolean selected03, boolean vertical)
    {
        JPanel aPanel = new JPanel(new BorderLayout());
        JPanel bPanel = new JPanel(new BorderLayout());

        radioButton01 = new JRadioButton(buttonLabel01);
        radioButton01.setActionCommand(buttonCommand01);
        radioButton01.setSelected(selected01);
        radioButton02 = new JRadioButton(buttonLabel02);
        radioButton02.setActionCommand(buttonCommand02);
        radioButton02.setSelected(selected02);

        if (buttonLabel03 != null)
        {
            radioButton03 = new JRadioButton(buttonLabel03);
            radioButton03.setActionCommand(buttonCommand03);
            radioButton03.setSelected(false);
        }

        ButtonGroup group = new ButtonGroup();
        group.add(radioButton01);
        group.add(radioButton02);
        if (buttonLabel03 != null) group.add(radioButton03);

        radioButton01.addActionListener(this);
        radioButton02.addActionListener(this);
        if (buttonLabel03 != null) radioButton03.addActionListener(this);
        if (buttonLabel03 == null)
        {
            if (vertical)
            {
                aPanel.add(radioButton01, BorderLayout.NORTH);
                aPanel.add(radioButton02, BorderLayout.CENTER);
            }
            else
            {
                aPanel.add(radioButton01, BorderLayout.WEST);
                aPanel.add(radioButton02, BorderLayout.CENTER);
            }
        }
        else
        {
            aPanel.add(radioButton01, BorderLayout.WEST);
            JPanel cPanel = new JPanel(new BorderLayout());
            cPanel.add(radioButton02, BorderLayout.WEST);
            cPanel.add(radioButton03, BorderLayout.CENTER);
            aPanel.add(cPanel, BorderLayout.CENTER);
        }
        bPanel.add(aPanel, BorderLayout.NORTH);

        Object[] out = new Object[4];
        out[0] = bPanel;
        out[1] = radioButton01;
        out[2] = radioButton02;
        if (buttonLabel03 != null) out[3] = radioButton03;
        return out;
    }
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 */
