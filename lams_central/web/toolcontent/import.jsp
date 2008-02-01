<%@ page language="java"  pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ taglib uri="tags-lams" prefix="lams"%>
<%@ taglib uri="tags-core" prefix="c"%>
<%@ taglib uri="tags-fmt" prefix="fmt"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<lams:html>
	<lams:head>
		<title><fmt:message key="title.import" /></title>
		<!-- ********************  CSS ********************** -->
		<lams:css />
		<script type="text/javascript">
			function closeWin(){
				window.close();
			}
			
			function verifyFilenameLength() {
				if (document.getElementById("UPLOAD_FILE").value.length == 0)	{
					var msg = "<fmt:message key="title.import.error"/>";
					alert(msg);
					return (false);
				}
				return (true);
			}
			
		</script>
	</lams:head>

	<body class="stripes">
			<div id="content">
			<h1>
				<fmt:message key="title.import" />
			</h1>

				<h2>
					<fmt:message key="title.import.instruction" />
				</h2>
				<p>
					&nbsp;
				</p>
				<p>
					<fmt:message key="msg.import.file.format"/>
				</p>
				<form action="<c:url value="/authoring/importToolContent.do"/>" method="post" enctype="multipart/form-data">
					<p>
						<fmt:message key="label.ld.zip.file" />
						<input type="file" name="UPLOAD_FILE" id="UPLOAD_FILE"/>
						<input type="submit" name="import" value="<fmt:message key="button.import" />" class="button" onclick="javascript:if (!verifyFilenameLength()) {return false;} "/>
					</p>
				</form>

			</div>
			<!--closes content-->


			<div id="footer">
			</div>
			<!--closes footer-->


	</body>
</lams:html>
