package codehole.shardino;

import java.util.ArrayList;
import java.util.List;

public class WeightedAddr {
    private String host;
    private int port;
    private int weight;

    public WeightedAddr() {}

    public WeightedAddr(String addr, int weight) {
        String[] splits = addr.split(":");
        this.host = splits[0];
        this.port = Integer.parseInt(splits[1]);
        this.weight = weight;
    }

    public WeightedAddr(String host, int port, int weight) {
        this.host = host;
        this.port = port;
        this.weight = weight;
    }

    public String getAddr() {
        return String.format("%s:%d", host, port);
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public static List<WeightedAddr> parse(String s) {
        String[] splits = s.split("&");
        List<WeightedAddr> addrs = new ArrayList<>();
        for (String split : splits) {
            String[] parts = split.split("=");
            String addr = parts[0];
            int weight = 100;
            if (parts.length > 1) {
                weight = Integer.parseInt(parts[1]);
            }
            addrs.add(new WeightedAddr(addr, weight));
        }
        return addrs;
    }
}
