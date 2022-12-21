package pillercs.app.vaadin.data.generator;

import com.vaadin.flow.spring.annotation.SpringComponent;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import pillercs.app.vaadin.data.entity.*;
import pillercs.app.vaadin.data.entity.IncomeType;
import pillercs.app.vaadin.data.enums.*;
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
                                      ApplicationRepository applicationRepository,
                                      RuleMessageRepository ruleMessageRepository) {
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
                        .state(ApplicationState.RECORD_BASIC_INFORMATION)
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

            addRuleMessages(ruleMessageRepository);

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

        String firstName = "";
        String lastName = "";

        while (firstName.length() < 3 || lastName.length() < 3) {
            firstName = faker.name().firstName();
            lastName = faker.name().lastName();
        }

        return Client.builder()
                .firstName(firstName)
                .lastName(lastName)
                .dateOfBirth(LocalDate.ofInstant(
                        faker.date().birthday(18, 60).toInstant(), ZoneId.systemDefault()))
                .mothersName(faker.name().nameWithMiddle())
                .gender(faker.number().numberBetween(0, 1) == 0 ? Gender.FEMALE : Gender.MALE)
                .address(address)
                .phoneNumber(faker.phoneNumber().cellPhone())
                .emailAddress(faker.internet().emailAddress())
                .build();
    }

    private void addRuleMessages(RuleMessageRepository ruleMessageRepository) {
        List<RuleMessageEntity> messages = new ArrayList<>();

        messages.add(RuleMessageEntity.builder()
                .messageType(MessageType.APPROVED)
                .ruleName(RuleName.OFFER_AVAILABILITY)
                .message("An offer can be calculated")
                .build());
        messages.add(RuleMessageEntity.builder()
                .messageType(MessageType.DECLINED)
                .ruleName(RuleName.OFFER_AVAILABILITY)
                .message("Clients income is too low or has too much outstanding loans")
                .build());
        messages.add(RuleMessageEntity.builder()
                .messageType(MessageType.APPROVED)
                .ruleName(RuleName.OFFER_CALCULATION)
                .message("Offers calculated")
                .build());
        messages.add(RuleMessageEntity.builder()
                .messageType(MessageType.DECLINED)
                .ruleName(RuleName.OFFER_CALCULATION)
                .message("No offers available")
                .build());
        messages.add(RuleMessageEntity.builder()
                .messageType(MessageType.APPROVED)
                .ruleName(RuleName.MIN_EMPLOYMENT)
                .message("The client has been working for the requested minimum time")
                .build());
        messages.add(RuleMessageEntity.builder()
                .messageType(MessageType.DECLINED)
                .ruleName(RuleName.MIN_EMPLOYMENT)
                .message("The minimum requirements for employment start date has not been met")
                .build());
        messages.add(RuleMessageEntity.builder()
                .messageType(MessageType.APPROVED)
                .ruleName(RuleName.EMPLOYMENT_END_DATE)
                .message("The employment end date matches the requirements")
                .build());
        messages.add(RuleMessageEntity.builder()
                .messageType(MessageType.DECLINED)
                .ruleName(RuleName.EMPLOYMENT_END_DATE)
                .message("The employment end date does not match the requirements")
                .build());
        messages.add(RuleMessageEntity.builder()
                .messageType(MessageType.APPROVED)
                .ruleName(RuleName.MIN_INCOME)
                .message("Income amount is acceptable")
                .build());
        messages.add(RuleMessageEntity.builder()
                .messageType(MessageType.DECLINED)
                .ruleName(RuleName.MIN_INCOME)
                .message("The minimum income rule has not been met")
                .build());
        messages.add(RuleMessageEntity.builder()
                .messageType(MessageType.APPROVED)
                .ruleName(RuleName.FRAUDSTER_APPLICANTS)
                .message("No bad track record found for client")
                .build());
        messages.add(RuleMessageEntity.builder()
                .messageType(MessageType.DECLINED)
                .ruleName(RuleName.FRAUDSTER_APPLICANTS)
                .message("One of the applicants was convicted due to malicious activity in the past")
                .build());
        messages.add(RuleMessageEntity.builder()
                .messageType(MessageType.APPROVED)
                .ruleName(RuleName.APPLICATION_COUNT)
                .message("Application count is acceptable")
                .build());
        messages.add(RuleMessageEntity.builder()
                .messageType(MessageType.DECLINED)
                .ruleName(RuleName.APPLICATION_COUNT)
                .message("One of the applicants had too many applications in the examined period")
                .build());

        ruleMessageRepository.saveAll(messages);
    }

}
