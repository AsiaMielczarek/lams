<%--
Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)
License Information: http://lamsfoundation.org/licensing/lams/2.0/

  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU General Public License version 2 as
  published by the Free Software Foundation.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA

  http://www.gnu.org/licenses/gpl.txt
--%>

<%@ taglib uri="tags-bean" prefix="bean"%> 
<%@ taglib uri="tags-html" prefix="html"%>
<%@ taglib uri="tags-logic" prefix="logic" %>
<%@ taglib uri="tags-core" prefix="c"%>
<%@ taglib uri="tags-fmt" prefix="fmt" %>
<%@ taglib uri="fck-editor" prefix="FCK" %>
<%@ taglib uri="tags-lams" prefix="lams" %>

<c:if test="${sbmtSuccess == 1}"> 			
	<logic:messagesPresent> 
	 <html:messages id="error"> 
		    <img src="images/success.gif" align="left" width=20 height=20>  <font size=2> <bean:write name="error"/> </font> </img>
	 </html:messages> 
	</logic:messagesPresent>
</c:if> 			


<c:if test="${sbmtSuccess != 1}"> 			
	<logic:messagesPresent> 
	 <html:messages id="error"> 
		    <img src="images/error.jpg" align="left" width=20 height=20>  <font size=2> <bean:write name="error"/> </font> </img>
	 </html:messages> 
	</logic:messagesPresent>
</c:if> 			



