/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.security.domain;
/**
 * The Permission object define the Permissions a user can have to access 
 * the data from security xmls.
 * @author : Sandeep Phadke
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2008-11-07 23:16:34 $
 *  * @created 24-Oct-2008 
 */

public class Permissions
{
    private boolean Create;
    private boolean Modify;
    private boolean Delete;
    private boolean Read;
    
    public Permissions()
    {}
    /**   Sets the value for variable Create.
     * @param String setValue
     */
    public void setCreate(boolean setValue){
        this.Create = setValue;
    }
   /**   gets the value for variable Create.
    * @return String Create
    */
    public boolean getCreate(){
           return this.Create ;
    }
    /**   Sets the value for variable Update.
    * @param String setValue
    */
    public void setModify(boolean setValue){
       this.Modify= setValue;
    }
    /**   gets the value for variable Update.
   * @return String Update
   */
    public boolean getModify(){
      return this.Modify ;
    }
    /**   Sets the value for variable Delete.
    * @param String setValue
    */
    public void setDelete(boolean setValue){
      this.Delete = setValue;
    }
    /**   gets the value for variable Delete.
     * @return String Delete
     */
    public boolean getDelete(){
        return this.Delete ;
    }
    /**   Sets the value for variable Read.
     * @param String setValue
     */
    public void setRead(boolean setValue){
        this.Read = setValue;
    }
    /**   gets the value for variable Read.
     * @return String Read
     */
    public boolean getRead(){
        return this.Read ;
    }
}
