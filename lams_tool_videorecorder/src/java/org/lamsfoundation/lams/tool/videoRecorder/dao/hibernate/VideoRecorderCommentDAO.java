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

package org.lamsfoundation.lams.tool.videoRecorder.dao.hibernate;

import java.util.List;
import java.util.Set;

import org.lamsfoundation.lams.dao.hibernate.BaseDAO;
import org.lamsfoundation.lams.tool.videoRecorder.dao.IVideoRecorderCommentDAO;
import org.lamsfoundation.lams.tool.videoRecorder.model.VideoRecorderComment;

/**
 * DAO for accessing the VideoRecorderRecording objects - Hibernate specific code.
 */
public class VideoRecorderCommentDAO extends BaseDAO implements IVideoRecorderCommentDAO {

	private static final String SQL_QUERY_FIND_BY_COMMENT_ID = "from "
		+ VideoRecorderComment.class.getName() + " as c"
		+ " where uid=?";
		
	private static final String SQL_QUERY_BY_USER_ID = "from " + VideoRecorderComment.class.getName() + " as c "
	+ "where c.createBy.userId=?";
	
	private static final String SQL_QUERY_BY_TOOL_SESSION_ID = "from " + VideoRecorderComment.class.getName() + " as c "
	+ "where c.videoRecorderSession.sessionId=?";
	
	private static final String SQL_QUERY_BY_TOOL_RECORDING_ID = "from " + VideoRecorderComment.class.getName() + " as c "
	+ "where c.recording.uid=?";
	
	public VideoRecorderComment getCommentById(Long recordingId) {
		List list = this.getHibernateTemplate().find(
				SQL_QUERY_FIND_BY_COMMENT_ID ,
				recordingId);
	
		if (list == null || list.isEmpty())
			return null;
	
		return (VideoRecorderComment) list.get(0);
	}
	
	public Set<VideoRecorderComment> getCommentsByUserId(Long userId){
		return (Set<VideoRecorderComment>)(this.getHibernateTemplate().find(SQL_QUERY_BY_USER_ID, userId));
	}

	public Set<VideoRecorderComment> getCommentsByToolSessionId(Long toolSessionId){
		return (Set<VideoRecorderComment>)(this.getHibernateTemplate().find(SQL_QUERY_BY_TOOL_SESSION_ID, toolSessionId));
	}
	
	public Set<VideoRecorderComment> getCommentsByRecordingId(Long recordingId) {
		return (Set<VideoRecorderComment>)(this.getHibernateTemplate().find(SQL_QUERY_BY_TOOL_RECORDING_ID, recordingId));
	}
	
    public void saveOrUpdate(VideoRecorderComment videoRecorderComment) {
		this.getHibernateTemplate().saveOrUpdate(videoRecorderComment);
		this.getHibernateTemplate().flush();
    }
}
