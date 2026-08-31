/********************************************************************************
 * Copyright (c) 2026 RSA FG
  * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
  * SPDX-License-Identifier: EPL-2.0
  * Contributors:
 *   RSA FG - SDIS - implementation
 ********************************************************************************/

package at.researchstudio.sdis.ah.aigym;

import at.researchstudio.sdis.ah.model.*;

import java.util.concurrent.ExecutionException;

public class AHClientImpl {
     private String arrowHeadServerAddress;
    private int arrowHeadSRServerPort;
    private int arrowHeadAUServerPort;
    private int arrowHeadEHServerPort;
    private static final String SR_ENDPOINT = "/serviceregistry/register";
    private static final String AU_ENDPOINT = "/authorization/mgmt/intracloud";
    private static final String EH_SUBSCRIPTION_ENDPOINT = "/eventhandler/subscribe";
    private static final String EH_PUBLISH_ENDPOINT = "/eventhandler/publish";
    private static final String SR_GET_ALL = "/serviceregistry/mgmt";
     public AHClientImpl(String arrowHeadServerAddress, int arrowHeadSRServerPort, int arrowHeadAUServerPort, int arrowHeadEHServerPort) {
        this.arrowHeadServerAddress = arrowHeadServerAddress;
        this.arrowHeadSRServerPort = arrowHeadSRServerPort;
        this.arrowHeadAUServerPort = arrowHeadAUServerPort;
        this.arrowHeadEHServerPort = arrowHeadEHServerPort;
    }

    public ServiceRegistryResponseDTO registerService(ServiceRegistryRequestDTO serviceRegistryRequestDTO) throws ExecutionException, InterruptedException {
        AHClient<ServiceRegistryRequestDTO, ServiceRegistryResponseDTO> ahClient
                = new AHClient<ServiceRegistryRequestDTO, ServiceRegistryResponseDTO>(arrowHeadServerAddress, arrowHeadSRServerPort, SR_ENDPOINT) {

        };
        return  ahClient.postForResponse(serviceRegistryRequestDTO);
    }

    public ServiceRegistryListResponseDTO getAllServices() throws ExecutionException, InterruptedException {
        AHClient<String, ServiceRegistryListResponseDTO> ahClient = new AHClient<String, ServiceRegistryListResponseDTO>(arrowHeadServerAddress, arrowHeadSRServerPort, SR_GET_ALL) {
        };
        return ahClient.getForResponse(null, null);
    }

    public AuthorizationResponseDTO requestAuthorization(AuthorizationIntraCloudRequestDTO authorizationIntraCloudRequestDTO) throws ExecutionException, InterruptedException {
        AHClient<AuthorizationIntraCloudRequestDTO, AuthorizationResponseDTO> ahClient
                = new AHClient<AuthorizationIntraCloudRequestDTO, AuthorizationResponseDTO>(arrowHeadServerAddress, arrowHeadAUServerPort, AU_ENDPOINT) {
        };
              return ahClient.postForResponse(authorizationIntraCloudRequestDTO);
    }

    public AuthorizationIntraCloudListResponseDTO getAllAuthorizations() throws ExecutionException, InterruptedException {
        AHClient<String, AuthorizationIntraCloudListResponseDTO> ahClient =
                new AHClient<String, AuthorizationIntraCloudListResponseDTO>(arrowHeadServerAddress, arrowHeadAUServerPort, AU_ENDPOINT) {
                };
        return ahClient.getForResponse(null, null);
    }

    public String subscribeEventType(SubscriptionRequestDTO subscriptionRequestDTO) throws ExecutionException, InterruptedException {
        AHClient<SubscriptionRequestDTO, String> ahClient
                = new AHClient<SubscriptionRequestDTO, String>(arrowHeadServerAddress, arrowHeadEHServerPort, EH_SUBSCRIPTION_ENDPOINT) {
        };
        return ahClient.postForResponse(subscriptionRequestDTO);
    }

    public String publishEvent(EventPublishRequestDTO eventPublishRequestDTO) throws ExecutionException, InterruptedException {
        AHClient<EventPublishRequestDTO, String> ahClient
                = new AHClient<EventPublishRequestDTO, String>(arrowHeadServerAddress, arrowHeadEHServerPort, EH_PUBLISH_ENDPOINT) {
        };
        return ahClient.postForResponse(eventPublishRequestDTO);
    }
}
