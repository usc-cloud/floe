package edu.usc.pgroup.floe.util;

import java.io.IOException;
import java.io.OutputStream;

public class NullOutputStream extends OutputStream {

  private boolean closed = false;  
    
  public void write(int b) throws IOException {
    if (closed) throw new IOException("Write to closed stream");
  }
  
  public void write(byte[] data, int offset, int length) 
   throws IOException {
    if (data == null) throw new NullPointerException("data is null");
    if (closed) throw new IOException("Write to closed stream");
  }
  
  public void close() {
    closed = true;   
  }   
}