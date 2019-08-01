package org.sunyuyangg.service.core.model;

public class Summary {

    private String user;
    private String nice;
    private String system;
    private String idle;
    private String ioWait;
    private String irq;
    private String softIRQ;
    private String steal;

    public Summary() {
    }

    public Summary(Tick prevTick, Tick tick) {
        long user = tick.getUser() -prevTick.getUser();
        long nice = tick.getNice() - prevTick.getNice();
        long sys = tick.getSystem() - prevTick.getSystem();
        long idle = tick.getIdle() - prevTick.getIdle();
        long iowait = tick.getIoWait() - prevTick.getIoWait();
        long irq = tick.getIrq()- prevTick.getIrq();
        long softirq = tick.getSoftIRQ() - prevTick.getSoftIRQ();
        long steal =   tick.getSteal() - prevTick.getSteal();
        long totalCpu = user + nice + sys + idle + iowait + irq + softirq + steal;

        this.user = String.format("%.1f%%", 100d * user / totalCpu);
        this.nice =  String.format("%.1f%%", 100d * nice / totalCpu);
        this.system = String.format("%.1f%%", 100d * sys / totalCpu);
        this.idle =  String.format("%.1f%%", 100d * idle / totalCpu);
        this.ioWait =  String.format("%.1f%%", 100d * iowait / totalCpu);
        this.irq =  String.format("%.1f%%", 100d * irq / totalCpu);
        this.softIRQ = String.format("%.1f%%", 100d * softirq / totalCpu);
        this.steal = String.format("%.1f%%", 100d * steal / totalCpu);
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getNice() {
        return nice;
    }

    public void setNice(String nice) {
        this.nice = nice;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getIdle() {
        return idle;
    }

    public void setIdle(String idle) {
        this.idle = idle;
    }

    public String getIoWait() {
        return ioWait;
    }

    public void setIoWait(String ioWait) {
        this.ioWait = ioWait;
    }

    public String getIrq() {
        return irq;
    }

    public void setIrq(String irq) {
        this.irq = irq;
    }

    public String getSoftIRQ() {
        return softIRQ;
    }

    public void setSoftIRQ(String softIRQ) {
        this.softIRQ = softIRQ;
    }

    public String getSteal() {
        return steal;
    }

    public void setSteal(String steal) {
        this.steal = steal;
    }
}
