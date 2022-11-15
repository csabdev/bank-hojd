package pillercs.app.vaadin.data.generator;

import com.github.javafaker.Faker;
import com.vaadin.flow.spring.annotation.SpringComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import pillercs.app.vaadin.data.entity.*;
import pillercs.app.vaadin.data.enums.Gender;
import pillercs.app.vaadin.data.enums.IncomeFrequency;
import pillercs.app.vaadin.data.enums.Role;
import pillercs.app.vaadin.data.repository.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@SpringComponent
public class DataGenerator {

    @Bean
    public CommandLineRunner loadData(ApplicantRepository applicantRepository,
                                      ClientRepository clientRepository,
                                      CashLoanProductRepository cashLoanProductRepository,
                                      IncomeTypeRepository incomeTypeRepository,
                                      AddressRepository addressRepository,
                                      ApplicationRepository applicationRepository) {
        return args -> {
            if (applicantRepository.count() != 0L) {
                log.info("Using existing database");
                return;
            }

            Faker faker = new Faker();

            List<Application> fakedApplications = new ArrayList<>();
            for (int i = 0; i < 15; i++) {
                Application application = Application.builder()
                        .createdByUser(faker.name().username())
                        .build();

                Client client = fakeClient(faker, addressRepository);
                client = clientRepository.save(client);

                Applicant applicant = Applicant.builder()
                        .application(application)
                        .client(client)
                        .role(Role.DEBTOR)
                        .build();

                application.setApplicants(Set.of(applicant));
                fakedApplications.add(application);
            }
            applicationRepository.saveAll(fakedApplications);

            List<Client> fakedClients = new ArrayList<>();
            for (int i = 0; i < 30; i++) {
                fakedClients.add(fakeClient(faker, addressRepository));
            }
            clientRepository.saveAll(fakedClients);

            cashLoanProductRepository.save(CashLoanProduct.builder()
                    .minAmount(200_000)
                    .maxAmount(10_000_000)
                    .minTerm(24)
                    .maxTerm(84)
                    .interestRate(0.1)
                    .currency("HUF")
                    .validFrom(LocalDateTime.of(2022, 11, 1, 0, 0, 0, 0))
                    .build());

            incomeTypeRepository.saveAll(List.of(IncomeType.builder()
                            .name("salary")
                            .frequency(IncomeFrequency.MONTHLY)
                            .isEmployerNeeded(true)
                            .build(),
                    IncomeType.builder()
                            .name("pension")
                            .frequency(IncomeFrequency.MONTHLY)
                            .isEmployerNeeded(false)
                            .build(),
                    IncomeType.builder()
                            .name("rent")
                            .frequency(IncomeFrequency.MONTHLY)
                            .isEmployerNeeded(false)
                            .build(),
                    IncomeType.builder()
                            .name("other")
                            .frequency(IncomeFrequency.MONTHLY)
                            .isEmployerNeeded(false)
                            .build()));

            log.info("Generated demo data");
        };
    }

    private Client fakeClient(Faker faker, AddressRepository addressRepository) {
        Address address = Address.builder()
                .country(faker.address().country())
                .city(faker.address().city())
                .postalCode(faker.address().zipCode())
                .streetNameAndNumber(faker.address().streetAddress())
                .build();

        address = addressRepository.save(address);

        return Client.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .dateOfBirth(LocalDate.ofInstant(
                        faker.date().birthday(18, 60).toInstant(), ZoneId.systemDefault()))
                .mothersName(faker.name().nameWithMiddle())
                .gender(faker.number().numberBetween(0, 1) == 0 ? Gender.FEMALE : Gender.MALE)
                .address(address)
                .phoneNumber(faker.phoneNumber().cellPhone())
                .emailAddress(faker.internet().emailAddress())
                .build();
    }

}
