/*
 * Copyright 2011 The Kuali Foundation Licensed under the Educational Community
 * License, Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.opensource.org/licenses/ecl1.php Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.kuali.rice.kns.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.bo.BusinessObjectRelationship;
import org.kuali.rice.kns.datadictionary.DataDictionaryEntry;
import org.kuali.rice.kns.datadictionary.PrimitiveAttributeDefinition;
import org.kuali.rice.kns.datadictionary.RelationshipDefinition;
import org.kuali.rice.kns.datadictionary.SupportAttributeDefinition;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DataObjectMetaDataService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.KualiModuleService;
import org.kuali.rice.kns.service.ModuleService;
import org.kuali.rice.kns.service.PersistenceStructureService;
import org.kuali.rice.kns.uif.service.ViewDictionaryService;
import org.kuali.rice.kns.uif.util.ObjectPropertyUtils;
import org.kuali.rice.kns.util.ObjectUtils;
import org.springframework.beans.BeanWrapper;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DataObjectMetaDataServiceImpl implements DataObjectMetaDataService {

    private DataDictionaryService dataDictionaryService;
    private KualiModuleService kualiModuleService;
    private PersistenceStructureService persistenceStructureService;
    private ViewDictionaryService viewDictionaryService;

    /**
     * @see org.kuali.rice.kns.service.DataObjectMetaDataService#listPrimaryKeyFieldNames(java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<String> listPrimaryKeyFieldNames(Class<?> clazz) {
        if (persistenceStructureService.isPersistable(clazz)) {
            return persistenceStructureService.listPrimaryKeyFieldNames(clazz);
        }

        ModuleService responsibleModuleService = getKualiModuleService().getResponsibleModuleService(clazz);
        if (responsibleModuleService != null && responsibleModuleService.isExternalizable(clazz))
            return responsibleModuleService.listPrimaryKeyFieldNames(clazz);

        // check the Data Dictionary for PK's of non PBO/EBO
        List<String> pks = dataDictionaryService.getDataDictionary().getDataObjectEntry(clazz.getName())
                .getPrimaryKeys();
        if (pks != null && !pks.isEmpty())
            return pks;

        return new ArrayList<String>();
    }

    /**
     * @see org.kuali.rice.kns.service.DataObjectMetaDataService#getPrimaryKeyFieldValues(java.lang.Object)
     */
    @Override
    public Map<String, ?> getPrimaryKeyFieldValues(Object dataObject) {
        return getPrimaryKeyFieldValues(dataObject, false);
    }

    /**
     * @see org.kuali.rice.kns.service.DataObjectMetaDataService#getPrimaryKeyFieldValues(java.lang.Object,
     *      boolean)
     */
    @Override
    public Map<String, ?> getPrimaryKeyFieldValues(Object dataObject, boolean sortFieldNames) {
        Map<String, Object> keyValueMap;

        if (sortFieldNames) {
            keyValueMap = new TreeMap<String, Object>();
        } else {
            keyValueMap = new HashMap<String, Object>();
        }

        BeanWrapper wrapper = ObjectPropertyUtils.wrapObject(dataObject);

        List<String> fields = listPrimaryKeyFieldNames(dataObject.getClass());
        for (String fieldName : fields) {
            keyValueMap.put(fieldName, wrapper.getPropertyValue(fieldName));
        }

        return keyValueMap;
    }

    /**
     * @see org.kuali.rice.kns.service.DataObjectMetaDataService#equalsByPrimaryKeys(java.lang.Object,
     *      java.lang.Object)
     */
    @Override
    public boolean equalsByPrimaryKeys(Object do1, Object do2) {
        boolean equal = true;

        if (do1 == null && do2 == null) {
            equal = true;
        } else if (do1 == null || do2 == null) {
            equal = false;
        } else if (!do1.getClass().getName().equals(do2.getClass().getName())) {
            equal = false;
        } else {
            Map<String, ?> do1Keys = getPrimaryKeyFieldValues(do1);
            Map<String, ?> do2Keys = getPrimaryKeyFieldValues(do2);
            for (Iterator<String> iter = do1Keys.keySet().iterator(); iter.hasNext();) {
                String keyName = iter.next();
                if (do1Keys.get(keyName) != null && do2Keys.get(keyName) != null) {
                    if (!do1Keys.get(keyName).toString().equals(do2Keys.get(keyName).toString())) {
                        equal = false;
                    }
                } else {
                    equal = false;
                }
            }
        }

        return equal;
    }

    /**
     * @see org.kuali.rice.kns.service.BusinessObjectMetaDataService#getDataObjectRelationship(java.lang.Object,
     *      java.lang.Class, java.lang.String, java.lang.String, boolean,
     *      boolean, boolean)
     */
    public BusinessObjectRelationship getDataObjectRelationship(Object dataObject, Class<?> dataObjectClass,
            String attributeName, String attributePrefix, boolean keysOnly, boolean supportsLookup,
            boolean supportsInquiry) {
        RelationshipDefinition ddReference = getDictionaryRelationship(dataObjectClass, attributeName);

        return getDataObjectRelationship(ddReference, dataObject, dataObjectClass, attributeName, attributePrefix,
                keysOnly, supportsLookup, supportsInquiry);
    }

    protected BusinessObjectRelationship getDataObjectRelationship(RelationshipDefinition ddReference,
            Object dataObject, Class<?> dataObjectClass, String attributeName, String attributePrefix,
            boolean keysOnly, boolean supportsLookup, boolean supportsInquiry) {
        BusinessObjectRelationship relationship = null;

        // if it is nested then replace the bo and attributeName with the
        // sub-refs
        if (ObjectUtils.isNestedAttribute(attributeName)) {
            if (ddReference != null) {
                if ((supportsLookup && getViewDictionaryService().isLookupable(ddReference.getTargetClass()))
                        || (supportsInquiry && getViewDictionaryService().isInquirable(ddReference.getTargetClass()))) {
                    relationship = populateRelationshipFromDictionaryReference(dataObjectClass, ddReference,
                            attributePrefix, keysOnly);

                    return relationship;
                }
            }

            // recurse down to the next object to find the relationship
            String localPrefix = StringUtils.substringBefore(attributeName, ".");
            String localAttributeName = StringUtils.substringAfter(attributeName, ".");
            if (dataObject == null) {
                dataObject = ObjectUtils.createNewObjectFromClass(dataObjectClass);
            }

            Object nestedObject = ObjectPropertyUtils.getPropertyValue(dataObject, localPrefix);
            Class<?> nestedClass = null;
            if (nestedObject == null) {
                nestedClass = ObjectPropertyUtils.getPropertyType(dataObject, localPrefix);
            }
            else {
                nestedClass = nestedObject.getClass();
            }
            
            String fullPrefix = localPrefix;
            if (StringUtils.isNotBlank(attributePrefix)) {
                fullPrefix = attributePrefix + "." + localPrefix;
            }

            relationship = getDataObjectRelationship(nestedObject, nestedClass, localAttributeName, fullPrefix, keysOnly,
                    supportsLookup, supportsInquiry);

            return relationship;
        }

        // non-nested reference, get peristence relationships first
        int maxSize = Integer.MAX_VALUE;

        // try persistable reference first
        if (getPersistenceStructureService().isPersistable(dataObjectClass)) {
            Map<String, BusinessObjectRelationship> rels = getPersistenceStructureService().getRelationshipMetadata(
                    dataObjectClass, attributeName, attributePrefix);
            if (rels.size() > 0) {
                for (BusinessObjectRelationship rel : rels.values()) {
                    if (rel.getParentToChildReferences().size() < maxSize) {
                        if ((supportsLookup && getViewDictionaryService().isLookupable(rel.getRelatedClass()))
                                || (supportsInquiry && getViewDictionaryService().isInquirable(rel.getRelatedClass()))) {
                            maxSize = rel.getParentToChildReferences().size();
                            relationship = rel;
                        }
                    }
                }
            }
        } else {
            ModuleService moduleService = getKualiModuleService().getResponsibleModuleService(dataObjectClass);
            if (moduleService != null && moduleService.isExternalizable(dataObjectClass)) {
                relationship = getRelationshipMetadata(dataObjectClass, attributeName, attributePrefix);
                if (relationship != null) {
                    return relationship;
                }
            }
        }

        if (ddReference != null && ddReference.getPrimitiveAttributes().size() < maxSize) {
            if ((supportsLookup && getViewDictionaryService().isLookupable(ddReference.getTargetClass()))
                    || (supportsInquiry && getViewDictionaryService().isInquirable(ddReference.getTargetClass()))) {
                relationship = populateRelationshipFromDictionaryReference(dataObjectClass, ddReference, null, keysOnly);
            }
        }

        return relationship;
    }

    protected RelationshipDefinition getDictionaryRelationship(Class<?> c, String attributeName) {
        DataDictionaryEntry entryBase = getDataDictionaryService().getDataDictionary().getDictionaryObjectEntry(
                c.getName());
        if (entryBase == null) {
            return null;
        }

        RelationshipDefinition relationship = null;

        List<RelationshipDefinition> ddRelationships = entryBase.getRelationships();

        int minKeys = Integer.MAX_VALUE;
        for (RelationshipDefinition def : ddRelationships) {
            // favor key sizes of 1 first
            if (def.getPrimitiveAttributes().size() == 1) {
                for (PrimitiveAttributeDefinition primitive : def.getPrimitiveAttributes()) {
                    if (primitive.getSourceName().equals(attributeName)
                            || def.getObjectAttributeName().equals(attributeName)) {
                        relationship = def;
                        minKeys = 1;
                        break;
                    }
                }
            } else if (def.getPrimitiveAttributes().size() < minKeys) {
                for (PrimitiveAttributeDefinition primitive : def.getPrimitiveAttributes()) {
                    if (primitive.getSourceName().equals(attributeName)
                            || def.getObjectAttributeName().equals(attributeName)) {
                        relationship = def;
                        minKeys = def.getPrimitiveAttributes().size();
                        break;
                    }
                }
            }
        }
        // check the support attributes
        if (relationship == null) {
            for (RelationshipDefinition def : ddRelationships) {
                if (def.hasIdentifier()) {
                    if (def.getIdentifier().getSourceName().equals(attributeName)) {
                        relationship = def;
                    }
                }
            }
        }

        return relationship;
    }

    protected BusinessObjectRelationship populateRelationshipFromDictionaryReference(Class<?> dataObjectClass,
            RelationshipDefinition ddReference, String attributePrefix, boolean keysOnly) {
        BusinessObjectRelationship relationship = new BusinessObjectRelationship(dataObjectClass,
                ddReference.getObjectAttributeName(), ddReference.getTargetClass());

        for (PrimitiveAttributeDefinition def : ddReference.getPrimitiveAttributes()) {
            if (StringUtils.isNotBlank(attributePrefix)) {
                relationship.getParentToChildReferences().put(attributePrefix + "." + def.getSourceName(),
                        def.getTargetName());
            } else {
                relationship.getParentToChildReferences().put(def.getSourceName(), def.getTargetName());
            }
        }

        if (!keysOnly) {
            for (SupportAttributeDefinition def : ddReference.getSupportAttributes()) {
                if (StringUtils.isNotBlank(attributePrefix)) {
                    relationship.getParentToChildReferences().put(attributePrefix + "." + def.getSourceName(),
                            def.getTargetName());
                    if (def.isIdentifier()) {
                        relationship.setUserVisibleIdentifierKey(attributePrefix + "." + def.getSourceName());
                    }
                } else {
                    relationship.getParentToChildReferences().put(def.getSourceName(), def.getTargetName());
                    if (def.isIdentifier()) {
                        relationship.setUserVisibleIdentifierKey(def.getSourceName());
                    }
                }
            }
        }

        return relationship;
    }

    protected BusinessObjectRelationship getRelationshipMetadata(Class<?> dataObjectClass, String attributeName,
            String attributePrefix) {

        RelationshipDefinition relationshipDefinition = getDictionaryRelationship(dataObjectClass, attributeName);
        if (relationshipDefinition == null) {
            return null;
        }

        BusinessObjectRelationship businessObjectRelationship = new BusinessObjectRelationship(
                relationshipDefinition.getSourceClass(), relationshipDefinition.getObjectAttributeName(),
                relationshipDefinition.getTargetClass());

        if (!StringUtils.isEmpty(attributePrefix)) {
            attributePrefix += ".";
        }

        List<PrimitiveAttributeDefinition> primitives = relationshipDefinition.getPrimitiveAttributes();
        for (PrimitiveAttributeDefinition primitiveAttributeDefinition : primitives) {
            businessObjectRelationship.getParentToChildReferences().put(
                    attributePrefix + primitiveAttributeDefinition.getSourceName(),
                    primitiveAttributeDefinition.getTargetName());
        }

        return businessObjectRelationship;
    }

    /**
     * Protected method to allow subclasses to access the dataDictionaryService.
     * 
     * @return Returns the dataDictionaryService.
     */
    protected DataDictionaryService getDataDictionaryService() {
        return this.dataDictionaryService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    /**
     * Protected method to allow subclasses to access the kualiModuleService.
     * 
     * @return Returns the persistenceStructureService.
     */
    protected KualiModuleService getKualiModuleService() {
        return this.kualiModuleService;
    }

    public void setKualiModuleService(KualiModuleService kualiModuleService) {
        this.kualiModuleService = kualiModuleService;
    }

    /**
     * Protected method to allow subclasses to access the
     * persistenceStructureService.
     * 
     * @return Returns the persistenceStructureService.
     */
    protected PersistenceStructureService getPersistenceStructureService() {
        return this.persistenceStructureService;
    }

    public void setPersistenceStructureService(PersistenceStructureService persistenceStructureService) {
        this.persistenceStructureService = persistenceStructureService;
    }

    protected ViewDictionaryService getViewDictionaryService() {
        if (this.viewDictionaryService == null) {
            this.viewDictionaryService = KNSServiceLocator.getViewDictionaryService();
        }
        return this.viewDictionaryService;
    }

    public void setViewDictionaryService(ViewDictionaryService viewDictionaryService) {
        this.viewDictionaryService = viewDictionaryService;
    }

}
