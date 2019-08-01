package org.sunyuyangg.service.core.method;

import com.ibm.staf.STAFResult;
import com.ibm.staf.service.STAFCommandParseResult;
import com.ibm.staf.service.STAFCommandParser;
import com.ibm.staf.service.STAFServiceInterfaceLevel30;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OptionMappingInfo {

    @Nullable
    private final String name;
    private int maxArgs;
    private boolean caseSensitive;
    private List<Option> options;
    private List<OptionGroup> optionGroups;
    private List<OptionNeed> optionNeeds;
    private STAFCommandParser commandParser;
    private STAFCommandParseResult parseResult;

    public OptionMappingInfo(String name, int maxArgs, boolean caseSensitive, List<Option> options, List<OptionGroup> optionGroups, List<OptionNeed> optionNeeds) {
        this.name = name;
        this.maxArgs = maxArgs;
        this.caseSensitive = caseSensitive;
        this.options = options;
        this.optionGroups = optionGroups;
        this.optionNeeds = optionNeeds;
        createCommandParser();
    }

    public OptionMappingInfo(OptionMappingInfo copy, STAFCommandParseResult parseResult) {
        this(copy.name, copy.maxArgs, copy.caseSensitive, copy.options, copy.optionGroups, copy.optionNeeds);
        this.parseResult = parseResult;
    }

    private void createCommandParser() {
        this.commandParser = new STAFCommandParser(this.maxArgs, this.caseSensitive);
        this.options.forEach(option -> this.commandParser.addOption(option.name, option.maxAllowed, option.valueRequirement));
        this.optionGroups.forEach(optionGroup -> this.commandParser.addOptionGroup(optionGroup.names, optionGroup.min, optionGroup.max));
        this.optionNeeds.forEach(optionNeed -> this.commandParser.addOptionNeed(optionNeed.needers, optionNeed.needees));
    }

    public STAFCommandParseResult getParseResult() {
        return parseResult;
    }

    @Nullable
    public String getName() {
        return this.name;
    }

    public String getMappingPath() {
        return this.options.stream().map(option -> option.name.toUpperCase()).collect(Collectors.joining("#"));
    }

    public static Builder builder(int maxArgs, boolean caseSensitive) {
        return new DefaultBuilder(maxArgs, caseSensitive);
    }

    public OptionMappingInfo getMatching(STAFServiceInterfaceLevel30.RequestInfo request) {
        STAFCommandParseResult parseResult = this.commandParser.parse(request.request);
        if(parseResult.rc != STAFResult.Ok) {
            return null;
        }
        return new OptionMappingInfo(this, parseResult);
    }

    public interface Builder {
        /**
         * used by MappingRegistry#getHandlerMethodsByMappingName
         * @param name
         * @return
         */
        Builder name(String name);
        Builder option(String name, int maxAllowed, int valueRequirement);
        Builder optionGroup(String names, int min, int max);
        Builder optionNeed(String needers, String needees);
        OptionMappingInfo build();
    }

    private static class DefaultBuilder implements Builder{

        private int maxArgs;
        private boolean caseSensitive;
        private List<Option> options = new ArrayList<>();
        private List<OptionGroup> optionGroups = new ArrayList<>();
        private List<OptionNeed> optionNeeds = new ArrayList<>();

        @Nullable
        private String mappingName;


        public DefaultBuilder(int maxArgs, boolean caseSensitive) {
            this.maxArgs = maxArgs;
            this.caseSensitive = caseSensitive;
        }

        @Override
        public Builder name(String name) {
            this.mappingName = name;
            return this;
        }

        @Override
        public Builder option(String name, int maxAllowed, int valueRequirement) {
            this.options.add( new Option(name, maxAllowed, valueRequirement));
            return this;
        }

        @Override
        public Builder optionGroup(String names, int min, int max) {
            this.optionGroups.add(new OptionGroup(names, min, max));
            return this;
        }

        @Override
        public Builder optionNeed(String needers, String needees) {
            this.optionNeeds.add(new OptionNeed(needers, needees));
            return this;
        }

        @Override
        public OptionMappingInfo build() {
            return new OptionMappingInfo(this.mappingName, this.maxArgs, this.caseSensitive, this.options, this.optionGroups, this.optionNeeds);
        }
    }


    private static class Option {
        public String name;
        public int maxAllowed;
        public int valueRequirement;

        public Option(String name, int maxAllowed, int valueRequirement) {
            this.name = name;
            this.maxAllowed = maxAllowed;
            this.valueRequirement = valueRequirement;
        }
    }

    private static class OptionGroup {
        public String names;
        public int min;
        public int max;

        public OptionGroup(String names, int min, int max) {
            this.names = names;
            this.min = min;
            this.max = max;
        }
    }

    private static class OptionNeed {
        public String needers;
        public String needees;

        public OptionNeed(String needers, String needees) {
            this.needers = needers;
            this.needees = needees;
        }
    }
}
