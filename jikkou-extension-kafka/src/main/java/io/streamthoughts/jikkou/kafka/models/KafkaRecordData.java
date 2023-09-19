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
package io.streamthoughts.jikkou.kafka.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.streamthoughts.jikkou.annotation.Reflectable;
import io.streamthoughts.jikkou.kafka.model.KafkaRecordHeader;
import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import lombok.Builder;
import lombok.Setter;
import lombok.Singular;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder(builderMethodName = "builder", toBuilder = true, setterPrefix = "with")
@With
@Setter
@JsonPropertyOrder({
    "headers",
    "key",
    "value"
})
@Jacksonized
@Reflectable
@Generated("jsonschema2pojo")
public class KafkaRecordData {

    /**
     * The record header.
     * 
     */
    @JsonProperty("headers")
    @JsonPropertyDescription("The record header.")
    @Singular
    private List<KafkaRecordHeader> headers = new ArrayList<KafkaRecordHeader>();
    /**
     * The record key.
     * 
     */
    @JsonProperty("key")
    @JsonPropertyDescription("The record key.")
    private io.streamthoughts.jikkou.kafka.model.DataHandle key;
    /**
     * The record value.
     * 
     */
    @JsonProperty("value")
    @JsonPropertyDescription("The record value.")
    private io.streamthoughts.jikkou.kafka.model.DataHandle value;

    /**
     * No args constructor for use in serialization
     * 
     */
    public KafkaRecordData() {
    }

    /**
     * 
     * @param headers
     * @param value
     * @param key
     */
    @ConstructorProperties({
        "headers",
        "key",
        "value"
    })
    public KafkaRecordData(List<KafkaRecordHeader> headers, io.streamthoughts.jikkou.kafka.model.DataHandle key, io.streamthoughts.jikkou.kafka.model.DataHandle value) {
        super();
        this.headers = headers;
        this.key = key;
        this.value = value;
    }

    /**
     * The record header.
     * 
     */
    @JsonProperty("headers")
    public List<KafkaRecordHeader> getHeaders() {
        return headers;
    }

    /**
     * The record key.
     * 
     */
    @JsonProperty("key")
    public io.streamthoughts.jikkou.kafka.model.DataHandle getKey() {
        return key;
    }

    /**
     * The record value.
     * 
     */
    @JsonProperty("value")
    public io.streamthoughts.jikkou.kafka.model.DataHandle getValue() {
        return value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(KafkaRecordData.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("headers");
        sb.append('=');
        sb.append(((this.headers == null)?"<null>":this.headers));
        sb.append(',');
        sb.append("key");
        sb.append('=');
        sb.append(((this.key == null)?"<null>":this.key));
        sb.append(',');
        sb.append("value");
        sb.append('=');
        sb.append(((this.value == null)?"<null>":this.value));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = ((result* 31)+((this.headers == null)? 0 :this.headers.hashCode()));
        result = ((result* 31)+((this.value == null)? 0 :this.value.hashCode()));
        result = ((result* 31)+((this.key == null)? 0 :this.key.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof KafkaRecordData) == false) {
            return false;
        }
        KafkaRecordData rhs = ((KafkaRecordData) other);
        return ((((this.headers == rhs.headers)||((this.headers!= null)&&this.headers.equals(rhs.headers)))&&((this.value == rhs.value)||((this.value!= null)&&this.value.equals(rhs.value))))&&((this.key == rhs.key)||((this.key!= null)&&this.key.equals(rhs.key))));
    }

}
