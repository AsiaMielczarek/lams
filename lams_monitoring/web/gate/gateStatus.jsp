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
<c:if test="${not GateForm.map.localFiles}">
	<p align="right">
			<c:if test="${not GateForm.map.gate.gateOpen}">
				<img height="43" src="../images/synch_active.gif" width="37"><br><span class="bodyBold"><fmt:message key="label.gate.closed"/></span>
			</c:if>
			<c:if test="${GateForm.map.gate.gateOpen}">
				<img height="43" src="../images/synch_not_active.gif" width="37"><br><span class="bodyBold"><fmt:message key="label.gate.gate.open"/></span>
			</c:if>
	</p>
</c:if>

<c:if test="${GateForm.map.localFiles}">
	<p align="left">
		<c:if test="${not GateForm.map.gate.gateOpen}">
				<span class="bodyBold"><fmt:message key="label.gate.closed"/></span>
			</c:if>
			<c:if test="${GateForm.map.gate.gateOpen}">
				<span class="bodyBold"><fmt:message key="label.gate.gate.open"/></span>
			</c:if>
	</p>
</c:if>


<c:if test="${not GateForm.map.readOnly}"> 
	<%@ include file="../template/finishbutton.jsp" %>
</c:if>
