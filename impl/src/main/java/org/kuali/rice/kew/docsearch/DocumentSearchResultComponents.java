/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
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
package org.kuali.rice.kew.docsearch;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.kew.lookupable.Column;


/**
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class DocumentSearchResultComponents {

	protected List<Column> columns;
	protected List<DocumentSearchResult> searchResults = new ArrayList<DocumentSearchResult>(); // list of DocumentSearchResult objects

	public DocumentSearchResultComponents(List<Column> columns, List<DocumentSearchResult> searchResults) {
		super();
		this.columns = columns;
		this.searchResults = searchResults;
	}

	/**
	 * @return the columns
	 */
	public List<Column> getColumns() {
		return columns;
	}

	/**
	 * @return the searchResults
	 */
	public List<DocumentSearchResult> getSearchResults() {
		return searchResults;
	}
}

