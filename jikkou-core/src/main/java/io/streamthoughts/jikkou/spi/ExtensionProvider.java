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
package io.streamthoughts.jikkou.spi;

import io.streamthoughts.jikkou.core.config.Configuration;
import io.streamthoughts.jikkou.core.extension.ExtensionRegistry;
import io.streamthoughts.jikkou.core.resource.ResourceRegistry;
import java.util.Locale;
import org.jetbrains.annotations.NotNull;

/**
 * Service interface for registering extensions and resources to Jikkou at runtime.
 */
public interface ExtensionProvider {

    /**
     * Returns the name of this provider.
     *
     * @return The provider name.
     */
    default String getName() {
        return this.getClass()
                .getSimpleName()
                .replace(ExtensionProvider.class.getSimpleName(), "")
                .toLowerCase(Locale.ROOT);
    }

    /**
     * Registers the extensions for this provider.
     *
     * @param registry      The ExtensionRegistry.
     * @param configuration The configuration.
     */
    void registerExtensions(@NotNull ExtensionRegistry registry,
                            @NotNull Configuration configuration);

    /**
     * Registers the resources for this provider.
     *
     * @param registry The ResourceRegistry
     */
    void registerResources(@NotNull ResourceRegistry registry);
}
