# service-core
Sample STAF service Development framework using the code of spring mvc

# How to use
```
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
```
