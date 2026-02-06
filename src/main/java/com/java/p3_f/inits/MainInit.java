package com.java.p3_f.inits;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.java.p3_f.inits.freeswitch.LibsInit;
import com.java.p3_f.inits.freeswitch.ResourceCreator;
import com.java.p3_f.inits.postgres.DataService;
import com.java.p3_f.inits.postgres.PostgresInit;
import com.java.p3_f.inits.repo.RepoInit;
import com.java.p3_f.serversettings.basic.BasicService;
import com.java.p3_f.serversettings.network.DefaultNetwork;



public class MainInit {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainInit.class);

    public static void mainInit(){
        LOGGER.info("    ----- STARTING INITIALIZER -----    ");
        RepoInit.initRepos();
        DefaultNetwork.createDefaultNetPlan();
        BasicService.initBasicSettings();
        DataService.initDataSettings();
        PostgresInit.init();
        LibsInit.initLibs();
        ResourceCreator.createResource();
    }

}
