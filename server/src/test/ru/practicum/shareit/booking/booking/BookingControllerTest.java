package ru.practicum.shareit.booking.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import java.time.LocalDateTime;
import java.util.Collections;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createBookingTest() throws Exception {
        NewBookingDto newBookingDto = new NewBookingDto(1L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2));
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);

        Mockito.when(bookingService.createBooking(ArgumentMatchers.eq(1L), ArgumentMatchers.any())).thenReturn(bookingDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBookingDto))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L));
    }

    @Test
    void approveBookingTest() throws Exception {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);

        Mockito.when(bookingService.approveBooking(1L, 1L, true)).thenReturn(bookingDto);

        mockMvc.perform(MockMvcRequestBuilders.patch("/bookings/1")
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1));
    }

    @Test
    void getBookingTest() throws Exception {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);

        Mockito.when(bookingService.getBookingById(1L, 1L)).thenReturn(bookingDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1));
    }

    @Test
    void getAllByUserTest() throws Exception {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);

        Mockito.when(bookingService.getAllBookingsByUser(1L, "ALL"))
                .thenReturn(Collections.singletonList(bookingDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1));
    }

    @Test
    void getAllByOwnerTest() throws Exception {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);

        Mockito.when(bookingService.getAllBookingsForOwner(1L, "ALL"))
                .thenReturn(Collections.singletonList(bookingDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1));
    }
}