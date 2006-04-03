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
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 * 
 * http://www.gnu.org/licenses/gpl.txt
 * ****************************************************************
 */
/* $$Id$$ */
package org.lamsfoundation.lams.themes;

/**
 * LAMS property types.
 */
public final class PropertyType {
	/** String property type */
    public static final int STRING = 1;
	/** Long property type */
    public static final int LONG = 2;
	/** Double property type */
    public static final int DOUBLE = 3;
	/** Date property type */
    public static final int DATE = 4;
	/** Boolean property type */
    public static final int BOOLEAN = 5;

    /**
     * The names of the supported property types,
     * as used to convert to .
     */
    public static final String TYPENAME_STRING = "String";
    public static final String TYPENAME_LONG = "Long";
    public static final String TYPENAME_DOUBLE = "Double";
    public static final String TYPENAME_DATE = "Date";
    public static final String TYPENAME_BOOLEAN = "Boolean";

    /**
     * Returns the name of the specified type.
     * @param type the property type
     * @return the name of the specified type, defaults to String if no match.
     */
    public static String nameFromValue(int type) {
        switch (type) {
            case STRING:
                return TYPENAME_STRING;
            case BOOLEAN:
                return TYPENAME_BOOLEAN;
            case LONG:
                return TYPENAME_LONG;
            case DOUBLE:
                return TYPENAME_DOUBLE;
            case DATE:
                return TYPENAME_DATE;
            default:
                return TYPENAME_STRING;
	    }
    }

    /**
     * Returns the numeric constant value of the type with the specified name.
     * @param name the name of the property type
     * @return the numeric constant value, defaulting to String if name not recognised.
     */
    public static int valueFromName(String name) {
        if (name.equals(TYPENAME_STRING)) {
            return STRING;
        } else if (name.equals(TYPENAME_BOOLEAN)) {
            return BOOLEAN;
        } else if (name.equals(TYPENAME_LONG)) {
            return LONG;
        } else if (name.equals(TYPENAME_DOUBLE)) {
            return DOUBLE;
        } else if (name.equals(TYPENAME_DATE)) {
            return DATE;
        } else {
            return STRING;
        }
    }

    /** private constructor to prevent instantiation */
    private PropertyType() {
    }
}

