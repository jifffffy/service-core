# service-core
Sample STAF service Development framework using the code of spring mvc

# How to use
```
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
}
```
