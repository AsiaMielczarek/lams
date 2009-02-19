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
package org.lamsfoundation.lams.tool.qa.dao.hibernate;

import java.util.Iterator;
import java.util.List;

import org.hibernate.FlushMode;
import org.lamsfoundation.lams.tool.qa.QaQueUsr;
import org.lamsfoundation.lams.tool.qa.QaUsrResp;
import org.lamsfoundation.lams.tool.qa.dao.IQaUsrRespDAO;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * 
 * @author Ozgur Demirtas
 * 
 */
public class QaUsrRespDAO extends HibernateDaoSupport implements IQaUsrRespDAO {
    private static final String LOAD_ATTEMPT_FOR_USER_AND_QUESTION_CONTENT = "from qaUsrResp in class QaUsrResp where qaUsrResp.queUsrId=:queUsrId and qaUsrResp.qaQueContentId=:qaQueContentId";

    public QaQueUsr getUserById(long userId) {
	return (QaQueUsr) this.getHibernateTemplate().load(QaQueUsr.class, new Long(userId));

    }

    public void createUserResponse(QaUsrResp qaUsrResp) {
	this.getSession().setFlushMode(FlushMode.AUTO);
	this.getHibernateTemplate().save(qaUsrResp);
    }

    public QaUsrResp retrieveQaUsrResp(long responseId) {
	return (QaUsrResp) this.getHibernateTemplate().get(QaUsrResp.class, new Long(responseId));
    }

    public QaUsrResp getAttemptByUID(Long uid) {
	String query = "from QaUsrResp attempt where attempt.responseId=?";

	HibernateTemplate templ = this.getHibernateTemplate();
	List list = getSession().createQuery(query).setLong(0, uid.longValue()).list();

	if (list != null && list.size() > 0) {
	    QaUsrResp attempt = (QaUsrResp) list.get(0);
	    return attempt;
	}
	return null;
    }

    /**
     * @see org.lamsfoundation.lams.tool.qa.dao.interfaces.IQaUsrRespDAO#saveUserResponse(com.lamsinternational.tool.qa.domain.QaUsrResp)
     */
    public void saveUserResponse(QaUsrResp resp) {
	this.getSession().setFlushMode(FlushMode.AUTO);
	this.getHibernateTemplate().save(resp);
    }

    /**
     * @see org.lamsfoundation.lams.tool.qa.dao.IQaUsrRespDAO#updateUserResponse(org.lamsfoundation.lams.tool.qa.QaUsrResp)
     */
    public void updateUserResponse(QaUsrResp resp) {
	this.getSession().setFlushMode(FlushMode.AUTO);
	this.getHibernateTemplate().update(resp);
    }

    public void removeUserResponse(QaUsrResp resp) {
	this.getSession().setFlushMode(FlushMode.AUTO);
	this.getHibernateTemplate().delete(resp);
    }

    public List getAttemptsForUserAndQuestionContent(final Long queUsrId, final Long qaQueContentId) {
	HibernateTemplate templ = this.getHibernateTemplate();
	List list = getSession().createQuery(LOAD_ATTEMPT_FOR_USER_AND_QUESTION_CONTENT).setLong("queUsrId",
		queUsrId.longValue()).setLong("qaQueContentId", qaQueContentId.longValue()).list();

	return list;
    }

    public void removeAttemptsForUserAndQuestionContent(final Long queUsrId, final Long qaQueContentId) {
	HibernateTemplate templ = this.getHibernateTemplate();
	List list = getSession().createQuery(LOAD_ATTEMPT_FOR_USER_AND_QUESTION_CONTENT).setLong("queUsrId",
		queUsrId.longValue()).setLong("qaQueContentId", qaQueContentId.longValue()).list();

	if (list != null && list.size() > 0) {
	    Iterator listIterator = list.iterator();
	    while (listIterator.hasNext()) {
		QaUsrResp qaUsrResp = (QaUsrResp) listIterator.next();
		this.getSession().setFlushMode(FlushMode.AUTO);
		templ.delete(qaUsrResp);
		templ.flush();
	    }
	}
    }

    public void removeUserResponseByQaQueId(Long qaQueId) {
	if (qaQueId != null) {
	    String query = "from resp in class org.lamsfoundation.lams.tool.qa.QaUsrResp"
		    + " where resp.qaQueContentId = ?";
	    Object obj = getSession().createQuery(query).setLong(0, qaQueId.longValue()).uniqueResult();
	    if (obj != null) {
		this.getSession().setFlushMode(FlushMode.AUTO);
		getHibernateTemplate().delete(obj);
	    }
	}
    }

}
