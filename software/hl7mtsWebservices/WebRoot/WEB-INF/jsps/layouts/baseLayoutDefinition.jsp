<%@ taglib uri="/WEB-INF/tld/struts-tiles.tld" prefix="tiles" %>
<!-- base Layout of the web pages, to put header, footer... -->

<tiles:definition id="default" page="defaultLayout.jsp" scope="request">
    <tiles:put name="header" value="header.jsp" />
    <tiles:put name="applicationHeader" value="applicationHeader.jsp" />
    <tiles:put name="mainMenu" value="mainMenu.jsp" />    
    <tiles:put name="subMenu" value="subMenu.jsp" />       
    <tiles:put name="applicationFooter" value="applicationFooter.jsp" />
    <tiles:put name="footer" value="footer.jsp" />        
</tiles:definition>