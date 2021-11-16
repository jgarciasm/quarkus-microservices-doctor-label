package com.cocus.doctor.labelling;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.cocus.doctor.labelling.client.Label;

import javax.inject.Inject;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Entity
@Schema(description="Each case is managed by a doctor and has an EHR (description)")
public class MedicalCase extends PanacheEntity {

    @NotNull
    @Column(columnDefinition="text")
    public String description;
    @NotNull
    public String doctorId;
    public Long label;
    public int caseLabelTime;
    
    public static List<MedicalCase> findByLabelId(Long id) {
        return find("label", id).list();
    }
    
    public String completeJsonCase(Label label) {
    	return "{" +
	            "\"id\":" + id +
	            ", \"description\":\"" + description + "\"" +
	            ", \"doctorId\":\"" + doctorId + "\"" +
	            ", \"label\":" + label +
	            ", \"caseLabelTime\":" + caseLabelTime +
	            '}';
    }

    @Override
    public String toString() {
    	return "MedicalCase{" +
	            "id=" + id +
	            ", description='" + description + '\'' +
	            ", doctorId='" + doctorId + '\'' +
	            ", label='" + label + '\'' +
	            ", caseLabelTime='" + caseLabelTime + '\'' +
	            '}';
    }

}
