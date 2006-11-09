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
  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301
  USA

  http://www.gnu.org/licenses/gpl.txt
--%>
<%@ page language="java"%>

<%@ taglib uri="tags-bean" prefix="bean" %>
<%@ taglib uri="tags-html" prefix="html" %>
<%@ taglib uri="tags-logic" prefix="logic" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" 
		"http://www.w3.org/TR/html4/loose.dtd">

<html> 
	<head>
		<title>Add file or package to workspace</title>
	</head>
	<body>
		<H1>Upload New Content</H1>
		
		<p>Upload some new content. You can either upload a single file or enter a directory name 
  			 to upload a package.
			 You cannot do both. The content will be stored in the repository and it will also be
			 stored in a map in the session. This map is the equivalent to an application storing the
			 node uuid and version ids in its own database tables. For the rest of this session,
			 this data will be used to indicate the filename/directory names of the nodes
			 in the node list below.</p>
			 
		<html:form action="/addFileContent" method="POST" enctype="multipart/form-data">
 			<% String uuid = request.getParameter("uuid");
			   if ( uuid != null && uuid.length() > 0 ) {
			%>
				<p>Adding a new version for Node <%=uuid%>. If the original node was a package,
				then you can only upload another package. If the original node was a file, you can
				only upload a file. <html:hidden property="uuid" value="<%=uuid%>"/> 
				</p>
			<% } %>

				
			<p>Version Description (Required for both): <html:text property="description" size="100"/></P>
			<% String type = request.getParameter("type");
				if ( type == null || type.equals("FILENODE") ) {
			%>
					<H3>Single File:</H3>
					<p>File to upload?&nbsp;<html:file property="theFile"/><html:errors property="theFile"/> 
					<html:submit property="method" value="uploadFile"/>
			<%
				} 
				if ( type == null || type.equals("PACKAGENODE") ) {
			%>
					<H3>Package:</H3>
					<p>Directory name:&nbsp;<html:text property="dirName" size="50"/> 
					Name&nbsp;of&nbsp;initial&nbsp;file&nbsp;in&nbsp;directory.&nbsp;<html:text property="entryString"/>
					<html:submit property="method" value="uploadPackage"/>
			<% 
				}
			%>

		<p>&nbsp;</p>
		<p>
		<html:submit property="method" value="logout"/>
		</html:form>
		<form>
			<INPUT TYPE="button" VALUE="View Node List" onClick="parent.location='nodeSelection.do?method=getList'">
		</form>
	
	</body>
</html>
