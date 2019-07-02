
import java.io.*;
import java.net.*;


/**
 * Created by dan.geabunea on 6/3/2016.
 */
public class udpCLIENT  {



    public static void main(String[] args) {

        int ClientPort = 50003;
        String receiverIP = "10.31.10.2";
        try (DatagramSocket clientSocket = new DatagramSocket(ClientPort)) {

            byte[] buffer0 = new byte[65507];
            byte[] buffer = new byte[1028];
            byte totalByte[]= new byte[35960];  // Bu dosya boyutu kadar olacak
            byte seqNumber[]=new byte[4];
            byte actNo[];
            int gelenPaket = -1;
            boolean first = true;
            String packetNumber = "";
            int packetNumberInt=0;
            // Set a timeout of XXX ms for the client.
            clientSocket.setSoTimeout(15000);
            while (true) {



                    // handshake
                if(first){
                    DatagramPacket datagramPacket0 = new DatagramPacket(buffer, 0, buffer.length);
                    clientSocket.receive(datagramPacket0);
                    String receivedMessage = new String(datagramPacket0.getData());
                    System.out.println(receivedMessage);
                    packetNumber += receivedMessage.charAt(0);
                    packetNumber += receivedMessage.charAt(1);
                    //packetNumberInt = Integer.parseInt(packetNumber);
                    packetNumberInt = Integer.valueOf(packetNumber);
					//packetNumberInt = 34;
					//totalByte = new byte[packetNumberInt*1028];
                    first = false;


                }else{
                    DatagramPacket datagramPacket = new DatagramPacket(buffer, 0, buffer.length);
                    clientSocket.receive(datagramPacket);
                    byte receivedByte[] = datagramPacket.getData();
                    int size = receivedByte.length;


                    int j =-1;
                    for(int i=3;i>-1;i--){
                        j++;
                        seqNumber[j] = receivedByte[receivedByte.length-1-i];
                    }
                    int seqNumberInt = bytesToInt(seqNumber);
                    gelenPaket++;
                    System.out.println("Incoming packet number : "+seqNumberInt);


                    for(int i=0;i<1024;i++){
                        int index = seqNumberInt*1024+i;
                        totalByte[index]=receivedByte[i];
                    }

                    // Acknowledgement
                    DatagramPacket geriBildirim = new DatagramPacket(
                            seqNumber,
                            seqNumber.length,
                            InetAddress.getByName(receiverIP),
                            50004
                    );

                    clientSocket.send(geriBildirim);
                    System.out.println("Outgoing ACK-NO : "+seqNumberInt);



                    if(seqNumberInt==packetNumberInt-2){
                        System.out.println(totalByte.length+" Byte Reached ");
                        File fileNew = new File("C:\\Users\\ensar\\Desktop\\yenigelen.jpg"); // GİVE THE PATH !!!!!!!!!!!!!
                        fileNew.createNewFile();

                        bytesToFile(totalByte,fileNew);
                    }

                }

            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Timeout. Client is closing.");

        }
    }

    public static void bytesToFile(byte[] bytes, File file) {
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(file));
            bos.write(bytes);
            bos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bos != null)
                    bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static int bytesToInt(byte[] bytes) {
        return ((bytes[0] & 0xff) << 24) |
                ((bytes[1] & 0xff) << 16) |
                ((bytes[2] & 0xff) << 8) |
                (bytes[3] & 0xff);
    }
    public static byte[] intToBytes(int number) {
        return new byte[]{
                (byte) ((number >>> 24) & 0xff),
                (byte) ((number >>> 16) & 0xff),
                (byte) ((number >>> 8) & 0xff),
                (byte) (number & 0xff)
        };
    }
}