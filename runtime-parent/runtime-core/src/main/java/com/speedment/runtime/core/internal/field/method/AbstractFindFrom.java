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
package com.speedment.runtime.core.internal.field.method;

import com.speedment.runtime.config.identifier.TableIdentifier;
import com.speedment.runtime.core.field.Field;
import com.speedment.runtime.core.field.method.FindFrom;
import com.speedment.runtime.core.field.trait.HasComparableOperators;
import com.speedment.runtime.core.field.trait.HasFinder;

import static java.util.Objects.requireNonNull;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 *
 * @param <ENTITY>     the source entity
 * @param <FK_ENTITY>  the target entity
 * @param <V>          the wrapper type
 * 
 * @author  Emil Forslund
 * @since   3.0.0
 */
abstract class AbstractFindFrom<
        ENTITY, 
        FK_ENTITY,
        V extends Comparable<? super V>,
        SOURCE extends Field<ENTITY> & HasComparableOperators<ENTITY, V> & HasFinder<ENTITY, FK_ENTITY>,
        TARGET extends Field<FK_ENTITY> & HasComparableOperators<FK_ENTITY, V>
    > implements FindFrom<ENTITY, FK_ENTITY> {
    
    private final SOURCE source;
    private final TARGET target;
    private final TableIdentifier<FK_ENTITY> identifier;
    private final Supplier<Stream<FK_ENTITY>> streamSupplier;

    AbstractFindFrom(
        SOURCE source,
        TARGET target,
        TableIdentifier<FK_ENTITY> identifier,
        Supplier<Stream<FK_ENTITY>> streamSupplier) {
        
        this.source         = requireNonNull(source);
        this.target         = requireNonNull(target);
        this.identifier     = requireNonNull(identifier);
        this.streamSupplier = requireNonNull(streamSupplier);
    }

    @Override
    public final SOURCE getSourceField() {
        return source;
    }

    @Override
    public final TARGET getTargetField() {
        return target;
    }

    @Override
    public final TableIdentifier<FK_ENTITY> getTableIdentifier() {
        return identifier;
    }
    
    protected final Stream<FK_ENTITY> stream() {
        return streamSupplier.get();
    }
}