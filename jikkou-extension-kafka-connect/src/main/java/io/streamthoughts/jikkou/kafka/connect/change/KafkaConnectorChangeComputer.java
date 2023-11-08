/*
 * Copyright 2023 The original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.streamthoughts.jikkou.kafka.connect.change;

import io.streamthoughts.jikkou.core.models.HasMetadataChange;
import io.streamthoughts.jikkou.core.reconcilier.Change;
import io.streamthoughts.jikkou.core.reconcilier.ChangeType;
import io.streamthoughts.jikkou.core.reconcilier.change.ConfigEntryChange;
import io.streamthoughts.jikkou.core.reconcilier.change.ConfigEntryChangeComputer;
import io.streamthoughts.jikkou.core.reconcilier.change.ResourceChangeComputer;
import io.streamthoughts.jikkou.core.reconcilier.change.ValueChange;
import io.streamthoughts.jikkou.kafka.connect.models.KafkaConnectorState;
import io.streamthoughts.jikkou.kafka.connect.models.V1KafkaConnector;
import io.streamthoughts.jikkou.kafka.connect.models.V1KafkaConnectorSpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;

public class KafkaConnectorChangeComputer extends ResourceChangeComputer<V1KafkaConnector, V1KafkaConnector, KafkaConnectorChange> {

    /**
     * Creates a new {@link ResourceChangeComputer} instance.
     */
    public KafkaConnectorChangeComputer() {
        super(
                ResourceChangeComputer.metadataNameKeyMapper(),
                ResourceChangeComputer.identityChangeValueMapper(),
                false
        );
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public List<KafkaConnectorChange> buildChangeForUpdating(V1KafkaConnector before,
                                                             V1KafkaConnector after) {
        return List.of(buildChange(before, after));
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public List<KafkaConnectorChange> buildChangeForNone(V1KafkaConnector before,
                                                         V1KafkaConnector after) {
        return List.of(buildChange(before, after));
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public List<KafkaConnectorChange> buildChangeForDeleting(V1KafkaConnector before) {
        // Compute change for 'connector.class'
        ValueChange<String> connectClassChange = ValueChange
                .withBeforeValue(before.getSpec().getConnectorClass());

        // Compute change for 'tasks.max'
        ValueChange<Integer> tasksMaxChange = ValueChange
                .withBeforeValue(before.getSpec().getTasksMax());

        // Compute change for 'config'
        List<ConfigEntryChange> configChanges =  before.getSpec().getConfig()
                .values()
                .stream()
                .map(config -> new ConfigEntryChange(config.getName(), ValueChange.withBeforeValue(config.value())))
                .toList();

        // Compute change for 'state'
        ValueChange<KafkaConnectorState> stateChange = ValueChange
                .withBeforeValue(before.getSpec().getState());

        return List.of(new KafkaConnectorChange(
                ChangeType.DELETE,
                before.getMetadata().getName(),
                connectClassChange,
                tasksMaxChange,
                stateChange,
                configChanges
        ));
    }

    @NotNull
    private static KafkaConnectorChange buildChange(V1KafkaConnector before,
                                                    V1KafkaConnector after) {
        // Compute change for 'config'
        List<ConfigEntryChange> configChanges = new ConfigEntryChangeComputer(true)
                .computeChanges(
                        before.getSpec().getConfig(),
                        after.getSpec().getConfig()
                ).stream()
                .map(HasMetadataChange::getChange)
                .toList();

        // Compute change for 'connector.class'
        ValueChange<String> connectClassChange = ValueChange
                .with(before.getSpec().getConnectorClass(), after.getSpec().getConnectorClass());

        // Compute change for 'tasks.max'
        ValueChange<Integer> tasksMaxChange = ValueChange
                .with(before.getSpec().getTasksMax(), after.getSpec().getTasksMax());

        // Compute change for 'state'
        KafkaConnectorState beforeState = before.getSpec().getState();

        // 'state' is optional and can be empty.
        KafkaConnectorState afterState = Optional.ofNullable(after.getSpec().getState())
                .orElse(KafkaConnectorState.RUNNING);

        ValueChange<KafkaConnectorState> stateChange = ValueChange
                .with(beforeState, afterState);

        List<Change> allChanges = new ArrayList<>(configChanges);
        allChanges.add(connectClassChange);
        allChanges.add(tasksMaxChange);
        allChanges.add(stateChange);
        ChangeType changeType = Change.computeChangeTypeFrom(allChanges);

        return new KafkaConnectorChange(
                changeType,
                after.getMetadata().getName(),
                connectClassChange,
                tasksMaxChange,
                stateChange,
                configChanges
        );
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public List<KafkaConnectorChange> buildChangeForCreating(V1KafkaConnector after) {

        final V1KafkaConnectorSpec spec = after.getSpec();

        // Compute change for connector 'config'
        List<ConfigEntryChange> configChanges =  spec.getConfig()
                .values()
                .stream()
                .map(config -> new ConfigEntryChange(config.getName(), ValueChange.withAfterValue(config.value())))
                .toList();

        // Compute change for 'connector.class'
        ValueChange<String> connectClassChange = ValueChange
                .withAfterValue(spec.getConnectorClass());

        // Compute change for 'tasks.max'
        ValueChange<Integer> tasksMaxChange = ValueChange
                .withAfterValue(spec.getTasksMax());

        // Compute change for 'state'
        ValueChange<KafkaConnectorState> stateChange = ValueChange
                .withAfterValue(spec.getState());

        return List.of(new KafkaConnectorChange(
                ChangeType.ADD,
                after.getMetadata().getName(),
                connectClassChange,
                tasksMaxChange,
                stateChange,
                configChanges
        ));
    }
}
