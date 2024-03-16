/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (c) The original authors
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.streamthoughts.jikkou.extension.aiven.collections;

import io.streamthoughts.jikkou.core.annotation.ApiVersion;
import io.streamthoughts.jikkou.core.annotation.Kind;
import io.streamthoughts.jikkou.core.models.DefaultResourceListObject;
import io.streamthoughts.jikkou.core.models.ObjectMeta;
import io.streamthoughts.jikkou.extension.aiven.models.V1KafkaQuota;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.beans.ConstructorProperties;
import java.util.List;

@ApiVersion("kafka.aiven.io/v1beta1")
@Kind("KafkaQuotaList")
public class V1KafkaQuotaList extends DefaultResourceListObject<V1KafkaQuota> {


    /**
     * Creates a new {@link V1KafkaQuotaList} instance.
     *
     * @param kind       The resource Kind.
     * @param apiVersion The resource API Version.
     * @param metadata   The resource metadata.
     * @param items      The items.
     */
    @ConstructorProperties({
        "kind",
        "apiVersion",
        "metadata",
        "items"
    })
    public V1KafkaQuotaList(@Nullable String kind,
                            @Nullable String apiVersion,
                            @Nullable ObjectMeta metadata,
                            @NotNull List<? extends V1KafkaQuota> items) {
        super(kind, apiVersion, metadata, items);
    }

    /**
     * Creates a new {@link V1KafkaQuota} instance.
     *
     * @param items The items.
     */
    public V1KafkaQuotaList(List<? extends V1KafkaQuota> items) {
        super(items);
    }
}
