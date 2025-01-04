package com.bytezone.network;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.security.SecureRandom;

public class OnionRouter {

    static class Node {
        private final String nodeAddress;
        private final SecretKey nodeKey;

        public Node(String address, SecretKey key) {
            this.nodeAddress = address;
            this.nodeKey = key;
        }

        public byte[] encryptLayer(byte[] data) throws Exception {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte[] iv = new byte[16]; // 16 bytes for AES
            new SecureRandom().nextBytes(iv); // Generate random IV
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, nodeKey, ivSpec);
            byte[] encryptedData = cipher.doFinal(data);
            // Combine IV and encrypted data for transmission
            byte[] combined = new byte[iv.length + encryptedData.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encryptedData, 0, combined, iv.length, encryptedData.length);
            return combined;
        }

        public byte[] decryptLayer(byte[] data) throws Exception {
            // Extract IV from the data
            byte[] iv = new byte[16]; // 16 bytes for AES
            System.arraycopy(data, 0, iv, 0, iv.length);
            byte[] encryptedData = new byte[data.length - iv.length];
            System.arraycopy(data, iv.length, encryptedData, 0, encryptedData.length);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, nodeKey, ivSpec);
            return cipher.doFinal(encryptedData);
        }

        public String getNodeAddress() {
            return nodeAddress;
        }
    }

    private final Node[] nodes;

    public OnionRouter() throws Exception {
        nodes = new Node[3];

        // Generate keys and initialize nodes
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);

        SecretKey key1 = keyGen.generateKey();
        SecretKey key2 = keyGen.generateKey();
        SecretKey key3 = keyGen.generateKey();

        nodes[0] = new Node("node1.address", key1);
        nodes[1] = new Node("node2.address", key2);
        nodes[2] = new Node("node3.address", key3);
    }

    public void sendOnionMessage(String message) {
        try {
            byte[] encryptedMessage = message.getBytes();

            // Encrypt each layer
            for (int i = nodes.length - 1; i >= 0; i--) {
                String nextHop = i < nodes.length - 1 ? nodes[i + 1].getNodeAddress() : "final.destination.address";
                String layer = Base64.getEncoder().encodeToString(encryptedMessage) + ":" + nextHop;
                encryptedMessage = nodes[i].encryptLayer(layer.getBytes());
            }

            System.out.println("Sending onion message to first node: " +
                    Base64.getEncoder().encodeToString(encryptedMessage));

            // Simulate routing through nodes
            routeMessageThroughNodes(encryptedMessage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void routeMessageThroughNodes(byte[] onionMessage) {
        try {
            byte[] currentMessage = onionMessage;

            for (Node node : nodes) {
                // Decrypt one layer
                byte[] decryptedLayer = node.decryptLayer(currentMessage);
                String layerContent = new String(decryptedLayer);

                // Extract payload and next hop
                String[] parts = layerContent.split(":", 2);
                String payload = parts[0];
                String nextHop = parts[1];

                System.out.println("Node " + node.getNodeAddress() +
                        " decrypted message. Next hop: " + nextHop);

                if ("final.destination.address".equals(nextHop)) {
                    System.out.println("Final destination reached. Original message: " +
                            new String(Base64.getDecoder().decode(payload)));
                    break;
                }

                currentMessage = Base64.getDecoder().decode(payload);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}