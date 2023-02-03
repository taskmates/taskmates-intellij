package me.taskmates.lib.utils;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import org.atmosphere.wasync.*;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("rawtypes")
public class WebSocketUtils {
    @NotNull
    public static DefaultAsyncHttpClient getAsyncHttpClient() {
        DefaultAsyncHttpClientConfig.Builder configBuilder = getConfigBuilder();
        return new DefaultAsyncHttpClient(configBuilder.build());
    }

    public static Client<? extends Options, ? extends OptionsBuilder, ? extends RequestBuilder> getClient() {
        return ClientFactory.getDefault().newClient();
    }

    public static Socket createSocket(Client client, AsyncHttpClient asyncHttpClient) {
        return client.create(
            client
                .newOptionsBuilder()
                .runtime(asyncHttpClient)
                .reconnect(false)
                .build());
    }

    @NotNull
    public static DefaultAsyncHttpClientConfig.Builder getConfigBuilder() {
        DefaultAsyncHttpClientConfig.Builder configBuilder = new DefaultAsyncHttpClientConfig.Builder();
        configBuilder.setFollowRedirect(true)
            .setWebSocketMaxFrameSize(Integer.MAX_VALUE)
            .setShutdownQuietPeriod(0)
            .setConnectTimeout(500)
            .setReadTimeout(500)
            .setUserAgent("loader/1.1")
            .setTcpNoDelay(true)
            .setKeepAlive(true);
        return configBuilder;
    }

    public static RequestBuilder getRequestBuilder(Client client, String uri) {
        return client.newRequestBuilder()
            .method(Request.METHOD.GET)
            .uri(uri)
            .encoder(new Encoder<String, String>() {
                @Override
                public String encode(String s) {
                    return s;
                }
            })
            .decoder(new Decoder<String, String>() {
                @Override
                public String decode(Event e, String s) {
                    return s;
                }
            })
            .transport(Request.TRANSPORT.WEBSOCKET);
    }
}
