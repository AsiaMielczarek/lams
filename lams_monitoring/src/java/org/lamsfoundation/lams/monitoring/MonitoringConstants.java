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
package org.lamsfoundation.lams.monitoring;

public class MonitoringConstants {

    public static final String MONITORING_SERVICE_BEAN_NAME = "monitoringService";
    public static final String CREATE_LESSON_MESSAGE_KEY = "createLessonClass";
    public static final String INIT_LESSON_MESSAGE_KEY = "initializeLesson";
    public static final String PERFORM_CHOSEN_GROUPING_KEY = "performChosenGrouping";
    public static final String KEY_ORGANISATION_ID = "organisationID";
    public static final String KEY_LESSON_ID = "lessonID";
    public static final String KEY_USER_ID = "userID";
    public static final String KEY_STAFF = "staff";
    public static final String KEY_LEARNER = "learners";
    public static final String JOB_START_LESSON = "startScheduleLessonJob";
    public static final String JOB_FINISH_LESSON = "finishScheduleLessonJob";
    public static final String JOB_EMAIL_MESSAGE = "emailScheduleMessageJob";
    public static final String PARAM_LESSON_START_DATE = "lessonStartDate";
    public static final String PARAM_SCHEDULED_NUMBER_DAYS_TO_LESSON_FINISH = "scheduledNumberDaysToLessonFinish";
    public static final String PARAM_LEARNER_ID = "learnerID";
    public static final String KEY_GROUPING_ACTIVITY = "groupingActivityID";
    public static final String KEY_GROUPS = "groups";
    public static final String KEY_GROUP_NAME = "groupName";
    public static final String KEY_GROUP_ORDER_ID = "orderID";
    public static final String KEY_GROUP_LEARNERS = "learners";
    public static final Object KEY_USERS = "users";
    public static final String PARAM_SCHEDULE_TIME_ZONE_IDX = "scheduleTimeZoneIdx";
	
    // ---------------------------------------------------------------------
    // Search type constants used in EmailNotificationsAction
    // ---------------------------------------------------------------------	
    public static final int LESSON_TYPE_ASSIGNED_TO_LESSON = 0;
    public static final int LESSON_TYPE_HAVENT_FINISHED_LESSON = 1;
    public static final int LESSON_TYPE_HAVE_FINISHED_LESSON = 2;
    public static final int LESSON_TYPE_HAVENT_STARTED_LESSON = 3;
    public static final int LESSON_TYPE_HAVE_STARTED_LESSON = 4;
    public static final int LESSON_TYPE_HAVENT_REACHED_PARTICULAR_ACTIVITY = 5;
    public static final int LESSON_TYPE_LESS_THAN_X_DAYS_TO_DEADLINE = 6;
    public static final int COURSE_TYPE_HAVE_FINISHED_PARTICULAR_LESSON = 7;
    public static final int COURSE_TYPE_HAVENT_STARTED_PARTICULAR_LESSON = 8;
    public static final int COURSE_TYPE_HAVENT_STARTED_ANY_LESSONS = 9;
    public static final int COURSE_TYPE_HAVE_FINISHED_THESE_LESSONS = 10;
    public static final int COURSE_TYPE_HAVENT_FINISHED_THESE_LESSONS = 11;
}