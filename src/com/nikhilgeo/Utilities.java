package com.nikhilgeo;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by nikhil on 13/2/15.
 */
public class Utilities {

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
}
