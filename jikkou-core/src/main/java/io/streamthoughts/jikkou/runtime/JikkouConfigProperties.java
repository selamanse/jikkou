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
package io.streamthoughts.jikkou.runtime;

import io.streamthoughts.jikkou.core.config.ConfigProperty;
import io.streamthoughts.jikkou.runtime.configurator.ExtensionConfigEntry;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class JikkouConfigProperties {

    public static String EXTENSION_PROVIDER_CONFIG_PREFIX = "extension.providers";
    public static final ConfigProperty<List<String>> EXTENSION_PATHS = ConfigProperty
            .ofList("extension.paths")
            .description("A list of directories from which to load external resource and extensions (controller, collector, transformation, validation, etc.")
            .orElse(Collections.emptyList());


    public static final ConfigProperty<Boolean> EXTENSIONS_PROVIDER_DEFAULT_ENABLED = ConfigProperty
            .ofBoolean(EXTENSION_PROVIDER_CONFIG_PREFIX + ".default.enabled")
            .description("Specify whether all extension providers should be enabled or disabled by default.")
            .orElse(true);

    public static final ConfigProperty<List<ExtensionConfigEntry>> VALIDATIONS_CONFIG = ConfigProperty
            .ofConfigList("validations")
            .description("The list of custom validations to apply on resources.")
            .map(configs -> configs.stream().map(ExtensionConfigEntry::of).collect(Collectors.toList()))
            .orElse(Collections.emptyList());

    public static final ConfigProperty<List<ExtensionConfigEntry>> TRANSFORMATION_CONFIG = ConfigProperty
            .ofConfigList( "transformations")
            .description("The list of custom transformations to apply on resources.")
            .map(configs -> configs.stream().map(ExtensionConfigEntry::of).collect(Collectors.toList()))
            .orElse(Collections.emptyList());

    public static final ConfigProperty<List<ExtensionConfigEntry>> REPORTERS_CONFIG = ConfigProperty
            .ofConfigList("reporters")
            .description("The list of custom reporters.")
            .map(configs -> configs.stream().map(ExtensionConfigEntry::of).collect(Collectors.toList()))
            .orElse(Collections.emptyList());

}
