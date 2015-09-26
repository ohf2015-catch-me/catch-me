package com.example.ashnabhatia.catchme2;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by bananer on 26.09.15.
 */
public final class ApiUrls {

    protected static final String PROTOCOL = "http";
    protected static final String HOST = "localhost";
    protected static final int PORT = 1337;

    private static URL buildURL(String path) {
        try {
            return new URL(PROTOCOL, HOST, PORT, path);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static URL gameDetails(String gameId) {
        return buildURL("/game/" + gameId);
    }

    public static URL createGame() {
        return buildURL("/game");
    }

    public static URL createQuestion(String gameId) {
        return buildURL("/game/"+gameId+"/question");
    }

    public static URL answerQuestion(String gameId, String questionId) {
        return buildURL("/game/"+gameId+"/question/" + questionId);
    }
}
