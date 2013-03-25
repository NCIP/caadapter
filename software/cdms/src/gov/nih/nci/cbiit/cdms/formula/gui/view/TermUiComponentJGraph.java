/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cdms.formula.gui.view;

import gov.nih.nci.cbiit.cdms.formula.core.TermType;

import javax.swing.*;
import java.awt.*;

import org.jgraph.graph.DefaultGraphCell;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Jan 18, 2011
 * Time: 12:20:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class TermUiComponentJGraph extends DefaultGraphCell
{
    private TermView viewMeta;

//    public TermUiComponentJGraph (String text)
//    {
//        super(text);
//        inintUI(null);
//    }
    public TermUiComponentJGraph (TermView meta)
    {
        super();
        viewMeta = meta;
        //inintUI(meta);
    }

//    private void inintUI ( TermView views)
//    {
//        String text="";
//        viewMeta=views;
//        if (views!=null)
//        {
//            if (views.getTerm().getType().equals(TermType.UNKNOWN))
//                text=views.getTerm().getName();
//            else
//                text=views.getTerm().getValue();
//            if (views.getTerm().getUnit()!=null&&views.getTerm().getUnit().trim().length()>0)
//                text=text+"("+views.getTerm().getUnit()+")";
//            this.setText(text);
//        }
//        setHorizontalAlignment(JLabel.CENTER);
//        int txtLength=0;
//        if (this.getText()!=null)
//            txtLength=this.getText().length();
//        setBounds(new Rectangle(txtLength*TermView.VIEW_CHARACTER_WEIDTH+TermView.VIEW_COMPONENT_PADDING, TermView.VIEW_COMPONENT_HEIGHT));
//    }

    public TermView getViewMeta() {
        return viewMeta;
    }

    public void setViewMeta(TermView view) {
            viewMeta = view;
    }
}

