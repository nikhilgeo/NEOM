package com.nikhilgeo;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by nikhil on 13/2/15.
 */
public class Inode_uid_process_Maping {

    /**
     * To Do List:
     * make get_pid_inode_processName a thread
     * Update hastable peridically
     * Optimization: There are pid with empty inode numbers -- Handle it/not > will remove empty inode in hashtable
     */
    private Utilities utilities;
    // Hashtable<indode, [process_name, pid]>
    // Static so that all the objects will share a single copy of hash table
    public static Hashtable<String, List<String>> inode_pid_pname_mapping = new Hashtable<String, List<String>>();

    /**
     * Filer for DirectoryStream Iterator to return directory only
     */
    private static class DirectoriesFilter implements DirectoryStream.Filter<Path> {
        @Override
        public boolean accept(Path entry) throws IOException {
            return Files.isDirectory(entry);
        }
    }

    /**
     * Get all the pid folders in the /proc
     * Invoke get_ProcessName() to find process name corresponding to pid
     * Invoke get_inode() to find all the inode numbers related to pid
     */
    public void get_pid_inode_processName() {

        String regex = "\\d+", pid_DirName, pid, processName;
        List<String> inodes_of_a_process;
        Path path = Paths.get("/proc");
        try {
            DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path, new DirectoriesFilter());

            //Find inodes and process_name for each pid
            for (Path p : directoryStream) {  // Will return only directory, no files: Ref DirectoriesFilter
                pid_DirName = p.getFileName().toString();
                if (pid_DirName.matches(regex)) // To select the pid/numerical folders only
                {
                    //System.out.println(p.getFileName());
                    pid = p.getFileName().toString();
                    processName = get_ProcessName(pid_DirName);
                    inodes_of_a_process = get_inode(pid_DirName);
                    add_inode_pid_pname_mapping(inodes_of_a_process, processName, pid); // Update HashTable: Is HashMap better ?
                }
            }
            //print_HashTable_getInode_pid_pname_mapping();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * get the all the inode numbers corresponding to a process.
     * ls -l /proc/pid/fd
     *
     * @param pid_dirName
     * @return
     */
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
                    //System.out.println(inode_link_target); // only inode link targets will be printed

                    //Alternative method: Use regex: TBD
                    //http://stackoverflow.com/questions/4662215/how-to-extract-a-substring-using-regex
                    inode = inode_link_target.substring(inode_link_target.indexOf('[') + 1, inode_link_target.indexOf(']'));
                    //System.out.println(inode); // only inode link targets will be printed
                    inodeList.add(inode);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return inodeList;
    }

    /**
     * Function to fetch the process name found in /proc/pid/cmdline file.
     *
     * @param pid_dirName
     * @return process name
     */
    private String get_ProcessName(String pid_dirName) {
        try {
            utilities = new Utilities();
            String cmdlineFile = "/proc/" + pid_dirName + "/cmdline";
            String processName = utilities.readFile_InOneGO(cmdlineFile);
            String processName_split[] = processName.split(" ");
            //System.out.println("Process Name: " + processName_split[0]);
            return processName_split[0];
        } catch (Exception ex) {
            ex.printStackTrace();
            return "Error: Unknown";
        }
    }

    /**
     * Add a new inode - pid - process name mapping into Hash Table
     * Hashtable<inode, List<pid, process_name>>
     *
     * @param inodes_of_a_process
     * @param processName
     * @param pid
     */
    private void add_inode_pid_pname_mapping(List<String> inodes_of_a_process, String processName, String pid) {
        List<String> pid_process = new ArrayList<String>();
        pid_process.add(pid);
        pid_process.add(processName);
        for (String inode : inodes_of_a_process) {
            //System.out.println(inode);
            inode_pid_pname_mapping.put(inode, pid_process);

        }

    }

    /**
     * Lookup an inode for process name and pid in the Hashtable
     *
     * @param inode
     * @return List<processName, pid>
     */
    public List<String> pid_processName_lookup(String inode) {
        List<String> pid_pName = null;
        try {
            pid_pName = inode_pid_pname_mapping.get(inode);
            if (pid_pName == null) {
                pid_pName = new ArrayList<String>();
                pid_pName.add("Unknown");
                pid_pName.add("Unknown");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return pid_pName;
    }

    /**
     * Hash Table enumeration method: Print all the <key,value> pair for DEBUG purpose.
     */
    public void print_HashTable_getInode_pid_pname_mapping() {
        Enumeration key_inode_iterator;
        String key_inode;
        key_inode_iterator = inode_pid_pname_mapping.keys();

        /*List<String> test = new ArrayList<String>();
        test = inode_pid_pname_mapping.get("73369");*/

        System.out.println("----------------The Whole HashTable: Begin-------------");

        while (key_inode_iterator.hasMoreElements()) {
            key_inode = (String) key_inode_iterator.nextElement();
            System.out.println(key_inode + ": " + inode_pid_pname_mapping.get(key_inode));
        }

        System.out.println("----------------The Whole HashTable: End-------------");

        System.out.println("------------Individual Element Lookup for inode(key): " + " ");
        //System.out.println(inode_pid_pname_mapping.get("12059"));
    }
}
