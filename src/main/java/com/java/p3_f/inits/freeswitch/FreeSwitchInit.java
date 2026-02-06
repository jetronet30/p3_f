package com.java.p3_f.inits.freeswitch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

@Component
public class FreeSwitchInit implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(FreeSwitchInit.class);

    private static final String FS_BIN_PATH = "/usr/local/freeswitch/bin/freeswitch";
    private static final String SRC_DIR = "/usr/local/src";

    @Override
    public void run(String... args) throws Exception {
        LOGGER.info("-- FreeSWITCH initialization started --");

        if (isFreeSwitchInstalled()) {
            LOGGER.info("FreeSWITCH already installed (found {}). Skipping installation.", FS_BIN_PATH);
            return;
        }

        LOGGER.warn("FreeSWITCH not found. Starting source build and installation. This may take a long time!");
        try {
            executeInstallationCommands();
        } catch (Exception e) {
            LOGGER.error("FreeSWITCH installation failed: {}", e.getMessage());
            throw e;
        }

        createService();
        LOGGER.info("FreeSWITCH installation completed.");
    }

    private boolean isFreeSwitchInstalled() {
        return new File(FS_BIN_PATH).exists() && new File(FS_BIN_PATH).canExecute();
    }

    private void executeInstallationCommands() throws IOException, InterruptedException {
        // ყველა კომანდა სიაში
        List<String[]> commands = Arrays.asList(
                // spandsp
                new String[] { "cd", SRC_DIR + "/spandsp" },
                new String[] { "./bootstrap.sh" },
                new String[] { "./configure" },
                new String[] { "make" },
                new String[] { "make", "install" },
                new String[] { "ldconfig" },

                // sofia-sip
                new String[] { "cd", SRC_DIR + "/sofia-sip" },
                new String[] { "./autogen.sh" },
                new String[] { "./configure" },
                new String[] { "make" },
                new String[] { "make", "install" },
                new String[] { "ldconfig" },

                // freeswitch
                new String[] { "cd", SRC_DIR + "/freeswitch" },
                new String[] { "./bootstrap.sh", "-j" },
                
                new String[] { "sed", "-i", "s/^applications\\/mod_signalwire/#applications\\/mod_signalwire/", "modules.conf" },
                new String[] { "sed", "-i", "s/^applications\\/mod_signalwire_consumer/#applications\\/mod_signalwire_consumer/", "modules.conf" },
                new String[] { "sed", "-i", "s/^applications\\/mod_signalwire_transcribe/#applications\\/mod_signalwire_transcribe/", "modules.conf" },
                new String[] { "sed", "-i", "s/^applications\\/mod_verto/#applications\\/mod_verto/", "modules.conf" },

                new String[] { "./configure" },
                new String[] { "make" },
                new String[] { "make", "install" },
                new String[] { "make", "cd-sounds-install" },
                new String[] { "make", "cd-moh-install" },

                // მომხმარებლის და ჯგუფის შექმნა შემოწმებით (რომ არ მოხდეს RuntimeException თუ უკვე არსებობს)
                new String[] { "sh", "-c", "getent group freeswitch || groupadd freeswitch" },
                new String[] { "sh", "-c", "getent passwd freeswitch || useradd -r -g freeswitch -d /usr/local/freeswitch freeswitch" },
                new String[] { "chown", "-R", "freeswitch:freeswitch", "/usr/local/freeswitch" });

        ProcessBuilder pb = new ProcessBuilder();
        pb.inheritIO(); 

        for (String[] cmd : commands) {
            if (cmd[0].equals("cd")) {
                pb.directory(new File(cmd[1]));
                continue;
            }

            LOGGER.info("Executing: {}", String.join(" ", cmd));
            pb.command(cmd);
            Process process = pb.start();
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                LOGGER.error("Command failed with exit code {}: {}", exitCode, String.join(" ", cmd));
                throw new RuntimeException("FreeSWITCH installation failed at: " + String.join(" ", cmd));
            }
        }
    }

    private void createService() {
        Path servicePath = Path.of("/etc/systemd/system/freeswitch.service");

        if (Files.exists(servicePath)) {
            LOGGER.info("freeswitch.service უკვე არსებობს");
            return;
        }

        String serviceContent = """
                [Unit]
                Description=FreeSWITCH
                After=network.target

                [Service]
                Type=forking
                Environment="DAEMON_OPTS=-nonat"
                WorkingDirectory=/usr/local/freeswitch/bin
                User=freeswitch
                Group=freeswitch
                ExecStartPre=/bin/sleep 20
                ExecStart=/usr/local/freeswitch/bin/freeswitch -ncwait
                ExecStop=/usr/local/freeswitch/bin/freeswitch -stop
                Restart=always
                LimitNOFILE=100000

                [Install]
                WantedBy=multi-user.target
                """;
        try {
            Files.writeString(
                    servicePath,
                    serviceContent,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.WRITE);

            LOGGER.info("freeswitch.service წარმატებით შეიქმნა");
            
            // სერვისის შექმნის შემდეგ systemd-ს დარეფრეშება
            new ProcessBuilder("systemctl", "daemon-reload").start().waitFor();
            new ProcessBuilder("systemctl", "enable", "freeswitch").start().waitFor();
            new ProcessBuilder("systemctl", "start", "freeswitch").start().waitFor();
            LOGGER.info("freeswitch.service ჩართულია და ავტომატურად იწყება სისტემის ჩართვისას");
            
        } catch (Exception e) {
            LOGGER.error("შეცდომა freeswitch.service შექმნისას: {}", e.getMessage());
        }
    }
}
