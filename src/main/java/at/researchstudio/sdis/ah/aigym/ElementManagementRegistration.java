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

import at.researchstudio.sdis.ah.model.ServiceRegistryListResponseDTO;
import at.researchstudio.sdis.ah.model.ServiceRegistryResponseDTO;
import at.researchstudio.sdis.ah.model.SystemRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutionException;


public class ElementManagementRegistration {
    protected static final Integer SERVICE_VERSION = 1;
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    protected int port;

    private String serverAddress;

    private String systemName;

    private String temperatureServiceDefinition;

    private String temperatureServiceUri;

    private String humidityServiceDefinition;

    private String humidityServiceUri;

    private String lightServiceDefinition;

    private String lightServiceUri;

    private String nutrientServiceDefinition;

    private String nutrientServiceUri;

    private String airServiceDefinition;

    private String airServiceUri;

    protected List<String> managementSystemServices;


    protected AHClientImpl ahClient = new AHClientImpl("", 0, 0, 0);
    protected ServiceRegistryResponseDTO serviceRegistryResponseDTO;
    protected SystemRequestDTO systemRequestDTO;
    protected Map<String, ServiceRegistryResponseDTO> registeredServices = new HashMap<>();
    DtoModelHelper modelHelper = new DtoModelHelper("", "");

    public int getPort() {
        return port;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public String getSystemName() {
        return systemName;
    }

    public String getTemperatureServiceDefinition() {
        return temperatureServiceDefinition;
    }

    public String getHumidityServiceDefinition() {
        return humidityServiceDefinition;
    }

    public String getLightServiceDefinition() {
        return lightServiceDefinition;
    }

    public String getNutrientServiceDefinition() {
        return nutrientServiceDefinition;
    }

    public String getAirServiceDefinition() {
        return airServiceDefinition;
    }

    public SystemRequestDTO getSystemRequestDTO() {
        return systemRequestDTO;
    }

    //@EventListener
    public void registerServices() throws InterruptedException {
        initManagementSystemServiceList();
        try {
            getRegisteredServices();
        } catch (ExecutionException | InterruptedException e) {
            throw new InterruptedException();
        }
        if (systemRequestDTO == null) {
            modelHelper.createSystemRequestDTO(serverAddress, systemName, port);
        }
        registerAllServices();
    }

    public ServiceRegistryResponseDTO getServiceByDefinition(String definition) {
        return registeredServices.get(definition);
    }

    public Collection<ServiceRegistryResponseDTO> getAllRegisteredServices() {
        return registeredServices.values();
    }

    public long getSystemId() {
        Optional<ServiceRegistryResponseDTO> dto = getAllRegisteredServices().stream().findFirst();
        if (dto.isPresent())
            return dto.get().getProvider().getId();
        else return 0;
    }

    public void registerAllServices() throws InterruptedException {
        try {
            registerManagementService(temperatureServiceDefinition, temperatureServiceUri, systemRequestDTO);
            registerManagementService(humidityServiceDefinition, humidityServiceUri, systemRequestDTO);
            registerManagementService(lightServiceDefinition, lightServiceUri, systemRequestDTO);
            registerManagementService(nutrientServiceDefinition, nutrientServiceUri, systemRequestDTO);
            registerManagementService(airServiceDefinition, airServiceUri, systemRequestDTO);
        } catch (ExecutionException | InterruptedException e) {
            throw new InterruptedException();
        }

    }

    protected void getRegisteredServices() throws ExecutionException, InterruptedException {
        ServiceRegistryListResponseDTO serviceRegistryListResponseDTO = ahClient.getAllServices();
        for (ServiceRegistryResponseDTO dto : serviceRegistryListResponseDTO.getData()) {
            if (managementSystemServices.contains(dto.getServiceDefinition().getServiceDefinition())) {
                registeredServices.put(dto.getServiceDefinition().getServiceDefinition(), dto);
            }
        }
    }

    protected void registerManagementService(String serviceDefinition, String serviceUri, SystemRequestDTO systemRequestDTO) throws ExecutionException, InterruptedException {
        if (registeredServices.containsKey(serviceDefinition)) {
            return;
        }
        ServiceRegistryResponseDTO sRegistryResponseDTO = ahClient.registerService(
                modelHelper.createServiceRegistryRequestDTO(systemRequestDTO, serviceDefinition, serviceUri)
        );
        registeredServices.put(serviceDefinition, sRegistryResponseDTO);
    }

    protected void initManagementSystemServiceList() {
        if (managementSystemServices == null) {
            managementSystemServices = new ArrayList<>();
            managementSystemServices.add(temperatureServiceDefinition);
            managementSystemServices.add(humidityServiceDefinition);
            managementSystemServices.add(lightServiceDefinition);
            managementSystemServices.add(nutrientServiceDefinition);
            managementSystemServices.add(airServiceDefinition);
        }
    }


}
