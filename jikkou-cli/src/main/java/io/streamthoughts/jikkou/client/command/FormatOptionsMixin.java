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
package io.streamthoughts.jikkou.client.command;

import io.streamthoughts.jikkou.core.io.writer.ResourceWriter;
import picocli.CommandLine.Option;

public final class FormatOptionsMixin {

    @Option(names = { "--output", "-o" },
            defaultValue = "YAML",
            description = "Prints the output in the specified format. Allowed values: ${COMPLETION-CANDIDATES} (default YAML)."
    )
    ResourceWriter.Format format;

    public ResourceWriter.Format format() {
        return format;
    }
}
