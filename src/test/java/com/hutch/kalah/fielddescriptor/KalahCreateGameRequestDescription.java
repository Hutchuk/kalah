package com.hutch.kalah.fielddescriptor;

import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.PayloadDocumentation;

public class KalahCreateGameRequestDescription {

    private KalahCreateGameRequestDescription(){}

    public static FieldDescriptor[] KalahCreateGameRequest() {
        return new FieldDescriptor[]{
                PayloadDocumentation.fieldWithPath("playerOneId").description("The playerId for player one.  Note that player one always moves first."),
                PayloadDocumentation.fieldWithPath("playerTwoId").description("The playerId for player two.")
        };
    }
}
