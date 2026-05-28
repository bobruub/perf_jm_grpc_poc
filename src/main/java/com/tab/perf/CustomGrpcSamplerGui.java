package com.tab.perf;

import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;
import javax.swing.*;
import java.awt.*;

public class CustomGrpcSamplerGui extends AbstractSamplerGui {

    private JTextField hostField;
    private JTextField portField;
    private JTextField tokenField;
    private JTextArea payloadArea;

    public CustomGrpcSamplerGui() {
        initUI();
    }

    @Override
    public String getLabelResource() {
        return null; 
    }

    @Override
    public String getStaticLabel() {
        return "TAB Custom gRPC Sampler Engine";
    }

    @Override
    public TestElement createTestElement() {
        CustomGrpcSampler sampler = new CustomGrpcSampler();
        modifyTestElement(sampler);
        return sampler;
    }

    @Override
    public void modifyTestElement(TestElement element) {
        super.configureTestElement(element);
        if (element instanceof CustomGrpcSampler) {
            CustomGrpcSampler sampler = (CustomGrpcSampler) element;
            sampler.setHost(hostField.getText());
            sampler.setPort(portField.getText());
            sampler.setToken(tokenField.getText());
            sampler.setPayload(payloadArea.getText());
        }
    }

    @Override
    public void configure(TestElement element) {
        super.configure(element);
        if (element instanceof CustomGrpcSampler) {
            CustomGrpcSampler sampler = (CustomGrpcSampler) element;
            hostField.setText(sampler.getHost());
            portField.setText(sampler.getPort());
            tokenField.setText(sampler.getToken());
            payloadArea.setText(sampler.getPayload());
        }
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 10));
        setBorder(makeBorder());
        add(makeTitlePanel(), BorderLayout.NORTH);

        // Core configuration container
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6); // Add natural structural margins
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Initialize input elements
        hostField = new JTextField(30);
        portField = new JTextField(6);
        tokenField = new JTextField(30);
        payloadArea = new JTextArea(12, 50);
        payloadArea.setLineWrap(true);
        payloadArea.setWrapStyleWord(true);

        // Row 0: Server Host
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 0.0;
        mainPanel.add(new JLabel("Server Host:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        mainPanel.add(hostField, gbc);

        // Row 1: Port Number
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0.0;
        mainPanel.add(new JLabel("Port Number:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        mainPanel.add(portField, gbc);

        // Row 2: JWT Token
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.weightx = 0.0;
        mainPanel.add(new JLabel("Authorization JWT Token (Raw):"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        mainPanel.add(tokenField, gbc);

        // Row 3: Label for JSON Payload (Span both columns)
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        mainPanel.add(new JLabel("Request JSON Body Payload:"), gbc);

        // Row 4: JSON Payload Box (Span both columns, scale vertically)
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        
        JScrollPane scrollPane = new JScrollPane(payloadArea);
        scrollPane.setMinimumSize(new Dimension(100, 150));
        mainPanel.add(scrollPane, gbc);

        add(mainPanel, BorderLayout.CENTER);
    }
}