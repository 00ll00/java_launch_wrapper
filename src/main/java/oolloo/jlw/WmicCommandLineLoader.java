package oolloo.jlw;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class WmicCommandLineLoader implements CommandLineLoader {

    @Override
    public String load() throws Exception {

        Process ps = Runtime.getRuntime().exec(
                "powershell -command \"(wmic process where processid=$((wmic process where processid=$PID get parentprocessid)[2]) get commandline)[2]\""
        );
        BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
        return br.readLine();
    }

}
