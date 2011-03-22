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

import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.lamsfoundation.lams.tool.qa.QaCondition;
import org.lamsfoundation.lams.tool.qa.QaContent;
import org.lamsfoundation.lams.tool.qa.QaQueContent;
import org.lamsfoundation.lams.tool.qa.dao.IQaContentDAO;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * @author Ozgur Demirtas
 * 
 */

public class QaContentDAO extends HibernateDaoSupport implements IQaContentDAO {
    static Logger logger = Logger.getLogger(QaContentDAO.class.getName());

    private static final String LOAD_QA_BY_SESSION = "select qa from QaContent qa left join fetch "
	    + "qa.qaSessions session where session.qaSessionId=:sessionId";

    public QaContent getQaByContentId(long qaId) {
	String query = "from QaContent as qa where qa.qaContentId = ?";
	HibernateTemplate templ = this.getHibernateTemplate();
	List list = getSession().createQuery(query).setLong(0, qaId).list();

	if (list != null && list.size() > 0) {
	    QaContent qa = (QaContent) list.get(0);
	    return qa;
	}
	return null;
    }

    public void updateQa(QaContent qa) {
	this.getSession().setFlushMode(FlushMode.AUTO);
	this.getHibernateTemplate().update(qa);
    }

    public QaContent getQaBySession(final Long sessionId) {
	return (QaContent) getHibernateTemplate().execute(new HibernateCallback() {

	    public Object doInHibernate(Session session) throws HibernateException {
		return session.createQuery(QaContentDAO.LOAD_QA_BY_SESSION).setLong("sessionId", sessionId.longValue())
			.uniqueResult();
	    }
	});
    }

    public void saveQa(QaContent qa) {
	this.getSession().setFlushMode(FlushMode.AUTO);
	this.getHibernateTemplate().save(qa);
    }

    public void saveOrUpdateQa(QaContent qa) {
	this.getSession().setFlushMode(FlushMode.AUTO);
	this.getHibernateTemplate().saveOrUpdate(qa);
    }

    public void createQa(QaContent qa) {
	this.getSession().setFlushMode(FlushMode.AUTO);
	this.getHibernateTemplate().save(qa);
    }

    public void UpdateQa(QaContent qa) {
	this.getSession().setFlushMode(FlushMode.AUTO);
	this.getHibernateTemplate().update(qa);
    }

    public void removeAllQaSession(QaContent qaContent) {
	this.getHibernateTemplate().deleteAll(qaContent.getQaSessions());
    }

    public void removeQa(Long qaContentId) {
	if (qaContentId != null) {
	    String query = "from qa in class org.lamsfoundation.lams.tool.qa.QaContent" + " where qa.qaContentId = ?";
	    Object obj = getSession().createQuery(query).setLong(0, qaContentId.longValue()).uniqueResult();
	    this.getSession().setFlushMode(FlushMode.AUTO);
	    getHibernateTemplate().delete(obj);
	}
    }

    public void deleteQa(QaContent qaContent) {
	this.getSession().setFlushMode(FlushMode.AUTO);
	this.getHibernateTemplate().delete(qaContent);
    }

    public void removeQaById(Long qaId) {
	this.getSession().setFlushMode(FlushMode.AUTO);
	removeQa(qaId);
    }

    public void flush() {
	this.getHibernateTemplate().flush();
    }

    public void deleteCondition(QaCondition condition) {
	if (condition != null && condition.getConditionId() != null) {
	    this.getSession().setFlushMode(FlushMode.AUTO);
	    this.getHibernateTemplate().delete(condition);
	}
    }

    public void removeQuestionsFromCache(QaContent qaContent) {
	if (qaContent != null) {

	    for (QaQueContent question : (Set<QaQueContent>) qaContent.getQaQueContents()) {
		getHibernateTemplate().evict(question);
	    }
	    getHibernateTemplate().evict(qaContent);
	}

    }

}