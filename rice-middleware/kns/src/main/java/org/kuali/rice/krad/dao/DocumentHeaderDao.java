/**
 * Copyright 2005-2013 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.krad.dao;

import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.util.LegacyDataFramework;

/**
 * This is the data access interface for DocumentHeader objects.
 * @deprecated use new KRAD Data framework {@link org.kuali.rice.krad.data.DataObjectService}
 */
@Deprecated
@LegacyDataFramework
public interface DocumentHeaderDao {

    /**
     * @return the class to be used when creating new instances of DocumentHeader objects
     */
    public Class getDocumentHeaderBaseClass();

    /**
     * @param id
     * @return documentHeader realted to the give document header id
     */
    public DocumentHeader getByDocumentHeaderId(String id);

}