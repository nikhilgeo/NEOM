package com.nikhilgeo;

import jdk.nashorn.internal.ir.CatchNode;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nikhil on 13/2/15.
 */
public class Utilities {

    /**
     * TO DO: Find the name of interfaces only one time, put it in some static
     *
     * @param path
     * @return
     */
    public String readFile_InOneGO(String path) {
        String fileContents = "";
        Charset encoding = Charset.forName("UTF-8");
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(path));
            return new String(encoded, encoding);
        } catch (Exception ex) {
            System.out.println("Error in Utilities.readFile_InOneGO : " + ex.getMessage());
        }
        return fileContents;
    }

    public String hex_to_decimal(String hex) {
        int decimal;
        decimal = Integer.parseInt(hex, 16);
        return String.valueOf(decimal);
    }

    public String little_endianIP_to_decimal(String little_endian_IP) {
        //System.out.println("little_endian_IP =" + little_endian_IP);
        String octect, IPAddr;
        int decimal;
        StringBuilder reversed_IP = new StringBuilder("");

        for (int index = little_endian_IP.length() - 1; index >= 0; index = index - 2) {
            octect = little_endian_IP.substring(index - 1, index + 1);
            //System.out.println("octect" + octect);
            decimal = Integer.parseInt(octect, 16);
            //System.out.println("decimal" + decimal);
            reversed_IP = reversed_IP.append(decimal);
            reversed_IP = reversed_IP.append(".");
        }
        IPAddr = reversed_IP.toString();
        return IPAddr.substring(0, IPAddr.length() - 1);
    }

    public String get_protocol_name(String port) {
        try {
            int port_number;
            port_number = Integer.valueOf(port);
            switch (port_number) {
                case 20:
                case 21:
                    return "FTP";
                case 22:
                    return "SSH";
                case 25:
                    return "SMTP";
                case 53:
                    return "DNS";
                case 80:
                    return "HTTP";
                case 110:
                    return "POP3";
                case 143:
                    return "IMAP";
                case 443:
                    return "HTTPS";
                default:
                    return port;
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return port;
        }
    }

    public ArrayList<NW_Interfaces> get_data_transfer(String pid) {
        String dev_data;
        String interface_details[];
        String interface_details_row[];
        int interface_count;
        ArrayList<NW_Interfaces> nw_interface_list = new ArrayList<NW_Interfaces>();
        NW_Interfaces nw_interfaces;
        try {
            dev_data = readFile_InOneGO("/proc/" + pid + "/net/dev");
            interface_details = dev_data.split("\n");
            interface_count = interface_details.length;
            for (int interface_index = 2; interface_index < interface_count; interface_index++) {
                interface_details_row = interface_details[interface_index].trim().split("\\s+");
//                System.out.println(interface_details_row[0]);
//                System.out.println(interface_details_row[1]);
//                System.out.println(interface_details_row[9]);
                nw_interfaces = new NW_Interfaces();
                nw_interfaces.setInterface_Name(interface_details_row[0]);
                nw_interfaces.setReceived_bytes(interface_details_row[1]);
                nw_interfaces.setTransmitted_bytes(interface_details_row[9]);
                nw_interface_list.add(nw_interfaces);
            }
            return nw_interface_list;
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }

        return null;
    }
}
