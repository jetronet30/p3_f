package com.java.p3_f.inits.freeswitch;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.java.p3_f.inits.repo.RepoInit;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ResourceCreator {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceCreator.class);

    private static final File OUT_DIR = RepoInit.MAIN_REPO;

    public static void createResource() {
        try {

            File[] existingFiles = OUT_DIR.listFiles();
            if (existingFiles != null && existingFiles.length > 0) {
                LOGGER.info("fs უკვე ამოღებულია MAINREPO-ში: " + OUT_DIR.getAbsolutePath());
                return;
            }

            ClassLoader classLoader = ResourceCreator.class.getClassLoader();
            java.net.URL resourceUrl = classLoader.getResource("fs");

            if (resourceUrl == null) {
                LOGGER.info("გაფრთხილება: resources/fs ფოლდერი ვერ მოიძებნა classpath-ში!");
                return;
            }

            URI resourceUri = resourceUrl.toURI();
            Path sourcePath = Paths.get(resourceUri);

            if (Files.isDirectory(sourcePath)) {
                LOGGER.info("კოპირება IDE რეჟიმში: resources/fs → " + OUT_DIR.getAbsolutePath());
                FileUtils.copyDirectory(sourcePath.toFile(), OUT_DIR);
            } else {

                LOGGER.info("ამოღება JAR-დან: fs → " + OUT_DIR.getAbsolutePath());
                extractFsFromJar();
            }

            LOGGER.info("fs წარმატებით გადატანილია: " + OUT_DIR.getAbsolutePath());

        } catch (Exception e) {
            LOGGER.info("შეცდომა fs ფოლდერის ამოღებისას: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void extractFsFromJar() {
        try {
            String jarPath = ResourceCreator.class
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI()
                    .getPath();

            try (JarFile jarFile = new JarFile(jarPath)) {
                java.util.Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String entryName = entry.getName();

                    if (entryName.startsWith("fs/") && !entry.isDirectory()) {
                        String relativePath = entryName.substring("fs/".length());
                        File destFile = new File(OUT_DIR, relativePath);
                        FileUtils.forceMkdirParent(destFile);
                        try (InputStream in = jarFile.getInputStream(entry)) {
                            FileUtils.copyInputStreamToFile(in, destFile);
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}