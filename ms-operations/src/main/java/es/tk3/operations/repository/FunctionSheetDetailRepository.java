package es.tk3.operations.repository;

import es.tk3.operations.model.FunctionSheetDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FunctionSheetDetailRepository extends JpaRepository<FunctionSheetDetail, Long> {

    @Query("select f from FunctionSheetDetail f WHERE f.functionSheet.id =:functionSheetId")
    Optional<FunctionSheetDetail> findByFunctionSheetDetails(@Param("functionSheetId") UUID functionSheetId);

}
