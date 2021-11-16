package com.cocus.doctor.label;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.Random;

@Entity
@Schema(description="Each Label consists of an ICD-10 condition code and description")
public class Label extends PanacheEntity {

    @NotNull
    @Size(min = 3, max = 50)
    public String code;
    @NotNull
    public String description;

    @Override
    public String toString() {
        return "Label{" +
            "id=" + id +
            ", code=" + code +
            ", description='" + description + '\'' +
            '}';
    }

}
