package com.cocus.doctor.labelling.client;

import io.quarkus.test.Mock;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.net.URI;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

@Mock
@ApplicationScoped
@RestClient
public class MockLabelService implements LabelService {

    public static final String DEFAULT_LABEL_CODE = "L4B3L-C0D3";
    public static final String DEFAULT_LABEL_DESCRIPTION = "D3SCR1PT10N";
    public static final int DEFAULT_LABEL_ID = 3;
    public static final String DEFAULT_LABEL_CREATED_URI = "http://localhost:8083/api/labels/" + DEFAULT_LABEL_ID;

	@Override
	public Response addLabel(Label label) {
		System.out.println("@@@ Mock 1 con label= " + label.code);
		URI uri = URI.create(DEFAULT_LABEL_CREATED_URI);
		return Response.created(uri).build();
	}

	@Override
	public Label getLabel(long id) {
		System.out.println("@@@ Mock 2 con id= " + id);
		Label label = new Label();
		label.id = id;
        label.code = DEFAULT_LABEL_CODE;
        label.description = DEFAULT_LABEL_DESCRIPTION;
        return label;
	}

	@Override
	public void deleteLabel(long id) {}

	@Override
	public List<Label> getLabels() {
		Label[] labels = new Label[2];
		Label label = new Label();
		label.id = 1L;
        label.code = DEFAULT_LABEL_CODE;
        label.description = DEFAULT_LABEL_DESCRIPTION;
        labels[0] = label;
        
        label = new Label();
		label.id = 2L;
        label.code = DEFAULT_LABEL_CODE + 2;
        label.description = DEFAULT_LABEL_DESCRIPTION + 2;
        labels[1] = label;
        
        return List.of(labels);
	}
}