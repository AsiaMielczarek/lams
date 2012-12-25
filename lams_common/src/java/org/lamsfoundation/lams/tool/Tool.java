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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301
 * USA
 * 
 * http://www.gnu.org/licenses/gpl.txt
 * ****************************************************************
 */
/* $$Id$$ */
package org.lamsfoundation.lams.tool;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import java.util.HashSet;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.lamsfoundation.lams.integration.ExtServerToolAdapterMap;
import org.lamsfoundation.lams.tool.dto.AuthoringToolDTO;

public class Tool implements Serializable {

    /** identifier field */
    private Long toolId;

    /** persistent field */
    private boolean supportsGrouping;

    /** persistent field */
    private String learnerUrl;

    /** persistent field */
    private String learnerPreviewUrl;

    /** persistent field */
    private String learnerProgressUrl;

    /** nullable persistent field */
    private String authorUrl;

    /** nullable persistent field */
    private String defineLaterUrl;

    /** persistent field */
    private String exportPortfolioLearnerUrl;

    /** persistent field */
    private String exportPortfolioClassUrl;

    /** persistent field */
    private String monitorUrl;

    /** persistent field */
    private String contributeUrl;

    /** persistent field */
    private String moderationUrl;

    /** persistent field */
    private String pedagogicalPlannerUrl;

    /** persistent field */
    private String helpUrl;

    /** persistent field */
    private String adminUrl;

    /** persistent field */
    private boolean supportsRunOffline;

    /** persistent field */
    private boolean valid;

    /** nullable persistent field */
    private long defaultToolContentId;

    /** persistent field */
    private String toolSignature;

    /** persistent field */
    private String toolDisplayName;

    /** nullable persistent field */
    private String description;

    /** persistent field */
    private String serviceName;

    /** persistent field */
    private Date createDateTime;

    /** persistent field */
    private Set activities;
    
    /** persistent field */
    private Set<ExtServerToolAdapterMap> mappedServers;

    /** persistent field */
    private Integer groupingSupportTypeId;

    /** persistent field */
    private String toolIdentifier;

    /** persistent field */
    private String toolVersion;

    /**
     * Name of the file (including the package) that contains the text strings for this activity. e.g.
     * org.lamsfoundation.lams.tool.sbmt.SbmtResources.properties.
     */
    private String languageFile;

    /** Does this tool produce output definitions / conditions */
    private Boolean supportsOutputs;

    /**
     * Null if not a tool adapter tool, otherwise this string maps to the external server which this tool adapter will
     * be used for
     */
    private String extLmsId;

    /**
     * Entries for an tool in a language property file
     */
    public static final String I18N_DISPLAY_NAME = "tool.display.name";
    public static final String I18N_DESCRIPTION = "tool.description";

    /** full constructor */
    public Tool(Long toolId, String learnerUrl, String learnerPreviewUrl, String learnerProgressUrl, String authorUrl,
	    String defineLaterUrl, String monitorUrl, String contributeUrl, String moderationUrl,
	    String exportPortfolioLearnerUrl, String exportPortfolioClassUrl, boolean supportsGrouping,
	    boolean supportsRunOffline, long defaultToolContentId, String toolSignature, String toolDisplayName,
	    String description, String className, Set activities, Integer groupingSupportTypeId, Date createDateTime,
	    String toolIdentifier, String toolVersion, String languageFile, boolean supportsOutputs, String extLmsId) {
	this.toolId = toolId;
	this.learnerUrl = learnerUrl;
	this.learnerPreviewUrl = learnerPreviewUrl;
	this.learnerProgressUrl = learnerProgressUrl;
	this.authorUrl = authorUrl;
	this.defineLaterUrl = defineLaterUrl;
	this.monitorUrl = monitorUrl;
	this.contributeUrl = contributeUrl;
	this.moderationUrl = moderationUrl;
	this.exportPortfolioLearnerUrl = exportPortfolioLearnerUrl;
	this.exportPortfolioClassUrl = exportPortfolioClassUrl;

	this.supportsGrouping = supportsGrouping;
	this.supportsRunOffline = supportsRunOffline;
	this.defaultToolContentId = defaultToolContentId;
	this.toolSignature = toolSignature;
	this.toolDisplayName = toolDisplayName;
	this.description = description;
	serviceName = className;
	this.activities = activities;
	this.groupingSupportTypeId = groupingSupportTypeId;
	this.createDateTime = createDateTime;
	this.toolIdentifier = toolIdentifier;
	this.toolVersion = toolVersion;
	this.languageFile = languageFile;
	this.supportsOutputs = supportsOutputs;
	this.extLmsId = extLmsId;
	this.mappedServers = new HashSet<ExtServerToolAdapterMap>();
    }

    /** default constructor */
    public Tool() {
    }

    /** minimal constructor */
    public Tool(Long toolId, String learnerUrl, String authorUrl, boolean supportsGrouping, boolean supportsRunOffline,
	    long defaultToolContentId, String toolSignature, String toolDisplayName, String className, Set activities,
	    Integer groupingSupportTypeId, Date createDateTime, String toolIdentifier, String toolVersion) {
	this.toolId = toolId;
	this.learnerUrl = learnerUrl;
	this.authorUrl = authorUrl;
	this.supportsGrouping = supportsGrouping;
	this.supportsRunOffline = supportsRunOffline;
	this.defaultToolContentId = defaultToolContentId;
	this.toolSignature = toolSignature;
	this.toolDisplayName = toolDisplayName;
	serviceName = className;
	this.activities = activities;
	this.groupingSupportTypeId = groupingSupportTypeId;
	this.createDateTime = createDateTime;
	this.toolIdentifier = toolIdentifier;
	this.toolVersion = toolVersion;
	this.mappedServers = new HashSet<ExtServerToolAdapterMap>();
    }

    public Long getToolId() {
	return toolId;
    }

    public void setToolId(Long toolId) {
	this.toolId = toolId;
    }

    public String getLearnerUrl() {
	return learnerUrl;
    }

    public void setLearnerUrl(String learnerUrl) {
	this.learnerUrl = learnerUrl;
    }

    public String getLearnerPreviewUrl() {
	return learnerPreviewUrl;
    }

    public void setLearnerPreviewUrl(String learnerPreviewUrl) {
	this.learnerPreviewUrl = learnerPreviewUrl;
    }

    public String getLearnerProgressUrl() {
	return learnerProgressUrl;
    }

    public void setLearnerProgressUrl(String learnerProgressUrl) {
	this.learnerProgressUrl = learnerProgressUrl;
    }

    /**
     * Does this tool support contribute? Will be true if the contributeURL is not null/empty string.
     */
    public boolean getSupportsContribute() {
	String contributeURL = getContributeUrl();
	return contributeURL != null && contributeURL.trim().length() > 0;
    }

    public String getAuthorUrl() {
	return authorUrl;
    }

    public void setAuthorUrl(String authorUrl) {
	this.authorUrl = authorUrl;
    }

    /**
     * Does this tool support define later? Will be true if the defineLaterURL is not null/empty string.
     */
    public boolean getSupportsDefineLater() {
	String url = getDefineLaterUrl();
	return url != null && url.trim().length() > 0;
    }

    /**
     * Does this tool support moderation? Will be true if the moderateURL is not null/empty string.
     */
    public boolean getSupportsModeration() {
	String url = getModerationUrl();
	return url != null && url.trim().length() > 0;
    }

    /**
     * @return Returns the supportsRunOffline.
     */
    public boolean getSupportsRunOffline() {
	return supportsRunOffline;
    }

    /**
     * @param supportsRunOffline
     *                The supportsRunOffline to set.
     */
    public void setSupportsRunOffline(boolean supportsRunOffline) {
	this.supportsRunOffline = supportsRunOffline;
    }

    public String getDefineLaterUrl() {
	return defineLaterUrl;
    }

    public void setDefineLaterUrl(String defineLaterUrl) {
	this.defineLaterUrl = defineLaterUrl;
    }

    public long getDefaultToolContentId() {
	return defaultToolContentId;
    }

    public void setDefaultToolContentId(long defaultToolContentId) {
	this.defaultToolContentId = defaultToolContentId;
    }

    public String getToolSignature() {
	return toolSignature;
    }

    public void setToolSignature(String toolSignature) {
	this.toolSignature = toolSignature;
    }

    public String getToolDisplayName() {
	return toolDisplayName;
    }

    public void setToolDisplayName(String toolDisplayName) {
	this.toolDisplayName = toolDisplayName;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public String getServiceName() {
	return serviceName;
    }

    public void setServiceName(String serviceName) {
	this.serviceName = serviceName;
    }

    public String getExportPortfolioLearnerUrl() {
	return exportPortfolioLearnerUrl;
    }

    public void setExportPortfolioLearnerUrl(String exportPortfolioLearnerUrl) {
	this.exportPortfolioLearnerUrl = exportPortfolioLearnerUrl;
    }

    public String getExportPortfolioClassUrl() {
	return exportPortfolioClassUrl;
    }

    public void setExportPortfolioClassUrl(String exportPortfolioClassUrl) {
	this.exportPortfolioClassUrl = exportPortfolioClassUrl;
    }

    public Set getActivities() {
	return activities;
    }

    public void setActivities(Set activities) {
	this.activities = activities;
    }

    /**
     * @return Returns the valid.
     */
    public boolean isValid() {
	return valid;
    }

    /**
     * @param valid
     *                The valid to set.
     */
    public void setValid(boolean valid) {
	this.valid = valid;
    }

    /**
     * @return Returns the groupingSupportTypeId.
     */
    public Integer getGroupingSupportTypeId() {
	return groupingSupportTypeId;
    }

    /**
     * @param groupingSupportTypeId
     *                The groupingSupportTypeId to set.
     */
    public void setGroupingSupportTypeId(Integer groupingSupportTypeId) {
	this.groupingSupportTypeId = groupingSupportTypeId;
    }

    /**
     * @return Returns the createDateTime.
     */
    public Date getCreateDateTime() {
	return createDateTime;
    }

    /**
     * @param createDateTime
     *                The createDateTime to set.
     */
    public void setCreateDateTime(Date createDateTime) {
	this.createDateTime = createDateTime;
    }

    /**
     * @return Returns the contributeUrl.
     */
    public String getContributeUrl() {
	return contributeUrl;
    }

    /**
     * @param contributeUrl
     *                The contributUrl to set.
     */
    public void setContributeUrl(String contributeUrl) {
	this.contributeUrl = contributeUrl;
    }

    /**
     * @return Returns the moderationUrl.
     */
    public String getModerationUrl() {
	return moderationUrl;
    }

    /**
     * @param moderationUrl
     *                The moderationUrl to set.
     */
    public void setModerationUrl(String moderationUrl) {
	this.moderationUrl = moderationUrl;
    }

    /**
     * @return Returns the monitorUrl.
     */
    public String getMonitorUrl() {
	return monitorUrl;
    }

    /**
     * @param monitorUrl
     *                The monitorUrl to set.
     */
    public void setMonitorUrl(String monitorUrl) {
	this.monitorUrl = monitorUrl;
    }

    /**
     * @return Returns the helpUrl.
     */
    public String getHelpUrl() {
	return helpUrl;
    }

    /**
     * @param helpUrl
     *                The helpUrl to set.
     */
    public void setHelpUrl(String helpUrl) {
	this.helpUrl = helpUrl;
    }

    /**
     * @return Returns the helpUrl.
     */
    public String getAdminUrl() {
	return adminUrl;
    }

    /**
     * @param helpUrl
     *                The helpUrl to set.
     */
    public void setAdminUrl(String adminUrl) {
	this.adminUrl = adminUrl;
    }

    public String getToolIdentifier() {
	return toolIdentifier;
    }

    public void setToolIdentifier(String toolIdentifier) {
	this.toolIdentifier = toolIdentifier;
    }

    public String getToolVersion() {
	return toolVersion;
    }

    public void setToolVersion(String toolVersion) {
	this.toolVersion = toolVersion;
    }

    public String getLanguageFile() {
	return languageFile;
    }

    public void setLanguageFile(String languageFile) {
	this.languageFile = languageFile;
    }

    /**
     * @return Returns the supportsOutputs.
     */
    public boolean getSupportsOutputs() {
	return supportsOutputs;
    }

    /**
     * @param supportsRunOffline
     *                The supportsRunOffline to set.
     */
    public void setSupportsOutputs(boolean supportsOutputs) {
	this.supportsOutputs = supportsOutputs;
    }

    public String getExtLmsId() {
	return extLmsId;
    }

    public void setExtLmsId(String extLmsId) {
	this.extLmsId = extLmsId;
    }

    @Override
    public String toString() {
	return new ToStringBuilder(this).append("toolId", getToolId()).toString();
    }

    @Override
    public boolean equals(Object other) {
	if (this == other) {
	    return true;
	}
	if (!(other instanceof Tool)) {
	    return false;
	}
	Tool castOther = (Tool) other;
	return new EqualsBuilder().append(this.getToolId(), castOther.getToolId()).isEquals();
    }

    @Override
    public int hashCode() {
	return new HashCodeBuilder().append(getToolId()).toHashCode();
    }

    public AuthoringToolDTO getAuthoringToolDTO() {
	return new AuthoringToolDTO(this);
    }

    public IToolVO createBasicToolVO() {
	IToolVO vo = new BasicToolVO(toolId, supportsGrouping, learnerUrl, learnerPreviewUrl, learnerProgressUrl,
		authorUrl, defineLaterUrl, exportPortfolioLearnerUrl, exportPortfolioClassUrl, monitorUrl,
		contributeUrl, moderationUrl, helpUrl, supportsRunOffline, defaultToolContentId, toolSignature,
		toolDisplayName, description, serviceName, createDateTime, groupingSupportTypeId, toolIdentifier,
		toolVersion, languageFile, supportsOutputs, extLmsId);
	return vo;
    }

    public String getPedagogicalPlannerUrl() {
	return pedagogicalPlannerUrl;
    }

    public void setPedagogicalPlannerUrl(String pedagogicalPlannerUrl) {
	this.pedagogicalPlannerUrl = pedagogicalPlannerUrl;
    }

    public Set<ExtServerToolAdapterMap> getMappedServers() {
        return mappedServers;
    }

    public void setMappedServers(Set<ExtServerToolAdapterMap> mappedServers) {
        this.mappedServers = mappedServers;
    }
}
