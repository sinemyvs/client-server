package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;



public class SocketServer {

    private final int rNumber;

    private Selector selector;
    private Map<SocketChannel, List<byte[]>> dataMapper;
    private InetSocketAddress listenAddress;

    public static void main(String[] args) throws Exception {
        Runnable server = new Runnable() {
            @Override
            public void run() {
                try {
                    new SocketServer("localhost", 3333).startServer();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        new Thread(server).start();

    }

    public SocketServer(String address, int port) throws IOException {
        listenAddress = new InetSocketAddress(address, port);
        dataMapper = new HashMap<SocketChannel, List<byte[]>>();

        rNumber = (int) (Math.random() * 10);
    }


    private void startServer() throws IOException {
        this.selector = Selector.open();
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.socket().bind(listenAddress);
        serverChannel.register(this.selector, SelectionKey.OP_ACCEPT);

        System.out.println("Server started...");

        while (true) {

            this.selector.select();


            Iterator<SelectionKey> keys = this.selector.selectedKeys().iterator();
            while (keys.hasNext()) {
                SelectionKey key = (SelectionKey) keys.next();
                keys.remove();

                if (!key.isValid()) {
                    continue;
                }

                if (key.isAcceptable()) {
                    this.accept(key);
                } else if (key.isReadable()) {
                    this.read(key);
                }
            }
        }
    }


    private void accept(SelectionKey key) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel channel = serverChannel.accept();
        channel.configureBlocking(false);
        Socket socket = channel.socket();
        SocketAddress remoteAddr = socket.getRemoteSocketAddress();
        System.out.println("Connected to: " + remoteAddr);


        dataMapper.put(channel, new ArrayList<byte[]>());
        channel.register(this.selector, SelectionKey.OP_READ);
    }


    private void read(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int numRead = -1;
        numRead = channel.read(buffer);

        if (numRead == -1) {
            this.dataMapper.remove(channel);
            Socket socket = channel.socket();
            SocketAddress remoteAddr = socket.getRemoteSocketAddress();
            System.out.println("Connection closed by client: " + remoteAddr);
            channel.close();
            key.cancel();
            return;
        }

        byte[] data = new byte[numRead];
        System.arraycopy(buffer.array(), 0, data, 0, numRead);
        System.out.println("Got: " + new String(data));
        compareNumber(Integer.parseInt(new String(data)), channel);
    }

    private String compareNumber(int guess, SocketChannel client) throws IOException {

        if (rNumber == guess) {
            byte[] message = new String("Congratulations!").getBytes();
            ByteBuffer buffer = ByteBuffer.wrap(message);
            client.write(buffer);
        } else if (rNumber > guess) {
            byte[] message = new String("Auto Generated Value was " + rNumber + ".Try bigger than " + guess + ".").getBytes();
            ByteBuffer buffer = ByteBuffer.wrap(message);
            client.write(buffer);
        } else {
            byte[] message = new String("Auto Generated Value was " + rNumber + ".Try lesser than " + guess + ".").getBytes();
            ByteBuffer buffer = ByteBuffer.wrap(message);
            client.write(buffer);
        }
        return null;
    }
}