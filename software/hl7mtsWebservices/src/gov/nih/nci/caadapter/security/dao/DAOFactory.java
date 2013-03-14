/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.security.dao;

import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

import gov.nih.nci.caadapter.security.config.SecurityConfig;

/**
 * The DAO Factory is a typical factory design pattern implementation for creating
 * and serving concrete DAO implementations to the business objects. The business
 * objects use the DAO interfaces and are not aware of the implementation classes.
 * An application selects a data access mechanism with application deployment
 * configuration.
 * @version 1.0
 * @created 02-Jul-2008 1:23:42 PM
 */
public class DAOFactory {

    private static  AbstractSecurityDAO abstractDao;
    private static boolean isInitialized =false;

    /**
     * Obtains a concrete DAO object based on the application deployment configuration.
     */
    public static AbstractSecurityDAO getDAO(){
         if (abstractDao!=null)
             return abstractDao;
         else
             init();
         return abstractDao;
    }

    /**
     * Initialize the DAOFactory with the application deployment configuration
     */
    private static void init(){
        try {
            abstractDao=(AbstractSecurityDAO)Class.forName(SecurityConfig.SECURITY_DATA_ACCESS_OBJECT).newInstance();
            if (abstractDao!=null)
                System.out.println("DAOFactory: AbstractSecurityDAO initialized");
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}