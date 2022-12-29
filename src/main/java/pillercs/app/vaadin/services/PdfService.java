package pillercs.app.vaadin.services;

import com.itextpdf.html2pdf.HtmlConverter;
import com.vaadin.flow.spring.annotation.SpringComponent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pillercs.app.vaadin.data.entity.CashLoanProduct;
import pillercs.app.vaadin.data.entity.Client;
import pillercs.app.vaadin.data.entity.Offer;
import pillercs.app.vaadin.data.repository.ApplicantRepository;
import pillercs.app.vaadin.data.repository.CashLoanProductRepository;
import pillercs.app.vaadin.data.repository.ClientRepository;
import pillercs.app.vaadin.data.repository.OfferRepository;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@SpringComponent
@RequiredArgsConstructor
@Slf4j
public class PdfService {
    private final ApplicantRepository applicantRepository;
    private final CashLoanProductRepository cashLoanProductRepository;
    private final OfferRepository offerRepository;

    public void createPdf(long applicationId) {
        Client client = applicantRepository.findApplicantsByApplicationIdWithClients(applicationId).get(0).getClient();
        String clientName = client.getFirstName() + "_" + client.getLastName();
        File output = new File(clientName + ".pdf");

        try {
            output.createNewFile();
            HtmlConverter.convertToPdf(createPersonalizedContract(applicationId),
                    new FileOutputStream(output));
            log.info("Pdf was created");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String createPersonalizedContract(long applicationId) {
        String filePath = "META-INF/resources/contract/contract.html";

        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(filePath);

        StringBuilder sb = new StringBuilder();

        try (InputStreamReader streamReader =
                     new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(streamReader)) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(replaceParams(line, applicationId));
                sb.append(System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    private String replaceParams(String line, long applicationId) {
        Map<String, String> replacementMap = createParams(applicationId);

        for (Map.Entry<String, String> entry : replacementMap.entrySet()) {
            line = line.replace(entry.getKey(), entry.getValue());
        }

        return line;
    }

    private Map<String, String> createParams(long applicationId) {
        Map<String, String> replacementMap = new HashMap<>();

        Offer offer = offerRepository.findByUnderwriting_Application_ApplicationId(applicationId)
                .stream()
                .filter(Offer::getAccepted)
                .findAny()
                .orElseThrow();

        Client client = applicantRepository.findApplicantsByApplicationIdWithClients(applicationId).get(0).getClient();
        String clientName = client.getFirstName() + " " + client.getLastName();
        replacementMap.put("${NAME}", clientName);

        Locale hungarianLocale = new Locale("hu", "HU");
        NumberFormat numberFormatter = NumberFormat.getInstance(hungarianLocale);
        numberFormatter.setGroupingUsed(true);
        long amount = offer.getLoanAmount();
        replacementMap.put("${AMOUNT}", numberFormatter.format(amount) + " HUF");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        replacementMap.put("${DATE}", LocalDate.now().format(formatter));
        replacementMap.put("${TERM}", offer.getTerm().toString());

        CashLoanProduct cashLoanProduct = cashLoanProductRepository.findCashLoanProductByValidToIsNull().orElseThrow();
        Double interestRate = cashLoanProduct.getInterestRate() * 100;
        replacementMap.put("${INTEREST}", interestRate.toString());

        return replacementMap;
    }
}
