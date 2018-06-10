package com.hutch.kalah.fielddescriptor;

import org.springframework.restdocs.payload.FieldDescriptor;

import java.math.BigInteger;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class KalahMoveRequestDescription {

    private KalahMoveRequestDescription() {
    }

    public static FieldDescriptor[] KalahMoveRequest() {
        return new FieldDescriptor[]{
                fieldWithPath("gameId").description("The identifier for the kalah game you want to perform the move on.").type(Long.class),
                fieldWithPath("movingPlayerId").description("The identifier for the player making the move.").type(BigInteger.class),
                fieldWithPath("pit").description("The pit the move will start from (Stones collected from this pit)" ).type(Integer.class)
};
    }
}
