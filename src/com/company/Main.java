package com.company;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws IOException {
        // write your code here
        Charset charsetD = Charset.forName("UTF-8");

        System.out.print(readFile("/proc/net/tcp", charsetD));
        System.out.println("Default Charset=" + Charset.defaultCharset());


    }

    static String readFile(String path, Charset encoding)
            throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
}