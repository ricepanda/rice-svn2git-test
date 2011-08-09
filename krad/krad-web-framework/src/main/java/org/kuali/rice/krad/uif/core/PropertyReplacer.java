/*
 * Copyright 2011 The Kuali Foundation
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
package org.kuali.rice.krad.uif.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.rice.krad.uif.util.ComponentUtils;

/**
 * Configuration for replacing a property value based on a condition
 *
 * <p>
 * A <code>Component</code> may be configured with one or more <code>PropertyReplacer</code> instances. Each defines
 * a condition to evaluate during the apply model phase, and if that condition succeeds the property on the component
 * given by {@link #getPropertyName()}, will be replaced with the value given by {@link #getReplacement()}. Conditions
 * are defined using an expression language and may reference any variables available in the component's context.
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class PropertyReplacer extends ConfigurableBase implements Serializable {
    private static final long serialVersionUID = -8405429643299461398L;

    private String propertyName;
    private String condition;
    private Object replacement;

    public PropertyReplacer() {
        super();
    }

    /**
     * Returns a list of nested components
     *
     * <p>
     * All nested components will be returned in the list. Current assumption is that
     * <code>PropertyReplacer</code> can only contain a <code>Component</code>, <code>List</code> or
     * <code>Map</code> for nested components
     * </p>
     *
     * @return List<Component> nested components
     */
    public List<Component> getNestedComponents() {
        ArrayList<Component> nestedComponents = new ArrayList<Component>();
        if (replacement instanceof Component) {
            nestedComponents.add(((Component) replacement));
        } else if (replacement instanceof List) {
            for (Object replacementItem : (List<?>) replacement) {
                if (replacementItem instanceof Component) {
                    nestedComponents.add((Component) replacementItem);
                }
            }
        } else if (replacement instanceof Map) {
            for (Object replacementItem : ((Map<?, ?>) replacement).values()) {
                if (replacementItem instanceof Component) {
                    nestedComponents.add((Component) replacementItem);
                }
            }
        }

        return nestedComponents;
    }

    public String getPropertyName() {
        return this.propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getCondition() {
        return this.condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public Object getReplacement() {
        return this.replacement;
    }

    public void setReplacement(Object replacement) {
        this.replacement = replacement;
    }

}
