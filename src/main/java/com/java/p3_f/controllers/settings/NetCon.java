package com.java.p3_f.controllers.settings;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class NetCon {

    @PostMapping("/network")
    public String postNet(Model m) {
        return "settings/network";
    }

}
