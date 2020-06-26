package com.exxeta.java.k8s.operator.builder;

import io.kubernetes.client.informer.SharedInformer;
import io.kubernetes.client.informer.SharedInformerFactory;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Namespace;
import io.kubernetes.client.openapi.models.V1NamespaceList;
import io.kubernetes.client.util.CallGeneratorParams;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class InformerBuilder {

    private static Logger LOGGER = LoggerFactory.getLogger(InformerBuilder.class);
    private final CoreV1Api api;

    public InformerBuilder(ApiClient client) {

        LOGGER.debug("Building NamespaceInformerBuilder");

        // infinite timeout
        OkHttpClient httpClient =
                client.getHttpClient().newBuilder().readTimeout(0, TimeUnit.SECONDS).build();
        client.setHttpClient(httpClient);
        Configuration.setDefaultApiClient(client);
        this.api = new CoreV1Api();
    }

    public SharedInformer<V1Namespace> buildNamespaceInformer(SharedInformerFactory factory) {

        LOGGER.debug("Building Informer for Namespace");
        return factory.sharedIndexInformerFor(
                (CallGeneratorParams params) -> {
                    return api.listNamespaceCall(
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            params.resourceVersion,
                            params.timeoutSeconds,
                            params.watch,
                            null
                    );
                },
                V1Namespace.class,
                V1NamespaceList.class
        );
    }
}
