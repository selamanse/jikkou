/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (c) The original authors
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.streamthoughts.jikkou.kafka.model.user;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.streamthoughts.jikkou.core.annotation.Reflectable;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = V1KafkaUserAuthentication.ScramSha512.class, name = "scram-sha-512"),
    @JsonSubTypes.Type(value = V1KafkaUserAuthentication.ScramSha256.class, name = "scram-sha-256")
})
public sealed interface V1KafkaUserAuthentication permits
    V1KafkaUserAuthentication.ScramSha512,
    V1KafkaUserAuthentication.ScramSha256 {

    int DEFAULT_ITERATIONS = 8192;

    @Reflectable
    @JsonPropertyOrder({
        "password",
        "iterations",
        "salt"
    })
    record ScramSha512(
        @JsonPropertyDescription("The password for the user. If not set, a new password is generated.")
        String password,
        @JsonPropertyDescription("The number of iterations used in the SCRAM credential.")
        Integer iterations,
        @JsonPropertyDescription(" A random salt generated by the client.")
        String salt
    ) implements V1KafkaUserAuthentication {

        public static Builder builder() {
            return new Builder();
        }

        public Builder toBuilder() {
            return builder()
                .withPassword(password)
                .withIterations(iterations)
                .withSalt(salt);
        }

        public static final class Builder {
            private String password;
            private Integer iterations;
            private String salt;

            public Builder withPassword(String password) {
                this.password = password;
                return this;
            }

            public Builder withIterations(Integer iterations) {
                this.iterations = iterations;
                return this;
            }

            public Builder withSalt(String salt) {
                this.salt = salt;
                return this;
            }

            public ScramSha256 build() {
                return new ScramSha256(password, iterations, salt);
            }
        }
    }

    @Reflectable
    @JsonPropertyOrder({
        "password",
        "iterations",
        "salt"
    })
    record ScramSha256(
        @JsonPropertyDescription("The password for the user. If not set, a new password is generated.")
        String password,
        @JsonPropertyDescription("The number of iterations used in the SCRAM credential.")
        Integer iterations,
        @JsonPropertyDescription(" A random salt generated by the client.")
        String salt
    ) implements V1KafkaUserAuthentication {

        public static Builder builder() {
            return new Builder();
        }

        public Builder toBuilder() {
            return builder()
                .withPassword(password)
                .withIterations(iterations)
                .withSalt(salt);
        }

        public static final class Builder {
            private String password;
            private Integer iterations;
            private String salt;

            public Builder withPassword(String password) {
                this.password = password;
                return this;
            }

            public Builder withIterations(Integer iterations) {
                this.iterations = iterations;
                return this;
            }

            public Builder withSalt(String salt) {
                this.salt = salt;
                return this;
            }

            public ScramSha256 build() {
                return new ScramSha256(password, iterations, salt);
            }
        }
    }
}
