package in.lakshay.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import in.lakshay.dto.ShowtimeDTO;

// reservation info for tickets
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDTO {
    private Long id;
    private String userName; // who made the reservation
    private Long showtimeId; // which showing
    private String movieTitle; // what movie
    private String theaterName; // which theater
    private String showDate; // formatted date
    private String showTime; // formatted time
    private List<SeatDTO> seats; // which seats reserved
    private LocalDateTime reservationTime; // when booked

    private Integer statusId; // status code
    private String statusValue; // human readable status
    private Double totalPrice; // how much paid
    private boolean paid; // payment status

    private ShowtimeDTO showtime; // full showtime details

    // frontend needs username not userName (ugh)
    public String getUsername() {
        return userName;
    }

    // frontend expects a user obj - hacky but works
    public Map<String, Object> getUser() {
        Map<String, Object> user = new HashMap<>();
        user.put("userName", this.userName);
        user.put("username", this.userName);
        user.put("id", 0); // Default ID - not used anyway
        user.put("email", ""); // Default email - not shown
        return user;
    }

    // explicit setters cuz lombok sometimes messes up
    public void setShowtime(ShowtimeDTO showtime) {
        this.showtime = showtime;
    }

    public void setShowTime(String showTime) {
        this.showTime = showTime;
    }

    public void setShowDate(String showDate) {
        this.showDate = showDate;
    }
}
