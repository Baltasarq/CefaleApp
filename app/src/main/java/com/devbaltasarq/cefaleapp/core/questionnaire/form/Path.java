// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core.questionnaire.form;


import java.util.ArrayDeque;


/** Represents the branch path to follow:
  * i.e. "data", "screening", "migraine" */
public class Path {
    public Path()
    {
        this.branches = new ArrayDeque<>();
    }

    /** Removes all pending branches' ids. */
    public void clear()
    {
        this.branches.clear();
    }

    /** Adds another branch id to the queue. */
    public void add(String brId)
    {
        this.branches.addLast( brId );
    }

    /** Gets the next branch id on queue.
      * It doesn't remove it.
      * @return the next branch id. */
    public String next()
    {
        return this.branches.getFirst();
    }

    /** Removes the next branch id in the queue. */
    public void remove()
    {
        this.branches.removeFirst();
    }

    /** @return the number of branch ids in the queue. */
    public int size()
    {
        return this.branches.size();
    }

    private final ArrayDeque<String> branches;
}
