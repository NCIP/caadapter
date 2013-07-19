package gov.nih.nci.cbiit.cmts.transform;

import gov.nih.nci.cbiit.cmts.common.ApplicationResult;

import java.util.List;
import java.io.*;
import java.net.*;

public class DefaultTransformer implements TransformationService {

	private boolean presentable;
	@Override
	public boolean isPresentable() {
		// TODO Auto-generated method stub
		return presentable;
	}

	@Override
	public void setPresentable(boolean value) {
		// TODO Auto-generated method stub
		presentable=value;
	}

	@Override
	public String transfer(String sourceFile, String processInstruction) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ApplicationResult> validateXmlData(Object validator,
			String xmlData) {
		// TODO Auto-generated method stub
		return null;
	}

    public String modifyMappingInstForWebFunction(String instruction, boolean isXSLT) throws Exception
    {
        if ((instruction == null)||(instruction.trim().equals(""))) throw new Exception("Mapping Unstruction is NULL or EMPTY.");
        StringBuffer queryString = new StringBuffer(instruction);

        StringBuffer mm = new StringBuffer();

        String quot = null;
        String strInQuot = "";
        String targetSite = null;

        for (int i=0;i<queryString.length();i++)
        {
            String achar = queryString.substring(i, i+1);
            boolean isQuotChar = (achar.equals("'"))||(achar.equals("\""));
            if (isXSLT) isQuotChar = (achar.equals("'"));
            if ((quot == null)&&(isQuotChar))
            {
                quot = achar;
                strInQuot = "";
                //continue;
            }
            else if (quot != null)
            {
                if (achar.equals(quot))
                {
                    String FUNCTION_SERVICE = "/WebFunctionService?";
                    String PARAMETER_LINE = "functionName=countDays&val01=2009-12-29&val02=2010-01-25";
                    int idx = strInQuot.indexOf(FUNCTION_SERVICE);
                    if (idx < 0) mm.append(quot + strInQuot + quot);
                    else
                    {
                        String NOT_CHANGE = "%NOT_CHANGE%";
                        if (targetSite == null)
                        {
                            String testURL = strInQuot.substring(0, idx + FUNCTION_SERVICE.length()) + PARAMETER_LINE;
                            String r = downloadFromURL(testURL);

                            if (r == null)
                            {
                                String testURL1 = null;
                                for(int j=0;j<5;j++)
                                {
                                    String testURL2 = null;
                                    if (j == 0)
                                    {
                                        if (gov.nih.nci.cbiit.cmts.util.FileUtil.getCodeBase() == null) continue;
                                        testURL2 = gov.nih.nci.cbiit.cmts.util.FileUtil.getCodeBase().toString();
                                        if (!testURL2.endsWith("/")) testURL2 = testURL2 + "/";
                                    }
                                    else if (j == 1) testURL2 = "http://caadapter-qa.nci.nih.gov/caadapter-cmts/";
                                    else if (j == 2) testURL2 = "http://caadapter-dev.nci.nih.gov/caadapter-cmts/";
                                    else if (j == 3) testURL2 = "http://caadapter-stage.nci.nih.gov/caadapter-cmts/";
                                    else if (j == 4) testURL2 = "http://caadapter.nci.nih.gov/caadapter-cmts/";

                                    String testURL3 = testURL2 + FUNCTION_SERVICE.substring(1) + PARAMETER_LINE;

                                    if (testURL.equals(testURL3)) continue;

                                    String r2 = downloadFromURL(testURL3);
                                    if (r2 != null)
                                    {
                                        testURL1 = testURL2;
                                        break;
                                    }
                                }
                                if (testURL1 == null) throw new Exception("Invalid WebFunctionService URL:" + strInQuot);
                                targetSite = testURL1;
                                System.out.println("WebFunctionService Code base switched: codeBase="+targetSite + ", from="+ strInQuot.substring(0, idx));
                            }
                            else
                            {
                                targetSite = NOT_CHANGE;
                                System.out.println("Keep WebFunctionService Code base="+ strInQuot.substring(0, idx));
                            }
                        }

                        if (targetSite.equals(NOT_CHANGE)) mm.append(quot + strInQuot + quot);
                        else mm.append(quot + targetSite + strInQuot.substring(idx + 1) + quot);
                    }
                    strInQuot = "";
                    quot = null;
                }
                else
                {
                    if (achar.equals("<")) strInQuot = strInQuot + "&lt;";
                    else if (achar.equals(">")) strInQuot = strInQuot + "&gt;";
                    else strInQuot = strInQuot + achar;
                }
            }
            else if (achar.equals("<"))
            {
                if (isXSLT) mm.append(achar);
                else mm.append("&lt;");
            }
            else if (achar.equals(">"))
            {
                if (isXSLT) mm.append(achar);
                else mm.append("&gt;");
            }
            else mm.append(achar);
        }
        return mm.toString();
    }

    private String downloadFromURL(String addr)
    {
        String r = null;
        try
        {
            r = downloadFromURLToString(addr);
        }
        catch(IOException ie)
        {
            System.out.println("Error on downloadFromURL() url=" + addr + ", message=" + ie.getMessage());
            return null;
        }

        if ((r == null)||(r.trim().equals("")))
        {
            System.out.println("NULL value from downloadFromURL() url=" + addr);
            return null;
        }
        int idx = r.indexOf("caAdapterResponse");
        if (idx < 0)
        {
            System.out.println("Invalid Content from downloadFromURL() url=" + addr + ", value=" + r);
            return null;
        }
        //System.out.println("Return value downloadFromURL() url=" + addr + ", value=" + r);
        return r;
    }
    private String downloadFromURLToString(String addr) throws IOException
    {
        if ((addr == null)||(addr.trim().equals(""))) throw new IOException("Null address.");
        URL ur = null;
        InputStream is = null;

        addr = addr.trim();

        try
        {
            ur = new URL(addr);
        }
        catch(MalformedURLException ue)
        {
            throw new IOException("Invalid URL : " + ue.getMessage());
        }
        URLConnection uc = null;
        try
        {
            uc = ur.openConnection();
        }
        catch(Exception ee)
        {
            throw new IOException("Fail- url.openConnection() : " + ee.getMessage());
        }

        try
        {
            uc.connect();
        }
        catch(SocketTimeoutException se)
        {
            throw new IOException("SocketTimeoutException : " + se.getMessage());
        }

        is = uc.getInputStream();

        if (is == null) throw new IOException("Null InputStream ");

        DataInputStream dis = new DataInputStream(is);

        byte bt = 0;
        String content = "";
        while(true)
        {
            try { bt = dis.readByte(); }
            catch(IOException ie) { break; }
            catch(NullPointerException ie) { break; }

            char ch = (char) bt;
            content = content + ch;
        }

        dis.close();
        is.close();
        if ((content == null)||(content.trim().equals(""))) return null;

        return content;
    }
}
