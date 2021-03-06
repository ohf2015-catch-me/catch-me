package com.example.ashnabhatia.catchme2;

import android.content.Context;
import android.util.Log;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import ch.uepaa.p2pkit.ConnectionCallbacks;
import ch.uepaa.p2pkit.ConnectionResult;
import ch.uepaa.p2pkit.ConnectionResultHandling;
import ch.uepaa.p2pkit.KitClient;
import ch.uepaa.p2pkit.discovery.InfoTooLongException;
import ch.uepaa.p2pkit.discovery.P2pListener;
import ch.uepaa.p2pkit.discovery.Peer;

public class P2PManager {

    private static final String TAG = "P2PManager";

    private static final String APP_KEY = "eyJzaWduYXR1cmUiOiJyQ0xlN3IwQWdJeWFpTE9rMnlpazd"+
            "0YVJtVzE2YmdvajFEZXE1b1kyV2dVMjYrWGZNLzBmVkhhd1BnTU5pZ1Z0bjVtM2RHdC8rQ1o1UktJc"+
            "jNPbXNlejJjODdSZytsNmVzem1RWS9CUTY5ZDNQMC8ySll2dm1sZ3FQRkxYSFJHQzFjTlBYY1RwWlp"+
            "hcXpQMnpnZGF1M0NCS0YvUTg0SHYxQU1Xdmg0N2JBb2M9IiwiYXBwSWQiOjEyNjMsInZhbGlkVW50a"+
            "WwiOjE2Nzk0LCJhcHBVVVVJRCI6IjFGNkNDNTg1LUY5QTMtNDI2MC1CNzY0LTcyMzA3RENGQjJBOCJ9";

    private final Map<UUID, String> games = new ConcurrentHashMap<>();
    private final static String prefix = "catch-me://";

    private final Set<GameListener> listeners = new HashSet<>();

    protected KitClient client;

    private static P2PManager instance;

    private P2PManager(final Context context) {

        // fake data
        /*games.put(UUID.randomUUID(), "56bf1ae9-7c04-4460-8b02-399ccb57fed0");
        games.put(UUID.randomUUID(), "f7355cf2-504d-4b66-81d4-872445ca57b8");
        games.put(UUID.randomUUID(), "a44f3d32-0e50-484f-abe2-a26c4b55930f");
        games.put(UUID.randomUUID(), "f54a1d4b-3d15-4a35-8e30-3604f9bbe6a1");
        games.put(UUID.randomUUID(), "dbfa7efc-8827-4a26-8a44-638ea7b80e4b");
        games.put(UUID.randomUUID(), "7e3ed240-10e4-4d69-90ad-cbc5f6ca369c");*/
        //games.put(UUID.randomUUID(), "3d2eb515-838c-4905-8602-b6b8b8f8dab7");

        final int statusCode = KitClient.isP2PServicesAvailable(context);
        if (statusCode == ConnectionResult.SUCCESS) {
            client = KitClient.getInstance(context);
            client.registerConnectionCallbacks(mConnectionCallbacks);

            if (client.isConnected()) {
                Log.d(TAG, "Client already initialized");
            } else {
                Log.d(TAG, "Connecting P2PKit client");
                client.connect(APP_KEY);
            }

        } else {
            ConnectionResultHandling.showAlertDialogForConnectionError(context, statusCode);
        }
    }

    public static P2PManager getInstance(Context context) {
        if(instance == null) {
            instance = new P2PManager(context);
        }
        return instance;
    }

    /**
     * Will notify the listener about every game found in the past and future, until removed.
     * @param listener
     */
    public void addListener(GameListener listener) {
        listeners.add(listener);
        for(String gameId : games.values()) {
            listener.onGameFound(gameId);
        }
    }


    private final ConnectionCallbacks mConnectionCallbacks = new ConnectionCallbacks() {

        @Override
        public void onConnected() {
            Log.d(TAG, "Successfully connected to P2P Services");
            client.getDiscoveryServices().addListener(mP2pDiscoveryListener);

        }

        @Override
        public void onConnectionSuspended() {
            Log.d(TAG, "Connection to P2P Services suspended");

        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            Log.d(TAG, "Connection to P2P Services failed with status: " + connectionResult.getStatusCode());
        }
    };

    public void removeListener(GameListener listener) {
        listeners.remove(listener);
    }

    public void setMyGameId(String gameId) {
        try {
            client.getDiscoveryServices().setP2pDiscoveryInfo((prefix +  gameId).getBytes());
        }
        catch(InfoTooLongException ex) {
            throw new RuntimeException(ex);
        }
    }

    public interface GameListener {
        void onGameFound(String gameId);
    }

    public Collection<String> getGameIds() {
        return games.values();
    }

    protected void receivedDiscoveryInfo(UUID nodeId, String discoveryInfo) {
        if(discoveryInfo.startsWith(prefix)) {
            String gameId = discoveryInfo.substring(prefix.length());
            if(!games.containsValue(gameId)) {
                games.put(nodeId, gameId);
                for(GameListener l : listeners) {
                    l.onGameFound(gameId);
                }
            }
        }
    }

    private final P2pListener mP2pDiscoveryListener = new P2pListener() {
        @Override
        public void onStateChanged(final int state) {
            Log.d(TAG, "P2pListener | State changed: " + state);
        }

        @Override
        public void onPeerDiscovered(final Peer peer) {
            byte[] bytes = peer.getDiscoveryInfo();
            if(bytes != null) {
                String info = new String(bytes);
                Log.d(TAG, "P2pListener | Peer discovered: " + peer.getNodeId() + " with info: " + info);
                receivedDiscoveryInfo(peer.getNodeId(), info);
            }
        }

        @Override
        public void onPeerLost(final Peer peer) {
            Log.d(TAG, "P2pListener | Peer lost: " + peer.getNodeId());
            // we'll leave games in the list even after losing contact.
            //games.remove(peer.getNodeId());
        }

        @Override
        public void onPeerUpdatedDiscoveryInfo(Peer peer) {
            String info = new String(peer.getDiscoveryInfo());
            Log.d(TAG, "P2pListener | Peer updated: " + peer.getNodeId() + " with new info: " + info);
            receivedDiscoveryInfo(peer.getNodeId(), info);
        }
    };


}
