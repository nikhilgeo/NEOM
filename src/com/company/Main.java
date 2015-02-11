package com.company;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws IOException {
        // write your code here
        Charset charsetD = Charset.forName("UTF-8");
        String tcpConnections = readFile("/proc/net/tcp", charsetD);
        System.out.print(tcpConnections);
        String tcpConArray[] = tcpConnections.split("\n");
        //totTCPCon = 1 because excluded the header
        for (int tcpConIndex = 1; tcpConIndex < tcpConArray.length; tcpConIndex++) {
            String tcpCon = tcpConArray[tcpConIndex];
            String pattern = "(\\s\\s)+";
            tcpCon = tcpCon.replaceAll(pattern, " ").trim();
            //System.out.println("tcpCon=" + tcpCon);
            String tcpIndividualConn[] = tcpCon.split(" ");
            String local_SocketHEX[] = tcpIndividualConn[1].split(":");
            String local_IP = little_endianIP_todecimal(local_SocketHEX[0]);
            String local_port = hexPort_todecimal(local_SocketHEX[1]);

            String rem_addressHEX[] = tcpIndividualConn[2].split(":");
            String rem_IP = little_endianIP_todecimal(rem_addressHEX[0]);
            String rem_port = hexPort_todecimal(rem_addressHEX[1]);

            String conStatus = tcpIndividualConn[3];
            String UID = tcpIndividualConn[7];
            String inode = tcpIndividualConn[9];

            System.out.println(local_IP + " " + local_port + " " + rem_IP + " " + rem_port + " " + inode);
            //System.out.println("tcpConFieldsLength=" + tcpCon);
            //System.out.println("tcpConFieldsLength=" + tcpConFields.length);
            //String  local_addressHEX = tcpConFields[1];

        }
        //System.out.println("eachTcpConnections=" + tcpConArray[1]);
        //System.out.println("Default Charset=" + Charset.defaultCharset());


    }

    private static String hexPort_todecimal(String hexPort) {
        int portNumber = 0;
        portNumber = Integer.parseInt(hexPort, 16);
        return String.valueOf(portNumber);
    }

    static String readFile(String path, Charset encoding)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    static String little_endianIP_todecimal(String little_endian_IP) {
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