<%
	 /****************************************************************
	 * Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)
	 * =============================================================
	 * License Information: http://lamsfoundation.org/licensing/lams/2.0/
	 * 
	 * This program is free software; you can redistribute it and/or modify
	 * it under the terms of the GNU General Public License version 2.0
	 * as published by the Free Software Foundation.
	 * 
	 * This program is distributed in the hope that it will be useful,
	 * but WITHOUT ANY WARRANTY; without even the implied warranty of
	 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	 * GNU General Public License for more details.
	 * 
	 * You should have received a copy of the GNU General Public License
	 * along with this program; if not, write to the Free Software
	 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
	 * USA
	 * 
	 * http://www.gnu.org/licenses/gpl.txt
	 * ****************************************************************
	 */

	/**
	 * HTMLEditor.tag
	 *	Author: Mitchell Seaton
	 *	Description: Creates a single instance of FCK Editor used over multiple fields.
	 * Wiki: 
	 */
%>
<%@ tag body-content="empty"%>
<%@ taglib uri="fck-editor" prefix="FCK"%>
<%@ taglib uri="tags-fmt" prefix="fmt"%>
<%@ taglib uri="tags-core" prefix="c"%>
<%@ taglib uri="tags-lams" prefix="lams"%>
<c:set var="lams">
	<lams:LAMSURL />
</c:set>

<div id="wyswygEditorScreen" style="visibility: hidden">
	<!-- position: absolute; z-index: 1000; top: 16px; left: 230px; -->
	<!--  Testing by Anthony, please delete this comment -->
	<div id="wyswygEditor">
		<div>
			<c:set var="language">
				<lams:user property="localeLanguage" />
			</c:set>

			<FCK:editor id="FCKeditor1" basePath="/lams/fckeditor/"
				imageBrowserURL="/FCKeditor/editor/filemanager/browser/default/browser.html?Type=Image&amp;Connector=connectors/jsp/connector"
				linkBrowserURL="/FCKeditor/editor/filemanager/browser/default/browser.html?Connector=connectors/jsp/connector"
				flashBrowserURL="/FCKeditor/editor/filemanager/browser/default/browser.html?Type=Flash&amp;Connector=connectors/jsp/connector"
				imageUploadURL="/FCKeditor/editor/filemanager/upload/simpleuploader?Type=Image"
				linkUploadURL="/FCKeditor/editor/filemanager/upload/simpleuploader?Type=File"
				flashUploadURL="/FCKeditor/editor/filemanager/upload/simpleuploader?Type=Flash"
				defaultLanguage="${language}" autoDetectLanguage="false">
			</FCK:editor>
		</div>
		<div style="text-align: center">
			<a href="#"
				onClick="saveWYSWYGEdittedText(activeEditorIndex); doPreview(activeEditorIndex)"><img
					src="${lams}images/tick.gif" border="0"
					alt="<fmt:message key="label.save"/>" />
			</a>
			<a href="#" onClick="doPreview(activeEditorIndex)"><img
					src="${lams}images/cross.gif" border="0"
					alt="<fmt:message key="label.cancel"/>" />
			</a>
		</div>
	</div>
</div>
