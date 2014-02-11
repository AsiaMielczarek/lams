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

package org.lamsfoundation.lams.tool.vote.dao;

import java.util.List;

import org.lamsfoundation.lams.tool.vote.pojos.VoteContent;
import org.lamsfoundation.lams.tool.vote.pojos.VoteSession;

/**
 * @author Ozgur Demirtas
 * 
 * <p>
 * Interface that defines the contract for VoteContent access
 * </p>
 */
public interface IVoteContentDAO {
    public VoteContent getVoteContentByUID(Long uid);

    public VoteContent findVoteContentById(Long voteContentId);

    public VoteContent getVoteContentBySession(Long voteSessionId);

    public void saveVoteContent(VoteContent voteContent);

    public void updateVoteContent(VoteContent voteContent);

    public void saveOrUpdateVote(VoteContent voteContent);

    public void removeVote(VoteContent voteContent);

    public void removeVoteById(Long voteContentId);

    public void removeVoteSessions(VoteContent voteContent);

    public void addVoteSession(Long voteContentId, VoteSession voteSession);

    public List findAll(Class objClass);

    public void flush();

    public void removeNominationsFromCache(VoteContent voteContent);
    
    public void delete(Object object);
}