package com.lightfall.eshop.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Rating {
    private int ratingId;
    private int userId;
    private int bookId;
    private BigDecimal rating;
    private Timestamp ratingTime;

    public Rating(int userId, int bookId, BigDecimal rating) {
        this.userId = userId;
        this.bookId = bookId;
        this.rating = rating;
    }
}
