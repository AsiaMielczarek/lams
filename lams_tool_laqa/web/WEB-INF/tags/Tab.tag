<%/****************************************************************
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
			 * Tab.tag
			 *	Author: Mitchell Seaton
			 *	Description: Creates a tab element.
			 * Wiki: 
			 */

			%>
<%@ tag body-content="empty"%>
<%@ attribute name="id" required="true" rtexprvalue="true"%>
<%@ attribute name="value" required="false" rtexprvalue="true"%>
<%@ attribute name="key" required="false" rtexprvalue="true"%>
<%@ attribute name="inactive" required="false" rtexprvalue="true"%>
<%@ attribute name="methodCall" required="false" rtexprvalue="true"%>

<%@ taglib uri="tags-core" prefix="c"%>
<%@ taglib uri="tags-bean" prefix="bean"%>
<%@ taglib uri="tags-lams" prefix="lams"%>
<c:set var="lams">
	<lams:LAMSURL />
</c:set>
<c:set var="tabTitle" value="${value}" />
<c:set var="tableftFile" value="aqua_tab_left.gif" />
<c:set var="tabrightFile" value="aqua_tab_right.gif" />
<c:set var="tabcentreClass" value="tab tabcentre" />

<%// Usually methodCall is selectTab, but the calling code can override methodCall if desired.
			// this is handy if the page needs different logic on initialisation and user switching tabs %>
<c:if test="${methodCall == null}">
	<c:choose>
		<c:when test="${dControl}">
			<c:set var="methodCall" value="doSelectTab" />
		</c:when>
		<c:otherwise>
			<c:set var="methodCall" value="selectTab" />
		</c:otherwise>
	</c:choose>
</c:if>

<c:if test="${key != null && value == null}">
	<c:set var="tabTitle">
		<bean:message name="key" scope="page" />
	</c:set>
</c:if>

<c:if test="${inactive}">
	<c:set var="tableftFile" value="aqua_tab_i_left.gif" />
	<c:set var="tabrightFile" value="aqua_tab_i_right.gif" />
	<c:set var="tabcentreClass" value="tab tabcentre_inactive" />
</c:if>

<!-- 
<td>
	<table border="0" cellspacing="0" cellpadding="0" width="120" summary="This table is being used for layout purposes only">
		<tr>
			<td width="8">
				<a href="#" onClick="${methodCall}(${id});return false;"><img src="${lams}images/${tableftFile}" name="tableft_${id}" width="8" height="22" border="0" id="tableft_${id}" /></a>
			</td>
			<td class="${tabcentreClass}" id="tab${id}" nowrap="nowrap">
				<a href="#" onClick="${methodCall}(${id});return false;" id="${id}">${tabTitle}</a>
			</td>
			<td width="8">
				<a href="#" onClick="${methodCall}(${id});return false;"><img src="${lams}images/${tabrightFile}" name="tabright_${id}" width="8" height="22" border="0" id="tabright_${id}" /></a>
			</td>
		</tr>
	</table>
</td>
-->

<div class="nav-box">
	<div class="tab-left" id="tab-left-${id}"></div>
	<div class="tab-middle" id="tab-middle-${id}">
		<a class="tab-middle-link" id="tab-middle-link-${id}" href="#" onclick="${methodCall}(${id});return false;">${tabTitle}</a>
	</div>
	<div class="tab-right" id="tab-right-${id}"></div>
</div>
