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


import at.researchstudio.sdis.ah.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class AuthorizeSubscribeService {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    private ElementManagementRegistration elementManagementRegistration;

    private IoTManagementRegistration ioTManagementRegistration;

    private AHClientImpl ahClient;

    private DtoModelHelper modelHelper;
    private Map<String, AuthorizationIntraCloudResponseDTO> authorizationIntraCloudResponseDTOMap = new HashMap<>();

    public void authorizationSetup() throws InterruptedException {
        getAllAuthorizations();
        if (authorizationIntraCloudResponseDTOMap.isEmpty()) {
            createElementManagementAuthorization();
            createIoTAuthorization();
        } else {
            logger.info(String.format("authorizationIntraCloudResponseDTOMap has %s! elements", authorizationIntraCloudResponseDTOMap.size()));
        }
        createSubscriptions();
    }

    public void createSubscriptions() {

        if (authorizationIntraCloudResponseDTOMap.isEmpty()) {
            logger.info("Services are not authorized - no subscription request to be sent");
            return;
        }
        for (AuthorizationIntraCloudResponseDTO dto : authorizationIntraCloudResponseDTOMap.values()) {
            String eventType = dto.getServiceDefinition().getServiceDefinition();
            String notifyUrl = "/" + dto.getConsumerSystem().getSystemName() + "/" + eventType;
            SystemRequestDTO subscriber = modelHelper.createSystemRequestDTO(
                    dto.getConsumerSystem().getAddress(),
                    dto.getConsumerSystem().getSystemName(),
                    dto.getConsumerSystem().getPort());
            SystemRequestDTO source = modelHelper.createSystemRequestDTO(
                    dto.getProviderSystem().getAddress(),
                    dto.getProviderSystem().getSystemName(),
                    dto.getProviderSystem().getPort());
           modelHelper.createSubscriptionRequestDTO(eventType, notifyUrl, subscriber, source);
        }
    }

    protected void getAllAuthorizations() throws InterruptedException {
        AuthorizationIntraCloudListResponseDTO intraCloudListResponseDTO = null;
        try {
            intraCloudListResponseDTO = ahClient.getAllAuthorizations();
            List<AuthorizationIntraCloudResponseDTO> intraCloudDtos = intraCloudListResponseDTO.getData();
            long elementManagementRegistrationId = elementManagementRegistration.getSystemId();
            long ioTManagementRegistrationId = ioTManagementRegistration.getSystemId();
            for (AuthorizationIntraCloudResponseDTO dto : intraCloudDtos) {
                if (dto.getConsumerSystem().getId() == elementManagementRegistrationId || dto.getConsumerSystem().getId() == ioTManagementRegistrationId) {
                    authorizationIntraCloudResponseDTOMap.put(dto.getServiceDefinition().getServiceDefinition(), dto);
                }
            }
        } catch (ExecutionException | InterruptedException e) {
            throw new InterruptedException();
        }
    }

    protected void createElementManagementAuthorization() throws InterruptedException {
        long consumerId = elementManagementRegistration.getSystemId();
        for (ServiceRegistryResponseDTO provider : ioTManagementRegistration.getAllRegisteredServices()) {
            AuthorizationIntraCloudRequestDTO requestDTO = modelHelper.createAuthorizationIntraCloudRequestDTO(consumerId, provider);
            createAuthorization(requestDTO);
        }
    }

    protected void createIoTAuthorization() throws InterruptedException {
        long consumerId = ioTManagementRegistration.getSystemId();
        for (ServiceRegistryResponseDTO provider : elementManagementRegistration.getAllRegisteredServices()) {
            AuthorizationIntraCloudRequestDTO requestDTO = modelHelper.createAuthorizationIntraCloudRequestDTO(consumerId, provider);
            createAuthorization(requestDTO);
        }
    }

    protected void createAuthorization(AuthorizationIntraCloudRequestDTO authorizationIntraCloudRequestDTO) throws InterruptedException {
        AuthorizationResponseDTO authorizationResponseDTO = null;
        try {
            authorizationResponseDTO = ahClient.requestAuthorization(authorizationIntraCloudRequestDTO);
            authorizationIntraCloudResponseDTOMap.put(
                    authorizationResponseDTO.getData().get(0).getServiceDefinition().getServiceDefinition(),
                    authorizationResponseDTO.getData().get(0)
            );
        } catch (ExecutionException | InterruptedException e) {
            throw new InterruptedException();
        }

    }
}
