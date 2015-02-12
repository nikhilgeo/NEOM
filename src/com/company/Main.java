package com.company;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main {
    static String tcpConnections, tcpCon, pattern;
    static String rem_IP, rem_port, local_IP, local_port, UID, inode, tcpConStatus, timestamp;
    static int conStatusCode;

    static enum tcp_status { /* As defined in ./include/net/tcp_states.h */
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
        // write your code here
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dt = new Date();
        System.out.println("Time: " + dateFormat.format(dt));

        Charset charsetD = Charset.forName("UTF-8");
        tcpConnections = readFile("/proc/net/tcp", charsetD);
        timestamp = dateFormat.format(dt);
        processFile(tcpConnections, timestamp);


        System.out.println("Time: " + dateFormat.format(dt));


        //System.out.println("eachTcpConnections=" + tcpConArray[1]);
        //System.out.println("Default Charset=" + Charset.defaultCharset());


    }

    private static void processFile(String Connections, String timestamp) {
        System.out.print(Connections);
        String tcpConArray[] = Connections.split("\n"); //Split to line by line
        //totTCPCon = 1 because excluded the header
        //Loop through each line
        System.out.println("After processing");

        for (int tcpConIndex = 1; tcpConIndex < tcpConArray.length; tcpConIndex++) {
            tcpCon = tcpConArray[tcpConIndex];
            pattern = "(\\s\\s)+"; // reg pattern to match two or more spaces
            tcpCon = tcpCon.replaceAll(pattern, " ").trim(); //replace all multiple spaces
            //System.out.println("tcpCon=" + tcpCon);
            String tcpIndividualConn[] = tcpCon.split(" "); //split each field by space

            String local_SocketHEX[] = tcpIndividualConn[1].split(":"); //split Local IP:Port
            local_IP = little_endianIP_to_decimal(local_SocketHEX[0]);
            local_port = hex_to_decimal(local_SocketHEX[1]);

            String rem_addressHEX[] = tcpIndividualConn[2].split(":"); //split Remote IP:Port
            rem_IP = little_endianIP_to_decimal(rem_addressHEX[0]);
            rem_port = hex_to_decimal(rem_addressHEX[1]);

            conStatusCode = Integer.parseInt(hex_to_decimal(tcpIndividualConn[3]));

            //Not optimized | performance degradation
            //as arrays are mutable, values() must return a copy of the array of elements just in case you happen to change it.
            // Creating this copy each time is relatively expensive
            tcpConStatus = tcp_status.values()[conStatusCode].toString();

            UID = tcpIndividualConn[7];
            inode = tcpIndividualConn[9];

            System.out.println("timestamp" + " " + "local_IP" + " " + "local_port" + " " + "rem_IP" + " " + "rem_port" + " " + "tcpConStatus");

            System.out.println(timestamp + "|" + local_IP + "|" + local_port + "|" + rem_IP + "|" + rem_port + "|" + tcpConStatus);
            //System.out.println("tcpConFieldsLength=" + tcpCon);
            //System.out.println("tcpConFieldsLength=" + tcpConFields.length);
            //String  local_addressHEX = tcpConFields[1];
        }
    }


    static String readFile(String path, Charset encoding)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    private static String hex_to_decimal(String hex) {
        int decimal;
        decimal = Integer.parseInt(hex, 16);
        return String.valueOf(decimal);
    }

    static String little_endianIP_to_decimal(String little_endian_IP) {
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
            reversed_IP = reversed_IP.append(":");
        }
        IPAddr = reversed_IP.toString();
        return IPAddr.substring(0, IPAddr.length() - 1);
    }
}