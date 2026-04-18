package com.codereviewer.dto;

import java.util.List;

public class BatchRequest {

    private List<CodeRequest> codes;
    private String webhookUrl;

    public BatchRequest() {}

    public List<CodeRequest> getCodes() { return codes; }
    public void setCodes(List<CodeRequest> codes) { this.codes = codes; }

    public String getWebhookUrl() { return webhookUrl; }
    public void setWebhookUrl(String webhookUrl) { this.webhookUrl = webhookUrl; }
}