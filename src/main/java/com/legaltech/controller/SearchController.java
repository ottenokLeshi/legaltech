package com.legaltech.controller;

import com.legaltech.model.search.SearchQuery;
import com.legaltech.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Searching controller.
 */
@RestController
public class SearchController {

    /**
     * Searching service implementation.
     */
    @Autowired
    private SearchService searchServiceImpl;

    /**
     * @param query - query
     * @return Object that wraps json
     * @throws Exception - exception
     */
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public Object search(final SearchQuery query) throws Exception {
        return searchServiceImpl.search(query);
    }
}
