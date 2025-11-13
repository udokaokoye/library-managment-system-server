package com.finalproject.library_management_system_backend.mappers;

import com.finalproject.library_management_system_backend.dtos.ReservationDto;
import com.finalproject.library_management_system_backend.model.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(target = "userFirstName", expression = "java(reservation.getUser().getFirstName() + \" \" + reservation.getUser().getLastName())")
    @Mapping(source = "book.id", target = "bookId")
    @Mapping(source = "book.title", target = "bookTitle")
    ReservationDto toDto(Reservation reservation);
}
