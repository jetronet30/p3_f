package com.java.p3_f.inits;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.java.p3_f.inits.repo.RepoInit;



public class MainInit {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainInit.class);

    public static void mainInit(){
        LOGGER.info("    ----- STARTING INITIALIZER -----    ");

        RepoInit.initRepos();

    }

}
