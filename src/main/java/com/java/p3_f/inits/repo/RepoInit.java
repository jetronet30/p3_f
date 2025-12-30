package com.java.p3_f.inits.repo;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class RepoInit {

    private static final Logger LOGGER = LoggerFactory.getLogger(RepoInit.class);

    public static final File MAIN_REPO = new File("./MAINREPO");


    public static  void initRepos(){
        LOGGER.info("...... init  repos !!!!!");

    }
    

}
