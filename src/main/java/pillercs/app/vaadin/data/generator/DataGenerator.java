package pillercs.app.vaadin.data.generator;

import com.github.javafaker.Faker;
import com.vaadin.flow.spring.annotation.SpringComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import pillercs.app.vaadin.data.entity.Applicant;
import pillercs.app.vaadin.data.entity.Application;
import pillercs.app.vaadin.data.enums.Role;
import pillercs.app.vaadin.data.repository.ApplicantRepository;
import pillercs.app.vaadin.data.repository.ApplicationRepository;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Slf4j
@SpringComponent
public class DataGenerator {

    @Bean
    public CommandLineRunner loadData(ApplicantRepository applicantRepository) {
        return args -> {
            if (applicantRepository.count() != 0L) {
                log.info("Using existing database");
                return;
            }

            Faker faker = new Faker();

            List<Applicant> fakedApplicants = new ArrayList<>();

            for (int i = 0; i < 15; i++) {
                Application application = Application.builder()
                        .createdByUser(faker.name().username())
                        .build();

                Applicant applicant = Applicant.builder()
                        .application(application)
                        .firstName(faker.name().firstName())
                        .lastName(faker.name().lastName())
                        .dateOfBirth(LocalDate.ofInstant(
                                faker.date().birthday(18, 60).toInstant(), ZoneId.systemDefault()))
                        .mothersName(faker.name().nameWithMiddle())
                        .role(Role.DEBTOR)
                        .build();

                fakedApplicants.add(applicant);
            }

            applicantRepository.saveAll(fakedApplicants);

            log.info("Generated demo data");
        };
    }

}
