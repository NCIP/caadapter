<%--L
  Copyright SAIC.

  Distributed under the OSI-approved BSD 3-Clause License.
  See http://ncip.github.com/caadapter/LICENSE.txt for details.
L--%>

<%@ page import="java.util.*" %>
<%@ taglib uri="/WEB-INF/tld/camod.tld" prefix="camod" %>

<DIV id="TipLayer" style="visibility:hidden;position:absolute;z-index:1000;top:-100;"></DIV>

 <table summary="" cellpadding="0" cellspacing="0" border="0" width="600" height="100%">

	  <!-- banner begins -->
	  <tr>
	    <td class="bannerHome"><img src="images/bannerHome.gif"></td>
	  </tr>
	  <!-- banner ends -->

	  <tr>
	    <td height="100%">
	    
	      <!-- target of anchor to skip menus -->
	      <a name="content" />                
	    
	      <table summary="" cellpadding="0" cellspacing="0" border="0" height="100%">
	        <tr>
	          <td>
	            <!-- welcome begins -->
	            <table width="601" height="100%" border="0" cellpadding="0" cellspacing="0" summary="" class="welcomeContentGray">
	   
	              <tr>
	                <td class="welcomeTitleCopy2" height="20">Welcome to the caAdapter Portal</td>
	              </tr>
	              
	              <tr>
	                <td valign="top"><br>                                

	                  <table width="587" border="0" align="center" cellpadding="0" cellspacing="10" class="contentPage">
	                      <tr valign="top">
                              <td width="50">
								<a href="/spores/searchSporeGrants.do"><img src="images/search.gif" alt="" name="" width="50" height="50" border="0"></a>
                              </td>
                              
                              <td width="216" height="70" class="homeMenuItem" onmouseover="changeMenuStyle(this,'homeMenuItemOver'),showCursor()" onmouseout="changeMenuStyle(this,'homeMenuItem'),hideCursor()" onclick="document.location.href='/spores/searchSporeGrants.do'">
                              	  <a href="/spores/searchSporeGrants.do"><strong>Search</span></strong></a><br>
								  Search SPORE Projects, Cores, Career Development Program, Development Research Projects, Clinical Interventions and Clinical Trials.
                              </td>
						  </tr>	   						  
	                      <tr valign="top">						                              
                              <td width="50">
                              	<a href="/spores/searchSporeGrants.do"><img src="images/reports.gif" alt="" name="" width="50" height="50" border="0"></a>
                              </td>
                              <td width="216" height=70 class="homeMenuItem" onmouseover="changeMenuStyle(this,'homeMenuItemOver'),showCursor()" onmouseout="changeMenuStyle(this,'homeMenuItem'),hideCursor()" onclick="document.location.href='/spores/searchSporeGrants.do'">
                              	  <a href="/spores/searchSporeGrants.do"><strong>Reports</span></strong></a><br>
                              	  Generate printer-friendly reports based on your search criteria
                              </td>
	                      </tr>
	                      <tr valign="top">	                              	                                      
                              <td width="50">
                              	<div align="center">
                              		<a href="/spores/admin.do"><img src="images/admin.gif" alt="" width="50" height="50" border="0"></a>
                              	</div>
                              </td>
                             <td width="221" height=70 class="homeMenuItem" onmouseover="changeMenuStyle(this,'homeMenuItemOver'),showCursor()" onmouseout="changeMenuStyle(this,'homeMenuItem'),hideCursor()" onclick="document.location.href='/spores/admin.do'">
                             	<a href="/spores/admin.do"><strong>Administrators</span></strong></a><br>
                              	Manage system users and system access, manage system data.
                              </td>
	                      </tr>
	                  </table>
	                  	                  
        	  </td></tr></table>	
			<!-- welcome ends -->	            
		</td></tr></table>
</td></tr></table>

<!--_____ main content ends _____-->
