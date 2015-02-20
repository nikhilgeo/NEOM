package com.nikhilgeo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nikhil on 18/2/15.
 */
public class NW_Interfaces {

    private Utilities utilities = new Utilities();
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

    /**
     * Read the /proc/pid/dev file
     * Get the interfaces name , data transmitted and received
     * TBD: Skip interface without any data transfer
     *
     * @param pid
     * @return
     */
    public ArrayList<NW_Interfaces> get_data_transfer(String pid) {
        String dev_data;
        String interface_details[];
        String interface_details_row[];
        int interface_count;
        ArrayList<NW_Interfaces> nw_interface_list = new ArrayList<NW_Interfaces>();
        NW_Interfaces nw_interfaces;
        try {
            dev_data = utilities.readFile_InOneGO("/proc/" + pid + "/net/dev");
            interface_details = dev_data.split("\n");
            interface_count = interface_details.length;
            for (int interface_index = 2; interface_index < interface_count; interface_index++) {
                interface_details_row = interface_details[interface_index].trim().split("\\s+");
//                System.out.println(interface_details_row[0]);
//                System.out.println(interface_details_row[1]);
//                System.out.println(interface_details_row[9]);
                //Skipping idle interfaces
                if (!interface_details_row[9].equals("0") || !interface_details_row[1].equals("0")) {
                    nw_interfaces = new NW_Interfaces();
                    nw_interfaces.setInterface_Name(interface_details_row[0]);
                    nw_interfaces.setReceived_bytes(interface_details_row[1]);
                    nw_interfaces.setTransmitted_bytes(interface_details_row[9]);
                    nw_interface_list.add(nw_interfaces);
                }
            }
            return nw_interface_list;
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }

        return null;
    }
}
