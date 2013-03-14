/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.security.dao;

import gov.nih.nci.caadapter.security.dao.SecurityAccessIF;
/**
 * This object is define as the ancestor of any concrete DAO implementatoin object.
 * It is abstract class but it carries the knowledge to communicate with
 * application environment
 * @author : Sandeep Phadke
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2008-11-07 23:16:08 $
 * @created 24-Oct-2008 
*/
public abstract class AbstractSecurityDAO implements  SecurityAccessIF {

//  private boolean initialized;
//  public boolean isInitialized() {
//      return initialized;
//  }
//
//
//  public void setInitialized(boolean initialized) {
//      this.initialized = initialized;
//  }


    public AbstractSecurityDAO(){

    }
    
//  public abstract void init(Object path);
    public void finalize() throws Throwable {

    }

    public abstract SecurityAccessIF getSecurityAccess();
   
}