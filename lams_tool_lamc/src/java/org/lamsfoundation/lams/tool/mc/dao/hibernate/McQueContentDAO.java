/***************************************************************************
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
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 * 
 * http://www.gnu.org/licenses/gpl.txt
 * ***********************************************************************/
/* $$Id$$ */
package org.lamsfoundation.lams.tool.mc.dao.hibernate;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.FlushMode;
import org.lamsfoundation.lams.tool.mc.pojos.McOptsContent;
import org.lamsfoundation.lams.tool.mc.pojos.McQueContent;
import org.lamsfoundation.lams.tool.mc.dao.IMcQueContentDAO;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;


/**
 * @author Ozgur Demirtas
 * 
 * <p>Hibernate implementation for database access to McQueContent for the mc tool.</p>
 */
public class McQueContentDAO extends HibernateDaoSupport implements IMcQueContentDAO {
	 	static Logger logger = Logger.getLogger(McQueContentDAO.class.getName());
	 	
	 	private static final String LOAD_QUESTION_CONTENT_BY_CONTENT_ID = "from mcQueContent in class McQueContent where mcQueContent.mcContentId=:mcContentId order by mcQueContent.displayOrder";
	 	
	 	private static final String CLEAN_QUESTION_CONTENT_BY_CONTENT_ID_SIMPLE = "from mcQueContent in class McQueContent where mcQueContent.mcContentId=:mcContentId";
	 	
	 	private static final String FIND_QUESTION_CONTENT_BY_UID = "from mcQueContent in class McQueContent where mcQueContent.uid=:uid";
	 	
	 	private static final String CLEAN_QUESTION_CONTENT_BY_CONTENT_ID = "from mcQueContent in class McQueContent where mcQueContent.mcContentId=:mcContentId";
	 	
	 	private static final String REFRESH_QUESTION_CONTENT 			= "from mcQueContent in class McQueContent where mcQueContent.mcContentId=:mcContentId order by mcQueContent.displayOrder";
	 	
	 	private static final String LOAD_QUESTION_CONTENT_BY_QUESTION_TEXT = "from mcQueContent in class McQueContent where mcQueContent.question=:question and mcQueContent.mcContentId=:mcContentUid";
	 	
	 	private static final String LOAD_QUESTION_CONTENT_BY_DISPLAY_ORDER = "from mcQueContent in class McQueContent where mcQueContent.displayOrder=:displayOrder and mcQueContent.mcContentId=:mcContentUid";
	 	
	 	private static final String GET_NEXT_AVAILABLE_DISPLAY_ORDER = "from mcQueContent in class McQueContent where mcQueContent.mcContentId=:mcContentId";
	 		 	
	 	
	 	public McQueContent findMcQuestionContentByUid(Long uid)
	    {
			HibernateTemplate templ = this.getHibernateTemplate();
			if ( uid != null) {
				List list = getSession().createQuery(FIND_QUESTION_CONTENT_BY_UID)
					.setLong("uid",uid.longValue())
					.list();
				
				if(list != null && list.size() > 0){
				    McQueContent mcq = (McQueContent) list.get(0);
					return mcq;
				}
			}
			return null;
	    }

	 	
	 	
	 	public McQueContent getMcQueContentByUID(Long uid)
		{
			 return (McQueContent) this.getHibernateTemplate()
	         .get(McQueContent.class, uid);
		}
		
		
	 	public McQueContent getToolDefaultQuestionContent(final long mcContentId)
	    {
	        HibernateTemplate templ = this.getHibernateTemplate();
			List list = getSession().createQuery(LOAD_QUESTION_CONTENT_BY_CONTENT_ID)
				.setLong("mcContentId", mcContentId)
				.list();
			
			if(list != null && list.size() > 0){
				McQueContent mcq = (McQueContent) list.get(0);
				return mcq;
			}
			return null;
	    }
	 	
	 	
	 	public List getAllQuestionEntries(final long mcContentId)
	    {
	        HibernateTemplate templ = this.getHibernateTemplate();
			List list = getSession().createQuery(LOAD_QUESTION_CONTENT_BY_CONTENT_ID)
				.setLong("mcContentId", mcContentId)
				.list();

			return list;
	    }
	 	
	 	public List refreshQuestionContent(final Long mcContentId)
	    {
	        HibernateTemplate templ = this.getHibernateTemplate();
			List list = getSession().createQuery(REFRESH_QUESTION_CONTENT)
				.setLong("mcContentId", mcContentId.longValue())
				.list();
			
			return list;
	    }
	 	
	 	
	 	public McQueContent getQuestionContentByQuestionText(final String question, final Long mcContentUid)
	    {
	        HibernateTemplate templ = this.getHibernateTemplate();
			List list = getSession().createQuery(LOAD_QUESTION_CONTENT_BY_QUESTION_TEXT)
				.setString("question", question)
				.setLong("mcContentUid", mcContentUid.longValue())				
				.list();
			
			if(list != null && list.size() > 0){
				McQueContent mcq = (McQueContent) list.get(0);
				return mcq;
			}
			return null;
	    }
	 	
	 	
	 	public McQueContent getQuestionContentByDisplayOrder(final Long displayOrder, final Long mcContentUid)
	    {
	        HibernateTemplate templ = this.getHibernateTemplate();
			List list = getSession().createQuery(LOAD_QUESTION_CONTENT_BY_DISPLAY_ORDER)
				.setLong("displayOrder", displayOrder.longValue())
				.setLong("mcContentUid", mcContentUid.longValue())				
				.list();
			
			if(list != null && list.size() > 0){
				McQueContent mcq = (McQueContent) list.get(0);
				return mcq;
			}
			return null;
	    }
	 	
	 	
	 	public void removeQuestionContentByMcUid(final Long mcContentUid)
	    {
			HibernateTemplate templ = this.getHibernateTemplate();
			List list = getSession().createQuery(LOAD_QUESTION_CONTENT_BY_CONTENT_ID)
				.setLong("mcContentId", mcContentUid.longValue())
				.list();

			if(list != null && list.size() > 0){
				Iterator listIterator=list.iterator();
		    	while (listIterator.hasNext())
		    	{
		    		McQueContent mcQueContent=(McQueContent)listIterator.next();
					this.getSession().setFlushMode(FlushMode.AUTO);
		    		templ.delete(mcQueContent);
		    		templ.flush();
		    	}
			}
	    }
	 	

	 	public void resetAllQuestions(final Long mcContentUid)
	    {
			HibernateTemplate templ = this.getHibernateTemplate();
			List list = getSession().createQuery(LOAD_QUESTION_CONTENT_BY_CONTENT_ID)
				.setLong("mcContentId", mcContentUid.longValue())
				.list();

			if(list != null && list.size() > 0){
				Iterator listIterator=list.iterator();
		    	while (listIterator.hasNext())
		    	{
		    		McQueContent mcQueContent=(McQueContent)listIterator.next();
					this.getSession().setFlushMode(FlushMode.AUTO);
		    		templ.update(mcQueContent);
		    	}
			}
	    }
	 	

	 	public void cleanAllQuestions(final Long mcContentUid)
	    {
			HibernateTemplate templ = this.getHibernateTemplate();
			List list = getSession().createQuery(CLEAN_QUESTION_CONTENT_BY_CONTENT_ID)
				.setLong("mcContentId", mcContentUid.longValue())
				.list();

			if(list != null && list.size() > 0){
				Iterator listIterator=list.iterator();
		    	while (listIterator.hasNext())
		    	{
		    		McQueContent mcQueContent=(McQueContent)listIterator.next();
	    			this.getSession().setFlushMode(FlushMode.AUTO);
	    			logger.debug("deleting mcQueContent: " + mcQueContent);
		    		templ.delete(mcQueContent);	
		    	}
			}
	    }

	 	
	 	public void cleanAllQuestionsSimple(final Long mcContentUid)
	    {
			HibernateTemplate templ = this.getHibernateTemplate();
			List list = getSession().createQuery(CLEAN_QUESTION_CONTENT_BY_CONTENT_ID_SIMPLE)
				.setLong("mcContentId", mcContentUid.longValue())
				.list();

			if(list != null && list.size() > 0){
				Iterator listIterator=list.iterator();
		    	while (listIterator.hasNext())
		    	{
		    		McQueContent mcQueContent=(McQueContent)listIterator.next();
	    			this.getSession().setFlushMode(FlushMode.AUTO);
	    			logger.debug("deleting mcQueContent: " + mcQueContent);
		    		templ.delete(mcQueContent);	
		    	}
			}
	    }

	 	
	 	public List getNextAvailableDisplayOrder(final long mcContentId)
	    {
	        HibernateTemplate templ = this.getHibernateTemplate();
			List list = getSession().createQuery(GET_NEXT_AVAILABLE_DISPLAY_ORDER)
				.setLong("mcContentId", mcContentId)
				.list();
			
			return list;
	    }

	 	
	 	public void saveMcQueContent(McQueContent mcQueContent)
	    {
	    	this.getHibernateTemplate().save(mcQueContent);
	    }
	    
		public void updateMcQueContent(McQueContent mcQueContent)
	    {
	    	this.getHibernateTemplate().update(mcQueContent);
	    }
		
		public void saveOrUpdateMcQueContent(McQueContent mcQueContent)
	    {
	    	this.getHibernateTemplate().saveOrUpdate(mcQueContent);
	    }
		
		public void removeMcQueContentByUID(Long uid)
	    {
			McQueContent mcq = (McQueContent)getHibernateTemplate().get(McQueContent.class, uid);
			this.getSession().setFlushMode(FlushMode.AUTO);
	    	this.getHibernateTemplate().delete(mcq);
	    }
		
		
		public void removeMcQueContent(McQueContent mcQueContent)
	    {
		    if ((mcQueContent != null) && (mcQueContent.getUid() != null))
		    {
				this.getSession().setFlushMode(FlushMode.AUTO);
		        this.getHibernateTemplate().delete(mcQueContent);
		        
		    }
	    }
		
		 public void flush()
	    {
	        this.getHibernateTemplate().flush();
	    }
	} 