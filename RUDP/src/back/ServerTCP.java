package back;

import java.io.*;
import java.net.*;


public class ServerTCP {

    public static void main(String[] args) throws Exception{
        ServerSocket s = new ServerSocket(43);
        Socket sr = s.accept();
        FileInputStream fileInputStream = new FileInputStream("C:\\Users\\ensar\\Desktop\\ders_term6\\a.jpg");
        byte b[] = new byte[1024];
        fileInputStream.read(b,0,b.length);
        OutputStream outputStream = sr.getOutputStream();




    }



}
