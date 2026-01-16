package com.java.p3_f.inits.freeswitch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
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
        executeInstallationCommands();
        LOGGER.info("FreeSWITCH installation completed.");
    }

    private boolean isFreeSwitchInstalled() {
        return new File(FS_BIN_PATH).exists() && new File(FS_BIN_PATH).canExecute();
    }

    private void executeInstallationCommands() throws IOException, InterruptedException {
        // ყველა კომანდა სიაში
        List<String[]> commands = Arrays.asList(
                // spandsp
                new String[]{"cd", SRC_DIR + "/spandsp"},
                new String[]{"./bootstrap.sh"},
                new String[]{"./configure"},
                new String[]{"make"},
                new String[]{"make", "install"},
                new String[]{"ldconfig"},

                // sofia-sip
                new String[]{"cd", SRC_DIR + "/sofia-sip"},
                new String[]{"./autogen.sh"},
                new String[]{"./configure"},
                new String[]{"make"},
                new String[]{"make", "install"},
                new String[]{"ldconfig"},

                // freeswitch
                new String[]{"cd", SRC_DIR+"/freeswitch"},
                new String[]{"./bootstrap.sh", "-j"},
                // modules.conf რედაქტირება (nano-ს ავტომატიზაცია რთულია, აქ კომენტარად ვტოვებთ ხელით)
                // შენიშვნა: nano-ს ავტომატურად გაშვება შესაძლებელია sed-ით:
                new String[]{"sed", "-i", "s/^applications\\/mod_signalwire/#applications\\/mod_signalwire/", "modules.conf"},
                new String[]{"sed", "-i", "s/^applications\\/mod_signalwire_consumer/#applications\\/mod_signalwire_consumer/", "modules.conf"},
                new String[]{"sed", "-i", "s/^applications\\/mod_signalwire_transcribe/#applications\\/mod_signalwire_transcribe/", "modules.conf"},
                new String[]{"sed", "-i", "s/^applications\\/mod_verto/#applications\\/mod_verto/", "modules.conf"},

                new String[]{"./configure"},
                new String[]{"make"},
                new String[]{"make", "install"},
                new String[]{"make", "cd-sounds-install"},
                new String[]{"make", "cd-moh-install"},

                // user/group
                new String[]{"groupadd", "freeswitch"},
                new String[]{"useradd", "-r", "-g", "freeswitch", "-d", "/usr/local/freeswitch", "freeswitch"},
                new String[]{"chown", "-R", "freeswitch:freeswitch", "/usr/local/freeswitch"}
        );

        ProcessBuilder pb = new ProcessBuilder();
        pb.inheritIO(); // ლოგები კონსოლში გამოვა

        for (String[] cmd : commands) {
            if (cmd[0].equals("cd")) {
                // cd არ არის external command, მაგრამ ProcessBuilder-ში directory ვაყენებთ
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
}