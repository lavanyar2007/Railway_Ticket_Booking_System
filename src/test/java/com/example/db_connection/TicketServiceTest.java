package com.example.db_connection;


import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.db_connection.entity.Ticket;
import com.example.db_connection.entity.Train;
import com.example.db_connection.entity.User;
import com.example.db_connection.repository.TicketRepository;
import com.example.db_connection.services.TicketService;
import com.example.db_connection.services.UserService;
import com.example.db_connection.services.TrainService;

@ExtendWith(MockitoExtension.class)
public class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private UserService userService;

    @Mock
    private TrainService trainService;

    @InjectMocks
    private TicketService ticketService;

    private Ticket ticket;
    private User user;
    private Train train;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");

        train = new Train();
        train.setId(1L);
        train.setName("Chennai Express");
        train.setSource("CBE");
        train.setDestination("CHENNAI");
        train.setBasePrice(1000);
        train.setDiscountPercentage(10);

        ticket = new Ticket();
        // ticket.setId(1L);
        ticket.setUser(user);
        ticket.setTrain(train);
    }

    @Test
    void getAllTickets_ShouldReturnListOfTickets() {
        List<Ticket> tickets = Arrays.asList(ticket);
        when(ticketRepository.findAll()).thenReturn(tickets);

        List<Ticket> result = ticketService.getAllTickets();

        assertEquals(1, result.size());
        assertEquals(ticket.getUser(), result.get(0).getUser());
        assertEquals(ticket.getTrain(), result.get(0).getTrain());
        verify(ticketRepository, times(1)).findAll();
    }

    @Test
    void getTicketById_WhenTicketExists_ShouldReturnTicket() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));

        Optional<Ticket> result = ticketService.getTicketById(1L);

        assertTrue(result.isPresent());
        assertEquals(ticket.getUser(), result.get().getUser());
        assertEquals(ticket.getTrain(), result.get().getTrain());
        verify(ticketRepository, times(1)).findById(1L);
    }

    @Test
    void getTicketById_WhenTicketDoesNotExist_ShouldReturnEmpty() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Ticket> result = ticketService.getTicketById(1L);

        assertFalse(result.isPresent());
        verify(ticketRepository, times(1)).findById(1L);
    }

    @Test
    void createTicket_ShouldSaveAndReturnTicket() {
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);
        when(trainService.getTrainById(1L)).thenReturn(Optional.of(train));
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));

        Ticket result = ticketService.createTicket(user.getId(), train.getId());

        assertNotNull(result);
        assertEquals(ticket.getUser(), result.getUser());
        assertEquals(ticket.getTrain(), result.getTrain());
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    @Test
    void updateTicket_WhenTicketExists_ShouldUpdateAndReturnTicket() {
        Ticket updatedTicket = new Ticket();
        user.setName("Harshini");

        updatedTicket.setUser(user);
        updatedTicket.setTrain(train);

        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        Ticket result = ticketService.updateTicket(1L, updatedTicket);

        assertEquals(updatedTicket.getUser(), result.getUser());
        assertEquals(updatedTicket.getTrain(), result.getTrain());
        verify(ticketRepository, times(1)).findById(1L);
        verify(ticketRepository, times(1)).save(ticket);
    }

    @Test
    void updateTicket_WhenTicketDoesNotExist_ShouldThrowException() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> ticketService.updateTicket(1L, ticket));
        verify(ticketRepository, times(1)).findById(1L);
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    void deleteTicket_ShouldDeleteTicket() {
        doNothing().when(ticketRepository).deleteById(1L);

        ticketService.deleteTicket(1L);

        verify(ticketRepository, times(1)).deleteById(1L);
    }
}
