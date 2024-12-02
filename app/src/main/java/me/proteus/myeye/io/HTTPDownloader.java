package me.proteus.myeye.io;

import org.asynchttpclient.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.Executors;

public class HTTPDownloader {

    private final AsyncHttpClient client;

    public HTTPDownloader() {
        this.client = Dsl.asyncHttpClient(Dsl.config()
                .setFollowRedirect(true));
    }

    public void download(String url, String path, String name) {

        Executors.newSingleThreadExecutor().execute(() -> {
            client.prepareGet(url).execute().toCompletableFuture()
                    .thenAccept(res -> {
                        System.out.println(res.getStatusCode() + " " + res.getStatusText());

                        File f = new File(path, name);
                        try (FileOutputStream fos = new FileOutputStream(f)) {

                            fos.write(res.getResponseBodyAsBytes());

                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    })
                    .exceptionally(e -> {
                        System.out.println("Exception: " + e.getMessage());
                        return null;
                    });

        });


    }

}
