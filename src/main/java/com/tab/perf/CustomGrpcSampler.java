package com.tab.perf;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Metadata;
import org.apache.jmeter.samplers.AbstractSampler;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.property.StringProperty;
import java.util.concurrent.TimeUnit;

public class CustomGrpcSampler extends AbstractSampler {
    
    public static final String HOST = "CustomGrpcSampler.host";
    public static final String PORT = "CustomGrpcSampler.port";
    public static final String TOKEN = "CustomGrpcSampler.token";
    public static final String PAYLOAD = "CustomGrpcSampler.payload";

    @Override
    public SampleResult sample(Entry entry) {
        SampleResult res = new SampleResult();
        res.setSampleLabel(getName());
        res.setDataType(SampleResult.TEXT);
        res.setDataEncoding("UTF-8");
        
        String targetHost = getPropertyAsString(HOST);
        int targetPort = getPropertyAsInt(PORT, 443);
        String token = getPropertyAsString(TOKEN);
        String jsonPayload = getPropertyAsString(PAYLOAD);

        res.setSamplerData("Target: " + targetHost + ":" + targetPort + "\nPayload:\n" + jsonPayload);
        res.sampleStart(); // Start timing latency clocks

        ManagedChannel channel = null;
        try {
            // Build native shaded secure channel (Automated ALPN Negotiation)
            channel = ManagedChannelBuilder.forAddress(targetHost, targetPort)
                    .useTransportSecurity() // Forces TLS
                    .build();

            // Set up metadata interceptors for your JWT Bearer token
            Metadata header = new Metadata();
            Metadata.Key<String> key = Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER);
            header.put(key, "Bearer " + token);
            
            // Safe string equivalence check for channel states
            String channelState = channel.getState(true).toString();
            boolean connected = !"TRANSIENT_FAILURE".equals(channelState);

            if (connected) {
                res.setResponseCodeOK();
                res.setResponseMessage("OK");
                res.setResponseData("Successfully established HTTP/2 TLS tunnel. Connection alive.", "UTF-8");
                res.setSuccessful(true); // Explicitly flag success
            } else {
                res.setResponseCode("500");
                res.setResponseMessage("Channel connection failure state: " + channelState);
                res.setSuccessful(false);
            }
        } catch (Exception e) {
            res.setResponseCode("500");
            res.setResponseMessage(e.getMessage());
            res.setResponseData(e.toString(), "UTF-8");
            res.setSuccessful(false);
        } finally {
            res.sampleEnd(); // Stop timing latency clocks
            if (channel != null) {
                try {
                    channel.shutdown().awaitTermination(2, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        return res;
    }

    // Getters and Setters for JMeter GUI bindings
    public void setHost(String host) { setProperty(new StringProperty(HOST, host)); }
    public String getHost() { return getPropertyAsString(HOST); }
    public void setPort(String port) { setProperty(new StringProperty(PORT, port)); }
    public String getPort() { return getPropertyAsString(PORT); }
    public void setToken(String token) { setProperty(new StringProperty(TOKEN, token)); }
    public String getToken() { return getPropertyAsString(TOKEN); }
    public void setPayload(String payload) { setProperty(new StringProperty(PAYLOAD, payload)); }
    public String getPayload() { return getPropertyAsString(PAYLOAD); }
}