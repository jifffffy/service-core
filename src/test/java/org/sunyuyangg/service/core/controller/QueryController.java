package org.sunyuyangg.service.core.controller;


import com.ibm.staf.service.STAFCommandParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.sunyuyangg.service.core.HandlerClient;
import org.sunyuyangg.service.core.annotation.Option;
import org.sunyuyangg.service.core.annotation.OptionMapping;
import org.sunyuyangg.service.core.model.Cpu;
import org.sunyuyangg.service.core.model.Summary;
import org.sunyuyangg.service.core.model.Tick;
import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.software.os.OperatingSystem;
import oshi.util.FormatUtil;
import oshi.util.Util;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class QueryController {

    private SystemInfo si;
    private HardwareAbstractionLayer hal;
    private OperatingSystem os;

    public QueryController() {
    }

    @PostConstruct
    private void init() {
        si = new SystemInfo();
        hal = si.getHardware();
        os = si.getOperatingSystem();
    }

    @OptionMapping(
            options = {
                    @Option(name = "ComputerSystem", maxAllowed = 1, valueRequirement = STAFCommandParser.VALUENOTALLOWED)
            },
            optionNeeds = {},
            optionGroup = {}

    )
    public ComputerSystem computerSystem() {
        return hal.getComputerSystem();
    }

    @OptionMapping(
            options = {
                    @Option(name = "memory", maxAllowed = 1, valueRequirement = STAFCommandParser.VALUENOTALLOWED)
            },
            optionNeeds = {},
            optionGroup = {}

    )
    public GlobalMemory memory() {
        return hal.getMemory();
    }

    @OptionMapping(
            options = {
                    @Option(name = "processor", maxAllowed = 1, valueRequirement = STAFCommandParser.VALUENOTALLOWED)
            },
            optionNeeds = {},
            optionGroup = {}

    )
    public CentralProcessor processor() {
        return hal.getProcessor();
    }

    @OptionMapping(
            options = {
                    @Option(name = "cpu", maxAllowed = 1, valueRequirement = STAFCommandParser.VALUENOTALLOWED)
            },
            optionNeeds = {},
            optionGroup = {}

    )
    public Cpu cpu() {
        Cpu cpu = new Cpu();
        CentralProcessor processor = hal.getProcessor();
        cpu.setUptime( FormatUtil.formatElapsedSecs(processor.getSystemUptime()));
        cpu.setInterrupts(processor.getContextSwitches() + " / " + processor.getInterrupts());
        List<Tick> tickList = new ArrayList();
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        if(prevTicks.length == 8) {
            tickList.add(new Tick(prevTicks));
        }
        // Wait a second...
        Util.sleep(1000);
        processor.updateAttributes();
        long[] ticks = processor.getSystemCpuLoadTicks();

        if (ticks.length == 8) {
            tickList.add(new Tick(ticks));
        }

        cpu.setCountingTicks(processor.getSystemCpuLoadBetweenTicks(prevTicks) * 100);
        cpu.setOsMXBean(processor.getSystemCpuLoad() * 100);
        double[] loadAverage = processor.getSystemLoadAverage(3);
        cpu.setAverages((loadAverage[0] < 0 ? " N/A" : String.format(" %.2f", loadAverage[0]))
                + (loadAverage[1] < 0 ? " N/A" : String.format(" %.2f", loadAverage[1]))
                + (loadAverage[2] < 0 ? " N/A" : String.format(" %.2f", loadAverage[2])));

        StringBuilder procCpu = new StringBuilder();
        long[][] prevProcTicks = processor.getProcessorCpuLoadTicks();
        double[] load = processor.getProcessorCpuLoadBetweenTicks(prevProcTicks);
        for (double avg : load) {
            procCpu.append(String.format(" %.1f%%", avg * 100));
        }

        cpu.setPerProcessor(procCpu.toString());

        long freq = processor.getVendorFreq();
        if (freq > 0) {
            cpu.setVendorFrequency(FormatUtil.formatHertz(freq));
        }
        freq = processor.getMaxFreq();
        if (freq > 0) {
            cpu.setMaxFrequency( FormatUtil.formatHertz(freq));
        }

        long[] freqs = processor.getCurrentFreq();
        if (freqs[0] > 0) {
            StringBuilder sb = new StringBuilder("Current Frequencies: ");
            for (int i = 0; i < freqs.length; i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(FormatUtil.formatHertz(freqs[i]));
            }
            cpu.setCurrentFrequencies(sb.toString());
        }
        if(tickList.size() == 2) {
            Summary summary = new Summary(tickList.get(0), tickList.get(1));
            cpu.setSummary(summary);
        }
        return cpu;
    }

    @OptionMapping(
            options = {
                    @Option(name = "usb", maxAllowed = 1, valueRequirement = STAFCommandParser.VALUENOTALLOWED),
                    @Option(name = "tree", maxAllowed = 1, valueRequirement = STAFCommandParser.VALUEALLOWED)
            },
            optionNeeds = {},
            optionGroup = {}

    )
    public List<UsbDevice> usb(boolean tree) {
        return Arrays.asList(hal.getUsbDevices(tree));
    }
}
