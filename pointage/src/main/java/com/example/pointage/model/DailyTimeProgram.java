package com.example.pointage.model;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "program_type")
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SingleShiftDailyTimeProgram.class, name = "SINGLE_SHIFT"),
        @JsonSubTypes.Type(value = DoubleShiftDailyTimeProgram.class, name = "DOUBLE_SHIFT")
})
@Getter
@Setter
public abstract class DailyTimeProgram implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean crossMidnight;
}
