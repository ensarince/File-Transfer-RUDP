
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by dan.geabunea on 6/3/2016.
 */
public class udpSERVER {


    public static void main(String[] args) {
        int ClientPort = 50003;
        String receiverIP = "192.168.1.21";

        try (DatagramSocket serverSocket = new DatagramSocket(50004)) {

            byte bytesFile[];
            byte bytesFileModule[] = new byte[1024];
            byte actNo[]=new byte[1000];

            // Give the path of the file to send
            File file = new File("C:\\Users\\ahmet\\Desktop\\BIM302_Project\\bim209.jpg");
            bytesFile = fileToBytes(file);
            System.out.println(bytesFile.length+" byte ");
            int fileSize = bytesFile.length;
            int numberOfpacket = fileSize/1024+1;
            // handshake
            String fileName = file.getName();
            String handshake = numberOfpacket+" Packet\n"+ "File: "+fileName+"\n"+"File Size:"+fileSize+" byte";

            DatagramPacket handshakePacket = new DatagramPacket(
                    handshake.getBytes(),
                    handshake.length(),
                    InetAddress.getByName(receiverIP),
                    ClientPort
            );
              serverSocket.send(handshakePacket);

            // divide
            System.out.println("Number of Packet : "+numberOfpacket);
            for(int i = 0; i < numberOfpacket-1; i++){ //paketleri geziyor
                for (int j = 0; j < 1024; j++){             // paketi bölüyor.
                    int index = i*1024+j;
                    bytesFileModule[j] = bytesFile[index];
                }
                byte seqNumber[] = intToBytes(i);
                byte sendPacket[];
                sendPacket = makePacket(bytesFileModule,seqNumber);

                DatagramPacket datagramPacket = new DatagramPacket(
                        sendPacket,
                        sendPacket.length,
                        InetAddress.getByName(receiverIP),
                        ClientPort
                );

                serverSocket.send(datagramPacket);
                System.out.println("Outgoing packet "+i+"\n Waiting for ACK...");

                // get acknowledgement  -----------------------------------------------------------


                DatagramPacket actDatagram = new DatagramPacket(actNo, 0, actNo.length);

                    serverSocket.receive(actDatagram);
                    actNo = actDatagram.getData();
                    int actInt = bytesToInt(actNo);
                     // Packet did not reached.
                     // Send again.
                     if(actInt!=i){
                         i--;
                    }
                    System.out.println("Packet "+actInt+" Reached");



               /* try
                {
                    Thread.sleep(100);
                    //  TimeUnit.MINUTES.sleep(1);
                }
                catch (InterruptedException e)
                {
                    Thread.currentThread().interrupt(); // restore interrupted status
                }*/

            }


        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static byte[] fileToBytes(File f) throws IOException{
        InputStream fis = new FileInputStream(f);
        if (f.length() > Integer.MAX_VALUE)
            throw new IOException("File size is too big.");

        ByteArrayOutputStream baos = new ByteArrayOutputStream((int) f.length());
        byte[] buffer = new byte[8 * 1024];

        int bytesRead;
        while ((bytesRead = fis.read(buffer)) != -1) {
            baos.write(buffer, 0, bytesRead);
        }
        fis.close();

        return baos.toByteArray();
    }
    /*
     * Combines given byte arrays into one byte array
     */
    public static byte[] makePacket(byte[]... bytes) {
        int offset = 0;
        int totalLength = 0;
        for (int i = 0; i < bytes.length; i++) {
            totalLength += bytes[i].length;
        }

        byte[] packet = new byte[totalLength];
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i].length <= 0)
                continue;
            System.arraycopy(bytes[i], 0, packet, offset, bytes[i].length);
            offset += bytes[i].length;
        }

        return packet;
    }
    public static byte[] intToBytes(int number) {
        return new byte[]{
                (byte) ((number >>> 24) & 0xff),
                (byte) ((number >>> 16) & 0xff),
                (byte) ((number >>> 8) & 0xff),
                (byte) (number & 0xff)
        };
    }
    public static int bytesToInt(byte[] bytes) {
        return ((bytes[0] & 0xff) << 24) |
                ((bytes[1] & 0xff) << 16) |
                ((bytes[2] & 0xff) << 8) |
                (bytes[3] & 0xff);
    }

}