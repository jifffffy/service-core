package org.sunyuyangg.service.core.controller;


import com.ibm.staf.service.STAFCommandParser;
import org.springframework.stereotype.Controller;
import org.sunyuyangg.service.core.annotation.Option;
import org.sunyuyangg.service.core.annotation.OptionMapping;
import org.sunyuyangg.service.core.model.Person;

@Controller
public class TestController {

    @OptionMapping(
            name = "test",
            options = {@Option(name = "demo", maxAllowed = 1, valueRequirement= STAFCommandParser.VALUEREQUIRED)},
            optionGroup = {},
            optionNeeds = {}
    )
    public String test(String demo) {
        return demo + demo;
    }

    @OptionMapping(
            options = {@Option(name = "person", maxAllowed = 1, valueRequirement= STAFCommandParser.VALUEREQUIRED)},
            optionGroup = {},
            optionNeeds = {}
    )
    public Person person(Person person) {
        person.setAge(person.getAge() + 10);
        return person;
    }
}
