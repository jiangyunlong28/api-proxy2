package com.hnttg.cibcredit.api.service.http.common;

import org.apache.http.conn.HttpClientConnectionManager;

import java.util.concurrent.TimeUnit;

public class IdleConnectionCleanThread extends Thread {

    private final HttpClientConnectionManager connMgr;
    private volatile boolean shutdown;

    public IdleConnectionCleanThread(HttpClientConnectionManager connMgr) {
        super();
        this.setName("idle-connection-monitor");
        this.setDaemon(true);
        this.connMgr = connMgr;
        this.start();
    }

    @Override
    public void run() {
        try {
            while (!shutdown) {
                synchronized (this) {
                    wait(15000);
                    // Close expired connections
                    this.connMgr.closeExpiredConnections();
                    // Close connections that have been idle longer than 30 sec
                    this.connMgr.closeIdleConnections(30, TimeUnit.SECONDS);
                }
            }
        } catch (InterruptedException ex) {
            // terminate
        }
    }

    public void shutdown() {
        synchronized (this) {
            shutdown = true;
            notifyAll();
        }
    }

}
