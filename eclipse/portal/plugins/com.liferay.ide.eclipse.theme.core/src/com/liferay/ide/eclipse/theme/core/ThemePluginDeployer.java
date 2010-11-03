/*******************************************************************************
 * Copyright (c) 2000-2010 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 *******************************************************************************/
package com.liferay.ide.eclipse.theme.core;

import com.liferay.ide.eclipse.project.core.util.ProjectUtil;
import com.liferay.ide.eclipse.server.core.AbstractPluginDeployer;
import com.liferay.ide.eclipse.server.core.IPortalConstants;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.model.ServerBehaviourDelegate;

/**
 * @author Greg Amerson
 */
public class ThemePluginDeployer extends AbstractPluginDeployer {

	public ThemePluginDeployer() {
	}

	@Override
	public boolean prePublishModule(int kind, int deltaKind, IModule[] moduleTree, IProgressMonitor monitor) {
		boolean publish = true;

		if ((kind != IServer.PUBLISH_FULL && kind != IServer.PUBLISH_INCREMENTAL && kind != IServer.PUBLISH_AUTO) ||
			moduleTree == null) {
			return publish;
		}

		if (deltaKind != ServerBehaviourDelegate.REMOVED) {
			try {
				addThemeModule(moduleTree[0]);
			}
			catch (Exception e) {
				ThemeCore.logError("Unable to pre-publish module.", e);
			}
		}

		return publish;
	}

	protected void addThemeModule(IModule module)
		throws CoreException {

		IProject project = module.getProject();

		// check to make sure they have a look-and-feel.xml file
		IFolder docroot = ProjectUtil.getDocroot(project);

		if (docroot != null && docroot.exists()) {
			if (!(docroot.exists(new Path("WEB-INF/" + IPortalConstants.LIFERAY_LOOK_AND_FEEL_XML_FILE))) ||
				!(docroot.exists(new Path("css")))) {

				ThemeCSSBuilder.cssBuild(project);
			}
		}
	}
}
