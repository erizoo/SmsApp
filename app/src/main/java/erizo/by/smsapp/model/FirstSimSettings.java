package erizo.by.smsapp.model;



public class FirstSimSettings {

    private Boolean status;
    private String simId;
    private String url;
    private String secretKey;
    private String frequencyOfRequests;
    private String frequencyOfSmsSending;

    public FirstSimSettings() {
    }

    public FirstSimSettings(Boolean status, String simId, String url, String secretKey, String frequencyOfRequests, String frequencyOfSmsSending) {
        this.status = status;
        this.simId = simId;
        this.url = url;
        this.secretKey = secretKey;
        this.frequencyOfRequests = frequencyOfRequests;
        this.frequencyOfSmsSending = frequencyOfSmsSending;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getSimId() {
        return simId;
    }

    public void setSimId(String simId) {
        this.simId = simId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getFrequencyOfRequests() {
        return frequencyOfRequests;
    }

    public void setFrequencyOfRequests(String frequencyOfRequests) {
        this.frequencyOfRequests = frequencyOfRequests;
    }

    public String getFrequencyOfSmsSending() {
        return frequencyOfSmsSending;
    }

    public void setFrequencyOfSmsSending(String frequencyOfSmsSending) {
        this.frequencyOfSmsSending = frequencyOfSmsSending;
    }

    @Override
    public String toString() {
        return "FirstSimSettings{" +
                "status=" + status +
                ", simId='" + simId + '\'' +
                ", url='" + url + '\'' +
                ", secretKey='" + secretKey + '\'' +
                ", frequencyOfRequests='" + frequencyOfRequests + '\'' +
                ", frequencyOfSmsSending='" + frequencyOfSmsSending + '\'' +
                '}';
    }
}
