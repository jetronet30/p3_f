package com.java.p3_f.serversettings.basic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasicMod {
    private String language;
    private String timeZone;
    private int listingPort;
    private String listingAddress;

}

