package in.lakshay.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import in.lakshay.dto.ShowtimeDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDTO {
    private Long id;
    private String userName;
    private Long showtimeId;
    private String movieTitle;
    private String theaterName;
    private String showDate;
    private String showTime;
    private List<SeatDTO> seats;
    private LocalDateTime reservationTime;
    private Integer statusId;
    private String statusValue;
    private Double totalPrice;
    private boolean paid;
    private ShowtimeDTO showtime;

    // Add username field for frontend compatibility
    public String getUsername() {
        return userName;
    }

    // Add user object for frontend compatibility
    public Map<String, Object> getUser() {
        Map<String, Object> user = new HashMap<>();
        user.put("userName", this.userName);
        user.put("username", this.userName);
        user.put("id", 0); // Default ID
        user.put("email", ""); // Default email
        return user;
    }

    // Explicit setter for showtime field
    public void setShowtime(ShowtimeDTO showtime) {
        this.showtime = showtime;
    }

    // Explicit setter for showTime field
    public void setShowTime(String showTime) {
        this.showTime = showTime;
    }

    // Explicit setter for showDate field
    public void setShowDate(String showDate) {
        this.showDate = showDate;
    }
}
