/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */

package gov.nih.nci.caadapter.sdtm;

public class SDTMRecordStructure
{

    String DOMAIN = "";

    String USUBJID = "";

    String SUBJID = "";

    String DM_RFSTDTC = "";

    String DM_RFENDTC = "";

    String DM_SITEID = "";

    String DM_INVID = "";

    String DM_BRTHDTC = "";

    String DM_AGE = "";

    String DM_AGEU = "";

    String DM_SEX = "";

    String DM_RACE = "";

    String DM_ARMCD = "";

    String DM_ARM = "";

    String DM_COUNTRY = "";

    String DM_DM_TC = "";

    String STUDYID = "";

    String DM_DMDY = "";

    public String getDM_AGE()
    {
        return DM_AGE;
    }

    public void setDM_AGE(String dm_age)
    {
        DM_AGE = dm_age;
    }

    public String getDM_AGEU()
    {
        return DM_AGEU;
    }

    public void setDM_AGEU(String dm_ageu)
    {
        DM_AGEU = dm_ageu;
    }

    public String getDM_ARM()
    {
        return DM_ARM;
    }

    public void setDM_ARM(String dm_arm)
    {
        DM_ARM = dm_arm;
    }

    public String getDM_ARMCD()
    {
        return DM_ARMCD;
    }

    public void setDM_ARMCD(String dm_armcd)
    {
        DM_ARMCD = dm_armcd;
    }

    public String getDM_BRTHDTC()
    {
        return DM_BRTHDTC;
    }

    public void setDM_BRTHDTC(String dm_brthdtc)
    {
        DM_BRTHDTC = dm_brthdtc;
    }

    public String getDM_COUNTRY()
    {
        return DM_COUNTRY;
    }

    public void setDM_COUNTRY(String dm_country)
    {
        DM_COUNTRY = dm_country;
    }

    public String getDM_DM_TC()
    {
        return DM_DM_TC;
    }

    public void setDM_DM_TC(String dm_dm_tc)
    {
        DM_DM_TC = dm_dm_tc;
    }

    public String getDM_DMDY()
    {
        return DM_DMDY;
    }

    public void setDM_DMDY(String dm_dmdy)
    {
        DM_DMDY = dm_dmdy;
    }

    public String getDM_INVID()
    {
        return DM_INVID;
    }

    public void setDM_INVID(String dm_invid)
    {
        DM_INVID = dm_invid;
    }

    public String getDM_RACE()
    {
        return DM_RACE;
    }

    public void setDM_RACE(String dm_race)
    {
        DM_RACE = dm_race;
    }

    public String getDM_RFENDTC()
    {
        return DM_RFENDTC;
    }

    public void setDM_RFENDTC(String dm_rfendtc)
    {
        DM_RFENDTC = dm_rfendtc;
    }

    public String getDM_RFSTDTC()
    {
        return DM_RFSTDTC;
    }

    public void setDM_RFSTDTC(String dm_rfstdtc)
    {
        DM_RFSTDTC = dm_rfstdtc;
    }

    public String getDM_SEX()
    {
        return DM_SEX;
    }

    public void setDM_SEX(String dm_sex)
    {
        DM_SEX = dm_sex;
    }

    public String getDM_SITEID()
    {
        return DM_SITEID;
    }

    public void setDM_SITEID(String dm_siteid)
    {
        DM_SITEID = dm_siteid;
    }

    public String getDOMAIN()
    {
        return DOMAIN;
    }

    public void setDOMAIN(String domain)
    {
        DOMAIN = domain;
    }

    public String getSTUDYID()
    {
        return STUDYID;
    }

    public void setSTUDYID(String studyid)
    {
        STUDYID = studyid;
    }

    public String getSUBJID()
    {
        return SUBJID;
    }

    public void setSUBJID(String subjid)
    {
        SUBJID = subjid;
    }

    public String getUSUBJID()
    {
        return USUBJID;
    }

    public void setUSUBJID(String usubjid)
    {
        USUBJID = usubjid;
    }

    public boolean hasAtLeastOneValue(SDTMRecordStructure stdmRec)
    {
        if (stdmRec.DOMAIN.length() > 0)
        {
            return true;
        }
        if (stdmRec.USUBJID.length() > 0)
        {
            return true;
        }
        if (stdmRec.SUBJID.length() > 0)
        {
            return true;
        }
        if (stdmRec.DM_RFSTDTC.length() > 0)
        {
            return true;
        }
        if (stdmRec.DM_RFENDTC.length() > 0)
        {
            return true;
        }
        if (stdmRec.DM_SITEID.length() > 0)
        {
            return true;
        }
        if (stdmRec.DM_INVID.length() > 0)
        {
            return true;
        }
        if (stdmRec.DM_BRTHDTC.length() > 0)
        {
            return true;
        }
        if (stdmRec.DM_AGE.length() > 0)
        {
            return true;
        }
        if (stdmRec.DM_AGEU.length() > 0)
        {
            return true;
        }
        if (stdmRec.DM_SEX.length() > 0)
        {
            return true;
        }
        if (stdmRec.DM_RACE.length() > 0)
        {
            return true;
        }
        if (stdmRec.DM_ARMCD.length() > 0)
        {
            return true;
        }
        if (stdmRec.DM_ARM.length() > 0)
        {
            return true;
        }
        if (stdmRec.DM_COUNTRY.length() > 0)
        {
            return true;
        }
        if (stdmRec.DM_DM_TC.length() > 0)
        {
            return true;
        }
        if (stdmRec.STUDYID.length() > 0)
        {
            return true;
        }
        if (stdmRec.DM_DMDY.length() > 0)
        {
            return true;
        }
        return false;
    }

    @Override
    public String toString()
    {
        String trim = STUDYID + "," + DOMAIN + "," + USUBJID + "," + SUBJID + "," + DM_RFSTDTC + "," + DM_RFENDTC + "," + DM_SITEID + "," + DM_INVID + "," + DM_BRTHDTC + "," + DM_AGE + "," + DM_AGEU + "," + DM_SEX + "," + DM_RACE + "," + DM_ARMCD + "," + DM_ARM + "," + DM_COUNTRY + "," + DM_DM_TC + "," + DM_DMDY;
        return trim;
    }

    public static String printCols()
    {
        return "STUDYID,DOMAIN,USUBJID,SUBJID,DM_RFSTDTC,DM_RFENDTC,DM_SITEID,DM_INVID,DM_BRTHDTC,DM_AGE,DM_AGEU,DM_SEX,DM_RACE,DM_ARMCD,DM_ARM,DM_COUNTRY,DM_DM_TC,DM_DMDY";
    }
}
