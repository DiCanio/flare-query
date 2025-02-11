import de.rwth.imi.flare.api.model.*;
import de.rwth.imi.flare.api.model.mapping.MappingEntry;
import de.rwth.imi.flare.executor.AuthlessRequestorConfig;
import de.rwth.imi.flare.executor.FlareExecutor;
import de.rwth.imi.flare.requestor.FhirRequestorConfig;
import de.rwth.imi.flare.requestor.FlareThreadPoolConfig;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ExecutorTest
{
    FhirRequestorConfig config;
    FlareExecutor executor;

    public ExecutorTest() throws URISyntaxException {
        config = new AuthlessRequestorConfig(new URI("http://localhost:8080/fhir/"), "50", new FlareThreadPoolConfig(4,16,10));
        executor = new FlareExecutor(config);
    }

    @Test
    public void testExecutor() throws ExecutionException, InterruptedException {
        Query query = buildQuery();
        CompletableFuture<Integer> calc = executor.calculatePatientCount(query);
        System.out.printf("found %d values", calc.get());
    }

    private Query buildQuery() {
        List<TerminologyCode> female_terminology = Arrays.asList(new TerminologyCode("76689-9", "http://loinc.org", "Sex assigned at birth"));
        ValueFilter female_filter = new ValueFilter(FilterType.CONCEPT, List.of(new TerminologyCode("female", "http://hl7.org/fhir/administrative-gender", "Female")), null, null , null, null, null);
        MappingEntry mapping = new MappingEntry(null, "Observation","code", "value-concept", new ArrayList<>(), "", new ArrayList<>());
        Criterion criterion1 = new Criterion(female_terminology, female_filter, mapping, null, null);
        CriteriaGroup criteriaGroup1 = new CriteriaGroup(List.of(criterion1));

        Query expectedResult = new Query();
        expectedResult.setInclusionCriteria(List.of(criteriaGroup1));
        expectedResult.setExclusionCriteria(new ArrayList<>());
        return expectedResult;
    }
}
