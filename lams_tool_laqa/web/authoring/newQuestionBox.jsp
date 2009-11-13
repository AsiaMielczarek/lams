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

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
        "http://www.w3.org/TR/html4/strict.dtd">

<%@ include file="/common/taglibs.jsp"%>
<lams:html>
    <lams:head>
        <%@ include file="/common/header.jsp"%>
        <%@ include file="/includes/jsp/qaWizardCommon.jsp"%>
        
        <lams:css style="tabbed" />
        <script src="<lams:LAMSURL />/includes/javascript/jqgrid/js/jquery-1.2.6.pack.js"></script>
        <script language="JavaScript" type="text/JavaScript">

            function submitMethod() {
                document.QaAuthoringForm.submit();
            }
            
            function submitMethod(actionMethod) {
                document.QaAuthoringForm.dispatch.value=actionMethod; 
                document.QaAuthoringForm.submit();
            }
            
            <c:choose>
                <c:when test="${wizardEnabled == true}">

                    // Creating a 3-dimentional javascript array for category/cognitive skill/question
                    // -------------------------------------------------------------------------------
                    var categoryArray = new Array();
                    var categoryIndex = 0;
                    var skillIndex = 0;
                    var qIndex = 0;
                    <c:forEach var="category" items="${wizardCategories}">
                        categoryIndex = categoryArray.length;
                        addCategory(unescape("${category.title}"), "${category.uid}",  categoryArray.length);
                        <c:forEach var="skill" items="${category.cognitiveSkills}">
                            skillIndex = categoryArray[categoryIndex].skills.length;
                            addSkill(unescape("${skill.title}"), "${skill.uid}", categoryIndex , skillIndex);
                            <c:forEach var="question" items="${skill.questions}">
                                qIndex = categoryArray[categoryIndex].skills[skillIndex].questions.length;
                                addQuestion(unescape("${question.question}"), "${question.uid}", categoryIndex, skillIndex, qIndex)
                            </c:forEach>
                        </c:forEach>
                    </c:forEach>
                    // -------------------------------------------------------------------------------
                
                    // the menus
                    var catMenu = null;
                    var skillMenu = null;
                    var qMenu = null;
                
                    // Sets up the triple menu with the appropriate data
                    function setUpTripleMenu() {
                        var qaWizardEnabledBox = document.QaAuthoringForm.qaWizardEnabled;

                        catMenu = document.QaAuthoringForm.catMenu;
                        skillMenu = document.QaAuthoringForm.skillMenu;
                        qMenu = document.QaAuthoringForm.qMenu;
                        
                        nullOptions(catMenu);
                        nullOptions(skillMenu);
                        nullOptions(qMenu);
                        
                        var i;
                        with (catMenu) {
                            options[0] = new Option("<fmt:message key="wizard.selectCategory" />", "none");
                            for(i = 0; i < categoryArray.length; i++)
                            {
                                options[i+1] = new Option(categoryArray[i].title, i);
                            } 
                            options[0].selected = true;
                        }
                    }

                    var heightOffSet = 0;
                    $(document).ready(function() {
                        $("a#gwizard").click(function() {
                            $("div.wizard").toggle();
                            
                            if (heightOffSet == 0)
                            {
                                heightOffSet = document.getElementById('wizardDiv').offsetHeight;
                            }
                            window.top.resizeIframe(heightOffSet,0);
                            heightOffSet = heightOffSet * -1;
                        });
                    });
                    
                    // Inserts the question template into the new question fckeditor area
                    function useQuestionTemplate(aMenu) {
                        if (aMenu.selectedIndex > 0) {
                            var obj = document.getElementById("newQuestion");
                            obj.value += aMenu.options[aMenu.selectedIndex].text;
                            var oEditor = FCKeditorAPI.GetInstance("newQuestion");
                            oEditor.InsertHtml(aMenu.options[aMenu.selectedIndex].text);
                        } 
                    }
                
                </c:when>
                <c:otherwise>
                    function setUpTripleMenu() {}
                </c:otherwise>
            </c:choose>
        </script>
        
        <style>
            .wizard {
                background:url('../../images/css/greyfade_bg.jpg') repeat-x 3px 0px;
                border: 2px solid #EEEEEE;
            }
        </style> 
    </lams:head>

    
    <body onload="javascript:setUpTripleMenu();">
        <html:form action="/authoring?validate=false"
            styleId="newQuestionForm" enctype="multipart/form-data" method="POST">
            <html:hidden property="dispatch" value="addSingleQuestion" />
            <html:hidden property="toolContentID" />
            <html:hidden property="currentTab" styleId="currentTab" />
            <html:hidden property="activeModule" />
            <html:hidden property="httpSessionID" />
            <html:hidden property="defaultContentIdStr" />
            <html:hidden property="defineLaterInEditMode" />
            <html:hidden property="contentFolderID" />
            <html:hidden property="editQuestionBoxRequest" value="false" />
            
            <c:if test="${wizardEnabled == true}">
                <a style="float:right;" id="gwizard" href="#"><fmt:message key="wizard.author.wizardTitle" /></a>
                <div class="wizard" id="wizardDiv" style="display:none;">
                    <h3>
                        <fmt:message key="wizard.author.wizardTitle" />
                    </h3>
                    
                    <table border="0">
                        <tr>
                            <td colspan="3" valign="top" style="font-size:11px">
                                <fmt:message key="wizard.author.info1" /><br />
                                <fmt:message key="wizard.author.info2" /><br />
                                <fmt:message key="wizard.author.info3" /><br /> 
                            </td>
                        </tr>  
                        <tr align="center">
                            <td align="center" valign="top" width="171" style="font-size:11px">
                                <fmt:message key="wizard.selectCategory" />
                            </td>
                            <td align="center" valign="top" width="176" style="font-size:11px">
                                 <fmt:message key="wizard.selectSkill" />
                            </td>
                            <td align="center" valign="top" width="169" style="font-size:11px">
                                 <fmt:message key="wizard.selectQuestion" />
                            </td>
                        </tr>
                        <tr align="center">
                            <td align="center" valign="top" width="171">
                                <select name="catMenu" onchange="changeCategory()" size="1" style="font-size:10px; width:100%;">
                                </select> 
                            </td>
                            <td align="center" valign="top" width="176">
                                <select name="skillMenu" onchange="changeSkill()" size="1" style="font-size:10px; width:100%;">
                                </select> 
                            </td>
                            <td align="center" valign="top" width="169">
                                <select name="qMenu" onchange="useQuestionTemplate(this)" size="1" style="font-size:10px; width:100%;">
                                </select>
                            </td>
                        </tr>
                    </table>
                </div>
            </c:if>

            <div class="field-name space-top">
                <fmt:message key="label.add.new.question"></fmt:message>
            </div>

            <lams:FCKEditor id="newQuestion"
                value="${qaGeneralAuthoringDTO.editableQuestionText}"
                contentFolderID="${qaGeneralAuthoringDTO.contentFolderID}"></lams:FCKEditor>

            <div class="field-name space-top">
                <fmt:message key="label.feedback"></fmt:message>
            </div>
            <lams:STRUTS-textarea property="feedback" rows="3" cols="75"/>
            
            <lams:ImgButtonWrapper>
                <a href="#" onclick="getElementById('newQuestionForm').submit();"
                    class="button-add-item"> <fmt:message key="label.save.question" />
                </a>
                <a href="#" onclick="javascript:window.parent.hideMessage()"
                    class="button space-left"> <fmt:message key="label.cancel" /> </a>
            </lams:ImgButtonWrapper>
            
            
        </html:form>
    </body>
</lams:html>
