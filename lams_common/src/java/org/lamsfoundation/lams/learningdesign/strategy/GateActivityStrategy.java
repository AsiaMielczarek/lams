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
package org.lamsfoundation.lams.learningdesign.strategy;

import java.util.ArrayList;
import java.util.List;

import org.lamsfoundation.lams.learningdesign.Activity;
import org.lamsfoundation.lams.learningdesign.GateActivity;
import org.lamsfoundation.lams.usermanagement.User;

/**
 * Activity strategy that deal with the calculation of all sub gate activities.
 * It is abstract calculation layer for all concrete gate activities.
 * 
 * @author Jacky Fang
 * @since  2005-4-6
 * @version 1.1
 * 
 */
public abstract class GateActivityStrategy extends SimpleActivityStrategy {
	protected GateActivity gateActivity = null;

	public GateActivityStrategy(GateActivity gateActivity) {
		this.gateActivity = gateActivity;
	}

	//---------------------------------------------------------------------
	// Template methods
	//---------------------------------------------------------------------
	        /**
     * Returns wether we should open the gate or close the gate. It's implementation depends on the type of the gate we
     * are dealing with. Generally, it needs the check up the current gate status. If the gate is already opened, we
     * will keep it open for current learner. Otherwise, we need to validate the open condition for this learner.
     * 
     * @param learner
     *            the learner who just arrived at the gate.
     * @param activity
     *            the gate activity.
     * @param lessonLearners
     *            all learners who have started the lesson
     * @return whether we should open it or not.
     */
    public boolean shouldOpenGateFor(User learner, List lessonLearners) {
	gateActivity.addLeaner(learner, false);

	boolean isOpen = gateActivity.getGateOpen().booleanValue();

	if (!isOpen) {
	    if (isOpenConditionMet(lessonLearners)) {
		gateActivity.setGateOpen(new Boolean(true));
	    } else {
		return gateActivity.getAllowedToPassLearners().contains(learner);
	    }
	}

	gateActivity.getAllGateUsers().clear();
	gateActivity.getWaitingLearners().clear();
	gateActivity.getAllowedToPassLearners().clear();
	return isOpen;
    }

    // ---------------------------------------------------------------------
	// Abstract methods
	//---------------------------------------------------------------------
	/**
	 * Check up the open condition according the gate type.
	 * @return return true if the condition is met.
	 */
	protected abstract boolean isOpenConditionMet(List lessonLearners);

	//---------------------------------------------------------------------
	// Overidden methods
	//---------------------------------------------------------------------
	/**
	 * @see org.lamsfoundation.lams.learningdesign.strategy.SimpleActivityStrategy#setUpContributionType(org.lamsfoundation.lams.learningdesign.Activity, java.util.ArrayList)
	 */
	@Override
	protected abstract void setUpContributionType(ArrayList<Integer> contributionTypes);

	/**
	 * Get the activity for this strategy. The activity should be set
	 * when the strategy is created.
	 */
	@Override
	protected Activity getActivity() {
		return gateActivity;
	}
}
