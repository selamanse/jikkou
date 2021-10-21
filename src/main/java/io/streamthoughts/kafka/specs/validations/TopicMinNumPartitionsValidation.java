/*
 * Copyright 2021 StreamThoughts.
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.streamthoughts.kafka.specs.validations;

import io.streamthoughts.kafka.specs.model.V1TopicObject;
import org.jetbrains.annotations.NotNull;

public class TopicMinNumPartitionsValidation extends TopicValidation {

    private static final int DEFAULT_MIN_NUM_PARTITIONS = 1;

    /**
     * {@inheritDoc}
     */
    @Override
    public void validateTopic(final @NotNull V1TopicObject topic) throws ValidationException {
        topic.partitions().ifPresent(p -> {
            if (p != V1TopicObject.NO_NUM_PARTITIONS && p < DEFAULT_MIN_NUM_PARTITIONS) {
                throw new ValidationException(String.format(
                        "Number of partitions for topic '%s' is less than the minimum required: %d < %d",
                        topic.name(),
                        p,
                        DEFAULT_MIN_NUM_PARTITIONS
                ), this);
            }
        });
    }
}
