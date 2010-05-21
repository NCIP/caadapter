package gov.nih.nci.caadapter.common.function;

import gov.nih.nci.caadapter.common.util.GeneralUtilities;

import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: May 14, 2010
 * Time: 1:07:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class NullFlavorFunctionHelper
{
    public NullFlavorFunctionHelper()
    {

    }
    public List<String> nullFlavor(String dataString, String nullFlavorInput)
    {
        return nullFlavor(dataString, nullFlavorInput, null);
    }
    public List<String> nullFlavor(String dataString, String nullFlavorInput, String nullFlavorDefaultSetting)
    {
        //process source data value
        String rtnValue1=dataString;
        if (rtnValue1 != null)
        {
            rtnValue1 = rtnValue1.trim();

            if ((rtnValue1.equals("\"\""))||(rtnValue1.equals("\"")))
            {
                //Null read from constant Function
                rtnValue1 = "";
            }

            if (rtnValue1.startsWith("\"")) rtnValue1 = rtnValue1.substring(1);
            if (rtnValue1.endsWith("\"")) rtnValue1 = rtnValue1.substring(0, rtnValue1.length()-1);

            if(rtnValue1.equalsIgnoreCase(GeneralUtilities.CAADAPTER_DATA_FIELD_NULL))
            {//Null read from CSV
                rtnValue1 = null;
            }
        }

        //set NullFlavorSetting
        List<String> result = doNullFlavor(rtnValue1, nullFlavorInput);
        if (result != null) return result;

        result = doNullFlavor(rtnValue1, nullFlavorDefaultSetting);
        if (result != null) return result;

        result = new ArrayList<String>();
        result.add(dataString);
        result.add(null);
        return result;
    }

    private List<String> doNullFlavor(String rtnValue1, String nullSetting)
    {
        List<String> nullSettingRtnList = new ArrayList<String>();
        if ((nullSetting == null)||(nullSetting.trim().equals(""))) return null;

        if (isNullFlavorValue(nullSetting))
        {
            if ((rtnValue1 == null)||(rtnValue1.equals("")))
            {
                nullSettingRtnList.add(null);
                nullSettingRtnList.add(nullSetting);
            }
            else
            {
                nullSettingRtnList.add(rtnValue1);
                nullSettingRtnList.add(null);
            }
            return nullSettingRtnList;
        }

        Double dblObj = parseNumericValue(rtnValue1);

        StringTokenizer st = new StringTokenizer(nullSetting, ";");

        boolean tag = false;
        String key = "";
        String val = "";
        String oneItem = null;
        while(st.hasMoreTokens())
        {
            if (tag)
            {
                if (val.equals("")) tag = false;
                else break;
            }

            String keyP = st.nextToken().trim();
            if (keyP.equals("")) continue;
            int idx = keyP.indexOf(":");
            if (idx < 0)
            {
                if (oneItem == null) oneItem = keyP;
                continue;
            }
            key = keyP.substring(0, idx).trim();
            val = keyP.substring(idx+1).trim();
            //System.out.println("%%% Key:" + key + ", Val:" + val);
            if (val.equals("")) continue;

            if ((key.startsWith("&"))||(key.startsWith("%"))) key = key.substring(1);
            if (key.equals("")) continue;

            if ((key.equalsIgnoreCase("NULL"))&&(rtnValue1 == null)) tag = true;
            if ((key.equalsIgnoreCase("NOT_NUMERIC"))&&(dblObj == null)) tag = true;

            if (rtnValue1 == null) continue;
            if ((key.equalsIgnoreCase("BLANK"))&&(rtnValue1.equals(""))) tag = true;

            if (rtnValue1.equals("")) continue;

            if ((key.equalsIgnoreCase("SPACE"))&&(rtnValue1.trim().equals(""))) tag = true;
            if ((key.equalsIgnoreCase("SPACES"))&&(rtnValue1.trim().equals(""))) tag = true;

            if ((!val.equals(""))&&(tag)) break;

            idx = key.indexOf("#");

            if (idx < 0) continue;

            String keyNum = key.substring(idx+1);
            key = key.substring(0, idx).trim();
            //System.out.println("%%%       KeyNum:" + keyNum + ", key=" + key);

            if ((key.equalsIgnoreCase("NOT_EQUAL"))||(key.equalsIgnoreCase("NOT_EQUALS"))||(key.equalsIgnoreCase("EQUAL"))||(key.equalsIgnoreCase("EQUALS"))||(key.equalsIgnoreCase("IF_VALUE")))
            {
                StringTokenizer st2 = new StringTokenizer(keyNum, ",");

                boolean cTag = false;

                while(st2.hasMoreTokens())
                {
                    String token = st2.nextToken();
                    if (token == null) token = "";
                    else token = token.trim();

                    if (token.equals("")) continue;
                    //System.out.println("%%%         "+key+" .. v_1:" + rtnValue1 + ", v_2:" + token + ", ");
                    if (rtnValue1.trim().equals(token.trim()))
                    {
                        if ((key.equalsIgnoreCase("NOT_EQUAL"))||(key.equalsIgnoreCase("NOT_EQUALS"))) cTag = true;
                        else
                        {
                            tag = true;
                            break;
                        }
                    }

                    if (dblObj != null)
                    {
                        Double dblObj2 = parseNumericValue(token);
                        if (dblObj2 == null) continue;

                        if (dblObj2.doubleValue() == dblObj.doubleValue())
                        {
                            if ((key.equalsIgnoreCase("NOT_EQUAL"))||(key.equalsIgnoreCase("NOT_EQUALS"))) cTag = true;
                            else
                            {
                                tag = true;
                                break;
                            }
                        }
                    }
                }
                if ((key.equalsIgnoreCase("NOT_EQUAL"))||(key.equalsIgnoreCase("NOT_EQUALS"))) if (!cTag) tag = true;
                //System.out.println("%%%            cTag:"+cTag+" .. tag:" + tag);
            }

            if (dblObj == null) continue;

            if ((!val.equals(""))&&(tag)) break;

            idx = keyNum.indexOf("~");
            //if (idx < 0) idx = keyNum.indexOf("-");
            Double val1 = null;
            Double val2 = null;

            if (idx < 0) val1 = parseNumericValue(keyNum);
            else
            {
                val1 = parseNumericValue(keyNum.substring(0, idx));
                val2 = parseNumericValue(keyNum.substring(idx+1));
            }

            if (val1 == null) continue;

            double ori = dblObj.doubleValue();
            double v1 = val1.doubleValue();
            //System.out.println("%%%       ori=" +ori+",  between:" + v1 + " ~ " + val2);
            if ((key.equalsIgnoreCase("GREATER_THAN"))&&(ori > v1)) tag = true;
            if ((key.equalsIgnoreCase("GREATER_EQUAL"))&&(ori >= v1)) tag = true;
            if ((key.equalsIgnoreCase("LESS_THAN"))&&(ori < v1)) tag = true;
            if ((key.equalsIgnoreCase("LESS_EQUAL"))&&(ori <= v1)) tag = true;
            if ((key.equals("GT"))&&(ori > v1)) tag = true;
            if ((key.equals("GE"))&&(ori >= v1)) tag = true;
            if ((key.equals("LT"))&&(ori < v1)) tag = true;
            if ((key.equals("LE"))&&(ori <= v1)) tag = true;

            if (val2 == null) continue;
            double v2 = val2.doubleValue();
            if (v1 > v2)
            {
                double t1 = v1;
                v1 = v2;
                v2 = t1;
            }

            if ((key.equalsIgnoreCase("BETWEEN"))&&(ori >= v1)&&(ori <= v2)) tag = true;
            if ((key.equalsIgnoreCase("NOT_BETWEEN"))&&((ori < v1)||(ori > v2))) tag = true;
            //if ((key.equalsIgnoreCase("BETWEEN_EQUAL"))&&(ori >= v1)&&(ori <= v2)) tag = true;
            //if ((key.equalsIgnoreCase("NOT_BETWEEN_EQUAL"))&&((ori <= v1)||(ori >= v2))) tag = true;
            //System.out.println("%%%       v1=" +v1+",  v2=" + v2 + ", || ori="+ori + ", || tag="+ tag);
        }

        String nullFalvorValue = null;
        if (tag)
        {
            if (val.equals("")) return null;
            else nullFalvorValue = val;
        }
        else
        {
            if (oneItem != null) nullFalvorValue = oneItem;
            else return null;
        }
        if (nullFalvorValue == null) return null;
        else nullFalvorValue = nullFalvorValue.trim();

        if (!isNullFlavorValue(nullFalvorValue)) return null;

        nullSettingRtnList.add(null);
        nullSettingRtnList.add(nullFalvorValue);
        return nullSettingRtnList;
    }
    public boolean isNullFlavorValue(String nullFalvorValue)
    {
        /*
        NI NoInformation No information whatsoever can be inferred from this exceptional value. This is the most general exceptional value. It is also the default exceptional value.
        OTH other The actual value is not an element in the value domain of a variable. (e.g., concept not provided by required code system).
        NINF negative infinity Negative infinity of numbers.
        PINF positive infinity Positive infinity of numbers.
        UNK unknown A proper value is applicable, but not known.
        ASKU asked but unknown Information was sought but not found (e.g., patient was asked but didn't know)
        NAV temporarily unavailable Information is not available at this time but it is expected that it will be available later.
        NASK not asked This information has not been sought (e.g., patient was not asked)
        TRC trace The content is greater than zero, but too small to be quantified.
        MSK masked There is information on this item available but it has not been provided by the sender due to security, privacy or other reasons. There may be an alternate mechanism for gaining access to this information.Note: using this null flavor does provide information that may be a breach of confidentiality, even though no detail data is provided. Its primary purpose is for those circumstances where it is necessary to inform the receiver that the information does exist without providing any detail.
        NA not applicable No proper value is applicable in this context (e.g., last menstrual period for a male).
        NP not present Value is not present in a message. This is only defined in messages, never in application data! All values not present in the message must be replaced by the applicable default, or no-information (NI) as the default of all defaults.
        */
        if (nullFalvorValue == null) nullFalvorValue = "";
        else nullFalvorValue = nullFalvorValue.trim();

        boolean cTag = false;
        if (nullFalvorValue.equals("NI")) cTag = true;
        if (nullFalvorValue.equals("OTH")) cTag = true;
        if (nullFalvorValue.equals("NINF")) cTag = true;
        if (nullFalvorValue.equals("PINF")) cTag = true;
        if (nullFalvorValue.equals("UNK")) cTag = true;
        if (nullFalvorValue.equals("ASKU")) cTag = true;
        if (nullFalvorValue.equals("NAV")) cTag = true;
        if (nullFalvorValue.equals("NASK")) cTag = true;
        if (nullFalvorValue.equals("TRC")) cTag = true;
        if (nullFalvorValue.equals("MSK")) cTag = true;
        if (nullFalvorValue.equals("NA")) cTag = true;
        if (nullFalvorValue.equals("NP")) cTag = true;

        return cTag;

    }

    public String validateNullFlavorSetteng(String nullSetting)
    {
        List<String> nullSettingRtnList = new ArrayList<String>();
        if ((nullSetting == null)||(nullSetting.trim().equals(""))) return "NullFlavor setting value is null";
        if (isNullFlavorValue(nullSetting)) return null;

        StringTokenizer st = new StringTokenizer(nullSetting, ";");

        boolean tag = false;
        String key = "";
        String val = "";
        String oneItem = null;
        while(st.hasMoreTokens())
        {
            tag = false;

            String keyP = st.nextToken().trim();
            if (keyP.equals("")) continue;
            int idx = keyP.indexOf(":");
            if (idx < 0)
            {
                if (oneItem == null) oneItem = keyP;
                continue;
            }
            key = keyP.substring(0, idx).trim();
            val = keyP.substring(idx+1).trim();
            //System.out.println("%%% Key:" + key + ", Val:" + val);
            if (val.equals("")) return "NullFlavor value is null : " + nullSetting;

            if ((key.startsWith("&"))||(key.startsWith("%"))) key = key.substring(1);
            if (key.equals("")) return "NullFlavor setting key word is null" + nullSetting;

            if (key.equalsIgnoreCase("NULL")) tag = true;
            if (key.equalsIgnoreCase("NOT_NUMERIC")) tag = true;

            //if (rtnValue1 == null) continue;
            if (key.equalsIgnoreCase("BLANK")) tag = true;

            //if (rtnValue1.equals("")) continue;

            if (key.equalsIgnoreCase("SPACE")) tag = true;
            if (key.equalsIgnoreCase("SPACES")) tag = true;

            if ((isNullFlavorValue(val))&&(tag)) continue;
            if ((!isNullFlavorValue(val))&&(tag)) return "Invalid nullFlavor value (1) : "+ val;

            idx = key.indexOf("#");

            if (idx < 0) return "NullFlavor setting key word '"+key+"' needs parameter(s). (#) : " + nullSetting;

            String keyNum = (key + " ").substring(idx+1);
            key = key.substring(0, idx).trim();

            keyNum = keyNum.trim();
            if (keyNum.equals("")) return "This NullFlavor setting key word '"+key+"' has empty parameter. : " + nullSetting;

            if ((key.equalsIgnoreCase("NOT_EQUAL"))||(key.equalsIgnoreCase("NOT_EQUALS"))||(key.equalsIgnoreCase("EQUAL"))||(key.equalsIgnoreCase("EQUALS"))||(key.equalsIgnoreCase("IF_VALUE")))
            {
                StringTokenizer st2 = new StringTokenizer(keyNum, ",");

                while(st2.hasMoreTokens())
                {
                    String token = st2.nextToken();
                    if (token == null) token = "";
                    else token = token.trim();
                    if (token.equals("")) return "Invalid parameter list : " + nullSetting;
                }
                if (isNullFlavorValue(val)) continue;
                if (!isNullFlavorValue(val)) return "Invalid nullFlavor value (2) : "+ val;
            }

            idx = keyNum.indexOf("~");

            tag = false;
            if (key.equalsIgnoreCase("GREATER_THAN")) tag = true;
            if (key.equalsIgnoreCase("GREATER_EQUAL")) tag = true;
            if (key.equalsIgnoreCase("LESS_THAN")) tag = true;
            if (key.equalsIgnoreCase("LESS_EQUAL")) tag = true;
            if (key.equals("GT")) tag = true;
            if (key.equals("GE")) tag = true;
            if (key.equals("LT")) tag = true;
            if (key.equals("LE")) tag = true;

            Double val1 = null;
            if (tag)
            {
                if (idx >= 0) return "This NullFlavor setting key word '"+key+"' doesn't need any range parameter. : " + nullSetting;

                val1 = parseNumericValue(keyNum);
                if (val1 == null) return "Invalid numeric parameter value : "+ keyNum;
                if (isNullFlavorValue(val)) continue;
                else return "Invalid nullFlavor value (3) : "+ val;
            }

            Double val2 = null;

            if (idx < 0) return "This NullFlavor setting key word '"+key+"' needs range parameter(~). : " + nullSetting;
            val1 = parseNumericValue(keyNum.substring(0, idx));
            if (val1 == null) return "Invalid numeric parameter1 range value : "+ keyNum;
            val2 = parseNumericValue(keyNum.substring(idx+1));
            if (val2 == null) return "Invalid numeric parameter2 range value : "+ keyNum;

            double v1 = val1.doubleValue();
            double v2 = val2.doubleValue();
            if (v1 > v2) return "Invalid range value : "+ keyNum;

            if (key.equalsIgnoreCase("BETWEEN")) tag = true;
            if (key.equalsIgnoreCase("NOT_BETWEEN")) tag = true;
            if (tag)
            {
                if (!isNullFlavorValue(val)) return "Invalid nullFlavor value (3) : "+ val;
            }
            else return "Invalid nullFlavor key word value : "+ key;
        }
        return null;
    }

    private Double parseNumericValue(String rtnValue1)
    {
        Double dblObj = null;
        if ((rtnValue1 != null)&&(!rtnValue1.trim().equals("")))
        {
            try
            {
                double inn = Double.parseDouble(rtnValue1);
                dblObj = new Double(inn);
            }
            catch(NumberFormatException ne)
            {
                dblObj = null;
            }
        }

        return dblObj;
    }
}
