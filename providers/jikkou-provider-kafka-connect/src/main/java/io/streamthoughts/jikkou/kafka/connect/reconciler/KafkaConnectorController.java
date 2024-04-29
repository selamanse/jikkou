/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (c) The original authors
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.streamthoughts.jikkou.kafka.connect.reconciler;

import static io.streamthoughts.jikkou.core.ReconciliationMode.CREATE;
import static io.streamthoughts.jikkou.core.ReconciliationMode.DELETE;
import static io.streamthoughts.jikkou.core.ReconciliationMode.FULL;
import static io.streamthoughts.jikkou.core.ReconciliationMode.UPDATE;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.streamthoughts.jikkou.core.ReconciliationContext;
import io.streamthoughts.jikkou.core.annotation.SupportedResource;
import io.streamthoughts.jikkou.core.config.Configuration;
import io.streamthoughts.jikkou.core.exceptions.JikkouRuntimeException;
import io.streamthoughts.jikkou.core.extension.ContextualExtension;
import io.streamthoughts.jikkou.core.extension.ExtensionContext;
import io.streamthoughts.jikkou.core.io.Jackson;
import io.streamthoughts.jikkou.core.models.CoreAnnotations;
import io.streamthoughts.jikkou.core.models.HasMetadata;
import io.streamthoughts.jikkou.core.models.change.ResourceChange;
import io.streamthoughts.jikkou.core.reconciler.ChangeExecutor;
import io.streamthoughts.jikkou.core.reconciler.ChangeHandler;
import io.streamthoughts.jikkou.core.reconciler.ChangeResult;
import io.streamthoughts.jikkou.core.reconciler.Controller;
import io.streamthoughts.jikkou.core.reconciler.DefaultChangeExecutor;
import io.streamthoughts.jikkou.core.reconciler.annotations.ControllerConfiguration;
import io.streamthoughts.jikkou.kafka.connect.ApiVersions;
import io.streamthoughts.jikkou.kafka.connect.KafkaConnectExtensionConfig;
import io.streamthoughts.jikkou.kafka.connect.KafkaConnectLabels;
import io.streamthoughts.jikkou.kafka.connect.api.KafkaConnectApi;
import io.streamthoughts.jikkou.kafka.connect.api.KafkaConnectApiFactory;
import io.streamthoughts.jikkou.kafka.connect.api.KafkaConnectClientConfig;
import io.streamthoughts.jikkou.kafka.connect.change.KafkaConnectorChangeComputer;
import io.streamthoughts.jikkou.kafka.connect.change.KafkaConnectorChangeDescription;
import io.streamthoughts.jikkou.kafka.connect.change.KafkaConnectorChangeHandler;
import io.streamthoughts.jikkou.kafka.connect.exception.KafkaConnectClusterNotFoundException;
import io.streamthoughts.jikkou.kafka.connect.models.V1KafkaConnector;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

@SupportedResource(type = V1KafkaConnector.class)
@SupportedResource(apiVersion = ApiVersions.KAFKA_V1BETA, kind = "KafkaConnectorChange")
@ControllerConfiguration(
    supportedModes = {CREATE, DELETE, UPDATE, FULL}
)
public final class KafkaConnectorController extends ContextualExtension implements Controller<V1KafkaConnector, ResourceChange> {

    private KafkaConnectExtensionConfig configuration;

    private KafkaConnectorCollector collector;

    /**
     * {@inheritDoc}
     **/
    @Override
    public void init(@NotNull ExtensionContext context) {
        super.init(context);
        this.configuration = new KafkaConnectExtensionConfig(context.appConfiguration());
        this.collector = new KafkaConnectorCollector();
        this.collector.init(context);
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public List<ChangeResult> execute(@NotNull final ChangeExecutor<ResourceChange> executor,
                                      @NotNull final ReconciliationContext context) {

        List<ResourceChange> changes = executor.changes();
        Map<String, List<ResourceChange>> changesByCluster = groupByKafkaConnectCluster(
            changes,
            change -> true
        );

        List<ChangeResult> results = new LinkedList<>();
        for (Map.Entry<String, List<ResourceChange>> entry : changesByCluster.entrySet()) {
            final String cluster = entry.getKey();
            KafkaConnectClientConfig connectClientConfig = getConnectClientConfig(cluster, entry.getValue());
            try (KafkaConnectApi api = KafkaConnectApiFactory.create(connectClientConfig)) {
                List<ChangeHandler<ResourceChange>> handlers = List.of(
                    new KafkaConnectorChangeHandler(api, cluster),
                    new ChangeHandler.None<>(change -> new KafkaConnectorChangeDescription(cluster, change))
                );
                DefaultChangeExecutor<ResourceChange> dedicatedExecutor = new DefaultChangeExecutor<>(
                    context,
                    entry.getValue()
                );
                results.addAll(dedicatedExecutor.applyChanges(handlers));
            }
        }

        return results;
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public List<ResourceChange> plan(
        @NotNull Collection<V1KafkaConnector> resources,
        @NotNull ReconciliationContext context) {

        Map<String, List<V1KafkaConnector>> resourcesByCluster = groupByKafkaConnectCluster(
            resources,
            context.selector()::apply);

        KafkaConnectorChangeComputer computer = new KafkaConnectorChangeComputer();

        List<ResourceChange> allChanges = new LinkedList<>();
        for (Map.Entry<String, List<V1KafkaConnector>> entry : resourcesByCluster.entrySet()) {
            KafkaConnectClientConfig connectClientConfig = getConnectClientConfig(entry.getKey(), entry.getValue());
            List<V1KafkaConnector> actualStates = collector.listAll(entry.getKey(), connectClientConfig, false)
                .stream()
                .filter(context.selector()::apply)
                .toList();
            allChanges.addAll(computer.computeChanges(actualStates, entry.getValue()));
        }
        return allChanges;
    }

    private KafkaConnectClientConfig getConnectClientConfig(final String connectClusterName,
                                                            final List<? extends HasMetadata> connectors) {
        List<KafkaConnectClientConfig> clientConfigs = connectors
            .stream()
            .map(connector -> connector.getMetadata().findAnnotationByKey(CoreAnnotations.JIKKOU_IO_CONFIG_OVERRIDE))
            .flatMap(Optional::stream)
            .map(config -> {
                try {
                    return Jackson.json().readValue(config.toString(), Map.class);
                } catch (JsonProcessingException e) {
                    throw new JikkouRuntimeException(String.format(
                        "Failed to parse JSON from metadata.annotation '%s': %s", CoreAnnotations.JIKKOU_IO_CONFIG_OVERRIDE, e.getMessage()
                    ), e);
                }
            })
            .map(Configuration::from)
            .map(KafkaConnectClientConfig::new)
            .toList();

        if (clientConfigs.isEmpty()) {
            return configuration.getConfigForCluster(connectClusterName)
                .orElseThrow(() -> new KafkaConnectClusterNotFoundException(String.format(
                        "No connect cluster configured for name '%s'", connectClusterName)
                    )
                );
        } else {
            if (clientConfigs.size() != connectors.size()) {
                throw new JikkouRuntimeException(String.format(
                    "Not all connector resources for cluster %s define the metadata.annotation '%s'",
                    connectClusterName, CoreAnnotations.JIKKOU_IO_CONFIG_OVERRIDE
                ));
            }

            if (new HashSet<>(clientConfigs).size() > 1) {
                throw new JikkouRuntimeException(String.format(
                    "Multiple config was defined for Kafka Connect cluster '%s' through the metadata.annotation '%s'",
                    connectClusterName, CoreAnnotations.JIKKOU_IO_CONFIG_OVERRIDE
                ));
            }
            return clientConfigs.getFirst();
        }
    }

    @NotNull
    private <T extends HasMetadata> Map<String, List<T>> groupByKafkaConnectCluster(@NotNull Collection<T> changes,
                                                                                    @NotNull Predicate<T> predicate) {
        return changes
            .stream()
            .filter(predicate)
            .collect(Collectors.groupingBy(
                it -> it.getMetadata()
                    .getLabelByKey(KafkaConnectLabels.KAFKA_CONNECT_CLUSTER)
                    .getValue()
                    .toString(),
                Collectors.toList())
            );
    }
}
