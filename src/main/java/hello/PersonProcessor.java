package hello;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.item.ItemProcessor;

public class PersonProcessor implements ItemProcessor<Person, Person> {

    private static final Logger log = LoggerFactory.getLogger(PersonProcessor.class);

    @Override
    public Person process(final Person person) throws Exception {
//        if(person.getFirstName().equals("wang")) {
//            throw new Exception("error name wang");
//        }

        final String firstName = person.getFirstName().toUpperCase();
        final String lastName = person.getLastName().toUpperCase();

        final Person transformedPerson = new Person(firstName, lastName);

        log.info("Converting (" + person + ") into (" + transformedPerson + ")");
        Thread.sleep(1000);

        return transformedPerson;
    }

}
