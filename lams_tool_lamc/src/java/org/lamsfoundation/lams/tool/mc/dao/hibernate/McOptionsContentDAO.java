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
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.FlushMode;
import org.lamsfoundation.lams.tool.mc.McCandidateAnswersDTO;
import org.lamsfoundation.lams.tool.mc.dao.IMcOptionsContentDAO;
import org.lamsfoundation.lams.tool.mc.pojos.McOptsContent;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;


/**
 * @author Ozgur Demirtas
 * <p>Hibernate implementation for database access to McOptionsContent for the mc tool.</p>
 */
public class McOptionsContentDAO extends HibernateDaoSupport implements IMcOptionsContentDAO {
	 	static Logger logger = Logger.getLogger(McOptionsContentDAO.class.getName());
	 	
	 	//private static final String FIND_MC_OPTIONS_CONTENT = "from " + McOptsContent.class.getName() + " as mco where mc_que_content_id=?";
	 	private static final String FIND_MC_OPTIONS_CONTENT = "from mcOptsContent in class McOptsContent where mcOptsContent.mcQueContentId=:mcQueContentUid order by mcOptsContent.uid";
	 	private static final String FIND_MC_OPTIONS_CONTENT_BY_UID = "from mcOptsContent in class McOptsContent where mcOptsContent.uid=:uid";
	 	
	 	private static final String LOAD_OPTION_CONTENT_BY_OPTION_TEXT = "from mcOptsContent in class McOptsContent where mcOptsContent.mcQueOptionText=:option and mcOptsContent.mcQueContentId=:mcQueContentUid";
	 	
	 	private static final String LOAD_PERSISTED_SELECTED_OPTIONS = "from mcOptsContent in class McOptsContent where mcOptsContent.mcQueContentId=:mcQueContentUid and  mcOptsContent.correctOption = 1";
	 	
	 	private static final String LOAD_CORRECT_OPTION = "from mcOptsContent in class McOptsContent where mcOptsContent.mcQueContentId=:mcQueContentUid and  mcOptsContent.correctOption = 1";
	 	
	 	public McOptsContent getMcOptionsContentByUID(Long uid)
		{
			 return (McOptsContent) this.getHibernateTemplate()
	         .get(McOptsContent.class, uid);
		}
		
	 	
	 	public List findMcOptionsContentByQueId(Long mcQueContentId)
	    {
			HibernateTemplate templ = this.getHibernateTemplate();
			if ( mcQueContentId != null) {
				List list = getSession().createQuery(FIND_MC_OPTIONS_CONTENT)
					.setLong("mcQueContentUid",mcQueContentId.longValue())
					.list();
				return list;
			}
			return null;
	    }

	 	
	 	public McOptsContent findMcOptionsContentByUid(Long uid)
	    {
			HibernateTemplate templ = this.getHibernateTemplate();
			if ( uid != null) {
				List list = getSession().createQuery(FIND_MC_OPTIONS_CONTENT_BY_UID)
					.setLong("uid",uid.longValue())
					.list();
				
				if(list != null && list.size() > 0){
					McOptsContent mco = (McOptsContent) list.get(0);
					return mco;
				}
			}
			return null;
	    }

	 	
	 	public List findMcOptionUidsByQueId(Long mcQueContentId)
	    {
	 		
	 		List listOptionUids= new LinkedList();
	 		
			HibernateTemplate templ = this.getHibernateTemplate();
			if ( mcQueContentId != null) {
				List list = getSession().createQuery(FIND_MC_OPTIONS_CONTENT)
					.setLong("mcQueContentUid",mcQueContentId.longValue())					
					.list();
				
				if(list != null && list.size() > 0){
					Iterator listIterator=list.iterator();
			    	while (listIterator.hasNext())
			    	{
			    		McOptsContent mcOptsContent=(McOptsContent)listIterator.next();
			    		listOptionUids.add(mcOptsContent.getUid().toString());
			    	}
				}
			}
			return listOptionUids;
	    }
	 	
	 	
	 	public List findMcOptionNamesByQueId(Long mcQueContentId)
	    {
	 		
	 		List listOptionNames= new LinkedList();
	 		
			HibernateTemplate templ = this.getHibernateTemplate();
			if ( mcQueContentId != null) {
				List list = getSession().createQuery(FIND_MC_OPTIONS_CONTENT)
					.setLong("mcQueContentUid",mcQueContentId.longValue())					
					.list();
				
				if(list != null && list.size() > 0){
					Iterator listIterator=list.iterator();
			    	while (listIterator.hasNext())
			    	{
			    		McOptsContent mcOptsContent=(McOptsContent)listIterator.next();
			    		listOptionNames.add(mcOptsContent.getMcQueOptionText());
			    	}
				}
			}
			return listOptionNames;
	    }
	 	
	 	
	 	public List populateCandidateAnswersDTO(Long mcQueContentId)
	    {
	 		List listCandidateAnswersData= new LinkedList();
	 		
			HibernateTemplate templ = this.getHibernateTemplate();
			if ( mcQueContentId != null) {
				List list = getSession().createQuery(FIND_MC_OPTIONS_CONTENT)
					.setLong("mcQueContentUid",mcQueContentId.longValue())
					.list();
				
				if(list != null && list.size() > 0){
					Iterator listIterator=list.iterator();
			    	while (listIterator.hasNext())
			    	{
			    	    McOptsContent mcOptsContent=(McOptsContent)listIterator.next();
			    	    McCandidateAnswersDTO mcCandidateAnswersDTO= new McCandidateAnswersDTO();
			    	    mcCandidateAnswersDTO.setCandidateAnswer(mcOptsContent.getMcQueOptionText());
			    	    mcCandidateAnswersDTO.setCorrect(new Boolean(mcOptsContent.isCorrectOption()).toString());
			    		listCandidateAnswersData.add(mcCandidateAnswersDTO);
			    	}
				}
			}
			return listCandidateAnswersData;
	    }

	 	
	 	public List findMcOptionCorrectByQueId(Long mcQueContentId)
	    {
	 		
	 		List listOptionCorrect= new LinkedList();
	 		
			HibernateTemplate templ = this.getHibernateTemplate();
			if ( mcQueContentId != null) {
				List list = getSession().createQuery(FIND_MC_OPTIONS_CONTENT)
					.setLong("mcQueContentUid",mcQueContentId.longValue())
					.list();
				
				if(list != null && list.size() > 0){
					Iterator listIterator=list.iterator();
			    	while (listIterator.hasNext())
			    	{
			    		McOptsContent mcOptsContent=(McOptsContent)listIterator.next();
			    		listOptionCorrect.add(new Boolean(mcOptsContent.isCorrectOption()).toString());
			    	}
				}
			}
			return listOptionCorrect;
	    }

	 	
	 	
	 	public McOptsContent getOptionContentByOptionText(final String option, final Long mcQueContentUid)
	    {
	        HibernateTemplate templ = this.getHibernateTemplate();
			List list = getSession().createQuery(LOAD_OPTION_CONTENT_BY_OPTION_TEXT)
				.setString("option", option)
				.setLong("mcQueContentUid", mcQueContentUid.longValue())				
				.list();
			
			if(list != null && list.size() > 0){
				McOptsContent mcq = (McOptsContent) list.get(0);
				return mcq;
			}
			return null;
	    }
	 	
	 	
	 	public List getPersistedSelectedOptions(Long mcQueContentId) 
	 	{
	 		HibernateTemplate templ = this.getHibernateTemplate();
			List list = getSession().createQuery(LOAD_PERSISTED_SELECTED_OPTIONS)
				.setLong("mcQueContentUid", mcQueContentId.longValue())
				.list();
			
			return list;
	 	}
	 	
	 	public List getCorrectOption(Long mcQueContentId) 
	 	{
	 		HibernateTemplate templ = this.getHibernateTemplate();
			List list = getSession().createQuery(LOAD_CORRECT_OPTION)
				.setLong("mcQueContentUid", mcQueContentId.longValue())
				.list();
			
			return list;
	 	}
		
		public void saveMcOptionsContent(McOptsContent mcOptsContent)
	    {
	    	this.getHibernateTemplate().save(mcOptsContent);
	    }
	    
		public void updateMcOptionsContent(McOptsContent mcOptsContent)
	    {
	    	this.getHibernateTemplate().update(mcOptsContent);
	    }
		
		
		public void removeMcOptionsContentByUID(Long uid)
	    {
			McOptsContent mco = (McOptsContent)getHibernateTemplate().get(McOptsContent.class, uid);
	    	this.getHibernateTemplate().delete(mco);
	    }
		
		
		public void removeMcOptionsContentByQueId(Long mcQueContentId)
	    {
			HibernateTemplate templ = this.getHibernateTemplate();
			List list = getSession().createQuery(FIND_MC_OPTIONS_CONTENT)
				.setLong("mcQueContentUid",mcQueContentId.longValue())
				.list();

			if(list != null && list.size() > 0){
				Iterator listIterator=list.iterator();
		    	while (listIterator.hasNext())
		    	{
		    		McOptsContent mcOptsContent=(McOptsContent)listIterator.next();
					this.getSession().setFlushMode(FlushMode.AUTO);
		    		templ.delete(mcOptsContent);
		    	}
			}
	    }
				
		
		public void removeMcOptionsContent(McOptsContent mcOptsContent)
	    {
			this.getSession().setFlushMode(FlushMode.AUTO);
	        this.getHibernateTemplate().delete(mcOptsContent);
	    }
		
		 public void flush()
	    {
	        this.getHibernateTemplate().flush();
	    }
	} 