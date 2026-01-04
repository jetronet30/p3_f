package com.java.p3_f.inits.freeswitch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LibsInit {
    private static final Logger LOGGER = LoggerFactory.getLogger(LibsInit.class);

    private static final List<String> REQUIRED_PACKAGES = Arrays.asList(
            "autoconf", "automake", "libtool", "libtool-bin", "g++", "make",
            "libssl-dev", "pkg-config", "libcurl4-openssl-dev", "libexpat1-dev",
            "libgdbm-dev", "libgnutls28-dev", "libjpeg-dev", "libncurses-dev",
            "libpcre3-dev", "libspeex-dev", "libsqlite3-dev", "libtiff5-dev",
            "libxml2-dev", "zlib1g-dev", "libopus-dev", "libsndfile1-dev",
            "libavformat-dev", "libswscale-dev", "yasm", "liblua5.3-dev",
            "libedit-dev", "libldns-dev", "libspandsp-dev", "libpq-dev",
            "postgresql-client", "mariadb-client", "libmariadb-dev",
            "libmariadb-dev-compat", "libspeexdsp-dev", "libpcre2-dev",
            "uuid-dev", "unixodbc", "unixodbc-dev"
    );

    public static void initLibs() {
        LOGGER.info("\n---- STARTING INIT LIBS FOR FREESWITCH ----\n");

        try {
            // ჯერ apt update (რეკომენდებულია)
            runCommand(List.of("sudo", "apt", "update"));

            List<String> missingPackages = new ArrayList<>();

            for (String pkg : REQUIRED_PACKAGES) {
                if (!isPackageInstalled(pkg)) {
                    missingPackages.add(pkg);
                }
            }

            if (missingPackages.isEmpty()) {
                LOGGER.info("ყველა საჭირო პაკეტი უკვე დაყენებულია.");
            } else {
                LOGGER.info("აკლია პაკეტები: {}", missingPackages);
                LOGGER.info("ვაყენებთ...");

                List<String> installCmd = new ArrayList<>();
                installCmd.add("sudo");
                installCmd.add("apt");
                installCmd.add("install");
                installCmd.add("-y");
                installCmd.addAll(missingPackages);

                runCommand(installCmd);

                LOGGER.info("დაყენება დასრულებულია.");
            }

        } catch (Exception e) {
            LOGGER.error("შეცდომა dependencies-ის დაყენებისას: {}", e.getMessage(), e);
        }

        LOGGER.info("\n---- INIT LIBS FOR FREESWITCH COMPLETED ----\n");
    }

    private static boolean isPackageInstalled(String packageName) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("dpkg-query", "-W", "-f=${Status}", packageName);
        Process process = pb.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line = reader.readLine();
            int exitCode = process.waitFor();

            // თუ პაკეტი არ არსებობს, exitCode=1 და output ცარიელია
            if (exitCode != 0) {
                return false;
            }

            return line != null && line.contains("install ok installed");
        }
    }

    private static void runCommand(List<String> command) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.inheritIO(); // output-ი კონსოლში გამოჩნდეს (ლოგშიც მოხვდება slf4j-ის გამო)
        Process process = pb.start();
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("ბრძანების შეცდომა: " + String.join(" ", command) + " (exit code: " + exitCode + ")");
        }
    }
}