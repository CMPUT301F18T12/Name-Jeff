/*
 *  Class Name: Problem
 *
 *  Version: Version 1.0
 *
 *  Date: November 1, 2018
 *
 *  Copyright (c) Team 12, CMPUT301, University of Alberta - All Rights Reserved. You may use, distribute, or modify this code under terms and conditions of the Code of Students Behaviour at the University of Alberta
 */


package com.example.jerry.healemgood.model.problem;

import com.example.jerry.healemgood.model.record.Record;

import java.util.ArrayList;
import java.util.Date;

import io.searchbox.annotations.JestId;

/**
 * Represents a problem
 *
 * @author xiacijie
 * @version 1.0
 * @since 1.0
 */

public class Problem {
    //pid is auto generated when problem is added to DB
    @JestId
    private String pId;
    //Title of the problem
    private String title;
    private Date createdDate;

    private String description;
    //Id of the patient who created this problem
    private String userId;

    /**
     * This initializes a Problem
     *
     * @param title     Title of the problem
     * @param date      The date when this problem is created
     * @param userId    The patient who created this problem
     */
    public Problem(String title, Date date,String userId) {
        this.title = title;
        this.createdDate = date; // The createdDate of a problem should be the date of the first record
        this.userId =userId;
    }

    /**
     * This gets and returns the Id of the problem
     *
     * @return pId
     */
    public String getpId() {
        return pId;
    }

    /**
     * This sets the Id of the problem
     *
     * @param pId
     */
    public void setpId(String pId) {
        this.pId = pId;
    }

    /**
     * This sets the title of the problem
     *
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * This gets and returns the title of the problem
     *
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * This gets and returns the date of the problem
     *
     * @return createDate
     */
    public Date getCreatedDate() {
        return createdDate;
    }

    /**
     * This sets the date of the problem
     *
     * @param createdDate
     */
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * This gets and returns the userId associated with the problem
     *
     * @return userId
     */
    public String getUserId() {
        return userId;
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
