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

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DtoModelHelper {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    protected static final Integer SERVICE_VERSION = 1;
    protected String arrowheadInterface;
    protected String arrowheadSecure;

    public DtoModelHelper(String arrowheadInterface, String arrowheadSecure) {
        this.arrowheadInterface = arrowheadInterface;
        this.arrowheadSecure = arrowheadSecure;
    }

    public SubscriptionRequestDTO createSubscriptionRequestDTO(String eventType, String notifyUrl, SystemRequestDTO subscriber, SystemRequestDTO source) {
        SubscriptionRequestDTO subscriptionRequestDTO = new SubscriptionRequestDTO();
        subscriptionRequestDTO.setEventType(eventType);
        subscriptionRequestDTO.setMatchMetaData(false);
        subscriptionRequestDTO.setNotifyUri(notifyUrl);
        subscriptionRequestDTO.setSubscriberSystem(subscriber);
        Set<SystemRequestDTO> sources = new HashSet<>();
        sources.add(source);
        subscriptionRequestDTO.setSources(sources);
        return subscriptionRequestDTO;
    }

    public SystemRequestDTO createSystemRequestDTO(String serverAddress, String systemName, int serverPort) {
        SystemRequestDTO systemRequestDTO = new SystemRequestDTO();
        systemRequestDTO.setAddress(serverAddress);
        systemRequestDTO.setSystemName(systemName);
        systemRequestDTO.setPort(serverPort);
        return systemRequestDTO;
    }

    public EventPublishRequestDTO createEventPublishRequestDTO(String payLoad, SystemRequestDTO source, String eventType) {
        EventPublishRequestDTO eventPublishRequestDTO = new EventPublishRequestDTO();
        eventPublishRequestDTO.setEventType(eventType);
        eventPublishRequestDTO.setPayload(payLoad);
        eventPublishRequestDTO.setSource(source);
        Instant instant = Instant.ofEpochMilli(System.currentTimeMillis());
        eventPublishRequestDTO.setTimeStamp(instant.toString());
        return eventPublishRequestDTO;
    }

    public AuthorizationIntraCloudRequestDTO createAuthorizationIntraCloudRequestDTO(Long consumerId, ServiceRegistryResponseDTO provider) {
        AuthorizationIntraCloudRequestDTO authorizationIntraCloudRequestDTO = new AuthorizationIntraCloudRequestDTO();
        authorizationIntraCloudRequestDTO.setConsumerId(consumerId);
        //following values comes from the provider service object
        ArrayList<Long> interfaceId = new ArrayList<>();
        interfaceId.add(provider.getInterfaces().get(0).getId());
        authorizationIntraCloudRequestDTO.setInterfaceIds(interfaceId);
        ArrayList<Long> providerIds = new ArrayList<>();
        providerIds.add(provider.getProvider().getId());
        authorizationIntraCloudRequestDTO.setProviderIds(providerIds);
        ArrayList<Long> serviceDefinitionIds = new ArrayList<>();
        serviceDefinitionIds.add(provider.getServiceDefinition().getId());
        authorizationIntraCloudRequestDTO.setServiceDefinitionIds(serviceDefinitionIds);
        return authorizationIntraCloudRequestDTO;
    }

    public ServiceRegistryRequestDTO createServiceRegistryRequestDTO(SystemRequestDTO systemRequestDTO, String serviceDefinition, String serviceUri) {
        ServiceRegistryRequestDTO serviceRegistryRequestDTO = new ServiceRegistryRequestDTO();
        serviceRegistryRequestDTO.setServiceDefinition(serviceDefinition);
        serviceRegistryRequestDTO.setServiceUri(serviceUri);
        List<String> ifList = new ArrayList<>();
        ifList.add(arrowheadInterface);
        serviceRegistryRequestDTO.setInterfaces(ifList);
        serviceRegistryRequestDTO.setSecure(arrowheadSecure);
        serviceRegistryRequestDTO.setVersion(SERVICE_VERSION);
        serviceRegistryRequestDTO.setProviderSystem(systemRequestDTO);
        return serviceRegistryRequestDTO;
    }
}
