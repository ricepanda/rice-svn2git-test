/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package mocks;

import org.kuali.rice.kew.actionitem.ActionItem;
import org.kuali.rice.kew.exception.EdenUserNotFoundException;
import org.kuali.rice.kew.mail.ActionListEmailService;

import edu.iu.uis.eden.user.WorkflowUser;

/**
 * Spring proxies cannot be cast to implementation classes...
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface MockEmailNotificationService extends ActionListEmailService {
	public void sendImmediateReminder(WorkflowUser user, ActionItem actionItem);
    public void sendDailyReminder();
    public void sendWeeklyReminder();

    public int immediateReminderEmailsSent(String networkId, Long documentId, String actionRequestCd) throws EdenUserNotFoundException;
    public Integer getTotalPeriodicRemindersSent(String emailReminderConstant);
    public Integer getTotalPeriodicRemindersSent();
    /**
     * Resets the reminder counts
     */
    public void resetReminderCounts();

    public boolean wasStyleServiceAccessed();
}
