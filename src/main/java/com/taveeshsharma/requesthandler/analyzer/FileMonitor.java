package com.taveeshsharma.requesthandler.analyzer;

import org.apache.commons.vfs2.*;
import org.apache.commons.vfs2.impl.DefaultFileMonitor;

import java.util.HashSet;
import java.util.Set;

public class FileMonitor {

    private String hostName;
    private int numRetryConnect;
    private int millisecondsTillRetryConnect;
    private int fileMonitorDelay;
    
    private FileObject file;
    private final Set<FileObject> createdFileObject;
    private Set<FileObject> pendingAnalysisFiles;
    private DefaultFileMonitor defaultFileMonitor;

    public FileMonitor(String hostName, int numRetryConnect,
                       int millisecondsTillRetryConnect, int fileMonitorDelay) {
        this.hostName = hostName;
        this.numRetryConnect = numRetryConnect;
        this.millisecondsTillRetryConnect = millisecondsTillRetryConnect;
        this.fileMonitorDelay = fileMonitorDelay;
        this.defaultFileMonitor = new DefaultFileMonitor(new RemoteFileListener());
        this.createdFileObject = new HashSet<>();
        this.pendingAnalysisFiles = null; //we will sotre the things we are analyzing into this set.
    }


    private class RemoteFileListener implements FileListener {

        @Override
        public void fileCreated(FileChangeEvent fileChangeEvent) throws Exception {
            //first check if the file is not
            getAllFiles(fileChangeEvent.getFile());
        }

        @Override
        public void fileDeleted(FileChangeEvent fileChangeEvent) throws Exception {
            synchronized (createdFileObject) {
                createdFileObject.remove(fileChangeEvent.getFile());
            }
        }

        @Override
        public void fileChanged(FileChangeEvent fileChangeEvent) throws Exception {
            //we don't expcet pcap files to change
        }

        /***
         * This just checks if the filename ends with pcap or pcapdump just to ensure that it is a pcap file. A more rigorous checkcan be implemented.
         * @param fileName
         * @return true if it is a pcap file and false if not
         */
        private boolean isValidFile(String fileName) {
            return fileName.endsWith("pcap");
        }

        /**
         * This method will get all pcap file and pcapdump files from the file. If the file is folder we recurse in all the folders within and get all the files
         * Might want to change this method so it is iterative and not recursive.
         *
         * @param fileObject
         * @throws FileSystemException
         */
        private void getAllFiles(FileObject fileObject) throws FileSystemException {
            synchronized (createdFileObject) {
                if (fileObject.isFile() && isValidFile(fileObject.getPublicURIString())) {
                    createdFileObject.add(fileObject);
                } else if (fileObject.isFolder()) {
                    for (FileObject file : fileObject.getChildren()) {
                        getAllFiles(file);
                    }
                }
            }
        }
    }

    /**
     * Method to start the FileMonitor and also for initial connection to the fileServer.
     * @return true if the start was successful and false if not.
     */
    public boolean start() {
        int count = 0;
        while (!connect() && ++count < this.numRetryConnect) {//we will attempt to connect three times before we officially stop
            try {
                Thread.sleep(this.millisecondsTillRetryConnect);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (count == numRetryConnect - 1)
                return false;
        }
        defaultFileMonitor.addFile(file);
        defaultFileMonitor.setDelay(this.fileMonitorDelay);
        defaultFileMonitor.start();
        return true;
    }

    /**
     * Method to connect to the server so as to get the files.
     * @return true i the connection was successful and false otherwise.
     */
    public boolean connect() {
        try {
            file = VFS.getManager().resolveFile(hostName);
            return true;
        } catch (FileSystemException e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * This method returns set of FileObjects that have been created since the last time we analyzed files successfully.
     * @return Set of FileObjects to be analyzed.
     */
    public Set<FileObject> getCreatedFiles() {
        synchronized (createdFileObject) {
            //here we first want to copy all this into the pending set so any new files created after will be dealt with separately.
            if(pendingAnalysisFiles==null)//create new object if pending is null
                pendingAnalysisFiles = new HashSet<>();
            pendingAnalysisFiles.addAll(createdFileObject);
            createdFileObject.clear();
        }
        return pendingAnalysisFiles;
    }

    /***
     * This method is called by the analyzer after it finishes analyzing the pcap files.
     * The actions to be taken then are to clear the pending Set
     */
    public void doneProcessing() {
        pendingAnalysisFiles.clear();
    }

}
