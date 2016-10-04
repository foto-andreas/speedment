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
package com.speedment.runtime.core.internal.field;

import com.speedment.runtime.typemapper.TypeMapper;
import com.speedment.runtime.core.field.ComparableForeignKeyField;
import com.speedment.runtime.core.field.method.BackwardFinder;
import com.speedment.runtime.core.field.method.FindFrom;
import com.speedment.runtime.core.field.method.ReferenceGetter;
import com.speedment.runtime.core.field.method.ReferenceSetter;
import com.speedment.runtime.core.field.predicate.FieldPredicate;
import com.speedment.runtime.core.field.predicate.Inclusion;
import com.speedment.runtime.core.field.trait.HasComparableOperators;
import com.speedment.runtime.core.internal.field.comparator.NullOrder;
import com.speedment.runtime.core.internal.field.comparator.ReferenceFieldComparatorImpl;
import com.speedment.runtime.core.internal.field.method.FindFromReference;
import com.speedment.runtime.core.internal.field.predicate.reference.ReferenceBetweenPredicate;
import com.speedment.runtime.core.internal.field.predicate.reference.ReferenceEqualPredicate;
import com.speedment.runtime.core.internal.field.predicate.reference.ReferenceGreaterOrEqualPredicate;
import com.speedment.runtime.core.internal.field.predicate.reference.ReferenceGreaterThanPredicate;
import com.speedment.runtime.core.internal.field.predicate.reference.ReferenceInPredicate;
import com.speedment.runtime.core.internal.field.predicate.reference.ReferenceIsNullPredicate;
import com.speedment.runtime.core.internal.field.predicate.reference.ReferenceLessOrEqualPredicate;
import com.speedment.runtime.core.internal.field.predicate.reference.ReferenceLessThanPredicate;
import com.speedment.runtime.core.internal.field.predicate.reference.ReferenceNotBetweenPredicate;
import com.speedment.runtime.core.internal.field.predicate.reference.ReferenceNotEqualPredicate;
import com.speedment.runtime.core.internal.field.predicate.reference.ReferenceNotInPredicate;
import com.speedment.runtime.core.internal.field.method.BackwardFinderImpl;

import java.util.Comparator;
import java.util.Set;
import java.util.function.Predicate;

import com.speedment.runtime.config.identifier.ColumnIdentifier;
import com.speedment.runtime.config.identifier.TableIdentifier;
import static java.util.Objects.requireNonNull;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * @param <ENTITY>     the entity type
 * @param <D>          the database type
 * @param <V>          the field type
 * @param <FK_ENTITY>  the foreign entity type
 * 
 * @author  Emil Forslund
 * @author  Per Minborg
 * 
 * @since  2.2.0
 */
public final class ComparableForeignKeyFieldImpl<ENTITY, D, V extends Comparable<? super V>, FK_ENTITY> 
implements ComparableForeignKeyField<ENTITY, D, V, FK_ENTITY> {

    private final ColumnIdentifier<ENTITY> identifier;
    private final ReferenceGetter<ENTITY, V> getter;
    private final ReferenceSetter<ENTITY, V> setter;
    private final HasComparableOperators<FK_ENTITY, V> referenced;
    private final TypeMapper<D, V> typeMapper;
    private final boolean unique;

    public ComparableForeignKeyFieldImpl(
            ColumnIdentifier<ENTITY> identifier,
            ReferenceGetter<ENTITY, V> getter,
            ReferenceSetter<ENTITY, V> setter,
            HasComparableOperators<FK_ENTITY, V> referenced,
            TypeMapper<D, V> typeMapper,
            boolean unique) {
        
        this.identifier = requireNonNull(identifier);
        this.getter     = requireNonNull(getter);
        this.setter     = requireNonNull(setter);
        this.referenced = requireNonNull(referenced);
        this.typeMapper = requireNonNull(typeMapper);
        this.unique     = unique;
    }
    
    /*****************************************************************/
    /*                           Getters                             */
    /*****************************************************************/

    @Override
    public ColumnIdentifier<ENTITY> identifier() {
        return identifier;
    }

    @Override
    public ReferenceSetter<ENTITY, V> setter() {
        return setter;
    }

    @Override
    public ReferenceGetter<ENTITY, V> getter() {
        return getter;
    }

    @Override
    public HasComparableOperators<FK_ENTITY, V> getReferencedField() {
        return referenced;
    }
    
    @Override
    public BackwardFinder<FK_ENTITY, ENTITY> backwardFinder(TableIdentifier<ENTITY> identifier, Supplier<Stream<ENTITY>> streamSupplier) {
        return new BackwardFinderImpl<>(this, identifier, streamSupplier);
    }
    
    @Override
    public FindFrom<ENTITY, FK_ENTITY> finder(TableIdentifier<FK_ENTITY> identifier, Supplier<Stream<FK_ENTITY>> streamSupplier) {
        return new FindFromReference<>(this, referenced, identifier, streamSupplier);
    }

    @Override
    public TypeMapper<D, V> typeMapper() {
        return typeMapper;
    }
    
    @Override
    public boolean isUnique() {
        return unique;
    }
    
    /*****************************************************************/
    /*                         Comparators                           */
    /*****************************************************************/
    
    @Override
    public Comparator<ENTITY> comparator() {
        return new ReferenceFieldComparatorImpl<>(this, NullOrder.NONE);
    }

    @Override
    public Comparator<ENTITY> comparatorNullFieldsFirst() {
        return new ReferenceFieldComparatorImpl<>(this, NullOrder.FIRST);
    }

    @Override
    public Comparator<ENTITY> comparatorNullFieldsLast() {
        return new ReferenceFieldComparatorImpl<>(this, NullOrder.LAST);
    }
    
    /*****************************************************************/
    /*                           Operators                           */
    /*****************************************************************/

    @Override
    public FieldPredicate<ENTITY> isNull() {
        return new ReferenceIsNullPredicate<>(this);
    }

    @Override
    public FieldPredicate<ENTITY> equal(V value) {
        return new ReferenceEqualPredicate<>(this, value);
    }

    @Override
    public Predicate<ENTITY> greaterThan(V value) {
        return new ReferenceGreaterThanPredicate<>(this, value);
    }

    @Override
    public Predicate<ENTITY> greaterOrEqual(V value) {
        return new ReferenceGreaterOrEqualPredicate<>(this, value);
    }

    @Override
    public Predicate<ENTITY> between(V start, V end, Inclusion inclusion) {
        return new ReferenceBetweenPredicate<>(this, start, end, inclusion);
    }

    @Override
    public Predicate<ENTITY> in(Set<V> values) {
        return new ReferenceInPredicate<>(this, values);
    }
    
    @Override
    public Predicate<ENTITY> notEqual(V value) {
        return new ReferenceNotEqualPredicate<>(this, value);
    }

    @Override
    public Predicate<ENTITY> lessThan(V value) {
        return new ReferenceLessThanPredicate<>(this, value);
    }

    @Override
    public Predicate<ENTITY> lessOrEqual(V value) {
        return new ReferenceLessOrEqualPredicate<>(this, value);
    }

    @Override
    public Predicate<ENTITY> notBetween(V start, V end, Inclusion inclusion) {
        return new ReferenceNotBetweenPredicate<>(this, start, end, inclusion);
    }

    @Override
    public Predicate<ENTITY> notIn(Set<V> values) {
        return new ReferenceNotInPredicate<>(this, values);
    }
}