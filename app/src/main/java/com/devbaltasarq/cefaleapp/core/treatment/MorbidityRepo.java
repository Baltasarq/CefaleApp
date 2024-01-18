// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.cefaleapp.core.treatment;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MorbidityRepo {
    public MorbidityRepo()
    {
        List<Morbidity> allMorbidities = Morbidity.collectAll();

        this.morbidities = new ArrayList<>( allMorbidities );

        for(Morbidity m: allMorbidities) {
            this.morbiditiesById.put( m.getId(), m );
        }
    }

    /** Return a morbidity, given its id.
      * @param id the id of the morbidity.
      * @return the morbidity associated to that id, or null.
      */
    public Morbidity getById(Morbidity.Id id)
    {
        return this.morbiditiesById.get( id );
    }

    /** @return all morbidities. */
    public List<Morbidity> getAll()
    {
        return new ArrayList<>( this.morbidities );
    }

    /** @return the number of morbidities. */
    public int size()
    {
        return this.morbidities.size();
    }

    /** Returns a morbidity in a given position.
      * @param i the position to retrieve.
      * @return gets that morbidity.
      */
    public Morbidity get(int i)
    {
        if ( i < 0
          || i >= this.size() )
        {
            throw new Error( "Morbidities.get(): " + i + "/" + this.size() );
        }

        return this.morbidities.get( i );
    }

    private Map<Morbidity.Id, Morbidity> morbiditiesById;
    private List<Morbidity> morbidities;
}
