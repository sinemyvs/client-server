package client;



import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import java.util.Scanner;

public class ClientTest {

    public static void main(String[] args) throws IOException, InterruptedException {
        try (SocketChannel client = SocketChannel.open()) {
            client.connect(new InetSocketAddress("localhost", 3333));
            System.out.println("Client... started");
            Scanner m = new Scanner(System.in);

            String[] messages = new String[3];

            System.out.println("numbers : ");
            messages[0]=m.next();
            messages[1]=m.next();
            messages[2]=m.next();

            for (int i = 0; i < 3; i++) {

                byte[] message = new String(messages[i]).getBytes();
                ByteBuffer buffer = ByteBuffer.wrap(message);
                client.write(buffer);
                System.out.println(messages[i]);

                ByteBuffer buffer2 = ByteBuffer.allocate(1024);
                int numRead = -1;
                numRead = client.read(buffer2);
                byte[] data = new byte[numRead];
                System.arraycopy(buffer2.array(), 0, data, 0, numRead);
                System.out.println("Got: " + new String(data));
                buffer.clear();
                Thread.sleep(5000);
            }

        }
    }

}