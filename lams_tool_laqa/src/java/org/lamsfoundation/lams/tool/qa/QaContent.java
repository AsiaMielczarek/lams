/****************************************************************
 * Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)
 * =============================================================
 * License Information: http://lamsfoundation.org/licensing/lams/2.0/
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301
 * USA
 * 
 * http://www.gnu.org/licenses/gpl.txt
 * ****************************************************************
 */

/* $$Id$$ */
package org.lamsfoundation.lams.tool.qa;

import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;
import org.lamsfoundation.lams.contentrepository.ItemNotFoundException;
import org.lamsfoundation.lams.contentrepository.RepositoryCheckedException;
import org.lamsfoundation.lams.contentrepository.client.IToolContentHandler;
import org.lamsfoundation.lams.learningdesign.TextSearchConditionComparator;

/**
 * @author Ozgur Demirtas
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */

/**
 * QaContent Value Object The value object that maps to our model database
 * table: tl_laqa11_content The relevant hibernate mapping resides in:
 * QaContent.hbm.xml
 * 
 * Holds content representation for the tool. Default content is made available
 * to the tool by the database.
 */
public class QaContent implements Serializable {
    static Logger logger = Logger.getLogger(QaContent.class.getName());

    /** identifier field */
    private Long uid;

    /** identifier field */
    private Long qaContentId;

    /** nullable persistent field */
    private String title;

    /** nullable persistent field */
    private String instructions;

    /** persistent field, used for export portfolio */
    private String content;

    /** nullable persistent field */
    private String reportTitle;

    /** nullable persistent field */
    private String monitoringReportTitle;

    /** nullable persistent field */
    private String offlineInstructions;

    /** nullable persistent field */
    private String onlineInstructions;

    /** nullable persistent field */
    private long createdBy;

    /** nullable persistent field */
    private boolean defineLater;

    private boolean reflect;

    private String reflectionSubject;

    /** nullable persistent field */
    private boolean runOffline;

    private boolean questionsSequenced;

    private boolean lockWhenFinished;

    private boolean showOtherAnswers;
    
    private boolean allowRichEditor;

    /** nullable persistent field */
    private boolean usernameVisible;

    /** nullable persistent field */
    private boolean synchInMonitor;

    /** nullable persistent field */
    private boolean contentLocked;

    /** nullable persistent field */
    private Date creationDate;

    /** nullable persistent field */
    private Date updateDate;

    /** persistent field */
    private Set qaQueContents;

    /** persistent field */
    private Set qaSessions;

    /** persistent field */
    private Set qaUploadedFiles;
    /** persistent field */
    private Set<QaCondition> conditions = new TreeSet<QaCondition>(new TextSearchConditionComparator());

    public QaContent() {
    };

    /** full constructor */
    public QaContent(Long qaContentId, String content, String title, String instructions, String reportTitle,
	    String monitoringReportTitle, String offlineInstructions, String onlineInstructions, long createdBy,
	    boolean defineLater, boolean runOffline, boolean questionsSequenced, boolean usernameVisible,
	    boolean synchInMonitor, boolean lockWhenFinished, boolean contentLocked, boolean showOtherAnswers,
	    boolean reflect, String reflectionSubject, Date creationDate, Date updateDate, Set qaQueContents,
	    Set qaSessions, Set qaUploadedFiles, Set<QaCondition> conditions, boolean allowRichEditor) {
	this.qaContentId = qaContentId;
	this.content = content;
	this.title = title;
	this.instructions = instructions;
	this.reportTitle = reportTitle;
	this.monitoringReportTitle = monitoringReportTitle;
	this.offlineInstructions = offlineInstructions;
	this.onlineInstructions = onlineInstructions;
	this.createdBy = createdBy;
	this.defineLater = defineLater;
	this.runOffline = runOffline;
	this.questionsSequenced = questionsSequenced;
	this.usernameVisible = usernameVisible;
	this.synchInMonitor = synchInMonitor;
	this.lockWhenFinished = lockWhenFinished;
	this.contentLocked = contentLocked;
	this.showOtherAnswers = showOtherAnswers;
	this.reflect = reflect;
	this.reflectionSubject = reflectionSubject;
	this.creationDate = creationDate;
	this.updateDate = updateDate;
	this.qaQueContents = qaQueContents;
	this.qaSessions = qaSessions;
	this.qaUploadedFiles = qaUploadedFiles;
	this.conditions = conditions;
	this.allowRichEditor = allowRichEditor;
	QaContent.logger.debug(QaContent.logger + " " + this.getClass().getName() + "in full constructor: QaContent()");
    }

    /**
     * Copy Construtor to create a new qa content instance. Note that we don't
     * copy the qa session data here because the qa session will be created
     * after we copied tool content.
     * 
     * @param qa
     *                the original qa content.
     * @param newContentId
     *                the new qa content id.
     * @return the new qa content object.
     */
    public static QaContent newInstance(IToolContentHandler toolContentHandler, QaContent qa, Long newContentId)
	    throws ItemNotFoundException, RepositoryCheckedException {
	QaContent newContent = new QaContent(newContentId, qa.getContent(), qa.getTitle(), qa.getInstructions(), qa
		.getReportTitle(), qa.getMonitoringReportTitle(), qa.getOfflineInstructions(), qa
		.getOnlineInstructions(), qa.getCreatedBy(), qa.isDefineLater(), qa.isRunOffline(), qa
		.isQuestionsSequenced(), qa.isUsernameVisible(), qa.isSynchInMonitor(), qa.isLockWhenFinished(), qa
		.isContentLocked(), qa.isShowOtherAnswers(), qa.isReflect(), qa.getReflectionSubject(), qa
		.getCreationDate(), qa.getUpdateDate(), new TreeSet(), new TreeSet(), new TreeSet(),
		new TreeSet<QaCondition>(new TextSearchConditionComparator()), qa.isAllowRichEditor());

	newContent.setQaQueContents(qa.deepCopyQaQueContent(newContent));

	newContent.setQaUploadedFiles(qa.deepCopyQaAttachments(toolContentHandler, newContent));
	newContent.setConditions(qa.deepCopyConditions(newContent));
	return newContent;
    }

    public Set deepCopyQaQueContent(QaContent newQaContent) {
	Set newQaQueContent = new TreeSet();
	for (Iterator i = this.getQaQueContents().iterator(); i.hasNext();) {
	    QaQueContent queContent = (QaQueContent) i.next();
	    newQaQueContent.add(QaQueContent.newInstance(queContent, newQaContent));
	}
	return newQaQueContent;
    }

    public Set deepCopyQaAttachments(IToolContentHandler toolContentHandler, QaContent newQaContent)
	    throws ItemNotFoundException, RepositoryCheckedException {
	Set attachments = new TreeSet();
	for (Iterator i = this.getQaUploadedFiles().iterator(); i.hasNext();) {
	    QaUploadedFile qaUploadedFile = (QaUploadedFile) i.next();
	    if (qaUploadedFile.getQaContent() != null) {
		QaUploadedFile newQaUploadedFile = QaUploadedFile.newInstance(toolContentHandler, qaUploadedFile,
			newQaContent);
		attachments.add(newQaUploadedFile);
	    }
	}
	return attachments;
    }

    public Set<QaCondition> deepCopyConditions(QaContent newQaContent) {

	Set<QaCondition> newConditions = new TreeSet<QaCondition>(new TextSearchConditionComparator());
	if (getConditions() != null) {
	    for (QaCondition condition : getConditions()) {
		newConditions.add(condition.clone(newQaContent));
	    }
	}

	return newConditions;
    }

    public Set deepCopyQaSession(QaContent newQaSession) {
	return new TreeSet();
    }

    public Set getQaQueContents() {
	if (qaQueContents == null) {
	    setQaQueContents(new TreeSet());
	}
	return qaQueContents;
    }

    public void setQaQueContents(Set qaQueContents) {
	this.qaQueContents = qaQueContents;
    }

    public Set getQaSessions() {
	if (qaSessions == null) {
	    setQaSessions(new TreeSet());
	}
	return qaSessions;
    }

    public void setQaSessions(Set qaSessions) {
	this.qaSessions = qaSessions;
    }

    /**
     * @return Returns the qaContentId.
     */
    public Long getQaContentId() {
	return qaContentId;
    }

    /**
     * @param qaContentId
     *                The qaContentId to set.
     */
    public void setQaContentId(Long qaContentId) {
	this.qaContentId = qaContentId;
    }

    /**
     * @return Returns the title.
     */
    public String getTitle() {
	return title;
    }

    /**
     * @param title
     *                The title to set.
     */
    public void setTitle(String title) {
	this.title = title;
    }

    @Override
    public String toString() {
	return new ToStringBuilder(this).append("qaContentId:", getQaContentId()).append("qa title:", getTitle())
		.append("qa instructions:", getInstructions()).append("creator user id", getCreatedBy()).append(
			"username_visible:", isUsernameVisible()).append("defineLater", isDefineLater()).append(
			"offline_instructions:", getOfflineInstructions()).append("online_instructions:",
			getOnlineInstructions()).append("report_title: ", getReportTitle()).append(
			"reflection subject: ", getReflectionSubject())
		.append("synch_in_monitor: ", isSynchInMonitor()).toString();
    }

    @Override
    public boolean equals(Object other) {
	if (!(other instanceof QaContent)) {
	    return false;
	}
	QaContent castOther = (QaContent) other;
	return new EqualsBuilder().append(this.getQaContentId(), castOther.getQaContentId()).isEquals();
    }

    @Override
    public int hashCode() {
	return new HashCodeBuilder().append(getQaContentId()).toHashCode();
    }

    /**
     * @return Returns the createdBy.
     */
    public long getCreatedBy() {
	return createdBy;
    }

    /**
     * @param createdBy
     *                The createdBy to set.
     */
    public void setCreatedBy(long createdBy) {
	this.createdBy = createdBy;
    }

    /**
     * @return Returns the defineLater.
     */
    public boolean isDefineLater() {
	return defineLater;
    }

    /**
     * @param defineLater
     *                The defineLater to set.
     */
    public void setDefineLater(boolean defineLater) {
	this.defineLater = defineLater;
    }

    /**
     * @return Returns the instructions.
     */
    public String getInstructions() {
	return instructions;
    }

    /**
     * @param instructions
     *                The instructions to set.
     */
    public void setInstructions(String instructions) {
	this.instructions = instructions;
    }

    /**
     * @return Returns the offlineInstructions.
     */
    public String getOfflineInstructions() {
	return offlineInstructions;
    }

    /**
     * @param offlineInstructions
     *                The offlineInstructions to set.
     */
    public void setOfflineInstructions(String offlineInstructions) {
	this.offlineInstructions = offlineInstructions;
    }

    /**
     * @return Returns the onlineInstructions.
     */
    public String getOnlineInstructions() {
	return onlineInstructions;
    }

    /**
     * @param onlineInstructions
     *                The onlineInstructions to set.
     */
    public void setOnlineInstructions(String onlineInstructions) {
	this.onlineInstructions = onlineInstructions;
    }

    /**
     * @return Returns the reportTitle.
     */
    public String getReportTitle() {
	return reportTitle;
    }

    /**
     * @param reportTitle
     *                The reportTitle to set.
     */
    public void setReportTitle(String reportTitle) {
	this.reportTitle = reportTitle;
    }

    /**
     * @return Returns the synchInMonitor.
     */
    public boolean isSynchInMonitor() {
	return synchInMonitor;
    }

    /**
     * @param synchInMonitor
     *                The synchInMonitor to set.
     */
    public void setSynchInMonitor(boolean synchInMonitor) {
	this.synchInMonitor = synchInMonitor;
    }

    /**
     * @return Returns the updateDate.
     */
    public Date getUpdateDate() {
	return updateDate;
    }

    /**
     * @param updateDate
     *                The updateDate to set.
     */
    public void setUpdateDate(Date updateDate) {
	this.updateDate = updateDate;
    }

    /**
     * @return Returns the usernameVisible.
     */
    public boolean isUsernameVisible() {
	return usernameVisible;
    }

    /**
     * @param usernameVisible
     *                The usernameVisible to set.
     */
    public void setUsernameVisible(boolean usernameVisible) {
	this.usernameVisible = usernameVisible;
    }

    /**
     * @return Returns the questionsSequenced.
     */
    public boolean isQuestionsSequenced() {
	return questionsSequenced;
    }

    /**
     * @param questionsSequenced
     *                The questionsSequenced to set.
     */
    public void setQuestionsSequenced(boolean questionsSequenced) {
	this.questionsSequenced = questionsSequenced;
    }

    /**
     * @return Returns the runOffline.
     */
    public boolean isRunOffline() {
	return runOffline;
    }

    /**
     * @param runOffline
     *                The runOffline to set.
     */
    public void setRunOffline(boolean runOffline) {
	this.runOffline = runOffline;
    }

    /**
     * @return Returns the monitoringReportTitle.
     */
    public String getMonitoringReportTitle() {
	return monitoringReportTitle;
    }

    /**
     * @param monitoringReportTitle
     *                The monitoringReportTitle to set.
     */
    public void setMonitoringReportTitle(String monitoringReportTitle) {
	this.monitoringReportTitle = monitoringReportTitle;
    }

    /**
     * @return Returns the contentLocked.
     */
    public boolean isContentLocked() {
	return contentLocked;
    }

    /**
     * @param contentLocked
     *                The contentLocked to set.
     */
    public void setContentLocked(boolean contentLocked) {
	this.contentLocked = contentLocked;
    }

    /**
     * @return Returns the qaUploadedFiles.
     */
    public Set getQaUploadedFiles() {
	if (qaUploadedFiles == null) {
	    qaUploadedFiles = new TreeSet();
	}
	return qaUploadedFiles;
    }

    /**
     * @param qaUploadedFiles
     *                The qaUploadedFiles to set.
     */
    public void setQaUploadedFiles(Set qaUploadedFiles) {
	this.qaUploadedFiles = qaUploadedFiles;
    }

    /**
     * @return Returns the uid.
     */
    public Long getUid() {
	return uid;
    }

    /**
     * @param uid
     *                The uid to set.
     */
    public void setUid(Long uid) {
	this.uid = uid;
    }

    /**
     * @return Returns the content.
     */
    public String getContent() {
	return content;
    }

    /**
     * @param content
     *                The content to set.
     */
    public void setContent(String content) {
	this.content = content;
    }

    /**
     * @return Returns the logger.
     */
    public static Logger getLogger() {
	return QaContent.logger;
    }

    /**
     * @param logger
     *                The logger to set.
     */
    public static void setLogger(Logger logger) {
	QaContent.logger = logger;
    }

    /**
     * @return Returns the creationDate.
     */
    public Date getCreationDate() {
	return creationDate;
    }

    /**
     * @param creationDate
     *                The creationDate to set.
     */
    public void setCreationDate(Date creationDate) {
	this.creationDate = creationDate;
    }

    /**
     * @return Returns the reflect.
     */
    public boolean isReflect() {
	return reflect;
    }

    /**
     * @param reflect
     *                The reflect to set.
     */
    public void setReflect(boolean reflect) {
	this.reflect = reflect;
    }

    /**
     * @return Returns the reflectionSubject.
     */
    public String getReflectionSubject() {
	return reflectionSubject;
    }

    /**
     * @param reflectionSubject
     *                The reflectionSubject to set.
     */
    public void setReflectionSubject(String reflectionSubject) {
	this.reflectionSubject = reflectionSubject;
    }

    /**
     * @return Returns the lockWhenFinished.
     */
    public boolean isLockWhenFinished() {
	return lockWhenFinished;
    }

    /**
     * @param lockWhenFinished
     *                The lockWhenFinished to set.
     */
    public void setLockWhenFinished(boolean lockWhenFinished) {
	this.lockWhenFinished = lockWhenFinished;
    }
    
    
    public boolean isAllowRichEditor() {
        return allowRichEditor;
    }

    public void setAllowRichEditor(boolean allowRichEditor) {
        this.allowRichEditor = allowRichEditor;
    }

    /**
     * @return Returns the showOtherAnswers.
     */
    public boolean isShowOtherAnswers() {
	return showOtherAnswers;
    }

    /**
     * @param showOtherAnswers
     *                The showOtherAnswers to set.
     */
    public void setShowOtherAnswers(boolean showOtherAnswers) {
	this.showOtherAnswers = showOtherAnswers;
    }

    public Set<QaCondition> getConditions() {
	return conditions;
    }

    public void setConditions(Set<QaCondition> conditions) {
	this.conditions = conditions;
    }
}