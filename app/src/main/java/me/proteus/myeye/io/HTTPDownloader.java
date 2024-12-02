package me.proteus.myeye.io;

import org.asynchttpclient.*;

import java.io.File;

public class HTTPDownloader {

    private final AsyncHttpClient client;

    public HTTPDownloader() {
        this.client = Dsl.asyncHttpClient(Dsl.config()
                .setFollowRedirect(true));
    }

    public void download(String url, String path) {

        client.prepareGet(url).execute().toCompletableFuture()
                .thenAccept(res -> {
                    System.out.println("Headers:");
                    System.out.println(res.getHeaders());
                })
                .exceptionally(e -> {
                   System.out.println("Exception: " + e.getMessage());
                   return null;
                });

    }

}
