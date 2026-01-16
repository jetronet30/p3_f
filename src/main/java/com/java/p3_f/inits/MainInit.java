package com.java.p3_f.inits;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.java.p3_f.inits.freeswitch.LibsInit;
import com.java.p3_f.inits.freeswitch.ResourceCreator;
import com.java.p3_f.inits.repo.RepoInit;
import com.java.p3_f.serversettings.network.DefaultNetwork;



public class MainInit {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainInit.class);

    public static void mainInit(){
        LOGGER.info("    ----- STARTING INITIALIZER -----    ");
        DefaultNetwork.createDefaultNetPlan();
        RepoInit.initRepos();
        LibsInit.initLibs();
        ResourceCreator.createResource();
    }

}
