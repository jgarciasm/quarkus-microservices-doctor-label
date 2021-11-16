package com.cocus.doctor.labelling.client;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.validation.constraints.NotNull;

@Schema(description="The label assigned to a case")
public class Label {

	@NotNull
	public Long id;
    @NotNull
    public String code;
    @NotNull
    public String description;
    
    @Override
    public String toString() {
    	return "{" +
	            "\"id\":" + id +
	            ", \"code\":\"" + code + "\"" +
	            ", \"description\":\"" + description + "\"" +
	            '}';
    }

}
