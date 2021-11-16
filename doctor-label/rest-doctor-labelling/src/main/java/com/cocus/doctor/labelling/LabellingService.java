package com.cocus.doctor.labelling;

import org.jboss.logging.Logger;

import com.cocus.doctor.labelling.client.Label;
import com.cocus.doctor.labelling.client.LabelService;

import io.quarkus.cache.CacheInvalidate;
import io.quarkus.cache.CacheResult;

import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

import static javax.transaction.Transactional.TxType.REQUIRED;
import static javax.transaction.Transactional.TxType.SUPPORTS;

@ApplicationScoped
@Transactional(REQUIRED)
public class LabellingService {

	private static final Logger LOGGER = Logger.getLogger(LabellingService.class);

	@Inject
	@RestClient
	LabelService labelService;
	
	@Transactional(SUPPORTS)
	@CacheResult(cacheName = "all-cases-cache")
	public List<MedicalCase> findAllCases() {
		return MedicalCase.listAll();
	}
	
	@CacheInvalidate(cacheName = "all-cases-cache")
    public void invalidateAllCasesCache() {}

	@Transactional(SUPPORTS)
	@CacheResult(cacheName = "case-cache")
	public MedicalCase findCaseById(Long id) {
		return MedicalCase.findById(id);
	}
	
	@CacheInvalidate(cacheName = "case-cache")
    public void invalidateCaseByIdCache(Long id) {}
	
	@Transactional(SUPPORTS)
	@CacheResult(cacheName = "case-by-label-cache")
	public List<MedicalCase> findCasesByLabel(Long id) {
		return MedicalCase.findByLabelId(id);
	}
	
	@CacheInvalidate(cacheName = "case-by-label-cache")
    public void invalidateCaseByLabelCache(Long id) {}

	public MedicalCase persistCase(MedicalCase cas) {
		MedicalCase.persist(cas);
		invalidateAllCasesCache();
        return cas;
	}
	
	public MedicalCase updateCase(@Valid MedicalCase cas) {
		
		Optional<MedicalCase> optional = MedicalCase.findByIdOptional(cas.id);
		MedicalCase entity = optional.orElseThrow(() -> new NotFoundException());

        entity.description = cas.description;
        entity.label = cas.label;
        entity.doctorId = cas.doctorId;
        entity.caseLabelTime = cas.caseLabelTime;
        invalidateAllCasesCache();
        invalidateCaseByIdCache(cas.id);
        invalidateCaseByLabelCache(cas.label);
        return entity;
    }
	
	public void deleteCase(Long id) {
        
		Optional<MedicalCase> optional = MedicalCase.findByIdOptional(id);
		MedicalCase cas = optional.orElseThrow(() -> new NotFoundException());
		
        invalidateAllCasesCache();
        invalidateCaseByIdCache(cas.id);
        invalidateCaseByLabelCache(cas.label);
        cas.delete();
    }
	
//	@Fallback(fallbackMethod = "fallbackFindAllLabels")
	@Transactional(SUPPORTS)
	public List<Label> findAllLabels() {
		return labelService.getLabels();
	}
	
//	public List<Label> fallbackFindAllLabels() {
//		List<Label>
//		return label;
//	}
	
	@Fallback(fallbackMethod = "fallbackFindLabelOfCase")
	@Transactional(SUPPORTS)
	public Label findLabelOfCase(Long labelId) {
		return labelService.getLabel(labelId);
	}
	
	public Label fallbackFindLabelOfCase(Long labelId) {
		Label label = new Label();
		label.id = labelId;
		return label;
	}
	
	@Fallback(fallbackMethod = "fallbackNewLabelToCase")
	public MedicalCase newLabelToCase(Label label, Long caseId) {
		
		String labelLocation = labelService.addLabel(label).getHeaderString("location");
		String[] segments = labelLocation.split("/");
        String labelId = segments[segments.length - 1];
		
        MedicalCase cas = findCaseById(caseId);     
        cas.label = Long.valueOf(labelId);
        return cas;
    }

	public MedicalCase fallbackNewLabelToCase(Label label, Long caseId) {
        MedicalCase cas = findCaseById(caseId);
        return cas;
	}
	
	@Fallback(fallbackMethod = "fallbackDeleteLabel")
    public Boolean deleteLabel(Long labelId) {
		labelService.deleteLabel(labelId);
		return true;
    }
	
	public Boolean fallbackDeleteLabel(Long labelId) {
		return false;
    }

}
