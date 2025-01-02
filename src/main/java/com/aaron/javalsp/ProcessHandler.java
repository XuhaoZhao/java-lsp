package com.aaron.javalsp;

import lombok.Setter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

public class ProcessHandler implements ProcessInterface{


    private final OutputStream stdin;
    private final BufferedReader stdout;
    private final ReentrantLock stdinLock = new ReentrantLock();
    private final ReentrantLock stdoutLock = new ReentrantLock();

    public ProcessHandler(Process process) throws IOException {
        this.stdin = process.getOutputStream();
        this.stdout = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
    }

    @Override
    public void send(String data) throws IOException {
        stdinLock.lock();
        try {
            stdin.write(data.getBytes(StandardCharsets.UTF_8));
            stdin.flush();
        } finally {
            stdinLock.unlock();
        }
    }

    @Override
    public String receive(){
        Optional<Integer> contentLength = Optional.empty();
        char[] buffer = new char[1024];

        while (true) {
            stdoutLock.lock();
            try {
                int n = stdout.read(buffer);
                if (n == 0) {
                    Thread.sleep(100);
                    continue;
                }

                String line = new String(buffer, 0, n);
                if (line.trim().isEmpty() && contentLength.isPresent()) {
                    int length = contentLength.get();
                    char[] content = new char[length];
                    stdout.read(content, 0, length);
                    return new String(content);
                } else if (line.startsWith("Content-Length: ")) {
                    contentLength = Optional.of(Integer.parseInt(line.substring("Content-Length: ".length()).trim()));
                }
            }catch (Exception e) {

            }finally {
                stdoutLock.unlock();
            }
        }
    }
}
