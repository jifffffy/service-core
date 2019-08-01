package org.sunyuyangg.service.core.model;

import oshi.hardware.CentralProcessor.TickType;

public class Tick {
    private long user;
    private long nice;
    private long system;
    private long idle;
    private long ioWait;
    private long irq;
    private long softIRQ;
    private long steal;


    public Tick() {
    }

    public Tick(long[] ticks) {
        this.user = ticks[TickType.USER.getIndex()];
        this.nice = ticks[TickType.NICE.getIndex()];
        this.system = ticks[TickType.SYSTEM.getIndex()];
        this.idle = ticks[TickType.IDLE.getIndex()];
        this.ioWait = ticks[TickType.IOWAIT.getIndex()];
        this.irq = ticks[TickType.IRQ.getIndex()];
        this.softIRQ = ticks[TickType.SOFTIRQ.getIndex()];
        this.steal = ticks[TickType.STEAL.getIndex()];
    }

    public long getUser() {
        return user;
    }

    public void setUser(long user) {
        this.user = user;
    }

    public long getNice() {
        return nice;
    }

    public void setNice(long nice) {
        this.nice = nice;
    }

    public long getSystem() {
        return system;
    }

    public void setSystem(long system) {
        this.system = system;
    }

    public long getIdle() {
        return idle;
    }

    public void setIdle(long idle) {
        this.idle = idle;
    }

    public long getIoWait() {
        return ioWait;
    }

    public void setIoWait(long ioWait) {
        this.ioWait = ioWait;
    }

    public long getIrq() {
        return irq;
    }

    public void setIrq(long irq) {
        this.irq = irq;
    }

    public long getSoftIRQ() {
        return softIRQ;
    }

    public void setSoftIRQ(long softIRQ) {
        this.softIRQ = softIRQ;
    }

    public long getSteal() {
        return steal;
    }

    public void setSteal(long steal) {
        this.steal = steal;
    }
}
