package com.java.p3_f.serversettings.network;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NetMod {
    private String inName;
    private String ip;
    private String gateWay;
    private String subnet;
    private String dns1;
    private String dns2;
    private String metric;
    private boolean isLink;
    private boolean isInternet;
}
