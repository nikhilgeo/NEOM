package com.nikhilgeo;

import java.util.List;

/**
 * Created by root on 18/2/15.
 */
public class NW_Interfaces {
    private String interface_Name, received_bytes, transmitted_bytes;

    public String getInterface_Name() {
        return interface_Name;
    }

    public String getReceived_bytes() {
        return received_bytes;
    }

    public String getTransmitted_bytes() {
        return transmitted_bytes;
    }

    public void setInterface_Name(String interface_Name) {
        this.interface_Name = interface_Name;
    }

    public void setReceived_bytes(String received_bytes) {
        this.received_bytes = received_bytes;
    }

    public void setTransmitted_bytes(String transmitted_bytes) {
        this.transmitted_bytes = transmitted_bytes;
    }
}
