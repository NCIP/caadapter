/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.security.dao.xmlDao;

import gov.nih.nci.caadapter.security.config.SecurityConfig;
import gov.nih.nci.caadapter.security.dao.AbstractSecurityDAO;
import gov.nih.nci.caadapter.security.dao.SecurityAccessIF;
import gov.nih.nci.caadapter.security.domain.Permissions;
import gov.nih.nci.caadapter.security.domain.SecurityObject;
import gov.nih.nci.caadapter.security.domain.User;
import gov.nih.nci.caadapter.security.domain.UserAddress;
import gov.nih.nci.caadapter.security.domain.UserEmail;
import gov.nih.nci.caadapter.security.domain.UserGroup;
import gov.nih.nci.caadapter.security.domain.UserSecurityObjectMapping;
import gov.nih.nci.caadapter.security.domain.WebServiceMapping;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.InputSource;


public class XmlDaoImpl extends AbstractSecurityDAO implements SecurityAccessIF
{

        String mappingPath=SecurityConfig.SECURITY_XML_MAPPING;
        SecurityXmlDatasource secXmlDSUser, secXmlDSSecObj, secXmlDSUserGroup, secXmlDSUsrSecObjMapping, secXmlDSWebSrvc ;
        long lastModifiedExp;
        public XmlDaoImpl(){
            init();
        }
        private void init()
        {
            initUser();
            intiSecObj();
            initUserGroup();
            initUserSecurityObjectMapping();
            initWebServiceMapping();
        }

        private void initUser(){
            String securityXmlPath=SecurityConfig.SECURITY_XML_USER;
            secXmlDSUser = initSecurityDatasource(mappingPath, securityXmlPath );
        }
        
        private void intiSecObj(){
            String securityXmlPath=SecurityConfig.SECURITY_XML_SECURITYOBJECT;
            secXmlDSSecObj = initSecurityDatasource(mappingPath, securityXmlPath );
        }
        
        private void initUserGroup(){
            String securityXmlPath=SecurityConfig.SECURITY_XML_USERGROUP;
            secXmlDSUserGroup = initSecurityDatasource(mappingPath, securityXmlPath );
        }

        private void initUserSecurityObjectMapping(){
            String securityXmlPath=SecurityConfig.SECURITY_XML_USERSECURITYOBJECTMAPPING;
            secXmlDSUsrSecObjMapping = initSecurityDatasource(mappingPath, securityXmlPath );
        }
        
        private void initWebServiceMapping(){
            String securityXmlPath=SecurityConfig.SECURITY_XML_WEBSERVICEMAPPING;
            secXmlDSWebSrvc = initSecurityDatasource(mappingPath, securityXmlPath );
        }

        
        private SecurityXmlDatasource initSecurityDatasource(String mappingPath, String securityXmlPath)
        {
            SecurityXmlDatasource secXmlDS=null; 
            Mapping securityMapping=new Mapping();
            //Read Security mapping
            
            System.out.println("XmlDaoImpl.initWithFilePath()...Security mapping:"+mappingPath);
            try {
                securityMapping.loadMapping(mappingPath);
                
                Unmarshaller unmarsh =new Unmarshaller(securityMapping);
                InputSource secSrc =new InputSource(new FileReader(securityXmlPath));
                secXmlDS =(SecurityXmlDatasource)unmarsh.unmarshal(secSrc);
                String a = new String("");
                
            } catch (MarshalException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ValidationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (MappingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return secXmlDS;   
        }
         
      

public List<SecurityObject> getAllSecurityObjects()
{
    // TODO Auto-generated method stub
    return null;
}
public List<UserGroup> getAllUserGroups()
{
    // TODO Auto-generated method stub
    return null;
}
public List<User> getAllUsers()
{
    // TODO Auto-generated method stub
    if (secXmlDSUser!=null)
        return secXmlDSUser.getUser();
    return null;
}
public List<WebServiceMapping> getAllWebserviceMappings()
{
    if (secXmlDSWebSrvc!=null)
        return secXmlDSWebSrvc.getWebServiceMapping();
    
    return null;
}
public List<UserSecurityObjectMapping> getAlluserSecObjMappings()
{
    System.out.println("get user mapping for all the users - getAlluserSecObjMappings" );
    if (secXmlDSUsrSecObjMapping!=null){
        System.out.println("secXmlDSUsrSecObjMapping.getUserSecurityObjectMapping().size: " + secXmlDSUsrSecObjMapping.getUserSecurityObjectMapping().size() );
        return secXmlDSUsrSecObjMapping.getUserSecurityObjectMapping();
    }
    return null;
}
public List<UserAddress> getUserAddresses(String userId)
{
    // TODO Auto-generated method stub
    return null;
}
public List<UserEmail> getUserEmailAddresses(String userId)
{
    // TODO Auto-generated method stub
    return null;
}
public List<WebServiceMapping> getUserWebserviceMappings(String User)
{
    // TODO Auto-generated method stub
    return null;
}
public UserSecurityObjectMapping getuserSecObjMapping(String userId)
{
    List<UserSecurityObjectMapping> allMappings = getAlluserSecObjMappings();
    System.out.println("No of mapping for user:" + userId + " = " + allMappings.size());
    for( int i=0; i < allMappings.size(); i++)
    {
        UserSecurityObjectMapping currentMapping = (UserSecurityObjectMapping) allMappings.get(i);
        System.out.println("Mapping loop " + i );
        System.out.println(currentMapping);
        if (currentMapping.getUserId().equals(userId)){
            System.out.println("returning mapping for " + userId );            
                    return currentMapping;
        }
    }
    return null;
}

public Permissions getUserObjectPermssions(String userId, int object)
{
    
    UserSecurityObjectMapping userObjMapping = getuserSecObjMapping(userId);
    
    if (userObjMapping!=null){
        System.out.println("Returning permission for " + userId );
        return userObjMapping.getPermissions();
    }
    return null;
}


public boolean validateUser(String userId, String password)
{
    List<User> allUsers = getAllUsers();
    for(User oneUser:getAllUsers())
    {
        if (oneUser.getUserId().equals(userId)){
            if (oneUser.getPassword().equals(password))
                if (oneUser.getActive())
                    return true;
        }
    }    
    // TODO Auto-generated method stub
    return false;
}
public SecurityAccessIF getSecurityAccess()
{
    // TODO Auto-generated method stub
    return this;
}
}
