package org.sunyuyangg.service.core.model;

public class Cpu {
    private String uptime;
    private String interrupts;
    private double countingTicks;
    private double osMXBean;
    private String averages;
    private String perProcessor;
    private String vendorFrequency;
    private String maxFrequency;
    private String currentFrequencies;
    private Summary summary;

    public Cpu() {
    }

    public String getUptime() {
        return uptime;
    }

    public void setUptime(String uptime) {
        this.uptime = uptime;
    }

    public String getInterrupts() {
        return interrupts;
    }

    public void setInterrupts(String interrupts) {
        this.interrupts = interrupts;
    }

    public double getCountingTicks() {
        return countingTicks;
    }

    public void setCountingTicks(double countingTicks) {
        this.countingTicks = countingTicks;
    }

    public double getOsMXBean() {
        return osMXBean;
    }

    public void setOsMXBean(double osMXBean) {
        this.osMXBean = osMXBean;
    }

    public String getAverages() {
        return averages;
    }

    public void setAverages(String averages) {
        this.averages = averages;
    }

    public String getPerProcessor() {
        return perProcessor;
    }

    public void setPerProcessor(String perProcessor) {
        this.perProcessor = perProcessor;
    }

    public String getVendorFrequency() {
        return vendorFrequency;
    }

    public void setVendorFrequency(String vendorFrequency) {
        this.vendorFrequency = vendorFrequency;
    }

    public String getMaxFrequency() {
        return maxFrequency;
    }

    public void setMaxFrequency(String maxFrequency) {
        this.maxFrequency = maxFrequency;
    }

    public String getCurrentFrequencies() {
        return currentFrequencies;
    }

    public void setCurrentFrequencies(String currentFrequencies) {
        this.currentFrequencies = currentFrequencies;
    }

    public Summary getSummary() {
        return summary;
    }

    public void setSummary(Summary summary) {
        this.summary = summary;
    }
}
