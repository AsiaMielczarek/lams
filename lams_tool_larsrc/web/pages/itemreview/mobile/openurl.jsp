<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<lams:html>
	<lams:head>
		<%@ include file="/common/mobileheader.jsp"%>
	</lams:head>
	<body>
		<div data-role="page" data-cache="false">
		
			<div data-role="content">
				<h1>
					${title}
				</h1>
			
				<p>
					<a href="javascript:;" onclick="javascipt:launchPopup('${popupUrl}','popupUrl');"
						style="width:200px;float:none;" class="button">
						<fmt:message key="open.in.new.window" />
					</a>
				</p>
			</div>

		<div>
	</body>
</lams:html>
