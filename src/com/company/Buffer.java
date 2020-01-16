package com.company;

import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class Buffer {
    public static void main(String[] args){
        ByteBuffer bb = ByteBuffer.allocate(10);
        Charset charset = Charset.forName("utf-8");
    }
}
