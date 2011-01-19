/*
 * Copyright 2005-2008 The Kuali Foundation
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
package org.kuali.rice.kns.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.dao.LookupDao;
import org.kuali.rice.kns.service.*;
import org.kuali.rice.core.service.KualiConfigurationService;
import org.kuali.rice.kns.util.KNSConstants;

/**
 * This class is the service implementation for the Lookup structure. It Provides a generic search mechanism against Business
 * Objects. This is the default implementation, that is delivered with Kuali.
 */
public class LookupServiceImpl implements LookupService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LookupServiceImpl.class);
    private static final Collection EMPTY_COLLECTION = new ArrayList(0);

    private LookupDao lookupDao;
    private KualiConfigurationService kualiConfigurationService;
    private DataDictionaryService dataDictionaryService;
    private PersistenceStructureService persistenceStructureService;
    
    public Collection findCollectionBySearchUnbounded(Class example, Map formProps) {
        return findCollectionBySearchHelper(example, formProps, true);
    }

    /**
     * Returns a collection of objects based on the given search parameters.
     * 
     * @return Collection returned from the search
     */
    public Collection findCollectionBySearch(Class example, Map formProps) {
        return findCollectionBySearchHelper(example, formProps, false);
    }

    public Collection findCollectionBySearchHelper(Class example, Map formProps, boolean unbounded) {
        return lookupDao.findCollectionBySearchHelper(example, formProps, unbounded, allPrimaryKeyValuesPresentAndNotWildcard(example, formProps));
    }

    /**
     * Retrieves a Object based on the search criteria, which should uniquely identify a record.
     * 
     * @return Object returned from the search
     */
    public Object findObjectBySearch(Class example, Map formProps) {
        if (example == null || formProps == null) {
            throw new IllegalArgumentException("Object and Map must not be null");
        }

        PersistableBusinessObject obj = null;
        try {
            obj = (PersistableBusinessObject) example.newInstance();
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException("Cannot get new instance of " + example.getName(), e);
        }
        catch (InstantiationException e) {
            throw new RuntimeException("Cannot instantiate " + example.getName(), e);
        }

        return lookupDao.findObjectByMap(obj, formProps);
    }
    
    public boolean allPrimaryKeyValuesPresentAndNotWildcard(Class boClass, Map formProps) {
        List pkFields = KNSServiceLocatorWeb.getBusinessObjectMetaDataService().listPrimaryKeyFieldNames(boClass);
        Iterator pkIter = pkFields.iterator();
        boolean returnVal = true;
        while (returnVal && pkIter.hasNext()) {
            String pkName = (String) pkIter.next();
            String pkValue = (String) formProps.get(pkName);
            
            if (StringUtils.isBlank(pkValue)) {
                returnVal = false;
            }
            else if (StringUtils.indexOfAny(pkValue, KNSConstants.QUERY_CHARACTERS) != -1) {
                returnVal = false;
            }
        }
        return returnVal;
    }

    /**
     * @return Returns the lookupDao.
     */
    public LookupDao getLookupDao() {
        return lookupDao;
    }

    /**
     * @param lookupDao The lookupDao to set.
     */
    public void setLookupDao(LookupDao lookupDao) {
        this.lookupDao = lookupDao;
    }

    public KualiConfigurationService getKualiConfigurationService() {
        return kualiConfigurationService;
    }

    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    public void setPersistenceStructureService(PersistenceStructureService persistenceStructureService) {
        this.persistenceStructureService = persistenceStructureService;
    }
}
