package it.broke31.filmm.smb;

import java.net.MalformedURLException;
import java.util.ArrayList;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbAuthException;
import jcifs.smb.SmbException;


public interface ISamba{
    String getPath();

    ArrayList<String> readListString() throws SmbException;


    String getParent();

        ISamba nextChildren(String children, NtlmPasswordAuthentication auth) throws MalformedURLException, SmbAuthException;

    boolean isFolder(String name) throws MalformedURLException, SmbException;

    NtlmPasswordAuthentication getAuth();

    boolean isProtected(String name);

    long lenghtOfFile(String title) throws MalformedURLException, SmbException;

}
