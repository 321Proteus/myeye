package me.proteus.myeye.io;

import org.asynchttpclient.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import io.netty.handler.codec.http.HttpHeaders;

public class HTTPDownloader {

    private final AsyncHttpClient client;

    public HTTPDownloader() {
        this.client = Dsl.asyncHttpClient(Dsl.config()
                .setFollowRedirect(true));
    }

    public CompletableFuture<Void> download(String url, File output) {

        CompletableFuture<Void> promise = new CompletableFuture<>();

        File path = new File(output.getParent());

        if (!path.mkdirs() && !path.exists()) {
            throw new RuntimeException("Sciezka do katalogu nie istnieje i nie mogla zostac utworzona");
        }

        try (FileOutputStream fos = new FileOutputStream(output)) {

            client.prepareGet(url).execute(new AsyncHandler<Void>() {
                private long total = 0;
                private long downloaded = 0;

                @Override
                public State onStatusReceived(HttpResponseStatus responseStatus) throws Exception {
                    System.out.println("Status: " + responseStatus.getStatusCode());
                    return State.CONTINUE;
                }

                @Override
                public State onHeadersReceived(HttpHeaders headers) throws Exception {
                    if (headers.contains("Content-Length")) {
                        total = Long.parseLong(headers.get("Content-Length"));
                        System.out.println("Do pobrania: " + total);
                    }
                    return State.CONTINUE;
                }

                @Override
                public State onBodyPartReceived(HttpResponseBodyPart bodyPart) throws Exception {
                    fos.write(bodyPart.getBodyPartBytes());
                    downloaded = bodyPart.length();
                    System.out.println("Pobrano: " + fos.getChannel().size() + " bajtow");
                    return State.CONTINUE;
                }

                @Override
                public void onThrowable(Throwable t) {
                    promise.completeExceptionally(t);
                }

                @Override
                public Void onCompleted() throws Exception {
                    fos.close();
                    promise.complete(null);
                    return null;
                }

            });

            return promise;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

}
