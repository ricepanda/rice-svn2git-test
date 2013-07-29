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
package org.kuali.rice.krad.data.metadata;

import java.beans.PropertyEditor;

import org.kuali.rice.core.api.data.DataType;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.keyvalues.KeyValuesFinder;

/**
 * Represents an attribute (persistent or non-persistent) on a data object.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)

 * 
 */
public interface DataObjectAttribute extends MetadataCommon {

	/**
	 * Get the data object type to which this attribute belongs.
	 */
	Class<?> getOwningType();

	/**
	 * To be used on attributes which have an associated business key that is shown to users rather than the "internal"
	 * key which is likely a meaningless (to the users) sequence number.
	 */
	String getDisplayAttributeName(); // KNS: displayLabelAttribute

	/**
	 * The maximum length value which will be accepted into this field.
	 */
	Long getMaxLength();

	/**
	 * The minimum length value which will be accepted into this field.
	 */
	Long getMinLength();

	/**
	 * Whether this attribute should be treated as case insensitive when performing lookups and searches.
	 */
	boolean isCaseInsensitive(); // KNS : forceUppercase

	/**
	 * Whether (at the data level) this attribute must have a non-null value.
	 */
	boolean isRequired(); // not null at ORM level

	/**
	 * The bean name (in the UIF data dictionary), which checks the entered value's characters for correctness.
	 */
	String getValidCharactersConstraintBeanName();

	/**
	 * To be used by the persistence layer when loading and persisting the data.
	 * (E.g., strip extra characters from phone numbers to leave only the digits for storage in the database.)
	 */
	PropertyEditor getPropertyEditor();

	/**
	 * Specify any security attributes which should apply to this attribute. (hidden/read only/masked)
	 */
	DataObjectAttributeSecurity getAttributeSecurity();

	/**
	 * If this field should be rendered using a drop-down list, specify the instance on this property.
	 */
	KeyValuesFinder getOptionsFinder();

	/**
	 * The derived krad-data data type used by the UIF to help generate the appropriate control and perform default
	 * validation.
	 */
    DataType getDataType();

	/**
	 * Whether or not this attribute of the data object is saved to persistent storage when saved via the
	 * {@link DataObjectService}.
	 */
	boolean isPersisted();

	/**
	 * If this attribute is inherited from a different data object, that object's type. Otherwise null.
	 */
	Class<?> getInheritedFromType();

	/**
	 * If this attribute is inherited from a different data object, the source attribute name. Otherwise null.
	 */
	String getInheritedFromAttributeName();

	/**
	 * Whether this attribute is inherited from a different data object.
	 */
	boolean isInherited();

	/**
	 * Obtains the "original" data object attribute in a chain of embedded attribute definitions.
	 */
	DataObjectAttribute getOriginalDataObjectAttribute();
}