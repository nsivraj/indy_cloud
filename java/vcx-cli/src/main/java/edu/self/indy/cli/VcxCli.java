package edu.self.indy.cli;

import com.evernym.sdk.vcx.LibVcx;

public class VcxCli {
  public static void main(String[] args) throws Exception {
    System.out.println("java.library.path: " + System.getProperty("java.library.path"));
    LibVcx.initByLibraryName("indycloud");
    MakeConnection makeConnection = new MakeConnection();
    makeConnection.execute();
  }
}