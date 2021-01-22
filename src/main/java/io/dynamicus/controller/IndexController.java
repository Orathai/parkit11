package io.dynamicus.controller;

import io.dynamicus.model.OpenIdConnectUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @RequestMapping("/index")
    public String index() {
        return "Welcome!";
    }

    @GetMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getUser() {
        OpenIdConnectUserDetails user = (OpenIdConnectUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        logger.debug("user: " + user.getName());
        return "Welcome : " + user.getName();
    }

}
