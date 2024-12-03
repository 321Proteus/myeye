package me.proteus.myeye.io;

import net.lingala.zip4j.ZipFile;

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

        Executors.newSingleThreadExecutor().execute(() -> client
                .prepareGet(url).execute().toCompletableFuture()
                .thenAccept(res -> {
                    System.out.println(res.getStatusCode() + " " + res.getStatusText());

                    new File(path).mkdirs();

//                    new FileSaver(path).getDirectoryTree(new File(path), 1);

                    File zip = new File(path, name + ".zip");

                    try (FileOutputStream fos = new FileOutputStream(zip)) {

                        fos.write(res.getResponseBodyAsBytes());

                        try {
                            new ZipFile(zip).extractAll(path + "/" + name);

                            zip.delete();

                        } catch (IOException e) {
                            System.out.println("beta");
                            throw new RuntimeException(e);
                        }


                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }


                })
                .exceptionally(e -> {
                    System.out.println("Exception: " + e.getMessage());
                    return null;
                }));


    }

}
