/*******************************************************************************
 * Copyright (c) 2008 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id: $
 ******************************************************************************/

package org.eclemma.runtime.equinox.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * This class is just a small helper utils for getting system properties.
 * 
 * @author Mikkel T Andersen
 */
public class PropertyUtils {

	/**
	 * converts the property to a list of strings using the , comma as a
	 * separator.
	 * 
	 * @param property
	 *            the property to slit up and add to the list.
	 * @return the list of properties as strings.
	 */
	public static List toList(String property) {
		List strings = new ArrayList();
		if (property == null || property.trim().length() == 0) {
			return strings;
		}

		StringTokenizer tokens = new StringTokenizer(property, ",");
		while (tokens.hasMoreTokens()) {
			strings.add(tokens.nextToken().trim());
		}
		return strings;
	}
}
