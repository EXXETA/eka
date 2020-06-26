package com.exxeta.java.k8s.operator.handler;

import com.exxeta.java.k8s.operator.builder.job.AddedNamespaceEventJobBuilder;
import com.exxeta.java.k8s.operator.job.RootJob;
import com.exxeta.java.k8s.operator.service.ElasticService;
import io.kubernetes.client.informer.ResourceEventHandler;
import io.kubernetes.client.openapi.models.V1Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;

public class KubernetesNamespaceEventHandler implements ResourceEventHandler<V1Namespace> {

    private static Logger LOGGER = LoggerFactory.getLogger(KubernetesNamespaceEventHandler.class);
    private ElasticService elasticService;
    private AddedNamespaceEventJobBuilder jobBuilder;
    private Queue<RootJob> jobQueue;

    public KubernetesNamespaceEventHandler(ElasticService elasticService, AddedNamespaceEventJobBuilder jobBuilder, Queue<RootJob> jobQueue) {
        this.elasticService = elasticService;
        this.jobBuilder = jobBuilder;
        this.jobQueue = jobQueue;
    }


    @Override
    public void onAdd(V1Namespace obj)
    {
        LOGGER.info("Added new namespace: " + obj.getMetadata().getName());
        RootJob addedEvent  = jobBuilder.buildAddedEvent(elasticService, obj.getMetadata().getName());
        jobQueue.add(addedEvent);
    }

    @Override
    public void onUpdate(V1Namespace oldObj, V1Namespace newObj) {

    }

    @Override
    public void onDelete(V1Namespace obj, boolean deletedFinalStateUnknown) {

    }

}
