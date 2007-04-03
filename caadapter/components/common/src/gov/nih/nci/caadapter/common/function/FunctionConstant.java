/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/function/FunctionConstant.java,v 1.1 2007-04-03 16:02:37 wangeug Exp $
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


package gov.nih.nci.caadapter.common.function;

import gov.nih.nci.caadapter.common.function.meta.FunctionMeta;
import gov.nih.nci.caadapter.common.util.FileUtil;

import java.util.List;
import java.io.*;

/**
 * Description of the class
 *
 * @author OWNER: doswellj
 * @author LAST UPDATE $Author: wangeug $
 * @version $Revision: 1.1 $
 * @date $Date: 2007-04-03 16:02:37 $
 * @since caAdapter v1.2
 */
public class FunctionConstant {
    private static final String LOGID = "$RCSfile: FunctionConstant.java,v $";
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/function/FunctionConstant.java,v 1.1 2007-04-03 16:02:37 wangeug Exp $";

    private String type;
    private String value;
    private String functionName;
    private String defaultFunctionName = "constant";
    private String[] functionNameArray;
    private String registryPrefix = "SaveFunction_";
    private String registryFileName = null;
    private String testSuffix = "&test";
    // constructors.
    public FunctionConstant()
    {
        findFunctionName(defaultFunctionName);
    }
    public FunctionConstant(String fName, String type, String value) throws FunctionException
    {
        if (!findFunctionName(fName)) throw new FunctionException("Invalid Function name : " + fName);
        functionName = fName;

        setType(type);
        setValue(value);

    }
    private boolean findFunctionName(String fName)
    {
        FunctionManager fManager = FunctionManager.getInstance();
        List<FunctionMeta> list = fManager.getFunctionList(defaultFunctionName);
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
    }
    // setters and getters
    public String getConstantFunctionName()
    {
        return functionName;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) throws FunctionException
    {
        if (functionName.equalsIgnoreCase(defaultFunctionName))
        {
            if (!((type.toLowerCase().indexOf("int") >= 0)||(type.toLowerCase().indexOf("string") >= 0)))
               throw new FunctionException("Invalid type in Function " + defaultFunctionName + " : " + type);
        }
        else
        {
            if (!functionName.equalsIgnoreCase(type)) throw new FunctionException("Invalid Function name or type : " + functionName + ", " + type);
        }
        this.type = type;
    }

    public String getValue()
    {
        return value;
    }
    public String getRegistryPrefix()
    {
        return registryPrefix;
    }
    public String getTestSuffix()
    {
        return testSuffix;
    }
    public void saveValue(String valueStr) throws FunctionException
    {
        if (!functionName.equalsIgnoreCase(functionNameArray[1])) throw new FunctionException("Invalid usage of savaValue function : " + functionName);

        try
        {
            if (valueStr.equals("")) { System.out.println("### input registry with null value."); return; }
            if (FunctionUtil.inputRegistry(registryPrefix+value, "3:"+valueStr)) throw new FunctionException("inputRegistry failure - already exists ("+FileUtil.getRegistryFileName()+") : " + registryPrefix+value +"=3:"+valueStr);
            registryFileName = FileUtil.getRegistryFileName();
            /*
            if (FileUtil.getRegistryFileName() == null)
            {
                FileUtil.inputRegistry(registryPrefix+value, "3:"+valueStr);
            }
            else
            {
                if (!FileUtil.changeRegistry(registryPrefix+value, "3:"+valueStr)) throw new FunctionException("Change Registry failure("+FileUtil.getRegistryFileName()+") : " + registryPrefix+value +"=3:"+valueStr);
            }
            */
        }
        catch(IOException ie)
        {
            throw new FunctionException(ie.getMessage()+".. ("+FileUtil.getRegistryFileName()+")");
        }

        /*
        String tempFileName = getRunningFileNameWithKeyCode(value);
        String tempPathName = getRunningPathNameWithKeyCode(value);

        try
        {
            FileUtil.saveStringIntoTemporaryFile(tempPathName, valueStr);
        }
        catch(IOException ie)
        {
            throw new FunctionException("File Writing Error in "+functionNameArray[1]+" Function(2) : " + tempFileName);
        }
        */
    }
    public String readValue() throws FunctionException
    {
        if (!functionName.equalsIgnoreCase(functionNameArray[2])) throw new FunctionException("Invalid usage of savaValue function : " + functionName);
        String output = "";
        try
        {
            //if (FileUtil.getRegistryFileName() == null) FileUtil.saveStringIntoTemporaryFile(registryFileName);
            String readS = FileUtil.readRegistry(registryPrefix+value);
            if (readS.startsWith("3:"))
            {
                output = readS.substring(2);
                if (!FileUtil.deleteRegistry(registryPrefix+value)) throw new FunctionException("delete Registry failure("+FileUtil.getRegistryFileName()+") : " + registryPrefix+value +"=4:"+output);
                //if (!FileUtil.changeRegistry(registryPrefix+value, "4:"+output)) throw new FunctionException("Change Registry failure("+FileUtil.getRegistryFileName()+") : " + registryPrefix+value +"=4:"+output);
            }
            else throw new FunctionException("Invalid sequence("+FileUtil.getRegistryFileName()+") : " + registryPrefix+value + "=" + readS);
        }
        catch(IOException ie)
        {
            throw new FunctionException(ie.getMessage()+".. k("+FileUtil.getRegistryFileName()+")");
        }
        return output;
        /*
        String tempFileName = getRunningFileNameWithKeyCode(value);
        String tempPathName = getRunningPathNameWithKeyCode(value);

        String tot = "";
        FileReader fr = null;

        try { fr = new FileReader(tempPathName); }
        catch(FileNotFoundException fe) { throw new FunctionException("FileNotFoundException in readValue : " + tempFileName); }

        BufferedReader br = new BufferedReader(fr);
        String readLineOfFile = "";

        try { while((readLineOfFile=br.readLine())!=null) tot = tot + readLineOfFile; }
        catch(IOException ie) { throw new FunctionException("File reading Error in readValue : " + tempFileName); }

        try
        {
            fr.close();
            br.close();
        }
        catch(IOException ie) { throw new FunctionException("File Closing Error in readValue : " + tempFileName); }
        File file = new File(tempPathName);
        file.delete();
        return tot.trim();
        */
    }
    public void setValue(String value) throws FunctionException
    {
        if (functionName.equalsIgnoreCase(defaultFunctionName))
        {
            if (type.toLowerCase().indexOf("int") >= 0)
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
                if (!check) throw new FunctionException("Invalid number format value : " + value);
                else this.value = value;
            }
            else this.value = value;
        }
        else if (functionName.equalsIgnoreCase(functionNameArray[1]))
        {
            value = value.trim();
            boolean testTag = false;

            if (value.toLowerCase().endsWith(testSuffix))
            {
                testTag = true;
                value = value.substring(0, value.length() - testSuffix.length());
            }
            if (value.length() < 5) throw new FunctionException("Key Code must be over 5 characters. : " + value);
            if (value.length() > 15) throw new FunctionException("Too long Key Code. : length=" + value.length());
            if (!checkKeyCode(value)) throw new FunctionException("Key Code includes invalid characters. : " + value);

            if (!testTag)
            {
                try
                {
                    if (!FunctionUtil.inputRegistry(registryPrefix+value, "1"))
                    {
                        throw new FunctionException("Already used Key Code : " + FileUtil.readRegistry(registryPrefix+value));
                    }
                    registryFileName = FileUtil.getRegistryFileName();
                }
                catch(IOException ie)
                {
                    throw new FunctionException(ie.getMessage()+".. x("+FileUtil.getRegistryFileName()+")");
                }
            }
            /*
            File workingDir = new File(FileUtil.getUIWorkingDirectoryPath());
            File[] files = workingDir.listFiles();
            boolean check = false;
            String tempFileName = getFileNameWithKeyCode(value);
            String tempPathName = getPathNameWithKeyCode(value);
            for (int i=0;i<files.length;i++)
            {
                File file = files[i];
                if (!file.isFile()) continue;
                String fName = file.getName();
                if (fName.equalsIgnoreCase(tempFileName)) check = true;
            }
            if (check) throw new FunctionException("Already used Key Code : " + value);

            try
            {
                FileUtil.saveStringIntoTemporaryFile(tempPathName, value);
            }
            catch(IOException ie)
            {
                throw new FunctionException("File Writing Error in "+functionNameArray[1]+" Function : " + tempFileName);
            }
            */
            this.value = value;
        }
        else if (functionName.equalsIgnoreCase(functionNameArray[2]))
        {
            try
            {
                String readS = FileUtil.readRegistry(registryPrefix+value);
                if (readS.equals("1"))
                {
                    if (!FileUtil.deleteRegistry(registryPrefix+value)) throw new FunctionException("Change Registry failure("+FileUtil.getRegistryFileName()+") : " + registryPrefix+value);
                    //if (!FileUtil.changeRegistry(registryPrefix+value, "2")) throw new FunctionException("Change Registry failure("+FileUtil.getRegistryFileName()+") : " + registryPrefix+value);
                }
                //else if (readS.equals("2")) {}
                else throw new FunctionException("Unidentified value ("+FileUtil.getRegistryFileName()+") : " + registryPrefix+value + "=" + readS);
            }
            catch(IOException ie)
            {
                throw new FunctionException(ie.getMessage()+".. y("+FileUtil.getRegistryFileName()+")");
            }
            this.value = value;
        }
    }
    public String[] getFunctionNameArray()
    {
        return functionNameArray;
    }
    /*
    public void deleteSavingValueFile() throws FunctionException
    {
        String tempPathName = getPathNameWithKeyCode(value);
        File file = new File(tempPathName);

        if (!file.exists()) throw new FunctionException("This Key Code is not setup yet (2). : " + tempPathName);
        if (!file.isFile()) throw new FunctionException("This Key Code is already used for other purpose(2). : " + tempPathName);
        if (!file.delete()) throw new FunctionException("Delete Failure. : " + tempPathName);
    }

    private String getFileNameWithKeyCode(String keyCode)
    {
        return Config.TEMPORARY_FILE_PREFIX + filePrefix + keyCode + Config.TEMPORARY_FILE_EXTENSION;
    }
    private String getPathNameWithKeyCode(String keyCode)
    {
        return FileUtil.getUIWorkingDirectoryPath()+ File.separator + getFileNameWithKeyCode(keyCode);
    }
    private String getRunningFileNameWithKeyCode(String keyCode)
    {
        return Config.TEMPORARY_FILE_PREFIX + filePrefix + keyCode + "_RUN_" +Config.TEMPORARY_FILE_EXTENSION;
    }
    private String getRunningPathNameWithKeyCode(String keyCode)
    {
        return FileUtil.getUIWorkingDirectoryPath()+ File.separator + getRunningFileNameWithKeyCode(keyCode);
    }
    */
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
