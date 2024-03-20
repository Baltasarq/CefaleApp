// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core.treatment;


public class BasicId implements Nameable {
    /** Creates a new Basic Id
      * @param key the key for this id.
      * @param name the name of this id.
      */
    public BasicId(String key, String name)
    {
        if ( key == null
          || key.isBlank() )
        {
            throw new Error( "BasicId: invalid null key" );
        }

        if ( name == null
          || name.isBlank() )
        {
            name = "";
        }

        this.key = key.trim().toUpperCase();
        this.name = name.trim();
    }

    /** @return the name of the group. */
    @Override
    public String getName()
    {
        return this.name;
    }

    /** @return the upper case char for this group. */
    public String getKey()
    {
        return this.key;
    }

    @Override
    public int hashCode()
    {
        return this.getKey().hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        boolean toret = false;

        if ( obj instanceof BasicId other ) {
            toret = ( this.getKey().equals( other.getKey() ) );
        }

        return toret;
    }

    @Override
    public String toString()
    {
        return this.getKey() + "(" + this.getName() + ")";
    }

    private final String key;
    private final String name;
}
