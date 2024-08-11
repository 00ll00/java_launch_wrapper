package oolloo.jlw;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class CimCommandLineLoader implements CommandLineLoader {

    @Override
    public String load() throws Exception {

        Process ps = Runtime.getRuntime().exec(
                "powershell -command \"(Get-CimInstance -classname win32_process -filter \"processid=$((Get-CimInstance -classname win32_process -filter \"processid=$PID\").parentprocessid)\").commandline\""
        );
        BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
        return br.readLine();
    }

}
