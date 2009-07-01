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

package org.lamsfoundation.lams.tool.wiki.dao.hibernate;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.FlushMode;
import org.lamsfoundation.lams.dao.hibernate.BaseDAO;
import org.lamsfoundation.lams.tool.wiki.dao.IWikiDAO;
import org.lamsfoundation.lams.tool.wiki.model.Wiki;
import org.lamsfoundation.lams.tool.wiki.model.WikiAttachment;
import org.lamsfoundation.lams.tool.wiki.model.WikiPage;
import org.springframework.orm.hibernate3.HibernateTemplate;

/**
 * DAO for accessing the Wiki objects - Hibernate specific code.
 */
public class WikiDAO extends BaseDAO implements IWikiDAO {

    private static final String FIND_FORUM_BY_CONTENTID = "from Wiki wiki where wiki.toolContentId=?";

    private static final String FIND_INSTRUCTION_FILE = "from " + WikiAttachment.class.getName()
	    + " as i where tool_content_id=? and i.file_uuid=? and i.file_version_id=? and i.file_type=?";

    public Wiki getByContentId(Long toolContentId) {
	List list = getHibernateTemplate().find(FIND_FORUM_BY_CONTENTID, toolContentId);
	if (list != null && list.size() > 0) {
	    Wiki wiki = (Wiki) list.get(0);
	    removeDuplicatePages(wiki);
	    return wiki;
	} else {
	    return null;
	}

    }

    public void saveOrUpdate(Wiki wiki) {
	// Removing duplicate pages 
	removeDuplicatePages(wiki);
	this.getHibernateTemplate().saveOrUpdate(wiki);
	//this.getHibernateTemplate().flush();
    }

    public void deleteInstructionFile(Long toolContentId, Long uuid, Long versionId, String type) {
	HibernateTemplate templ = this.getHibernateTemplate();
	if (toolContentId != null && uuid != null && versionId != null) {
	    List list = getSession().createQuery(FIND_INSTRUCTION_FILE).setLong(0, toolContentId.longValue()).setLong(
		    1, uuid.longValue()).setLong(2, versionId.longValue()).setString(3, type).list();
	    if (list != null && list.size() > 0) {
		WikiAttachment file = (WikiAttachment) list.get(0);
		this.getSession().setFlushMode(FlushMode.AUTO);
		templ.delete(file);
		templ.flush();
	    }
	}

    }

    /**
     * Although we are dealing with a set, still somehow duplicates are coming
     * through. This method removes them.
     * 
     * @param wiki
     */
    public void removeDuplicatePages(Wiki wiki) {
	Set<WikiPage> wikiPages = wiki.getWikiPages();
	if (wikiPages != null) {
	    Set<WikiPage> wikiPagesCopy = new HashSet<WikiPage>(wikiPages);
	    Iterator<WikiPage> it = wikiPages.iterator();
	    while (it.hasNext()) {
		WikiPage page = (WikiPage) it.next();
		if (containsDuplicate(page, wikiPagesCopy)) {
		    it.remove();
		    wikiPagesCopy = new HashSet<WikiPage>(wikiPages);
		}
	    }
	}
    }

    private boolean containsDuplicate(WikiPage compPage, Set<WikiPage> wikiPages) {
	int count = 0;
	for (WikiPage page : wikiPages) {
	    if (page.getTitle().equals(compPage.getTitle())) {
		count++;
	    }
	}
	return count > 1;
    }
}
