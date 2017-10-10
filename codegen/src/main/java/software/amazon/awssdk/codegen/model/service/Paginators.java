/*
 * Copyright 2011-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *    http://aws.amazon.com/apache2.0
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and
 * limitations under the License.
 */

package software.amazon.awssdk.codegen.model.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Paginators {

    public static final Paginators NONE = new Paginators(Collections.emptyMap());

    @JsonProperty("pagination")
    private final Map<String, PaginatorDefinition> paginators;

    // Needed for JSON deserialization
    private Paginators() {
        this(new HashMap<>());
    }

    private Paginators(Map<String, PaginatorDefinition> paginators) {
        this.paginators = paginators;
    }

    /**
     * Returns a map of operation name to its
     * {@link PaginatorDefinition}.
     */
    public Map<String, PaginatorDefinition> getPaginators() {
        return paginators;
    }

    public PaginatorDefinition getPaginatorDefinition(String operationName) {
        return paginators.get(operationName);
    }

}
