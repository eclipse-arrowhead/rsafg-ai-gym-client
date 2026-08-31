/********************************************************************************
 * Copyright (c) 2026 RSA FG
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   RSA FG - SDIS - implementation
 ********************************************************************************/

package at.researchstudio.sdis.ah.aigym;

import at.researchstudio.sdis.ah.model.EventPublishRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

public class EventPublishService {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    private AHClientImpl ahClient = new AHClientImpl("localhost", 2020, 1212, 3030);

    public void publish(EventPublishRequestDTO publishRequestDTO) throws InterruptedException {
        try {
            ahClient.publishEvent(publishRequestDTO);
        } catch (ExecutionException | InterruptedException e) {
            throw new InterruptedException();
        }
    }

}
