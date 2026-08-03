
import at.researchstudio.sdis.ah.aigym.AHClientImpl;
import at.researchstudio.sdis.ah.aigym.DtoModelHelper;
import at.researchstudio.sdis.ah.model.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Main {//this class provides an example on how to use the AH AI-gym client using Java code, same logi can be used in Zeppelin Notebook

    static DtoModelHelper modelHelper = new DtoModelHelper("HTTP-INSECURE-JSON", "NOT_SECURE");
    static AHClientImpl impl = new AHClientImpl("192.168.1.100", 8443, 8445, 8455);

    static SystemRequestDTO mcSystemDTO = modelHelper.createSystemRequestDTO("192.168.1.100", "humiditymc", 80);

    //published events should be sent to the AHAdapterService for compatibility matters
    // http://192.168.1.100:7078/zeppelinapi
    static SystemRequestDTO noteBookSystemDTO = modelHelper.createSystemRequestDTO("192.168.1.100", "zeppelinapi", 7078);

    static String eventType = "humiditymcevent";

    public static void main(String[] args) {
        //registerMC();
        //long consumerId = registerNotebook();

        //requestAuthorization(consumerId);

        //subscribe();

        try {
            publish();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void registerMC() throws Exception {
        ServiceRegistryResponseDTO serviceRegistryResponseDTO = impl.registerService(
                modelHelper.createServiceRegistryRequestDTO(mcSystemDTO, "humidityservice", "/humidity"));
        System.out.println(serviceRegistryResponseDTO.toString());
    }

    private static long registerNotebook() throws ExecutionException, InterruptedException {
        ServiceRegistryResponseDTO serviceRegistryResponseDTO = impl.registerService(
                modelHelper.createServiceRegistryRequestDTO(noteBookSystemDTO, "notebookadapterservice", "/zeppelinapi"));
        System.out.println(serviceRegistryResponseDTO.toString());
        return serviceRegistryResponseDTO.getProvider().getId();
    }

    private static void requestAuthorization(long consumerId) throws ExecutionException, InterruptedException {
        ServiceRegistryResponseDTO responseDTO = null;
        ServiceRegistryListResponseDTO l = impl.getAllServices();
        for (ServiceRegistryResponseDTO dto : l.getData()) {
            if (dto.getProvider().getSystemName().equalsIgnoreCase(mcSystemDTO.getSystemName())) {
                responseDTO = dto;
            }
        }
        if (responseDTO != null) {
            System.out.println(responseDTO.toString());
            AuthorizationIntraCloudRequestDTO intraCloudRequestDTO = modelHelper.createAuthorizationIntraCloudRequestDTO(consumerId, responseDTO);
            AuthorizationResponseDTO authorizationResponseDTO = impl.requestAuthorization(intraCloudRequestDTO);
            System.out.println(authorizationResponseDTO.toString());
        }

    }

    private static void subscribe() throws ExecutionException, InterruptedException {
        String response = impl.subscribeEventType(modelHelper.createSubscriptionRequestDTO(
                eventType,
                "/zeppelinapi",
                noteBookSystemDTO,
                mcSystemDTO));
    }

    private static void publish() throws ExecutionException, InterruptedException {
        String json = "{\"eventType\": \"humiditymcevent\", \"source\": {\"systemName\": \"humiditymc\", \"address\": \"192.168.1.100\", \"port\": 80}, \"metaData\": {\"notifyNoteBook\": \"/api/notebook/run/2KW4VJSHJ/20250528-145246_2000830900\"}, \"payload\": \"{\\\"params\\\": {\\\"temperature\\\": \\\"333\\\",\\\"humidity\\\": \\\"333\\\"}}\", \"timeStamp\": \"2025-05-28T19:30:44.898Z\"}";
        EventPublishRequestDTO dto = modelHelper.createEventPublishRequestDTO(
                "{\"params\": {\"param1\": \"3909\",\"param2\": \"39993\"}}",
                mcSystemDTO,
                eventType);
        Map meta = new HashMap();
        meta.put("notifyNoteBook", "/api/notebook/run/2KW4VJSHJ/20250528-145246_2000830900");
        dto.setMetaData(meta);
        impl.publishEvent(dto);
    }

    public static void test(String[] args) {
        System.out.println("Temperature: " + Double.valueOf("${temperature}"));
        System.out.println("Humidity: " + Double.valueOf("${humidity}"));
        fanControlModel1(34.5,55.90);
    }

    private static String fanControlModel1(double temperature, double humidity){
        return processModel1(temperature, humidity);
    }

    private static String processModel1(double temperature, double humidity){
        if (temperature <= 24.0 && humidity > 60.0){
            return "{\"action\": \"increase fan speed\"}";
        }
        return "{\"action\": \"decrease fan speed\"}";
    }

    private static void llmService(String requestStr) throws ExecutionException, InterruptedException {
        HttpClient client;
        client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://192.168.122.96:8000/generate"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestStr))
                .build();
        CompletableFuture<HttpResponse<String>> responseFuture = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> res = responseFuture.get();
    }
}


/**
 * Post:
 * http://localhost:7077/api/notebook/run/2KW4VJSHJ/20250528-145246_2000830900?param2=1
 * {
 * "params": {
 * "param1": "2",
 * "param2": "4"
 * }
 * }
 */