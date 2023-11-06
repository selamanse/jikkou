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
package io.streamthoughts.jikkou.core.selectors;

import io.streamthoughts.jikkou.common.annotation.InterfaceStability.Evolving;
import io.streamthoughts.jikkou.core.models.HasMetadata;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * Default interface for selecting resources that should be involved in the reconciliation process.
 */
@Evolving
public interface Selector {

    /**
     * Returns the unique name of this filter.
     * @return  the string name, cannot be null.
     */
    default String name() {
        final String className = this.getClass().getSimpleName();
        return className
                .replace("Resource", "")
                .replace("Selector", "");
    }

    /**
     * Apply this filter of the specified resource.
     *
     * @param resource the resource to be filtered.
     * @return         {@code true} if the resource should be kept for the reconciliation, otherwise {@code false}.
     */
    boolean apply(@NotNull HasMetadata resource);

    /**
     * Gets the string selector expressions for this selector.
     *
     * @return  a list of selector string expressions.
     */
    default List<String> getSelectorExpressions() {
        throw new UnsupportedOperationException();
    }

}
