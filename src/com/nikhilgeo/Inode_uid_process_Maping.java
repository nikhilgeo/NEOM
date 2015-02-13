package com.nikhilgeo;

import java.io.IOException;
import java.nio.file.*;

/**
 * Created by nikhil on 13/2/15.
 */
public class Inode_uid_process_Maping {
    /**
     * * To DO **
     */
// create a hashtable with multi value
//read all the dir in /proc pids - Done
//read the cmd in those folder process name - Done
//read the fd ln -s in /proc inode


    private Utilities utilities;

    //Filer for DirectoryStream Iterator
    private static class DirectoriesFilter implements DirectoryStream.Filter<Path> {
        @Override
        public boolean accept(Path entry) throws IOException {
            return Files.isDirectory(entry);
        }
    }

    void getvalues() {

        String regex = "\\d+", pid, processName, inode;
        Path path = Paths.get("/proc");
        Path pid_DirName;
        try {
            DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path, new DirectoriesFilter());
            for (Path p : directoryStream) {
                pid_DirName = p.getFileName();
                if (pid_DirName.toString().matches(regex)) // To match the pid/numerical folders only
                {
                    System.out.println(p.getFileName());
                    getProcessName(pid_DirName);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void getProcessName(Path pid_dirName) {
        utilities = new Utilities();
        String cmdlineFile = pid_dirName.toString() + "/cmdline";
        Path cmdlineFilePath = Paths.get(cmdlineFile);
        String processName = utilities.readFile_InOneGO(cmdlineFilePath)
    }
}
