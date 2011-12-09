package gov.nih.nci.cbiit.cmts.ui.main;

import gov.nih.nci.cbiit.cmts.ui.common.DefaultSettings;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Sep 12, 2011
 * Time: 2:16:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class BannerMainPanel extends JPanel
{
    Container mainContainer;

    public BannerMainPanel()
    {
        super();
        mainContainer = null;
        //this.setLayout(new BorderLayout());
        doMain();
    }
    public BannerMainPanel(Container con)
    {
        super();
        mainContainer = con;
        //this.setLayout(new BorderLayout());
        doMain();
    }
    private void doMain()
    {
        String s = null;
        if (mainContainer != null)
        {
            s = JOptionPane.showInputDialog(mainContainer, "Select Banner Image code (0-4)", "Select Banner", JOptionPane.PLAIN_MESSAGE);
        }
        if (s == null) s = "0";
        else s = s.trim();
        if (s.equals("1"))
        {
            doOneImage("NCICBBanner.jpg");
        }
        else if (s.equals("2"))
        {
            doOneImage("cabig_header_logo_green.jpg");
        }
        else if (s.equals("3"))
        {
            doOneImage("logo_cabig_darkBlue.jpg");
        }
        else if (s.equals("4"))
        {
            doOneImage("caBIG_NCICB_Banner.JPG");
        }
        else if (s.equals("5"))
        {
            doOneImage("NCICB_caBIG_Banner.JPG");
        }
        else if (s.equals("6"))
        {
            doOneImage("NCI_caBIG_Banner.JPG");
        }

        else
        {
            doThreeImages("banner_NCI_Red_caBIG_blue_Left.bmp", "banner_NCI_Red_caBIG_blue_middle.bmp", "banner_NCI_Red_caBIG_blue_Right.bmp");
            //doThreeImages("banner_NCI_Red_caAdapter_blue_Left.bmp", "banner_NCI_Red_caBIG_blue_middle.bmp", "banner_NCI_Red_caBIG_blue_Right.bmp");
        }
    }

    private void doOneImage(String imageName)
    {
        Image bannerImage = DefaultSettings.getImage(imageName);
		ImageIcon imageIcon = new ImageIcon(bannerImage);
		this.setLayout(new FlowLayout(FlowLayout.LEADING));
		JLabel label = new JLabel(imageIcon);
		this.add(label);

    }
    private void doThreeImages(String imageNameL, String imageNameM, String imageNameR)
    {
        this.setLayout(new BorderLayout());

        Image bannerImageL = DefaultSettings.getImage(imageNameL);
		ImageIcon imageIconL = new ImageIcon(bannerImageL);
        JLabel labelL = new JLabel(imageIconL);
		this.add(labelL, BorderLayout.WEST);

        Image bannerImageM = DefaultSettings.getImage(imageNameM);
		ImageIcon imageIconM = new ImageIcon(bannerImageM);
        JLabel labelM = new JLabel(imageIconM);
		this.add(labelM, BorderLayout.CENTER);

        Image bannerImageR = DefaultSettings.getImage(imageNameR);
		ImageIcon imageIconR = new ImageIcon(bannerImageR);
        JLabel labelR = new JLabel(imageIconR);
		this.add(labelR, BorderLayout.EAST);

    }
}
