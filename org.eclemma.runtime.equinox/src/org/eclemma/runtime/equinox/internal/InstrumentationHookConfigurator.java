/*******************************************************************************
 * Copyright (c) 2008 Mountainminds GmbH & Co. KG
 * This software is provided under the terms of the Eclipse Public License v1.0
 * See http://www.eclipse.org/legal/epl-v10.html.
 *
 * $Id: $
 ******************************************************************************/

package org.eclemma.runtime.equinox.internal;

import org.eclemma.runtime.equinox.ICoverageAnalyzer;
import org.eclipse.osgi.baseadaptor.HookConfigurator;
import org.eclipse.osgi.baseadaptor.HookRegistry;

/**
 * This class is referenced in the hookconfigurator.properties file and installs
 * our hooks.
 * 
 * @author Marc R. Hoffmann
 */
public class InstrumentationHookConfigurator implements HookConfigurator {

	protected ICoverageAnalyzer getAnalyzer() {
		return new EMMAAnalyzer();
	}

	public void addHooks(HookRegistry hookRegistry) {
		InstrumentationHook hook = new InstrumentationHook(getAnalyzer());
		hookRegistry.addAdaptorHook(hook);
		hookRegistry.addClassLoadingHook(hook);
	}
}
