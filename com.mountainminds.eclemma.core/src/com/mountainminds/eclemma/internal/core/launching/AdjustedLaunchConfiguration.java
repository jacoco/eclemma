/*******************************************************************************
 * Copyright (c) 2006, 2013 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *    
 ******************************************************************************/
package com.mountainminds.eclemma.internal.core.launching;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchDelegate;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;

/**
 * Internal wrapper for {@link ILaunchConfiguration} instances. The only purpose
 * of the wrapper is to adds an VM argument to the launch configuration without
 * modifying the original configuration.
 */
class AdjustedLaunchConfiguration implements ILaunchConfiguration {

  private static final String KEY = IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS;

  private final ILaunchConfiguration delegate;

  private final String extraVMArgument;

  public AdjustedLaunchConfiguration(String extraVMArgument,
      ILaunchConfiguration delegate) {
    this.extraVMArgument = extraVMArgument;
    this.delegate = delegate;
  }

  public boolean hasAttribute(String attributeName) throws CoreException {
    return KEY.equals(attributeName) || delegate.hasAttribute(attributeName);
  }

  public String getAttribute(String attributeName, String defaultValue)
      throws CoreException {
    if (KEY.equals(attributeName)) {
      return getVMArguments();
    } else {
      return delegate.getAttribute(attributeName, defaultValue);
    }
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public Map getAttributes() throws CoreException {
    final Map map = new HashMap(delegate.getAttributes());
    map.put(KEY, getVMArguments());
    return map;
  }

  private String getVMArguments() throws CoreException {
    final String original = delegate.getAttribute(KEY, ""); //$NON-NLS-1$
    if (original.length() > 0) {
      return extraVMArgument + ' ' + original;
    } else {
      return extraVMArgument;
    }
  }

  public boolean isWorkingCopy() {
    return false;
  }

  public ILaunchConfigurationWorkingCopy copy(String name) throws CoreException {
    return new AdjustedLaunchConfigurationWorkingCopy(extraVMArgument,
        delegate.copy(name), null);
  }

  public ILaunchConfigurationWorkingCopy getWorkingCopy() throws CoreException {
    return new AdjustedLaunchConfigurationWorkingCopy(extraVMArgument,
        delegate.getWorkingCopy(), this);
  }

  // delegate-only methods:

  public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
    return delegate.getAdapter(adapter);
  }

  public boolean contentsEqual(ILaunchConfiguration configuration) {
    return delegate.contentsEqual(configuration);
  }

  public void delete() throws CoreException {
    delegate.delete();
  }

  public boolean exists() {
    return delegate.exists();
  }

  public boolean getAttribute(String attributeName, boolean defaultValue)
      throws CoreException {
    return delegate.getAttribute(attributeName, defaultValue);
  }

  public int getAttribute(String attributeName, int defaultValue)
      throws CoreException {
    return delegate.getAttribute(attributeName, defaultValue);
  }

  @SuppressWarnings("rawtypes")
  public List getAttribute(String attributeName, List defaultValue)
      throws CoreException {
    return delegate.getAttribute(attributeName, defaultValue);
  }

  @SuppressWarnings("rawtypes")
  public Set getAttribute(String attributeName, Set defaultValue)
      throws CoreException {
    return delegate.getAttribute(attributeName, defaultValue);
  }

  @SuppressWarnings("rawtypes")
  public Map getAttribute(String attributeName, Map defaultValue)
      throws CoreException {
    return delegate.getAttribute(attributeName, defaultValue);
  }

  public String getCategory() throws CoreException {
    return delegate.getCategory();
  }

  public IFile getFile() {
    return delegate.getFile();
  }

  @SuppressWarnings("deprecation")
  public IPath getLocation() {
    return delegate.getLocation();
  }

  public IResource[] getMappedResources() throws CoreException {
    return delegate.getMappedResources();
  }

  public String getMemento() throws CoreException {
    return delegate.getMemento();
  }

  public String getName() {
    return delegate.getName();
  }

  @SuppressWarnings("rawtypes")
  public Set getModes() throws CoreException {
    return delegate.getModes();
  }

  public ILaunchDelegate getPreferredDelegate(
      @SuppressWarnings("rawtypes") Set modes) throws CoreException {
    return delegate.getPreferredDelegate(modes);
  }

  public ILaunchConfigurationType getType() throws CoreException {
    return delegate.getType();
  }

  public boolean isLocal() {
    return delegate.isLocal();
  }

  public boolean isMigrationCandidate() throws CoreException {
    return delegate.isMigrationCandidate();
  }

  public ILaunch launch(String mode, IProgressMonitor monitor)
      throws CoreException {
    return delegate.launch(mode, monitor);
  }

  public ILaunch launch(String mode, IProgressMonitor monitor, boolean build)
      throws CoreException {
    return delegate.launch(mode, monitor, build);
  }

  public ILaunch launch(String mode, IProgressMonitor monitor, boolean build,
      boolean register) throws CoreException {
    return delegate.launch(mode, monitor, build, register);
  }

  public void migrate() throws CoreException {
    delegate.migrate();
  }

  public boolean supportsMode(String mode) throws CoreException {
    return delegate.supportsMode(mode);
  }

  public boolean isReadOnly() {
    return delegate.isReadOnly();
  }

}
