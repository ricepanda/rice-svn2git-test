/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kim.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.namespace.QName;

import org.kuali.rice.lifecycle.Lifecycle;
import org.kuali.rice.resourceloader.ResourceLoader;
import org.kuali.rice.resourceloader.SpringResourceLoader;
import org.kuali.rice.test.RiceTestCase;

/**
 * This is test base that should be used for all KIM unit tests.  All non-web unit 
 * tests for KIM should extend this base class.
 * 
 * @author Ryan Kirkendall
 */
public class KIMTestCase extends RiceTestCase {
    
    	private static final String KIM_TEST_CONTEXT_LOC = "classpath:KimTestHarnessSpring.xml";
    	private ResourceLoader springContextResourceLoader;
    
	@Override
	public List<Lifecycle> getPerTestLifecycles() {
		return new ArrayList<Lifecycle>();
	}

	@Override
	protected List<Lifecycle> getSuiteLifecycles() {
		List<Lifecycle> lifeCycles = super.getPerTestLifecycles();
		this.springContextResourceLoader = new SpringResourceLoader(new QName("kimtestharness"), KIM_TEST_CONTEXT_LOC);
		lifeCycles.add(this.springContextResourceLoader);
		return lifeCycles;
	}


    @Override
    protected List<String> getConfigLocations() {
	return Arrays.asList(new String[]{"classpath:META-INF/kim-test-config.xml"});
    }

    @Override
    protected String getDerbySQLFileLocation() {
	return null;
    }

    @Override
    protected String getModuleName() {
	return "kim";
    }
}