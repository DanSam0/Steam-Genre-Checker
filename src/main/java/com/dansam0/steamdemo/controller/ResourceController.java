package com.dansam0.steamdemo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Controller
public class ResourceController {

    @Autowired
    private ResourceLoader resourceLoader;

    @GetMapping("/styles/{code}.css")
    @ResponseBody
    public ResponseEntity<String> getStyle(@PathVariable("code") String code) throws IOException {

        InputStream inputStream = resourceLoader.getResource("classpath:static/css/" + code + ".css").getInputStream();

        if(inputStream == null)
            throw new RuntimeException("Didn't find requested style with name: " + code);

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuffer stringBuffer = new StringBuffer();
        String line = null;
        while ((line = bufferedReader.readLine()) != null){
            stringBuffer.append(line+"\n");
        }

        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "text/css; charset=utf-8");

        return new ResponseEntity<>(stringBuffer.toString(), httpHeaders, HttpStatus.OK);
    }

}
