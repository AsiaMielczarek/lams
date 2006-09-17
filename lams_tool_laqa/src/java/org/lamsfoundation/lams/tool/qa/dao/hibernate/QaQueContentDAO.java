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
 * ***********************************************************************/

/* $$Id$$ */
package org.lamsfoundation.lams.tool.qa.dao.hibernate;

import java.util.List;

import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import org.apache.log4j.Logger;

import org.lamsfoundation.lams.tool.qa.QaContent;
import org.lamsfoundation.lams.tool.qa.QaQueContent;
import org.lamsfoundation.lams.tool.qa.dao.IQaQueContentDAO;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;




/**
 * @author Ozgur Demirtas
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

public class QaQueContentDAO extends HibernateDaoSupport implements IQaQueContentDAO {
	 	static Logger logger = Logger.getLogger(QaQueContentDAO.class.getName());
	 	private static final String LOAD_QUESTION_CONTENT_BY_CONTENT_ID = "from qaQueContent in class QaQueContent where qaQueContent.qaContentId=:qaContentId";
	 	private static final String GET_QUESTION_IDS_FOR_CONTENT = "select qaQueContent.qaQueContentId from QaQueContent qaQueContent where qaQueContent.qaContentId = :qa";
	 	private static final String LOAD_QUESTION_CONTENT_BY_QUESTION_TEXT = "from qaQueContent in class QaQueContent where qaQueContent.question=:question and qaQueContent.qaContentId=:qaContentId";
	 	private static final String LOAD_QUESTION_CONTENT_BY_DISPLAY_ORDER = "from qaQueContent in class QaQueContent where qaQueContent.displayOrder=:displayOrder and qaQueContent.qaContentId=:qaContentId";
	 	private static final String SORT_QUESTION_CONTENT_BY_DISPLAY_ORDER = "from qaQueContent in class QaQueContent where qaQueContent.qaContentId=:qaContentId order by qaQueContent.displayOrder";
	 	
	 	public QaQueContent getToolDefaultQuestionContent(final long qaContentId)
	    {
	 	    /*
	 	    logger.debug("running getToolDefaultQuestionContent: "  + qaContentId);
	        return (QaQueContent) getHibernateTemplate().execute(new HibernateCallback()
	         {
	             public Object doInHibernate(Session session) throws HibernateException
	             {
	                 return session.createQuery(LOAD_QUESTION_CONTENT_BY_CONTENT_ID)
	                               .setLong("qaContentId", qaContentId)
	                               .uniqueResult();
	             }
	         });
	         */

	 	    HibernateTemplate templ = this.getHibernateTemplate();
			List list = getSession().createQuery(LOAD_QUESTION_CONTENT_BY_CONTENT_ID)
				.setLong("qaContentId", qaContentId)
				.list();

			if(list != null && list.size() > 0){
				QaQueContent qa = (QaQueContent) list.get(0);
				return qa;
			}
			return null;
	    }
	 	

	 	public QaQueContent getQaQueById(long qaQueContentId)
	 	{
			String query = "from QaQueContent as qu where qu.qaQueContentId = ?";
		    HibernateTemplate templ = this.getHibernateTemplate();
			List list = getSession().createQuery(query)
				.setLong(0,qaQueContentId)
				.list();
			
			if(list != null && list.size() > 0){
				QaQueContent qa = (QaQueContent) list.get(0);
				return qa;
			}
			return null;
	 	}

	 	
	 	public QaQueContent getQuestionContentByQuestionText(final String question, Long qaContentId)
	    {
	        HibernateTemplate templ = this.getHibernateTemplate();

	        List list = getSession().createQuery(LOAD_QUESTION_CONTENT_BY_QUESTION_TEXT)
			.setString("question", question)
			.setLong("qaContentId", qaContentId.longValue())				
			.list();
			
			if(list != null && list.size() > 0){
				QaQueContent qa = (QaQueContent) list.get(0);
				return qa;
			}
			return null;
	    }


	 	public QaQueContent getQuestionContentByDisplayOrder(Long displayOrder, Long qaContentId)
	    {
	        HibernateTemplate templ = this.getHibernateTemplate();

	        List list = getSession().createQuery(LOAD_QUESTION_CONTENT_BY_DISPLAY_ORDER)
			.setLong("displayOrder", displayOrder.longValue())
			.setLong("qaContentId", qaContentId.longValue())				
			.list();
			
			if(list != null && list.size() > 0){
				QaQueContent qa = (QaQueContent) list.get(0);
				return qa;
			}
			return null;
	    }

	 	public List getAllQuestionEntriesSorted(final long qaContentId)
	    {
	        HibernateTemplate templ = this.getHibernateTemplate();
			List list = getSession().createQuery(SORT_QUESTION_CONTENT_BY_DISPLAY_ORDER)
				.setLong("qaContentId", qaContentId)
				.list();

			return list;
	    }


	 	public List getQuestionIndsForContent(QaContent qa)
	    {
	    	   
			  List listDefaultQuestionIds=(getHibernateTemplate().findByNamedParam(GET_QUESTION_IDS_FOR_CONTENT,
	                "qa",
	                qa));
			  
			  return listDefaultQuestionIds;
	    }
	 	
	 	public List getAllQuestionEntries(final long qaContentId)
	    {
	        HibernateTemplate templ = this.getHibernateTemplate();
			List list = getSession().createQuery(LOAD_QUESTION_CONTENT_BY_CONTENT_ID)
				.setLong("qaContentId", qaContentId)
				.list();

			return list;
	    }
	 	
	 	
		public void createQueContent(QaQueContent queContent) 
	    {
			this.getSession().setFlushMode(FlushMode.AUTO);
	    	this.getHibernateTemplate().save(queContent);
	    }
		
		public void updateQaQueContent(QaQueContent qaQueContent)
	    {
			this.getSession().setFlushMode(FlushMode.AUTO);
	    	this.getHibernateTemplate().update(qaQueContent);
	    }

		
		public void removeQueContent(long qaQueContentId) 
	    {
			QaQueContent qaQueContent= (QaQueContent) this.getHibernateTemplate().load(QaQueContent.class, new Long(qaQueContentId));
			this.getSession().setFlushMode(FlushMode.AUTO);
	    	this.getHibernateTemplate().delete(qaQueContent);
	    }
		
		
		public void removeQaQueContent(QaQueContent qaQueContent)
	    {
			this.getSession().setFlushMode(FlushMode.AUTO);
	        this.getHibernateTemplate().delete(qaQueContent);
	    }

        
        public List getQaQueContentsByContentId(long qaContentId){
            return getHibernateTemplate().findByNamedParam(LOAD_QUESTION_CONTENT_BY_CONTENT_ID, "qaContentId", new Long(qaContentId));
        }
} 