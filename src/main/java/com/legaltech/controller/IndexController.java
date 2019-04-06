package com.legaltech.controller;

import com.legaltech.service.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Indexing controller.
 */
@RestController
public class IndexController {

    /**
     * Indexing service implementation.
     */
    @Autowired
    private IndexService indexServiceImpl;

    /**
     * @return Object that wraps json with error or success
     */
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public Map<String, String> index() {
        Map<String, String> result = new LinkedHashMap<>();

        try {
            indexServiceImpl.index();
            result.put("status", "success");
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", e.getMessage());
        }

        return result;
    }

}
