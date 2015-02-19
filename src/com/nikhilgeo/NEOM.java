package com.nikhilgeo;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NEOM {
    static String tcpConnections, tcpCon, pattern;
    static String rem_IP, rem_port, local_IP, local_port, UID, inode, tcpConStatus, timestamp, pid, processName, protocol;
    static int conStatusCode;
    static Utilities utilities = new Utilities();
    static Inode_uid_process_Maping inode_uid_process_maping = new Inode_uid_process_Maping();

    public static enum tcp_status { /* As defined in ./include/net/tcp_states.h */
        TCP_ESTABLISHED,
        TCP_SYN_SENT,
        TCP_SYN_RECV,
        TCP_FIN_WAIT1,
        TCP_FIN_WAIT2,
        TCP_TIME_WAIT,
        TCP_CLOSE,
        TCP_CLOSE_WAIT,
        TCP_LAST_ACK,
        TCP_LISTEN,
        TCP_CLOSING,    /* Now a valid state */
        TCP_MAX_STATES  /* Leave at the end! */
    }


    public static void main(String[] args) throws IOException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS");
        Date dt = new Date();
        System.out.println("Start Time: " + dateFormat.format(dt));

        tcpConnections = utilities.readFile_InOneGO("/proc/net/tcp");
        if (tcpConnections != "") {
            timestamp = dateFormat.format(dt);
            inode_uid_process_maping.get_pid_inode_processName(); //Initialize the HashTable
            processFile(tcpConnections, timestamp);
        } else {
            System.out.println("No data from File read");
        }
        System.out.println("End Time: " + dateFormat.format(dt));

        //System.out.println("eachTcpConnections=" + tcpConArray[1]);
        //System.out.println("Default Charset=" + Charset.defaultCharset());
    }

    private static void processFile(String Connections, String timestamp) {

        System.out.print(Connections);
        ArrayList<NW_Interfaces> nw_inter_list = new ArrayList<NW_Interfaces>();
        List<String> pid_pname = new ArrayList<String>();
        String tcpConArray[] = Connections.split("\n"); //Split to line by line


        // To print hashtable before lookup, just to make sure hashtable is intact
        inode_uid_process_maping.print_HashTable_getInode_pid_pname_mapping();
        System.out.println("------------------After processing----------");
        System.out.println("timestamp" + "|" + "local_IP" + "|" + "local_port" + "|" + "rem_IP" + "|" + "rem_port" + "|" + "tcpConStatus" + "|" + "inode" + "|" + "pid" + "|" + "processName");

        //totTCPCon = 1 because excluded the header
        //Loop through each line
        for (int tcpConIndex = 1; tcpConIndex < tcpConArray.length; tcpConIndex++) {
            //System.out.println(timestamp + "|" + local_IP + "|" + local_port + "|" + rem_IP + "|" + rem_port + "|" + tcpConStatus + "|" + inode + "|" + UID);
            StringBuilder log_per_con = new StringBuilder(timestamp);
            tcpCon = tcpConArray[tcpConIndex];
             /* Not needed regex based split implemented
            pattern = "(\\s\\s)+"; // reg pattern to match even number of spaces
            tcpCon = tcpCon.replaceAll(pattern, " ").trim(); //replace all even no of spaces with single space
            tcpCon = tcpCon.replaceAll(pattern, " ").trim(); //replace the even no of spaces created coz of above
            //System.out.println("tcpCon=" + tcpCon);
            String tcpIndividualConn[] = tcpCon.split(" "); //split each field by space
            */

            String tcpIndividualConn[] = tcpCon.split("\\s+");

            //System.out.println("Length of tcpIndividualConn= " + tcpIndividualConn.length);
            //System.out.println(timestamp + "|" + local_IP + "|" + local_port + "|" + rem_IP + "|" + rem_port + "|" + tcpConStatus + "|" + inode + "|" + UID);

            String local_SocketHEX[] = tcpIndividualConn[2].split(":"); //split Local IP:Port
            local_IP = utilities.little_endianIP_to_decimal(local_SocketHEX[0]);
            local_port = utilities.hex_to_decimal(local_SocketHEX[1]);

            String rem_addressHEX[] = tcpIndividualConn[3].split(":"); //split Remote IP:Port
            rem_IP = utilities.little_endianIP_to_decimal(rem_addressHEX[0]);
            rem_port = utilities.hex_to_decimal(rem_addressHEX[1]);
            protocol = utilities.get_protocol_name(rem_port);

            conStatusCode = Integer.parseInt(utilities.hex_to_decimal(tcpIndividualConn[4]));

            //Not optimized | performance degradation
            //as arrays are mutable, values() must return a copy of the array of elements just in case you happen to change it.
            // Creating this copy each time is relatively expensive
            tcpConStatus = tcp_status.values()[conStatusCode].toString();

            UID = tcpIndividualConn[8];
            inode = tcpIndividualConn[10];

            if (!inode.equals("0")) { //Some connection will have inode =0 skip them
                //System.out.println("Inode for lookup =" + inode);
                pid_pname = inode_uid_process_maping.pid_processName_lookup(inode); //pid and processname lookup in Hashtable
                //System.out.println(pid_pname);
                if (pid_pname != null) {
                    pid = pid_pname.get(0);
                    //System.out.println("pid" + pid);
                    processName = pid_pname.get(1);
                    //System.out.println("processName" + processName);
                    log_per_con.append("|" + local_IP + "|" + local_port + "|" + rem_IP + "|" + rem_port + "|" + tcpConStatus + "|inode:" + inode + "|pid:" + pid + "|" + processName + "|protocol:" + protocol);
                    nw_inter_list = utilities.get_data_transfer(pid);
                    for (NW_Interfaces item : nw_inter_list) {
                        log_per_con.append("|" + item.getInterface_Name() + "| Recv:" + item.getReceived_bytes() + "| Trns:" + item.getTransmitted_bytes());
                    }

                } //if (pid_pname != null)
                System.out.println(log_per_con.toString());
            } //if(inode == "0")
            //System.out.println("timestamp" + " " + "local_IP" + " " + "local_port" + " " + "rem_IP" + " " + "rem_port" + " " + "tcpConStatus");

            //System.out.println(timestamp + "|" + local_IP + "|" + local_port + "|" + rem_IP + "|" + rem_port + "|" + tcpConStatus + "|" + inode + "|" + pid + "|" + processName + "|" + protocol);
            //System.out.println("tcpConFieldsLength=" + tcpCon);
            //System.out.println("tcpConFieldsLength=" + tcpConFields.length);
            //String  local_addressHEX = tcpConFields[1];
        }
    }


}