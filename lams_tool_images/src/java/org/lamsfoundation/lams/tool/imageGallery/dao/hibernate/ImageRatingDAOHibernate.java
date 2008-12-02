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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 * USA 
 * 
 * http://www.gnu.org/licenses/gpl.txt 
 * **************************************************************** 
 */

/* $Id$ */
package org.lamsfoundation.lams.tool.imageGallery.dao.hibernate;

import java.util.List;

import org.lamsfoundation.lams.tool.imageGallery.dao.ImageRatingDAO;
import org.lamsfoundation.lams.tool.imageGallery.model.ImageRating;

/**
 * Hibernate implementation of <code>ImageCommentDAO</code>.
 * 
 * @author Andrey Balan
 * @see org.lamsfoundation.lams.tool.imageGallery.dao.ImageCommentDAO
 */
public class ImageRatingDAOHibernate extends BaseDAOHibernate implements ImageRatingDAO {

    private static final String FIND_BY_IMAGE_AND_USER = "from " + ImageRating.class.getName()
	    + " as r where r.createBy.userId = ? and r.imageGalleryItem.uid=?";

    private static final String FIND_BY_IMAGE_UID = "from " + ImageRating.class.getName()
	    + " as r where r.imageGalleryItem.uid=?";

    public ImageRating getImageRatingByImageAndUser(Long imageUid, Long userId) {
	List list = getHibernateTemplate().find(FIND_BY_IMAGE_AND_USER, new Object[] { userId, imageUid });
	if (list == null || list.size() == 0)
	    return null;
	return (ImageRating) list.get(0);
    }

    public List<ImageRating> getImageRatingsByImageUid(Long imageUid) {
	return getHibernateTemplate().find(FIND_BY_IMAGE_UID, imageUid);
    }

}
