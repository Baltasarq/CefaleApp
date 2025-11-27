// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.cefaleapp.core;


import com.devbaltasarq.cefaleapp.core.treatment.Nameable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public interface Identifiable {
    Nameable getId();

    /** Generates a new list with all corresponding objects.
      * @param ALL the collection of all objects.
      * @param IDS the collection of all ids.
      * @return a new list with the objects corresponding to the given identifiers.
      * @param <T> The identifiable objects, i.e. Mordbity, Medicine...
      * @param <U> The id object, i.e. Medicine.Id, Morbidity.Id...
      */
    static <T extends Identifiable, U extends Nameable> List<T> objListFromIdList(
            final Map<U, T> ALL,
            final Collection<U> IDS)
    {
        return IDS.stream().map(
                        id -> Objects.requireNonNull( ALL.get( id ) ) )
                .collect( Collectors.toList() );
    }
}
