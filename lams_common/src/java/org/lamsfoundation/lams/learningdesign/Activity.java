/***************************************************************************
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
 * ************************************************************************
 */
/* $$Id$$ */
package org.lamsfoundation.lams.learningdesign;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.lamsfoundation.lams.learningdesign.dto.AuthoringActivityDTO;
import org.lamsfoundation.lams.learningdesign.dto.BranchActivityEntryDTO;
import org.lamsfoundation.lams.learningdesign.dto.LibraryActivityDTO;
import org.lamsfoundation.lams.learningdesign.dto.ProgressActivityDTO;
import org.lamsfoundation.lams.learningdesign.dto.ValidationErrorDTO;
import org.lamsfoundation.lams.usermanagement.User;
import org.lamsfoundation.lams.util.MessageService;
import org.lamsfoundation.lams.util.Nullable;

/**
 * Base class for all activities. If you add another subclass, 
 * you must update ActivityDAO.getActivityByActivityId() and add a  
 * ACTIVITY_TYPE constant.
 * 
 * @hibernate.class table="lams_learning_activity"
 */
public abstract class Activity implements Serializable,Nullable {

    //---------------------------------------------------------------------
    // Class Level Constants
    //---------------------------------------------------------------------
	/*
	 * static final variables indicating the type of activities
	 * available for a LearningDesign. As new types of activities 
	 * are added, these constants must be updated, as well as 
	 * ActivityDAO.getActivityByActivityId()
	 */
	/* *****************************************************************/
	public static final int TOOL_ACTIVITY_TYPE = 1;
	public static final int GROUPING_ACTIVITY_TYPE = 2;
	public static final int SYNCH_GATE_ACTIVITY_TYPE = 3;
	public static final int SCHEDULE_GATE_ACTIVITY_TYPE = 4;
	public static final int PERMISSION_GATE_ACTIVITY_TYPE = 5;
	public static final int PARALLEL_ACTIVITY_TYPE = 6;
	public static final int OPTIONS_ACTIVITY_TYPE = 7;
	public static final int SEQUENCE_ACTIVITY_TYPE = 8;
	public static final int SYSTEM_GATE_ACTIVITY_TYPE = 9;
	public static final int CHOSEN_BRANCHING_ACTIVITY_TYPE = 10;
	public static final int GROUP_BRANCHING_ACTIVITY_TYPE = 11;
	public static final int TOOL_BRANCHING_ACTIVITY_TYPE = 12;
	/******************************************************************/
	
	/**
	* static final variables indicating the the category of activities
    *******************************************************************/
	public static final int CATEGORY_SYSTEM = 1;
	public static final int CATEGORY_COLLABORATION = 2;
	public static final int CATEGORY_ASSESSMENT = 3;
	public static final int CATEGORY_CONTENT = 4;
	public static final int CATEGORY_SPLIT = 5;
	public static final int CATEGORY_RESPONSE = 6;
	/******************************************************************/
	
	/**
	 * static final variables indicating the grouping_support of activities
	 *******************************************************************/
	public static final int GROUPING_SUPPORT_NONE = 1;
	public static final int GROUPING_SUPPORT_OPTIONAL = 2;
	public static final int GROUPING_SUPPORT_REQUIRED = 3;
	/******************************************************************/
	
	
	/** 
	 * Entries for an activity in a language property file 
	 */
	public static final String I18N_TITLE = "activity.title";
	public static final String I18N_DESCRIPTION = "activity.description";
	public static final String I18N_HELP_TEXT = "activity.helptext";

	
    //---------------------------------------------------------------------
    // Instance variables
    //---------------------------------------------------------------------
	/** WDDX packet specific attribute created to identify the
	 * type of object being passed.*/
	public static final String OBJECT_TYPE ="Activity";

	/** identifier field */
	private Long activityId;

	/** FLASH generated value. Unique per LearningDesign.
	 * Required by flash only.*/
	private Integer activityUIID;

	/** Description of the activity*/
	private String description;

	/** Title of the activity*/
	private String title;
	
	/** Help text for the activity*/
	private String helpText;

	/** UI specific attribute indicating the
	 * position of the activity*/
	private Integer xcoord;

	/** UI specific attribute indicating the
	 * position of the activity*/
	private Integer ycoord;

	/** Indicates the order in which the activities
	 * appear inside complex activities. Starts from 
	 * 0, 1 and so on.*/
	private Integer orderId;

	/** Indicates whether the content of this activity 
	 * would be defined later in the monitoring environment or not.*/
	private Boolean defineLater;
	
	/** Indicates whether this activity is available offline*/
	private Boolean runOffline;

	/** Date this activity was created */
	private Date createDateTime;

	/** The image that represents the icon of this 
	 * activity in the UI*/
	private String libraryActivityUiImage;

	/** The LearningLibrary of which this activity is a part*/
	private LearningLibrary learningLibrary;

	/** The activity that acts as a container/parent for
	 * this activity. Normally would be one of the 
	 * complex activities which have child activities 
	 * defined inside them. */
	private Activity parentActivity;
	
	/** Single Library can have one or more activities
	 * defined inside it. This field indicates which
	 * activity is this.*/
	private Activity libraryActivity;

	/** The LearningDesign to which this activity belongs*/
	private LearningDesign learningDesign;

	/** The Grouping that applies to this activity*/
	private Grouping grouping;
	
	/** The grouping_ui_id of the Grouping that
	 * applies that to this activity
	 * */
	private Integer groupingUIID;

	/** The type of activity */
	private Integer activityTypeId;
	
	/** The category of activity */
	private Integer activityCategoryID;

	/** persistent field */
	private Transition transitionTo; 
	
	/** persistent field */
	private Transition transitionFrom;
	
	/** the activity_ui_id of the parent activity */
	private Integer parentUIID;
	
	private Boolean applyGrouping;
	
	private Integer groupingSupportType;

	/** Name of the file (including the package) that contains the text strings for
	 * this activity. e.g. org.lamsfoundation.lams.tool.sbmt.SbmtResources.properties. */
	private String languageFile;

	/** An activity is readOnly when a learner starts doing the activity.
	 * Used in editOnFly. */
	private Boolean readOnly;

	/** An activity is initialised if it is ready to be used in lesson ie the tool content
	 * is set up, schedule gates are scheduled, etc. Used to detect which activities
	 * need to be initialised for live edit. */
	private Boolean initialised;

	/** If stopAfterActivity is true, then the progress engine should "end" the lesson at this point.
	 * Used to arbitrarily stop somewhere in a design, such as at the end of the branch. The normal
	 * final activity of a design does not necessarily have this set - the progress engine will just
	 * stop when it runs out of transitions to follow. */
	private Boolean stopAfterActivity;
	
    /**
     * The activities that supplied inputs to this activity.
     */
    private Set inputActivities;
    
 
   //---------------------------------------------------------------------
    // Object constructors
    //---------------------------------------------------------------------
	
	/* For the createDateTime fields, if the value is null, then it will default
	 * to the current time.
	 */
	
	/** full constructor */
	public Activity(
			Long activityId,
			Integer id,
			String description,
			String title,
			Integer xcoord,
			Integer ycoord,
			Integer orderId,
			Boolean defineLater,
			Date createDateTime,
			LearningLibrary learningLibrary,
			Activity parentActivity,
			Activity libraryActivity,
			Integer parentUIID,
			LearningDesign learningDesign,
			Grouping grouping,
			Integer activityTypeId, 
			Transition transitionTo,
			Transition transitionFrom,
			String languageFile,
			Boolean stopAfterActivity,
			Set inputActivities) {
		this.activityId = activityId;
		this.activityUIID = id;
		this.description = description;
		this.title = title;
		this.xcoord = xcoord;
		this.ycoord = ycoord;
		this.orderId = orderId;
		this.defineLater = defineLater;
		this.createDateTime = createDateTime != null ? createDateTime : new Date();
		this.learningLibrary = learningLibrary;
		this.parentActivity = parentActivity;
		this.parentUIID = parentUIID;
		this.libraryActivity = libraryActivity;
		this.learningDesign = learningDesign;
		this.grouping = grouping;
		this.activityTypeId = activityTypeId;
		this.transitionTo = transitionTo;
		this.transitionFrom = transitionFrom;
		this.languageFile = languageFile;
		this.readOnly = false;
		this.initialised = false;
		this.stopAfterActivity = stopAfterActivity;
		this.inputActivities = inputActivities;
	}	
	/** default constructor */
	public Activity() {
		this.grouping = null;	
		this.createDateTime = new Date(); //default value is set to when the object is created
		this.readOnly = false;
		this.initialised = false;
		this.stopAfterActivity = false;
	}

	/** minimal constructor */
	public Activity(
			Long activityId,
			Boolean defineLater,
			Date createDateTime,
			LearningLibrary learningLibrary,
			Activity parentActivity,
			LearningDesign learningDesign,
			Grouping grouping,
			Integer activityTypeId,
			Transition transitionTo,
			Transition transitionFrom) {
		this.activityId = activityId;
		this.defineLater = defineLater;
		this.createDateTime = createDateTime != null ? createDateTime : new Date();
		this.learningLibrary = learningLibrary;
		this.parentActivity = parentActivity;
		this.learningDesign = learningDesign;
		this.grouping = grouping;
		this.activityTypeId = activityTypeId;
		this.transitionTo = transitionTo;
		this.transitionFrom = transitionFrom;
		this.readOnly = false;
		this.initialised = false;
		this.stopAfterActivity = false;
	}
	
	public static Activity getActivityInstance(int activityType)
	{
		// the default constructors don't set up the activity type
		// so we need to do that manually
		// also default to a sensible category type
		Activity activity = null;
		switch(activityType){
			case TOOL_ACTIVITY_TYPE:
				activity = new ToolActivity();
				activity.setActivityCategoryID(CATEGORY_CONTENT);
				break;
			case OPTIONS_ACTIVITY_TYPE:
				activity = new OptionsActivity();
				activity.setActivityCategoryID(CATEGORY_SYSTEM);
				break;
			case PARALLEL_ACTIVITY_TYPE:
				activity = new ParallelActivity();
				activity.setActivityCategoryID(CATEGORY_SPLIT);
				break;
			case SEQUENCE_ACTIVITY_TYPE:
				activity = new SequenceActivity();
				activity.setActivityCategoryID(CATEGORY_SYSTEM);
				break;
			case SYNCH_GATE_ACTIVITY_TYPE:
				activity = new SynchGateActivity();
				activity.setActivityCategoryID(CATEGORY_SYSTEM);
				break;
			case SCHEDULE_GATE_ACTIVITY_TYPE:
				activity = new ScheduleGateActivity();
				activity.setActivityCategoryID(CATEGORY_SYSTEM);
				break;
			case PERMISSION_GATE_ACTIVITY_TYPE:
				activity = new PermissionGateActivity();
				activity.setActivityCategoryID(CATEGORY_SYSTEM);
				break;
			case SYSTEM_GATE_ACTIVITY_TYPE:
				activity = new SystemGateActivity();
				activity.setActivityCategoryID(CATEGORY_SYSTEM);
				break;
			case CHOSEN_BRANCHING_ACTIVITY_TYPE:
				activity = new ChosenBranchingActivity();
				activity.setActivityCategoryID(CATEGORY_SYSTEM);
				break;
			case GROUP_BRANCHING_ACTIVITY_TYPE:
				activity = new GroupBranchingActivity();
				activity.setActivityCategoryID(CATEGORY_SYSTEM);
				break;
			case TOOL_BRANCHING_ACTIVITY_TYPE:
				activity = new ToolBranchingActivity();
				activity.setActivityCategoryID(CATEGORY_SYSTEM);
				break;
			default:
				activity = new GroupingActivity();
				activity.setActivityCategoryID(CATEGORY_SYSTEM);
				break;
		}
		activity.setActivityTypeId(new Integer(activityType));
		return activity;
	}
	
    //---------------------------------------------------------------------
    // Getters and Setters
    //---------------------------------------------------------------------	
	/**
	 * @hibernate.activityUIID generator-class="native" type="java.lang.Long"
	 *               column="activity_id"
	 */
	public Long getActivityId() {
		return this.activityId;
	}

	public void setActivityId(Long activityId) {
		this.activityId = activityId;
	}

	/**
	 * @hibernate.property column="activityUIID" length="11"
	 */
	public Integer getActivityUIID() {
		return this.activityUIID;
	}

	public void setActivityUIID(Integer id) {
		this.activityUIID = id;
	}

	/**
	 * @hibernate.property column="description" length="65535"
	 */
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @hibernate.property column="title" length="255"
	 */
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @hibernate.property column="xcoord" length="11"
	 */
	public Integer getXcoord() {
		return this.xcoord;
	}

	public void setXcoord(Integer xcoord) {
		this.xcoord = xcoord;
	}

	/**
	 * @hibernate.property column="ycoord" length="11"
	 */
	public Integer getYcoord() {
		return this.ycoord;
	}

	public void setYcoord(Integer ycoord) {
		this.ycoord = ycoord;
	}

	/**
	 * @hibernate.property column="order_id" length="11"
	 */
	public Integer getOrderId() {
		return this.orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	/**
	 * @hibernate.property column="define_later_flag" length="4" not-null="true"
	 */
	public Boolean getDefineLater() {
		return this.defineLater;
	}

	public void setDefineLater(Boolean defineLater) {
		this.defineLater = defineLater;
	}

	/**
	 * @hibernate.property column="create_date_time" length="19" not-null="true"
	 */
	public Date getCreateDateTime() {
		return this.createDateTime;
	}

	/* If createDateTime is null, then it will default to the current date/time */
	public void setCreateDateTime(Date createDateTime) {
		this.createDateTime = createDateTime != null ? createDateTime : new Date();
	}

	/**
	 * @hibernate.many-to-one not-null="true"
	 * @hibernate.column name="learning_library_id"
	 *  
	 */
	public org.lamsfoundation.lams.learningdesign.LearningLibrary getLearningLibrary() {
		return this.learningLibrary;
	}

	public void setLearningLibrary(
			org.lamsfoundation.lams.learningdesign.LearningLibrary learningLibrary) {
		this.learningLibrary = learningLibrary;
	}

	/**
	 * @hibernate.many-to-one not-null="true"
	 * @hibernate.column name="parent_activity_id"
	 *  
	 */
	public org.lamsfoundation.lams.learningdesign.Activity getParentActivity() {
		return this.parentActivity;
	}

	public void setParentActivity(Activity parentActivity) {
		this.parentActivity = parentActivity;
	}

	/**
	 * @hibernate.many-to-one not-null="true"
	 * @hibernate.column name="learning_design_id"
	 *  
	 */
	public org.lamsfoundation.lams.learningdesign.LearningDesign getLearningDesign() {
		return this.learningDesign;
	}

	public void setLearningDesign(
			org.lamsfoundation.lams.learningdesign.LearningDesign learningDesign) {
		this.learningDesign = learningDesign;
	}

	/**
	 * @hibernate.many-to-one not-null="true"
	 * @hibernate.column name="grouping_id"
	 *  
	 */
	public org.lamsfoundation.lams.learningdesign.Grouping getGrouping() {
		return this.grouping;
	}

	public void setGrouping(
			org.lamsfoundation.lams.learningdesign.Grouping grouping) {
		this.grouping = grouping;
	}

	/**
	 * @hibernate.many-to-one not-null="true"
	 * @hibernate.column name="learning_activity_type_id"
	 *  
	 */
	public Integer getActivityTypeId() {
		return this.activityTypeId;
	}

	public void setActivityTypeId(Integer activityTypeId) {
		this.activityTypeId = activityTypeId;
	}

	/**
	 * @return Returns the applyGrouping.
	 */
	public Boolean getApplyGrouping() {
		return applyGrouping;
	}
	/**
	 * @param applyGrouping The applyGrouping to set.
	 */
	public void setApplyGrouping(Boolean applyGrouping) {
		this.applyGrouping = applyGrouping;
	}
	/**
	 * @return Returns the groupingSupportType.
	 */
	public Integer getGroupingSupportType() {
		return groupingSupportType;
	}
	/**
	 * @param groupingSupportType The groupingSupportType to set.
	 */
	public void setGroupingSupportType(Integer groupingSupportType) {
		this.groupingSupportType = groupingSupportType;
	}	
	
	/**
	 * @return Returns the readOnly.
	 */
	public Boolean getReadOnly() {
		return readOnly;
	}
	/**
	 * @param readOnly The readOnly to set.
	 */
	public void setReadOnly(Boolean readOnly) {
		this.readOnly = readOnly;
	}	

	/**
	 * @return Returns the initialised flag.
	 */
	public Boolean isInitialised() {
		return initialised;
	}
	/**
	 * @param readOnly The readOnly to set.
	 */
	public void setInitialised(Boolean initialised) {
		this.initialised = initialised;
	}	

	/**
	 * @return Returns the stopAfterActivity flag.
	 */
	public Boolean isStopAfterActivity() {
		return stopAfterActivity;
	}
	/**
	 * @param readOnly The stopAfterActivity to set.
	 */
	public void setStopAfterActivity(Boolean stopAfterActivity) {
		this.stopAfterActivity = stopAfterActivity;
	}	

    /**
     * @return Returns the inputActivities.
     */
    public Set getInputActivities()
    {
        if(this.inputActivities == null)
            this.setInputActivities(new HashSet());
        return inputActivities;
    }
    /**
     * @param InputActivities The InputActivities to set.
     */
    public void setInputActivities(Set inputActivities)
    {
        this.inputActivities = inputActivities;
    }

	public String toString() {
		return new ToStringBuilder(this)
			.append("activityId", activityId)
			.append("activityUIID", activityUIID)
			.append("description", description)
			.toString();
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if (!(other instanceof Activity))
			return false;
		Activity castOther = (Activity) other;
		return new EqualsBuilder()
		.append(this.getActivityId(),castOther.getActivityId())
		.append(this.getActivityUIID(),castOther.getActivityUIID())
		.isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder()
			.append(getActivityId())
			.append(getActivityUIID())
			.toHashCode();
	}

	/**
	 * @hibernate.property column="library_activity_ui_image" length="255"
	 * @return Returns the libraryActivityUiImage.
	 */
	public String getLibraryActivityUiImage() {
		return libraryActivityUiImage;
	}

	/**
	 * @param libraryActivityUiImage
	 *            The libraryActivityUiImage to set.
	 */
	public void setLibraryActivityUiImage(String libraryActivityUiImage) {
		this.libraryActivityUiImage = libraryActivityUiImage;
	}	
	
	/**
	 * This function returns the Transition that 
	 * STARTS FROM THIS ACTIVITY. In simpler words the
	 * next activity in the transition.
	 * 
	 * For example, if we have a transition as  following
	 * A --> B --> C. For activity B this function would
	 * return C. That is the Transition FROM activity B.
	 * 
	 * @return Returns the transitionFrom.
	 */
	public Transition getTransitionFrom() {
		return transitionFrom;
	}
	/**
	 * @param transitionFrom The transitionFrom to set.
	 */
	public void setTransitionFrom(Transition transitionFrom) {
		this.transitionFrom = transitionFrom;
	}
	/**
	 * This function returns the Transition that 
	 * POINTS TO THIS ACTIVITY and NOT the transition 
	 * that this activity points to.
	 * 
	 * For example, if we have a transition as  following
	 * A --> B --> C. For activity B this function would
	 * return A. That is the Transition that points TO 
	 * activity B.
	 * 
	 * @return Returns the transitionTo.
	 */
	public Transition getTransitionTo() {
		return transitionTo;
	}
	/**
	 * @param transitionTo The transitionTo to set.
	 */
	public void setTransitionTo(Transition transitionTo) {
		this.transitionTo = transitionTo;
	}	
	public Integer getParentUIID() {
		return parentUIID;
	}
	public void setParentUIID(Integer parent_ui_id) {
		this.parentUIID = parent_ui_id;
	}	
	public Activity getLibraryActivity() {
		return libraryActivity;
	}
	public void setLibraryActivity(Activity libraryActivity) {
		this.libraryActivity = libraryActivity;
	}
	public String getHelpText() {
		return helpText;
	}
	public void setHelpText(String helpText) {
		this.helpText = helpText;
	}
	public Integer getGroupingUIID() {
		return groupingUIID;
	}
	public void setGroupingUIID(Integer groupingUIID) {
		this.groupingUIID = groupingUIID;
	}
	public Boolean getRunOffline() {
		return runOffline;
	}
	public void setRunOffline(Boolean runOffline) {
		this.runOffline = runOffline;
	}
	
	public Integer getActivityCategoryID() 
	{
		return activityCategoryID;
	}
	public void setActivityCategoryID(Integer activityCategoryID) 
	{
		this.activityCategoryID = activityCategoryID;
	}
	
	public String getLanguageFile() {
		return languageFile;
	}
	public void setLanguageFile(String languageFile) {
		this.languageFile = languageFile;
	}
	   
    //---------------------------------------------------------------------
    // Service Methods 
    //---------------------------------------------------------------------
	
	/**
	 * This method that get all tool activities belong to the current activity. 
	 * 
	 * As the activity object structure might be infinite, we recursively loop
	 * through the entire structure and added all tool activities into the set 
	 * that we want to return. This method calls a method getToolActivitiesInActivity()
	 * which must be defined in subclasses for tool or a complex activities.
	 * This handles the polymorphic aspect of this function. (Note: we can't
	 * use instanceOf as we are dealing with Hibernate proxies.)
	 * 
	 * @return the set of all tool activities.
	 */
	public Set getAllToolActivities()
	{
	    SortedSet toolActivities = new TreeSet(new ActivityOrderComparator());
	    getToolActivitiesInActivity(toolActivities);
	    return toolActivities;
	}
	
	protected void getToolActivitiesInActivity(SortedSet toolActivities) {
		
		// a simple activity doesn't have any tool activities
	
	}
	
	/**
	 * Return the group information for the requested user when he is running
	 * current activity instance, based on the grouping data in the activity.
	 * @param learner the requested user	
	 * @return the group that this user belongs to.
	 */
	public Group getGroupFor(User learner)
	{
		return getGroupFor(learner, this.getGrouping());
	}
	
	/**
	 * Return the group information for the requested user when he is running
	 * current activity instance, based on the given grouping.
	 * <P>
	 * If we are using the grouping set up in the activity, the grouping will be 
	 * this.getGrouping(). If the activity isn't grouped, then it should use
	 * the class grouping.
	 * 
	 * @param learner the requested user	
	 * @return the group that this user belongs to.
	 */
	protected Group getGroupFor(User learner, Grouping inGrouping)
	{
	    if(inGrouping==null)
	        throw new IllegalArgumentException("Exception occured in " +
	        		"getGroupFor, no grouping has been defined");
	    
	    for(Iterator i=inGrouping.getGroups().iterator();i.hasNext();)
	    {
	        Group group = (Group)i.next();
	        if(inGrouping.isLearnerGroup(group)&&group.hasLearner(learner))
	            return group;
	    }
	    
	    return new NullGroup();
	}
    //---------------------------------------------------------------------
    // Activity Type checking methods
    //---------------------------------------------------------------------
	/**
	 * Check up whether an activity is tool activity or not.
	 * @return is this activity a tool activity?
	 */
	public boolean isToolActivity()
	{
	    return getActivityTypeId().intValue()==TOOL_ACTIVITY_TYPE;
	}
	
	/**
	 * Check up whether an activity is sequence activity or not.
	 * @return is this activity a sequence activity?
	 */
	public boolean isSequenceActivity()
	{
	    return getActivityTypeId().intValue()==SEQUENCE_ACTIVITY_TYPE;
	}
	
	public boolean isParallelActivity()
	{
		return getActivityTypeId().intValue()== PARALLEL_ACTIVITY_TYPE;
	}

	public boolean isOptionsActivity()
	{
		return getActivityTypeId().intValue()== OPTIONS_ACTIVITY_TYPE;
	}

	public boolean isComplexActivity()
	{
		return getActivityTypeId().intValue()== SEQUENCE_ACTIVITY_TYPE || 
			   getActivityTypeId().intValue()== PARALLEL_ACTIVITY_TYPE ||
			   getActivityTypeId().intValue()== OPTIONS_ACTIVITY_TYPE ||
			   getActivityTypeId().intValue()== CHOSEN_BRANCHING_ACTIVITY_TYPE ||
			   getActivityTypeId().intValue()== GROUP_BRANCHING_ACTIVITY_TYPE ||
			   getActivityTypeId().intValue()== TOOL_BRANCHING_ACTIVITY_TYPE;
	}
	
	public boolean isSystemToolActivity()
	{
		return ISystemToolActivity.class.isInstance(this);
	}

	public boolean isGateActivity()
	{
		return getActivityTypeId().intValue()== SCHEDULE_GATE_ACTIVITY_TYPE ||
			   getActivityTypeId().intValue()== PERMISSION_GATE_ACTIVITY_TYPE ||
			   getActivityTypeId().intValue()== SYNCH_GATE_ACTIVITY_TYPE ||
			   getActivityTypeId().intValue() == SYSTEM_GATE_ACTIVITY_TYPE;
	}
	
	/**
	 * Check up whether an activity is synch gate activity or not.
	 * @return is this activity a synch gate activity?
	 */
	public boolean isSynchGate()
	{
	    return getActivityTypeId().intValue() == SYNCH_GATE_ACTIVITY_TYPE;
	}
	
	/**
	 * Check up whether an activity is permission gate activity or not.
	 * @return is this activity a permission gate activity.
	 */
	public boolean isPermissionGate()
	{
	    return getActivityTypeId().intValue() == PERMISSION_GATE_ACTIVITY_TYPE;
	}
	
	/**
	 * Check up whether an activity is schedule gate activity or not.
	 * @return is this activity a schedule gate activity.
	 */
	public boolean isScheduleGate()
	{
	    return getActivityTypeId().intValue() == SCHEDULE_GATE_ACTIVITY_TYPE;
	}
	
	/**
	 * Check up whether an activity is schedule gate activity or not.
	 * @return is this activity a schedule gate activity.
	 */
	public boolean isSystemGate()
	{
	    return getActivityTypeId().intValue() == SYSTEM_GATE_ACTIVITY_TYPE;
	}
	
	/**
	 * /**
	 * Check up whether an activity is grouping activity or not.
	 * @return is this activity a grouping activity
	 */
	public boolean isGroupingActivity()
	{
		return getActivityTypeId().intValue()== GROUPING_ACTIVITY_TYPE;
	}
	
	/**
	 * Check up whether an activity is some sort of branching activity or not
	 * @return is this activity a branching activity
	 */
	public boolean isBranchingActivity()
	{
		return getActivityTypeId().intValue()== CHOSEN_BRANCHING_ACTIVITY_TYPE ||
		   getActivityTypeId().intValue()== GROUP_BRANCHING_ACTIVITY_TYPE ||
		   getActivityTypeId().intValue()== TOOL_BRANCHING_ACTIVITY_TYPE;
	}


	/**
	 * Check up whether an activity is branching activity based on the monitor choice or not.
	 * @return is this activity a branching activity
	 */
	public boolean isChosenBranchingActivity()
	{
		return getActivityTypeId().intValue()== CHOSEN_BRANCHING_ACTIVITY_TYPE;
	}

	/**
	 * Check up whether an activity is branching activity based on an existing group or not.
	 * @return is this activity a branching activity
	 */
	public boolean isGroupBranchingActivity()
	{
		return getActivityTypeId().intValue()== GROUP_BRANCHING_ACTIVITY_TYPE;
	}

	/**
	 * Check up whether an activity is branching activity based on another activity's output or not.
	 * @return is this activity a branching activity
	 */
	public boolean isToolBranchingActivity()
	{
		return getActivityTypeId().intValue()== TOOL_BRANCHING_ACTIVITY_TYPE;
	}

	public boolean isActivityReadOnly()
	{
		return readOnly.equals(Boolean.TRUE);
	}
	
    //---------------------------------------------------------------------
    // Data Transfer object creation methods
    //---------------------------------------------------------------------
	/**
	 * Return the activity dto for progress view.
	 * @return the activity dto.
	 */
	public ProgressActivityDTO getProgressActivityData()
	{
	    return new ProgressActivityDTO(this.activityId);
	}
	

	/** Get the authoring DTO for this activity. Overidden by ComplexActivity.
	 */
	public Set<AuthoringActivityDTO> getAuthoringActivityDTOSet(ArrayList<BranchActivityEntryDTO> branchMappings)
	{
		Set<AuthoringActivityDTO> dtoSet = new TreeSet<AuthoringActivityDTO>(new ActivityDTOOrderComparator());
		dtoSet.add(new AuthoringActivityDTO(this, branchMappings));
		return dtoSet;
	}
	
	public LibraryActivityDTO getLibraryActivityDTO(){
		return new LibraryActivityDTO(this);
	}
	
	/** Create a deep copy of the this activity. It should return the same
	 * subclass as the activity being copied. Generally doesn't copy the "link" type
	 * fields like transitions, learning design, etc
	 * @return deep copy of this object
	 */
   public abstract Activity createCopy();

   protected void copyToNewActivity(Activity newActivity ) {

	   	newActivity.setActivityUIID(this.getActivityUIID());
	   	newActivity.setDescription(this.getDescription());
	   	newActivity.setTitle(this.getTitle());
	   	newActivity.setHelpText(this.getHelpText());
	   	newActivity.setXcoord(this.getXcoord());
	   	newActivity.setYcoord(this.getYcoord());    	    
	   	newActivity.setActivityTypeId(this.getActivityTypeId());
			
	   	newActivity.setGroupingSupportType(this.getGroupingSupportType());
	   	newActivity.setApplyGrouping(this.getApplyGrouping());
	   	newActivity.setActivityCategoryID(this.getActivityCategoryID());
			
	   	newActivity.setGrouping(this.getGrouping());
	   	newActivity.setGroupingUIID(this.getGroupingUIID());
	
	   	newActivity.setDefineLater(this.getDefineLater());    	
	   	newActivity.setCreateDateTime(new Date()); 
	   	newActivity.setRunOffline(this.getRunOffline());
	   	newActivity.setLearningLibrary(this.getLearningLibrary());    	
	   	newActivity.setLibraryActivity(this.getLibraryActivity());
	   	newActivity.setLibraryActivityUiImage(this.getLibraryActivityUiImage());
	   	newActivity.setLanguageFile(this.getLanguageFile());
			
	   	newActivity.setOrderId(this.getOrderId());
	   	newActivity.setReadOnly(this.getReadOnly());
	   	newActivity.setStopAfterActivity(this.isStopAfterActivity());

	   	if ( this.getInputActivities()!=null && this.getInputActivities().size() > 0 ) {
		   	newActivity.setInputActivities(new HashSet());
	   		newActivity.getInputActivities().addAll(this.getInputActivities());
	   	}
   }
	
	
   // ---------------------------------------------------------------------
   // Data Validation methods
   //---------------------------------------------------------------------
   
   /**
	 * Validate activity
	 * 
	 */
	public Vector<ValidationErrorDTO> validateActivity(MessageService messageService) { 
		return null; 
	}

   
}