// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core.treatment;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class IdsRepo {
    public IdsRepo()
    {
        this.ids = new HashMap<>();
    }

    /** Adds a new id to the repo.
      * @param id the id to store.
      */
    public void add(BasicId id)
    {
        if ( id == null ) {
            throw new Error( "RepoId.add(): missing id" );
        }

        this.ids.putIfAbsent( id.getKey(), id );
    }

    /** @return the number of id's stored. */
    public int size()
    {
        return this.ids.size();
    }

    /** Gets the corresponding BasicId.
      * @param key the key to look in the repo for.
      * @return the corresponding ID for that key.
      */
    public BasicId get(String key)
    {
        if ( key == null ) {
            key = "";
        }

        return this.ids.get( key.trim().toUpperCase() );
    }

    /** Returns all the ids, sorted by key (lexicograph)
      * @return a list of BasicId objects.
      */
    public List<BasicId> getAll()
    {
        final ArrayList<BasicId> TORET = new ArrayList<>( this.ids.values() );

        TORET.sort( Comparator.comparing( BasicId::getKey ) );
        return TORET;
    }

    private final Map<String, BasicId> ids;
}
