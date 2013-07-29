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
package org.kuali.rice.kim.impl.identity.external;

import org.eclipse.persistence.annotations.Convert;
import org.kuali.rice.kim.api.identity.CodedAttribute;
import org.kuali.rice.kim.api.identity.external.EntityExternalIdentifierType;
import org.kuali.rice.kim.api.identity.external.EntityExternalIdentifierTypeContract;
import org.kuali.rice.kim.framework.identity.external.EntityExternalIdentifierTypeEbo;
import org.kuali.rice.kim.impl.identity.CodedAttributeBo;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@AttributeOverrides({
        @AttributeOverride(name="code",column=@Column(name="EXT_ID_TYP_CD"))
})
@Table(name = "KRIM_EXT_ID_TYP_T")
public class EntityExternalIdentifierTypeBo extends CodedAttributeBo implements EntityExternalIdentifierTypeEbo, EntityExternalIdentifierTypeContract {
    private static final long serialVersionUID = 1058518958597912170L;
    @javax.persistence.Convert(converter=org.kuali.rice.krad.data.converters.BooleanYNConverter.class)
    @Column(name = "ENCR_REQ_IND")
    private boolean encryptionRequired;

    @Override
    public boolean isEncryptionRequired() {
        return encryptionRequired;
    }

    public void setEncryptionRequired(boolean encryptionRequired) {
        this.encryptionRequired = encryptionRequired;
    }

    public static EntityExternalIdentifierTypeBo from(EntityExternalIdentifierType immutable) {
        EntityExternalIdentifierTypeBo bo = CodedAttributeBo.from(EntityExternalIdentifierTypeBo.class, CodedAttribute.Builder.create(immutable).build());
        bo.setEncryptionRequired(immutable.isEncryptionRequired());
        return bo;
    }

    public static EntityExternalIdentifierType to(EntityExternalIdentifierTypeBo bo) {
        if (bo == null) {
            return null;
        }

        return EntityExternalIdentifierType.Builder.create(bo).build();
    }



}