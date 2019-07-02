package back;

import java.io.*;
import java.net.*;


public class ClientTCP {


    public static void main(String[] args) throws  Exception {

        byte b[] = new byte[1024];
        Socket sr = new Socket("localhost",6553);
        InputStream ins = sr.getInputStream();
        FileOutputStream fileOutputStream = new FileOutputStream("C:\\Users\\ahmet\\Desktop\\aaa.txt");

        ins.read(b,0,b.length);
        fileOutputStream.write(b,0,b.length);

    }
}
