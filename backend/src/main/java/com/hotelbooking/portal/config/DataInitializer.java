package com.hotelbooking.portal.config;

import com.hotelbooking.portal.entity.Hotel;
import com.hotelbooking.portal.entity.Room;
import com.hotelbooking.portal.repository.HotelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * Seeds initial sample data for at least 5 hotels (with physical rooms)
 * on application startup if the database is currently empty.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final HotelRepository hotelRepository;

    public DataInitializer(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    @Override
    public void run(String... args) {
        if (hotelRepository.count() > 0) {
            log.info("Hotel database already contains {} records. Skipping seed data initialization.", hotelRepository.count());
            return;
        }

        log.info("Database empty. Seeding initial sample hotels and rooms...");

        // Hotel 1: New York
        Hotel hotel1 = new Hotel(
                "The Grand Heritage Hotel",
                "New York, USA",
                "Located in the heart of Manhattan, The Grand Heritage combines timeless architectural elegance with modern luxury. Enjoy panoramic city skyline views, award-winning dining, and effortless access to Central Park and Broadway.",
                "Free High-Speed Wi-Fi, Rooftop Swimming Pool, Complimentary Breakfast, 24/7 Fitness Center, Valet Parking, Room Service",
                new BigDecimal("220.00"),
                5,
                "https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=800&q=80",
                4.8
        );
        hotel1.addRoom(new Room("101", "Deluxe King", hotel1));
        hotel1.addRoom(new Room("102", "Deluxe King", hotel1));
        hotel1.addRoom(new Room("103", "Executive Suite", hotel1));
        hotel1.addRoom(new Room("104", "Premier Double", hotel1));
        hotel1.addRoom(new Room("105", "Skyline Suite", hotel1));

        // Hotel 2: Miami
        Hotel hotel2 = new Hotel(
                "Azure Coastal Resort & Spa",
                "Miami, USA",
                "Escape to the sun-drenched shores of South Beach. Step directly onto powder-white sands, unwind by our oceanfront infinity pool, or indulge in holistic botanical treatments at our seaside wellness center.",
                "Private Beach Access, Infinity Pool, Full-Service Spa, Free High-Speed Wi-Fi, Cocktail Beach Bar, Free Valet Parking",
                new BigDecimal("185.00"),
                4,
                "https://images.unsplash.com/photo-1582719508461-905c673771fd?auto=format&fit=crop&w=800&q=80",
                4.9
        );
        hotel2.addRoom(new Room("201", "Ocean Suite", hotel2));
        hotel2.addRoom(new Room("202", "Deluxe Cabana", hotel2));
        hotel2.addRoom(new Room("203", "Coastal King", hotel2));
        hotel2.addRoom(new Room("204", "Beachfront Studio", hotel2));

        // Hotel 3: Paris
        Hotel hotel3 = new Hotel(
                "Le Petit Marais Boutique Hotel",
                "Paris, France",
                "A romantic boutique hideaway nestled along cobblestone streets in the historic Marais district. Just a short stroll from the Louvre, artisan bakeries, and renowned Parisian art galleries.",
                "Artisan French Breakfast, Free High-Speed Wi-Fi, Espresso Lounge, City Bike Rentals, Concierge Desk, Air Conditioning",
                new BigDecimal("160.00"),
                3,
                "https://images.unsplash.com/photo-1551882547-ff40c63fe5fa?auto=format&fit=crop&w=800&q=80",
                4.7
        );
        hotel3.addRoom(new Room("301", "Classic Parisian", hotel3));
        hotel3.addRoom(new Room("302", "Marais Balcony Suite", hotel3));
        hotel3.addRoom(new Room("303", "Artist Atelier", hotel3));

        // Hotel 4: Tokyo
        Hotel hotel4 = new Hotel(
                "Shinjuku Garden Suites",
                "Tokyo, Japan",
                "Where modern Tokyo convenience meets tranquil Japanese minimalism. Features soothing indoor cypress onsen baths, serene Zen rock garden views, and seamless high-speed transit connections.",
                "Traditional Onsen Bath, Free High-Speed Wi-Fi, Japanese Rock Garden, Smart Room Controls, 24/7 Tea Station, Metro Proximity",
                new BigDecimal("135.00"),
                6,
                "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?auto=format&fit=crop&w=800&q=80",
                4.8
        );
        hotel4.addRoom(new Room("401", "Modern Tatami", hotel4));
        hotel4.addRoom(new Room("402", "City View Queen", hotel4));
        hotel4.addRoom(new Room("403", "Shinjuku Suite", hotel4));
        hotel4.addRoom(new Room("404", "Zen King", hotel4));
        hotel4.addRoom(new Room("405", "Compact Studio", hotel4));
        hotel4.addRoom(new Room("406", "Garden Deluxe", hotel4));

        // Hotel 5: London
        Hotel hotel5 = new Hotel(
                "The Royal Mayfair Residence",
                "London, UK",
                "Quintessential British sophistication in prestigious Mayfair. Surrounded by world-class Bond Street shopping and lush royal parks, offering discrete luxury and bespoke hospitality.",
                "Afternoon Tea Salon, Butler Service, Free High-Speed Wi-Fi, Marble Bathrooms, Executive Meeting Suite, Valet Parking",
                new BigDecimal("210.00"),
                4,
                "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?auto=format&fit=crop&w=800&q=80",
                4.9
        );
        hotel5.addRoom(new Room("501", "Mayfair King", hotel5));
        hotel5.addRoom(new Room("502", "Royal Suite", hotel5));
        hotel5.addRoom(new Room("503", "Westminster Queen", hotel5));
        hotel5.addRoom(new Room("504", "Kensington Club", hotel5));

        // Hotel 6: Zurich (Bonus Alpine hotel)
        Hotel hotel6 = new Hotel(
                "Alpine Serenity Lodge",
                "Zurich, Switzerland",
                "A scenic mountain sanctuary featuring sweeping views of snow-capped peaks. Relax by roaring stone fireplaces, rejuvenate in the cedar sauna, and enjoy direct trail access.",
                "Panoramic Mountain Balcony, Heated Indoor Pool, Finnish Sauna, Fireplace Lounge, Free High-Speed Wi-Fi, Ski Gear Storage",
                new BigDecimal("195.00"),
                3,
                "https://images.unsplash.com/photo-1571896349842-33c89424de2d?auto=format&fit=crop&w=800&q=80",
                4.9
        );
        hotel6.addRoom(new Room("601", "Alpine Loft", hotel6));
        hotel6.addRoom(new Room("602", "Mountain View Suite", hotel6));
        hotel6.addRoom(new Room("603", "Swiss Pine Room", hotel6));

        List<Hotel> sampleHotels = Arrays.asList(hotel1, hotel2, hotel3, hotel4, hotel5, hotel6);
        hotelRepository.saveAll(sampleHotels);

        log.info("Successfully seeded {} sample hotels with their respective room capacities.", sampleHotels.size());
    }
}
