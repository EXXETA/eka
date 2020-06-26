package com.exxeta.java.k8s.operator;

import com.exxeta.java.k8s.operator.Pojo.Beats;
import com.exxeta.java.k8s.operator.Pojo.NamespaceExemptions;
import com.exxeta.java.k8s.operator.builder.InformerBuilder;
import com.exxeta.java.k8s.operator.builder.job.AddedNamespaceEventJobBuilder;
import com.exxeta.java.k8s.operator.builder.job.BeatFallbackIndexJobBuilder;
import com.exxeta.java.k8s.operator.builder.job.StartupPolicyJobBuilder;
import com.exxeta.java.k8s.operator.config.Environments;
import com.exxeta.java.k8s.operator.handler.KubernetesNamespaceEventHandler;
import com.exxeta.java.k8s.operator.job.RootJob;
import com.exxeta.java.k8s.operator.service.BeatValidator;
import com.exxeta.java.k8s.operator.service.ElasticService;
import com.exxeta.java.k8s.operator.service.EnvVariableValidator;
import com.exxeta.java.k8s.operator.service.LoggerService;
import com.exxeta.java.k8s.operator.utility.JobUtilityMethods;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.kubernetes.client.informer.SharedInformer;
import io.kubernetes.client.informer.SharedInformerFactory;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.models.V1Namespace;
import io.kubernetes.client.util.ClientBuilder;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;

import static com.exxeta.java.k8s.operator.config.Environments.*;


public class Main {
    public static void main(String[] args) throws IOException {
        final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(Main.class);
        LoggerService loggerService = new LoggerService();
        EnvVariableValidator validator = new EnvVariableValidator();
        BeatValidator beatValidator = new BeatValidator();
        ObjectMapper objectMapper = new ObjectMapper();
        Queue<RootJob> jobQueue = new LinkedList<>();

        //ConfigImport
        Map<Environments, String> configMap = validator.validate();

        Beats beats = objectMapper.readValue(configMap.get(BEATS), Beats.class);
        NamespaceExemptions namespaceExemptions = objectMapper.readValue(configMap.get(NAMESPACE_EXEMPTIONS), NamespaceExemptions.class);
        if (!beatValidator.validate(beats) ||
                !beatValidator.setIlmPolicyRequestBody(beats)) {
            LOGGER.error("Error while validating beats");
            System.exit(-1);
        }


        //Elasticsearch
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(configMap.get(ELASTICSEARCH_USERNAME), configMap.get(ELASTICSEARCH_PASSWORD)));

        RestClient clientElastic =
                RestClient.builder(
                        new HttpHost(configMap.get(ELASTICSEARCH_MASTER_HOSTNAME), Integer.parseInt(configMap.get(ELASTICSEARCH_MASTER_PORT)), "https"))
                        .setHttpClientConfigCallback(
                                httpAsyncClientBuilder -> httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider))
                        .build();

        ElasticService elasticService = new ElasticService(clientElastic, loggerService);

        //Kubernetes
        ApiClient client = ClientBuilder.cluster().build();
        InformerBuilder informerBuilder = new InformerBuilder(client);
        SharedInformerFactory factory = new SharedInformerFactory();
        SharedInformer<V1Namespace> namespaceInformer = informerBuilder.buildNamespaceInformer(factory);

        AddedNamespaceEventJobBuilder builder = new AddedNamespaceEventJobBuilder(configMap, beats, namespaceExemptions);
        namespaceInformer.addEventHandler(new KubernetesNamespaceEventHandler(elasticService, builder, jobQueue));

        //Start application
        StartupPolicyJobBuilder startupPolicyJobBuilder = new StartupPolicyJobBuilder(elasticService, configMap, beats);
        jobQueue.add(startupPolicyJobBuilder.build());

        BeatFallbackIndexJobBuilder jobBuilderFallback = new BeatFallbackIndexJobBuilder(configMap, beats, elasticService);
        jobQueue.add(jobBuilderFallback.build());

        LOGGER.info("Starting Informer..");
        factory.startAllRegisteredInformers();

        JobUtilityMethods jobUtilityMethods = new JobUtilityMethods();
        while (true) {
            try {
                RootJob job = jobQueue.poll();
                if (Objects.nonNull(job)) {
                    job.execute();
                } else {
                    Thread.sleep(jobUtilityMethods.getSleepTimer(configMap.get(SLEEP_TIMER)));
                }
            } catch(InterruptedException e) {
                LOGGER.error("Interrupted during Queue Events",e);
            }
        }
    }
}