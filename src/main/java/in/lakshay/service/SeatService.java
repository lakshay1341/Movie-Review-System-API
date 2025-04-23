package in.lakshay.service;

import in.lakshay.dto.SeatDTO;
import in.lakshay.entity.Seat;
import in.lakshay.entity.Showtime;
import in.lakshay.exception.ResourceNotFoundException;
import in.lakshay.repo.SeatRepository;
import in.lakshay.repo.ShowtimeRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SeatService {
    private final SeatRepository seatRepository;
    private final ShowtimeRepository showtimeRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public SeatService(SeatRepository seatRepository, ShowtimeRepository showtimeRepository, ModelMapper modelMapper) {
        this.seatRepository = seatRepository;
        this.showtimeRepository = showtimeRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional(readOnly = true)
    public List<SeatDTO> getSeatsByShowtime(Long showtimeId) {
        log.info("Fetching seats for showtime id: {}", showtimeId);
        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new ResourceNotFoundException("Showtime not found with id: " + showtimeId));

        return seatRepository.findByShowtime(showtime).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SeatDTO> getAvailableSeatsByShowtime(Long showtimeId) {
        log.info("Fetching available seats for showtime id: {}", showtimeId);
        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new ResourceNotFoundException("Showtime not found with id: " + showtimeId));

        return seatRepository.findByShowtimeAndIsReserved(showtime, false).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private SeatDTO mapToDTO(Seat seat) {
        SeatDTO dto = new SeatDTO();
        dto.setId(seat.getId());
        dto.setSeatNumber(seat.getSeatNumber());
        dto.setIsReserved(seat.getIsReserved());

        if (seat.getShowtime() != null) {
            dto.setShowtimeId(seat.getShowtime().getId());
        }

        return dto;
    }
}
