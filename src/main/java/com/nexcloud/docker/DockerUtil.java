package com.nexcloud.docker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;

public class DockerUtil {
    private static final Logger logger = LoggerFactory.getLogger(DockerUtil.class);

    public static final String API_VERSION = "v1.29";

    public static String procDockerApi(String uri, String method, Map<String, String> headers, String body) {
        ArrayList<String> headerArr = new ArrayList<>();
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                headerArr.add("-H");
                headerArr.add(entry.getKey() + ": " + entry.getValue());
            }
        } else {
            headerArr.add("-H");
            headerArr.add("Content-Type" + ": " + "application/json");
        }

        ArrayList<String> bodyArr = new ArrayList<>();
        if (!StringUtils.isEmpty(body)) {
            bodyArr.add("-d");
            bodyArr.add(body);
        }

        ArrayList<String> commandArr = new ArrayList<>();
        commandArr.add("curl");
        commandArr.add("--unix-socket");
        commandArr.add("/var/run/docker.sock");
        commandArr.add("-X");
        commandArr.add(method);
        commandArr.add("http:/" + API_VERSION + uri);
        commandArr.addAll(headerArr);
        commandArr.addAll(bodyArr);

        String[] command = commandArr.toArray(new String[commandArr.size()]);

        logger.info(String.join(" ", command));

        ProcessBuilder process = new ProcessBuilder(command);
        Process p;
        String result = null;
        try {
            p = process.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append(System.getProperty("line.separator"));
            }
            result = builder.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}