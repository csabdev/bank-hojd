package pillercs.app.vaadin.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pillercs.app.vaadin.data.entity.CashLoanProduct;

import java.util.Optional;

public interface CashLoanProductRepository extends JpaRepository<CashLoanProduct, Long> {

    Optional<CashLoanProduct> findCashLoanProductByValidToIsNull();
}