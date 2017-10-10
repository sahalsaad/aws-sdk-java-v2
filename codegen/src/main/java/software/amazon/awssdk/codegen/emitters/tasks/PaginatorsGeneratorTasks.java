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
package software.amazon.awssdk.codegen.emitters.tasks;

import software.amazon.awssdk.codegen.emitters.GeneratorTask;
import software.amazon.awssdk.codegen.emitters.GeneratorTaskParams;
import software.amazon.awssdk.codegen.emitters.PoetGeneratorTask;
import software.amazon.awssdk.codegen.model.service.PaginatorDefinition;
import software.amazon.awssdk.codegen.poet.ClassSpec;
import software.amazon.awssdk.codegen.poet.model.PaginatorResponseClassSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static software.amazon.awssdk.utils.FunctionalUtils.safeFunction;

public class PaginatorsGeneratorTasks extends BaseGeneratorTasks {

    private final String modelClassDir;

    public PaginatorsGeneratorTasks(GeneratorTaskParams dependencies) {
        super(dependencies);
        this.modelClassDir = dependencies.getPathProvider().getModelDirectory();
    }

    @Override
    protected boolean hasTasks() {
        return model.getHasPaginators();
    }

    @Override
    protected List<GeneratorTask> createTasks() throws Exception {
        info("Emitting paginator classes");
        List<GeneratorTask> tasks = new ArrayList<>();

        model.getPaginators().entrySet().stream()
                .map(safeFunction(this::createTask))
                .forEach(tasks::add);

        return tasks;
    }

    private GeneratorTask createTask(Map.Entry<String, PaginatorDefinition> entry) throws IOException {

        ClassSpec classSpec = new PaginatorResponseClassSpec(model, entry.getKey(), entry.getValue());

        return new PoetGeneratorTask(modelClassDir, model.getFileHeader(), classSpec);
    }

}
