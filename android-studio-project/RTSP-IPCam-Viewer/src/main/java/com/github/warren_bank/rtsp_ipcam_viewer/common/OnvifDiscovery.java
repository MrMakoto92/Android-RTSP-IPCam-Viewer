package com.github.warren_bank.rtsp_ipcam_viewer.common;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OnvifDiscovery {

    public interface DiscoveryCallback {
        void onDevicesFound(List<String> deviceUrls);
        void onError(String errorMsg);
    }

    public static void discoverDevices(Context context, DiscoveryCallback callback) {
        new Thread(() -> {
            List<String> discoveredIps = new ArrayList<>();
            WifiManager.MulticastLock multicastLock = null;

            try {
                // Habilitar recepción Multicast en el dispositivo
                WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                if (wifi != null) {
                    multicastLock = wifi.createMulticastLock("onvif_discovery");
                    multicastLock.acquire();
                }

                int port = 3702;
                InetAddress group = InetAddress.getByName("239.255.255.250");
                
                // Mensaje SOAP estándar de WS-Discovery para ONVIF
                String uuid = UUID.randomUUID().toString();
                String probe = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                        "<e:Envelope xmlns:e=\"http://www.w3.org/2003/05/soap-envelope\" " +
                        "xmlns:w=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\" " +
                        "xmlns:d=\"http://schemas.xmlsoap.org/ws/2005/04/discovery\">" +
                        "<e:Header>" +
                        "<w:MessageID>urn:uuid:" + uuid + "</w:MessageID>" +
                        "<w:To>urn:schemas-xmlsoap-org:ws:2005:04:discovery</w:To>" +
                        "<w:Action>http://schemas.xmlsoap.org/ws/2005/04/discovery/Probe</w:Action>" +
                        "</e:Header>" +
                        "<e:Body>" +
                        "<d:Probe/>" +
                        "</e:Body>" +
                        "</e:Envelope>";

                byte[] sendData = probe.getBytes();
                DatagramSocket socket = new DatagramSocket();
                socket.setSoTimeout(3000); // Tiempo de espera: 3 segundos

                DatagramPacket packet = new DatagramPacket(sendData, sendData.length, group, port);
                socket.send(packet);

                byte[] recvBuf = new byte[4096];
                long startTime = System.currentTimeMillis();

                // Escuchar respuestas de cámaras ONVIF durante 3 segundos
                while (System.currentTimeMillis() - startTime < 3000) {
                    try {
                        DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
                        socket.receive(receivePacket);
                        
                        String ip = receivePacket.getAddress().getHostAddress();
                        if (!discoveredIps.contains(ip)) {
                            discoveredIps.add(ip);
                        }
                    } catch (Exception ignored) {
                        // Timeout alcanzado
                    }
                }

                socket.close();
                if (multicastLock != null && multicastLock.isHeld()) {
                    multicastLock.release();
                }

                // Devolver resultados en el hilo principal de la UI
                new Handler(Looper.getMainLooper()).post(() -> callback.onDevicesFound(discoveredIps));

            } catch (Exception e) {
                if (multicastLock != null && multicastLock.isHeld()) {
                    multicastLock.release();
                }
                new Handler(Looper.getMainLooper()).post(() -> callback.onError(e.getMessage()));
            }
        }).start();
    }
}
