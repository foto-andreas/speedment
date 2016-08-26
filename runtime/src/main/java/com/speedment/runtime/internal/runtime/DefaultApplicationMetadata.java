/**
 *
 * Copyright (c) 2006-2016, Speedment, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); You may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.speedment.runtime.internal.runtime;

import com.speedment.common.injector.Injector;
import com.speedment.common.injector.annotation.Config;
import com.speedment.runtime.ApplicationMetadata;
import com.speedment.runtime.config.Project;
import com.speedment.runtime.internal.util.document.DocumentTranscoder;

import java.io.File;

/**
 * The default implementation of the {@link ApplicationMetadata} interface.
 * This class will load the metadata from a .json-file. The default location
 * of the file is {@code src/main/json/speedment.json}, but a custom path
 * can be separated by setting the {@link #METADATA_LOCATION} param in the
 * {@link Injector}.
 * 
 * @author  Emil Forslund
 * @since   2.4.0
 */
public final class DefaultApplicationMetadata implements ApplicationMetadata {
    
    public final static String METADATA_LOCATION = "metadata_location";
    
    /**
     * Specified the location of the .json-file from which the metadata
     * is loaded.
     */
    private @Config(
        name=METADATA_LOCATION, 
        value="src/main/json/speedment.json"
    ) File metadataLocation;
    
    /**
     * Should only be instantiated by the {@link Injector}.
     */
    private DefaultApplicationMetadata() {}

    @Override
    public Project makeProject() {
        return DocumentTranscoder.load(metadataLocation.toPath());
    }
}