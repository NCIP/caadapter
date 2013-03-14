/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.dataviewer.helper;

import gov.nih.nci.caadapter.dataviewer.MainDataViewerFrame;

import java.awt.event.ComponentListener;
import java.awt.event.ComponentEvent;

/**
 * Created by IntelliJ IDEA.
 * User: hjayanna
 * Date: Apr 27, 2007
 * Time: 4:10:02 PM
 * To change this template use File | Settings | File Templates.
 */
    public class DialogComponentListener implements ComponentListener
{

        private MainDataViewerFrame mainDataViewerFrame = null;

        public DialogComponentListener(MainDataViewerFrame mainDataViewerFrame)
        {
            this.mainDataViewerFrame = mainDataViewerFrame;
        }

        public void componentHidden(ComponentEvent componentEvent)
        {
        }

        public void componentMoved(ComponentEvent componentEvent)
        {
        }

        public void componentResized(ComponentEvent componentEvent)
        {
           // mainDataViewerFrame.getSplitPane().setDividerLocation(1.0);
        }

        public void componentShown(ComponentEvent componentEvent)
        {
        }
    }