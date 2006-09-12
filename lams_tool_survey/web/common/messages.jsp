<%-- Error Messages --%>
<logic:messagesPresent>
    <div class="error" id="errorMessages">
        <html:messages id="error">
            <html:img page="/includes/images/iconWarning.gif" 
               styleClass="icon"/>
            <c:out value="${error}" escapeXml="false"/><br/>
        </html:messages>
    </div>
</logic:messagesPresent>

<%-- Success Messages --%>
<logic:messagesPresent message="true">
    <div class="message" id="successMessages">
        <html:messages id="message" message="true">
            <html:img page="/includes/images/iconInformation.gif" styleClass="icon"/>
            <c:out value="${message}" escapeXml="false"/><br/>
        </html:messages>
    </div>
</logic:messagesPresent>
