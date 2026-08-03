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


import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class IoTManagementRegistration extends ElementManagementRegistration {

    private String temperatureServiceDefinition;

    private String temperatureServiceUri;

    private String humidityServiceDefinition;

    private String humidityServiceUri;

    private String lightServiceDefinition;

    private String lightServiceUri;

    @Override
    public void registerAllServices() throws InterruptedException {
        try {
            registerManagementService(temperatureServiceDefinition, temperatureServiceUri, systemRequestDTO);
            registerManagementService(humidityServiceDefinition, humidityServiceUri, systemRequestDTO);
            registerManagementService(lightServiceDefinition, lightServiceUri, systemRequestDTO);
        } catch (ExecutionException | InterruptedException e) {
            throw new InterruptedException();
        }

    }

    @Override
    protected void initManagementSystemServiceList() {
        if (managementSystemServices == null) {
            managementSystemServices = new ArrayList<>();
            managementSystemServices.add(temperatureServiceDefinition);
            managementSystemServices.add(humidityServiceDefinition);
            managementSystemServices.add(lightServiceDefinition);
        }
    }
}
