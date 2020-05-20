package com.example.websocketchat.runner;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CmdRunner {
    private static Logger logger = Logger.getLogger(CmdRunner.class);
//    private static String command = "cd C:\\Users\\Prabu.Ayyappan\\RAM\\Projects\\dashboard\\plugin-tests\\serenity-cucumber4-test && mvn verify";
    private static String command = "ipconfig";
    public String runCMD(String command) {
        Runtime runtime = Runtime.getRuntime();
        this.command = command;
        String commandArray[] = {"cmd", "/c", this.command};
        /*message.setFrom("client");
        message.setTo("server");
        message.setMessage("");*/
        try {
            Process exec = runtime.exec(commandArray);
            OutputStream outputStream = exec.getOutputStream();

            logger.info("------------------Start-------------------");
            logger.info("execution.isAlive: "+exec.isAlive());
            BufferedReader reader;
            do {
                reader = new BufferedReader(new InputStreamReader(exec.getInputStream()));
            }while (exec.isAlive());
            String result = "";
            List<String> resultList = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                resultList.add(line);
            }
            result = resultList.stream().collect(Collectors.joining(String.format("%n")));
//            message.setMessage(result);
            reader.close();
            logger.info("execution.isAlive: "+exec.isAlive());
            logger.info("------------------End-------------------");
//            stompSession.send(url, new ObjectMapper().writeValueAsString(message));
//            stompSession.send(url, message);
//            Gson gson = new Gson();
//            result = gson.toJson(message);
//            String resultMessage = String.format("{\"from\":\"anji\",\"to\":\"name\",\"message\":\"%s\"}", result);
//            stompSession.send(url, resultMessage.getBytes());
            /*stompSession.send(url, gson.toJson(message).getBytes());*/
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
