package gov.nih.nci.caadapter.sdtm;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import gov.nih.nci.caadapter.common.MetaObjectImpl;
import gov.nih.nci.caadapter.common.SDKMetaData;
import gov.nih.nci.caadapter.common.util.PropertiesResult;
/**

 * @author OWNER: Harsha Jayanna
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v3.2 revision $Revision: 1.1 $
 */
public class SDTMMetadata extends MetaObjectImpl implements SDKMetaData {
    private String datatype;

    private String name;

    private String xPath;

    //ItemOID="STUDYID" OrderNumber="1" Mandatory="Yes" Role="Identifier"
    String ItemOID; String OrderNo; String Mandatory; String Role;
    

    public SDTMMetadata(String _ItemOID,  String _Mandatory, String _OrderNo, String _Role) {
	
	StringTokenizer _str = new StringTokenizer(_ItemOID, "&");
	
	_ItemOID = _str.nextToken();
	xPath = _str.nextToken();
	_OrderNo =_OrderNo.substring(0, _OrderNo.indexOf("&"));
	_Mandatory =_Mandatory.substring(0, _Mandatory.indexOf("&"));
	_Role =_Role.substring(0, _Role.indexOf("&"));

	this.name = _ItemOID;
	this.ItemOID = _ItemOID;
	this.OrderNo = _OrderNo;
	this.Mandatory = _Mandatory;
	this.Role = _Role;
	
    }

    public String getItemOID(){
	return ItemOID;
    }
    public String getOrderNo(){
	return OrderNo;
    }
    public String getMandatory(){
	return Mandatory;
    }
    public String getRole(){
	return Role;
    }
    
    @Override
    public PropertiesResult getPropertyDescriptors() throws Exception {
	Class beanClass = this.getClass();
	//PropertyDescriptor _name = new PropertyDescriptor("Name", beanClass, "getName", null);
	PropertyDescriptor _class = new PropertyDescriptor("Type", beanClass, "getClassName", null);
	PropertyDescriptor _item = new PropertyDescriptor("ItemOID",beanClass , "getItemOID", null);
	PropertyDescriptor _order = new PropertyDescriptor("OrderNumber",beanClass , "getOrderNo", null);
	PropertyDescriptor _mandatory = new PropertyDescriptor("Mandatory",beanClass , "getMandatory", null);
	PropertyDescriptor _role = new PropertyDescriptor("Role",beanClass , "getRole", null);
	PropertyDescriptor _xpath = new PropertyDescriptor("XPATH",beanClass , "getXPath", null);
//	PropertyDescriptor[] propertiesArray = new PropertyDescriptor[]
//			{ _name, _class };
	List<PropertyDescriptor> propList = new ArrayList<PropertyDescriptor>();
	propList.add(_item);
	propList.add(_class);
	propList.add(_order);
	propList.add(_mandatory);
	
	propList.add(_role);
	propList.add(_xpath);
	//propList.add(_item);
	PropertiesResult result = new PropertiesResult();
	result.addPropertyDescriptors(this, propList);
	return result;
	
	// TODO Auto-generated method stub
        //return super.getPropertyDescriptors();
    }
    
    /**
         * @param name
         */
    public SDTMMetadata(String name) {
	super();
	// TODO Auto-generated constructor stub
	this.name = name;
    }

    /**
         * @param name
         * @param path
         */
    public SDTMMetadata(String name, String path) {
	super();
	// TODO Auto-generated constructor stub
	this.name = name;
	xPath = path;
    }

    /**
         * @param datatype
         * @param name
         * @param path
         */
    public SDTMMetadata(String datatype, String name, String path) {
	super();
	// TODO Auto-generated constructor stub
	this.datatype = datatype;
	this.name = name;
	xPath = path;
    }

    public String getDatatype() {
	return datatype;
    }

    public String getName() {
	return name;
    }


 
    /**
         * 
         * @param datatype
         */
    public void setDatatype(String datatype) {
	this.datatype = datatype;
    }

    /**
         * 
         * @param datatype
         */
    public void setName(String name) {
	this.name = name;
    }

    public String toString() {
	return getName();
    }

    /**
         * 
         * @param xPath
         */
    public void setXPath(String xPath) {
	this.xPath = xPath;
    }

    /**

    /**
         * 
         * @param isForeignKey
         */
    public String getParentXPath() {
	String parentXPath;
	int attributeStartPoint = this.xPath.lastIndexOf(".");
	parentXPath = this.xPath.substring(0, attributeStartPoint);
	return parentXPath;
    }

    public boolean isMapped() {
	// TODO Auto-generated method stub
	return false;
    }

    public void setMapStatus(boolean newValue) {
	// TODO Auto-generated method stub
	
    }

    public String getXPath() {
	// TODO Auto-generated method stub
	return xPath;
    }
}
