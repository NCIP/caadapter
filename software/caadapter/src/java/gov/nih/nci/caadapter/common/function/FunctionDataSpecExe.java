package gov.nih.nci.caadapter.common.function;

import gov.nih.nci.caadapter.common.function.meta.FunctionMeta;
import gov.nih.nci.caadapter.common.util.RegistryUtil;
import gov.nih.nci.caadapter.castor.function.impl.C_dataSpec;
import gov.nih.nci.caadapter.castor.function.impl.TypeDef;

import java.util.List;
import java.util.StringTokenizer;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Apr 22, 2010
 * Time: 7:59:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class FunctionDataSpecExe
{
    private static final String LOGID = "$RCSfile: FunctionDataSpecExe.java,v $";
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/function/FunctionDataSpecExe.java,v 1.4 2010-04-27 18:57:45 umkis Exp $";

    private String type;
    private String value;
    private String functionName;
    //private String defaultFunctionName = "constant";
    //private String[] functionNameArray;
    //private String registryPrefix = "SaveFunction_";
    //private String registryFileName = null;
    //private String testSuffix = "&test";

    private C_dataSpec dataSpec = null;
    // constructors.

    public FunctionDataSpecExe(String fName, C_dataSpec spec, String typ, String val) throws FunctionException
    {
        if (!findFunctionName(fName)) throw new FunctionException("Invalid Function name : " + fName);
        if (spec == null) throw new FunctionException("DataSpec is null");
        functionName = fName;
        dataSpec = spec;

        setType(typ);
        setValue(val);

    }
    private boolean findFunctionName(String fName)
    {
        FunctionManager fManager = FunctionManager.getInstance();
        List<FunctionMeta> list = fManager.getFunctionByName(fName);

        if ((list == null)||(list.size() == 0)) return false;

        return true;
        /*
        boolean search = false;
        functionNameArray = new String[list.size()];
        for(int i=0;i<list.size();i++)
        {
            FunctionMeta meta = list.get(i);
            String metaName = meta.getFunctionName();
            functionNameArray[i] = metaName;
            if (metaName.equals(fName)) search = true;
        }
        return search;
        */
    }
    // setters and getters
    public String getFunctionName()
    {
        return functionName;
    }
    public String getType()
    {
        return type;
    }
    public void setType(String type) throws FunctionException
    {
        setTypeOrValue(type, true);
    }
    private void setTypeOrValue(String type, boolean isType) throws FunctionException
    {
        C_dataSpec spec = dataSpec;
        TypeDef typeDef = null;

        if (isType) typeDef = spec.getC_typeDef();
        else typeDef = spec.getC_valueDef();

        String valueArr = typeDef.getValueArray();

        if (type == null) type = "";
        if (isType)type = type.trim();

        if (type.equals(""))
        {
            String defaultV = typeDef.getDefault();
            if (defaultV == null) defaultV = "";
            if (isType) defaultV = defaultV.trim();
            if (!defaultV.equals(""))
            {
                if (isType) this.type = defaultV;
                else value = defaultV;
                return;
            }
        }

        if (valueArr == null) valueArr = "";
        valueArr = valueArr.trim();

        if (valueArr.equals(""))
        {
            if (isType) this.type = type;
            else value = type;
            return;
        }

        StringTokenizer st = new StringTokenizer(valueArr, ",");

        while(st.hasMoreTokens())
        {
            String str = st.nextToken();
            if (isType) str = str.trim();
            if (str.equalsIgnoreCase(type))
            {
                if (isType) this.type = str;
                else value = str;
                return;
            }
        }
        String sstr = "type";
        if (!isType) sstr = "value";
        throw new FunctionException("Invalid " + sstr + " for " + spec.getName() + " function : " + type);
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value) throws FunctionException
    {
        setTypeOrValue(value, false);

        if ((type.toLowerCase().startsWith("int"))||
            (type.toLowerCase().startsWith("numb"))||
            (type.toLowerCase().startsWith("numer"))||
            (type.toLowerCase().startsWith("float"))||
            (type.toLowerCase().startsWith("doubl"))||
            (type.toLowerCase().startsWith("deci")))
        {
            boolean check = true;
            try { long ng = Long.parseLong(value); }
            catch(NumberFormatException ne) { check = false; }
            if (!check)
            {
                check = true;
                try { double dbl = Double.parseDouble(value); }
                catch(NumberFormatException ne) { check = false; }
            }
            if (!check)
            {
                this.value = null;
                throw new FunctionException("Invalid number format value : " + value);
            }

        }


    }

    public C_dataSpec getDataSpec()
    {
       return dataSpec;
    }

    private boolean checkKeyCode(String keyCode)
    {
        boolean check = true;
        for (int i=0;i<keyCode.length();i++)
        {
            String achar = keyCode.substring(i, i+1);
            if ((i==0)&&(achar.equals("%"))) continue;
            char[] chars = achar.toCharArray();
            char chr = chars[0];
            int ascii = (int) chr;

            boolean charCheck = false;
            if ((ascii >= 48)&&(ascii <= 57)) charCheck = true;
            else if ((ascii >= 65)&&(ascii <= 90)) charCheck = true;
            else if ((ascii >= 97)&&(ascii <= 122)) charCheck = true;
            else if (ascii == 95) charCheck = true;
            //System.out.println("Ascii : " + ascii + ", for=>" + achar + ", check=>" + charCheck);
            if (!charCheck) check = false;
        }
        //System.out.println("Final : " + check);
        return check;
    }
}
