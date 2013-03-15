/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */

package gov.nih.nci.caadapter.sdtm;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author OWNER: Harsha Jayanna
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v3.2 revision $Revision: 1.4 $
 * @date       $Date: 2008-09-29 19:08:04 $
 */
public class SDTMRecord
{
	ArrayList DOMAIN = null;

	ArrayList USUBJID = null;

	ArrayList SUBJID = null;

	ArrayList DM_RFSTDTC = null;

	ArrayList DM_RFENDTC = null;

	ArrayList DM_SITEID = null;

	ArrayList DM_INVID = null;

	ArrayList DM_BRTHDTC = null;

	ArrayList DM_AGE = null;

	ArrayList DM_AGEU = null;

	ArrayList DM_SEX = null;

	ArrayList DM_RACE = null;

	ArrayList DM_ARMCD = null;

	ArrayList DM_ARM = null;

	ArrayList DM_COUNTRY = null;

	ArrayList DM_DM_TC = null;

	ArrayList STUDYID = null;

	ArrayList DM_DMDY = null;

	// HashMap sdtmDataHolder = new HashMap();
	public SDTMRecord() {
		// sdtmDataHolder = _sdtmHM;
		USUBJID = new ArrayList();
		DM_AGE = new ArrayList();
		DM_AGEU = new ArrayList();
		DM_ARM = new ArrayList();
		DM_ARMCD = new ArrayList();
		DM_BRTHDTC = new ArrayList();
		DM_COUNTRY = new ArrayList();
		DM_DM_TC = new ArrayList();
		DM_INVID = new ArrayList();
		DM_RACE = new ArrayList();
		DM_SEX = new ArrayList();
		DM_RFENDTC = new ArrayList();
		DM_RFSTDTC = new ArrayList();
		DM_SITEID = new ArrayList();
		DOMAIN = new ArrayList();
		STUDYID = new ArrayList();
		SUBJID = new ArrayList();
		DM_DMDY = new ArrayList();
	}

	public boolean print(String header, String sdtmPath) throws Exception
	{
		BufferedWriter out = new BufferedWriter(new FileWriter(sdtmPath));
		String _tmp = header.toString().replace('[', ' ');
		_tmp = _tmp.replace(']', ' ');
		out.write(_tmp + "\n");
		ArrayList<Integer> _tofindMax = new ArrayList<Integer>();
		_tofindMax.add(USUBJID.size());
		_tofindMax.add(DM_AGE.size());
		_tofindMax.add(DM_AGEU.size());
		_tofindMax.add(DM_ARM.size());
		_tofindMax.add(DM_ARMCD.size());
		_tofindMax.add(DM_BRTHDTC.size());
		_tofindMax.add(DM_COUNTRY.size());
		_tofindMax.add(DM_DM_TC.size());
		_tofindMax.add(DM_INVID.size());
		_tofindMax.add(DM_RACE.size());
		_tofindMax.add(DM_SEX.size());
		_tofindMax.add(DM_RFENDTC.size());
		_tofindMax.add(DM_RFSTDTC.size());
		_tofindMax.add(DM_SITEID.size());
		_tofindMax.add(DOMAIN.size());
		_tofindMax.add(STUDYID.size());
		_tofindMax.add(SUBJID.size());
		System.out.println("the max is " + Collections.max(_tofindMax));
		Integer max = Collections.max(_tofindMax);
		for (int ii = 0; ii < max.intValue(); ii++) {
			String a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r;
			try {
				a = (String) STUDYID.get(ii);
			} catch (Exception e1) {
				a = "";
			}
			try {
				b = (String) DOMAIN.get(ii);
			} catch (Exception e1) {
				b = "";
			}
			try {
				c = (String) USUBJID.get(ii);
			} catch (Exception e1) {
				c = "";
			}
			try {
				d = (String) SUBJID.get(ii);
			} catch (Exception e1) {
				d = "";
			}
			try {
				e = (String) DM_RFSTDTC.get(ii);
			} catch (Exception e1) {
				e = "";
			}
			try {
				f = (String) DM_RFENDTC.get(ii);
			} catch (Exception e1) {
				f = "";
			}
			try {
				g = (String) DM_SITEID.get(ii);
			} catch (Exception e1) {
				g = "";
			}
			try {
				h = (String) DM_INVID.get(ii);
			} catch (Exception e1) {
				h = "";
			}
			try {
				i = (String) DM_BRTHDTC.get(ii);
			} catch (Exception e1) {
				i = "";
			}
			try {
				j = (String) DM_AGE.get(ii);
			} catch (Exception e1) {
				j = "";
			}
			try {
				k = (String) DM_AGEU.get(ii);
			} catch (Exception e1) {
				k = "";
			}
			try {
				l = (String) DM_SEX.get(ii);
			} catch (Exception e1) {
				l = "";
			}
			try {
				m = (String) DM_RACE.get(ii);
			} catch (Exception e1) {
				m = "";
			}
			try {
				n = (String) DM_ARMCD.get(ii);
			} catch (Exception e1) {
				n = "";
			}
			try {
				o = (String) DM_ARM.get(ii);
			} catch (Exception e1) {
				o = "";
			}
			try {
				p = (String) DM_COUNTRY.get(ii);
			} catch (Exception e1) {
				p = "";
			}
			try {
				q = (String) DM_DM_TC.get(ii);
			} catch (Exception e1) {
				q = "";
			}
			try {
				r = (String) DM_DMDY.get(ii);
			} catch (Exception e1) {
				r = "";
			}
			out.write(a + "," + b + "," + c + "," + d + "," + e + "," + f + "," + g + "," + h + "," + i + "," + j + "," + k + "," + l + "," + m + "," + n + "," + o + "," + p + "," + q + "," + r + "\n");
			// System.out.println(a + "," + b + "," + c + "," + d + "," + e
			// + "," + f + "," + g + "," + h + "," + i + "," + j + "," + k +
			// "," + l + "," + m + "," + n + "," + o + "," + p + "," + q +
			// "," + r);
		}
		out.close();
		return true;
	}

	public void setUSUBJID(String val)
	{
		USUBJID.add(val);
	}

	public void setDM_AGE(String val)
	{
		DM_AGE.add(val);
	}

	public void setDM_AGEU(String val)
	{
		DM_AGEU.add(val);
	}

	public void setDM_ARM(String val)
	{
		DM_ARM.add(val);
	}

	public void setDM_ARMCD(String val)
	{
		DM_ARMCD.add(val);
	}

	public void setDM_BRTHDTC(String val)
	{
		DM_BRTHDTC.add(val);
	}

	public void setDM_COUNTRY(String val)
	{
		DM_COUNTRY.add(val);
	}

	public void setDM_DM_TC(String val)
	{
		DM_DM_TC.add(val);
	}

	public void setDM_INVID(String val)
	{
		DM_INVID.add(val);
	}

	public void setDM_RACE(String val)
	{
		DM_RACE.add(val);
	}

	public void setDM_RFENDTC(String val)
	{
		DM_RFENDTC.add(val);
	}

	public void setDM_RFSTDTC(String val)
	{
		DM_RFSTDTC.add(val);
	}

	public void setDM_SEX(String val)
	{
		DM_SEX.add(val);
	}

	public void setDM_SITEID(String val)
	{
		DM_SITEID.add(val);
	}

	public void setDOMAIN(String val)
	{
		DOMAIN.add(val);
	}

	public void setSTUDYID(String val)
	{
		STUDYID.add(val);
	}

	public void setSUBJID(String val)
	{
		SUBJID.add(val);
	}

	public void setDM_DMDY(String val)
	{
		DM_DMDY.add(val);
	}
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
*/
