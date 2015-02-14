package com.nikhilgeo;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

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
    public Hashtable inode_pid_pname_mapping = new Hashtable();

    //Filer for DirectoryStream Iterator to return directory only
    private static class DirectoriesFilter implements DirectoryStream.Filter<Path> {
        @Override
        public boolean accept(Path entry) throws IOException {
            return Files.isDirectory(entry);
        }
    }

    public void get_pid_inode_processName() {

        String regex = "\\d+", pid_DirName, pid, processName;
        List<String> inodes_of_a_process = new ArrayList<String>();
        Path path = Paths.get("/proc");
        try {
            DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path, new DirectoriesFilter());

            //Find inodes and process_name for each pid
            for (Path p : directoryStream) {  // Will return only directory, no files: Ref DirectoriesFilter
                pid_DirName = p.getFileName().toString();
                if (pid_DirName.matches(regex)) // To select the pid/numerical folders only
                {
                    System.out.println(p.getFileName());
                    pid = p.getFileName().toString();
                    processName = get_ProcessName(pid_DirName);
                    inodes_of_a_process = get_inode(pid_DirName);
                    update_inode_pid_pname_mapping(inodes_of_a_process, processName, pid); // Update HashTable: Is HashMap better ?
                    System.out.println(inode_pid_pname_mapping.get("37017"));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void update_inode_pid_pname_mapping(List<String> inodes_of_a_process, String processName, String pid) {
       List<String> pid_process = new ArrayList<String>();
        pid_process.add(processName);
        pid_process.add(pid);
        for (String inode : inodes_of_a_process) {
            // Code for adding the the values in the hashtable: TBD
            //System.out.println(inode);
            inode_pid_pname_mapping.put(inode,pid_process);

        }

    }

    private List<String> get_inode(String pid_dirName) {
        //readSymbolicLink(Path link)
        List<String> inodeList = new ArrayList<String>();


        Path inode_link_target_path;
        String inode_link_target, inode;
        String socket_regex = "socket:\\[\\d+\\]"; //socket:[92942]
        String pipe_regex = "pipe:\\[\\d+\\]"; //pipe:[92942] // Might not need : TBD
        try {
            Path path = Paths.get("/proc/" + pid_dirName + "/fd");
            DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path);

            for (Path p : directoryStream) {

                inode_link_target_path = Files.readSymbolicLink(p);
                inode_link_target = inode_link_target_path.toString();

                // To check if the link target is socket and pipe inode numbers
                // pipe:[92942] possibly can be removed: double check :TBD
                if (inode_link_target.matches(socket_regex) || inode_link_target.matches(pipe_regex)) {
                    //System.out.println(p.getFileName());
                    System.out.println(inode_link_target); // only inode link targets will be printed

                    //Alternative method: Use regex: TBD
                    //http://stackoverflow.com/questions/4662215/how-to-extract-a-substring-using-regex
                    inode = inode_link_target.substring(inode_link_target.indexOf('[') + 1, inode_link_target.indexOf(']'));
                    System.out.println(inode); // only inode link targets will be printed
                    inodeList.add(inode);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return inodeList;
    }

    private String get_ProcessName(String pid_dirName) {
        try {
            utilities = new Utilities();
            String cmdlineFile = "/proc/" + pid_dirName + "/cmdline";
            String processName = utilities.readFile_InOneGO(cmdlineFile);
            String processName_split[] = processName.split(" ");
            System.out.println("Process Name: " + processName_split[0]);
            return processName_split[0];
        } catch (Exception ex) {
            ex.printStackTrace();
            return "Error: Unknown";
        }
    }

}
