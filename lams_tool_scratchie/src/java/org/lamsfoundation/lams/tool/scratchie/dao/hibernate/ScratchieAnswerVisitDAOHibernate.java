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
package org.lamsfoundation.lams.tool.scratchie.dao.hibernate;

import java.util.List;

import org.lamsfoundation.lams.tool.scratchie.dao.ScratchieAnswerVisitDAO;
import org.lamsfoundation.lams.tool.scratchie.model.ScratchieAnswerVisitLog;

public class ScratchieAnswerVisitDAOHibernate extends BaseDAOHibernate implements ScratchieAnswerVisitDAO {

    private static final String FIND_BY_ANSWER_AND_USER = "from " + ScratchieAnswerVisitLog.class.getName()
	    + " as r where r.user.userId = ? and r.scratchieAnswer.uid=?";
    
    private static final String FIND_BY_SCRATCHIE_USER_AND_ITEM = "from " + ScratchieAnswerVisitLog.class.getName()
	    + " as r where r.user.uid=? and r.scratchieAnswer.scratchieItem.uid = ?  order by r.accessDate asc";
    
    private static final String FIND_BY_SCRATCHIE_USER = "from " + ScratchieAnswerVisitLog.class.getName()
	    + " as r where r.user.uid=? order by r.accessDate asc";

    private static final String FIND_VIEW_COUNT_BY_USER = "select count(*) from "
	    + ScratchieAnswerVisitLog.class.getName() + " as r where  r.sessionId=? and  r.user.userId =?";
    
    private static final String FIND_VIEW_COUNT_BY_USER_AND_ITEM = "select count(*) from "
	    + ScratchieAnswerVisitLog.class.getName() + " as l where l.sessionId=? and l.user.userId =? and l.scratchieAnswer.scratchieItem.uid=?";

    @Override
    public ScratchieAnswerVisitLog getScratchieAnswerLog(Long answerUid, Long userId) {
	List list = getHibernateTemplate().find(FIND_BY_ANSWER_AND_USER, new Object[] { userId, answerUid });
	if (list == null || list.size() == 0)
	    return null;
	return (ScratchieAnswerVisitLog) list.get(0);
    }

    @Override
    public int getLogCountTotal(Long toolSessionId, Long userId) {
	List list = getHibernateTemplate().find(FIND_VIEW_COUNT_BY_USER, new Object[] { toolSessionId, userId });
	if (list == null || list.size() == 0)
	    return 0;
	return ((Number) list.get(0)).intValue();
    }
    
    @Override
    public int getLogCountPerItem(Long toolSessionId, Long userId, Long itemUid) {
	List list = getHibernateTemplate().find(FIND_VIEW_COUNT_BY_USER_AND_ITEM, new Object[] { toolSessionId, userId, itemUid });
	if (list == null || list.size() == 0)
	    return 0;
	return ((Number) list.get(0)).intValue();
    }
    
    @Override
    public List<ScratchieAnswerVisitLog> getLogsByScratchieUserAndItem(Long userUid, Long itemUid) {
	return getHibernateTemplate().find(FIND_BY_SCRATCHIE_USER_AND_ITEM, new Object[] { userUid, itemUid });
    }
    
    @Override
    public List<ScratchieAnswerVisitLog> getLogsByScratchieUser(Long userUid) {
	return getHibernateTemplate().find(FIND_BY_SCRATCHIE_USER, new Object[] { userUid });
    }

}
