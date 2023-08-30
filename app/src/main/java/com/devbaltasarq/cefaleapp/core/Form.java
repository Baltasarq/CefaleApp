// CefaleApp (c) 2023 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core;


import com.devbaltasarq.cefaleapp.core.form.Branch;
import com.devbaltasarq.cefaleapp.core.form.Question;

import java.util.HashMap;
import java.util.Map;


public class Form {
    protected Form()
    {
        this.branchesById = new HashMap<>();
    }

    public void addBranch(Branch branch)
    {
        if ( this.head == null ) {
            this.head = branch;
        }

        this.branchesById.put( branch.getId(), branch );
    }

    /** Sets the head question's id.
     * This must be set AFTER the questions are loaded,
     * since it uses the map to look for the question's id.
     * @param headId the id of the first question of the branch.
     */
    public void setHeadId(String headId)
    {
        this.head = this.branchesById.get( headId );
    }

    public Branch getHead()
    {
        return this.head;
    }

    public Branch getBranchById(String id)
    {
        return this.branchesById.get( id );
    }

    public int getNumBranches()
    {
        return this.branchesById.size();
    }

    public int calcNumQuestions()
    {
        int toret = 0;

        for(final Branch BRANCH: this.branchesById.values()) {
            toret += BRANCH.getNumQuestions();
        }

        return toret;
    }

    public Question locate(String id)
    {
        final Branch BR = this.getBranchById( Question.getBranchFromId( id ) );

        return BR.getQuestionById( id );
    }

    private Branch head;
    private final Map<String, Branch> branchesById;
}
