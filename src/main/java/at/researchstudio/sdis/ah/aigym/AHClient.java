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

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public abstract class AHClient<T, E> {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    protected Gson gson = new Gson();
    private HttpClient client;
    protected E response;
    protected final String arrowHeadServerAddress;
    protected final int arrowHeadServerPort;
    protected final String endPoint;
    protected String pathParameter;
    private HashMap<String, String> parameterList;
    private String url;
    private Type tType;
    private Type eType;

    protected AHClient(String arrowHeadServerAddress, int arrowHeadServerPort, String endPoint) {
        client = HttpClient.newHttpClient();
        this.arrowHeadServerAddress = arrowHeadServerAddress;
        this.arrowHeadServerPort = arrowHeadServerPort;
        this.endPoint = endPoint;
        TypeToken<T> tTypeToken = new TypeToken<T>(getClass()) {
        };
        tType = (Class<T>) tTypeToken.getRawType();
        TypeToken<E> eTypeToken = new TypeToken<E>(getClass()) {
        };
        eType = (Class<E>) eTypeToken.getRawType();
         createServiceUrl();
    }

    public E postForResponse(T requestDto) throws ExecutionException, InterruptedException {
        String requestStr = gson.toJson(requestDto);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestStr))
                .build();
        CompletableFuture<HttpResponse<String>> responseFuture = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> res = responseFuture.get();
        return handleResponseBody(res);
    }

    public E getForResponse(String pathParameter, HashMap<String, String> parameterList) throws ExecutionException, InterruptedException {
        this.pathParameter = pathParameter;
        this.parameterList = parameterList;
        addPathParameter();
        addGetParameters();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .build();
        CompletableFuture<HttpResponse<String>> responseFuture = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> res = responseFuture.get();
        return handleResponseBody(res);
    }

    protected E handleResponseBody(HttpResponse<String> res) {
        if (!eType.getTypeName().equalsIgnoreCase(String.class.getTypeName())) {
            response = gson.fromJson(res.body(), eType);
        }
        return response;
    }

    protected void createServiceUrl() {
        removeFromPathEnd(endPoint, "/");
        //create base url
        url = "http://" + this.arrowHeadServerAddress + ":" + this.arrowHeadServerPort + addSlashToPathStart(endPoint);
    }

    protected void addPathParameter() {
        if (pathParameter != null && !pathParameter.isEmpty()) {
            addSlashToPathStart(pathParameter);
            url.concat(pathParameter);
        }
    }

    protected void addGetParameters() {
        if (parameterList != null && !parameterList.isEmpty()) {
            String params = "?";
            for (Map.Entry<String, String> param : parameterList.entrySet()) {
                params.concat(param.getKey());
                params.concat("=");
                params.concat(param.getValue());
                params.concat("&");
            }
            if (params.endsWith("&")) {
                removeFromPathEnd(params, "&");
            }
            url.concat(params);
        }
    }

    private String addSlashToPathStart(String path) {
        if (!path.startsWith("/")) {
            return "/" + path;
        }
        return path;
    }

    private void removeFromPathEnd(String path, String toRemove) {
        if (path.endsWith(toRemove)) {
            path.substring(0, path.length() - 1);
        }
    }
}
