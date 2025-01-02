package com.aaron.javalsp;

import java.io.IOException;

public interface ProcessInterface {

    void send(String data) throws IOException;

    String receive() throws IOException;
}
