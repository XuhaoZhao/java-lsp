package com.aaron.javalsp.controller;


import com.aaron.javalsp.CustomClient;
import com.aaron.javalsp.controller.model.FindReferenceRequestBody;
import org.eclipse.lsp4j.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/v1")
public class HighLevelCodeFunc {

    @Autowired
    private CustomClient customClient;

    @PostMapping("find-reference")
    public List<Location> findReference(@RequestBody FindReferenceRequestBody findReferenceRequestBody){
        return customClient.findReference(findReferenceRequestBody.getPath(), findReferenceRequestBody.getLine(), findReferenceRequestBody.getCharacter());
    }
}
