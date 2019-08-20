package org.sunyuyangg.service.core.method;

import com.ibm.staf.service.STAFCommandParser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HelpMessage {
    private String command;
    private String description;
    private String options;

    public HelpMessage() {
    }

    public HelpMessage(OptionMappingInfo info) {
        this.description = info.getDesc();
        List<OptionMappingInfo.Option> options = info.getOptions();
        if (options.size() > 0) {
            this.command = options.get(0).name;
        }
        List<String> list = new ArrayList<>();
        addOption(options, list);
        this.options = list.stream().collect(Collectors.joining(" "));
    }

    private void addOption(List<OptionMappingInfo.Option> options, List<String> list) {
        for (int i = 1; i < options.size(); i++) {
            OptionMappingInfo.Option option = options.get(i);
            switch (option.maxAllowed) {
                case 0:
                    list.add("[" + option.name.toUpperCase() + "* " + handleOptionValue(option) + "]");
                    break;
                default:
                    switch (option.minAllowed) {
                        case 0:
                            list.add("[" + option.name.toUpperCase() + handleOptionValue(option) + "]");
                            break;
                        default:
                            list.add("<" + option.name.toUpperCase() + handleOptionValue(option) + ">");
                    }
            }
        }
    }


    private String handleOptionValue(OptionMappingInfo.Option option) {
        String value = "";
        switch (option.valueRequirement) {
            case STAFCommandParser.VALUEREQUIRED:
                value = "<" + option.name.toLowerCase() + ">";
                break;
            case STAFCommandParser.VALUEALLOWED:
                value = "[" + option.name.toLowerCase() + "]";
                break;
            case STAFCommandParser.VALUENOTALLOWED:
                break;
        }
        return value;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }
}
