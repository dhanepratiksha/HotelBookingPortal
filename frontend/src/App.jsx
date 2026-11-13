import React, { useState, useEffect, useCallback } from 'react';
import Navbar from './components/Navbar';
import SearchBar from './components/SearchBar';
import SmartRecommendationCard from './components/SmartRecommendationCard';
import HotelCard from './components/HotelCard';
import HotelDetailsModal from './components/HotelDetailsModal';
import BookingModal from './components/BookingModal';
import BookingConfirmationModal from './components/BookingConfirmationModal';
import RetrieveBookingModal from './components/RetrieveBookingModal';
import { searchHotels, getSmartStayRecommendation } from './api/api';

export default function App() {
  const [hotels, setHotels] = useState([]);
  const [smartRecommendation, setSmartRecommendation] = useState(null);
  const [location, setLocation] = useState('');
  const [checkIn, setCheckIn] = useState('');
  const [checkOut, setCheckOut] = useState('');

  const [loading, setLoading] = useState(true);
  const [errorMsg, setErrorMsg] = useState('');

  // Modals
  const [detailsHotel, setDetailsHotel] = useState(null);
  const [bookingHotel, setBookingHotel] = useState(null);
  const [confirmedBooking, setConfirmedBooking] = useState(null);
  const [showRetrieveModal, setShowRetrieveModal] = useState(false);

  const loadHotels = useCallback(async (loc = '', cIn = '', cOut = '') => {
    try {
      setLoading(true);
      setErrorMsg('');

      // Fetch matching hotels and smart stay recommendation in parallel
      const [hotelsRes, recRes] = await Promise.all([
        searchHotels(loc, cIn, cOut),
        getSmartStayRecommendation(loc, cIn, cOut).catch(() => ({ data: null }))
      ]);

      if (hotelsRes.success && hotelsRes.data) {
        setHotels(hotelsRes.data);
      }
      if (recRes && recRes.data) {
        setSmartRecommendation(recRes.data);
      } else {
        setSmartRecommendation(null);
      }
    } catch (err) {
      setErrorMsg(err.message || 'Unable to connect to the backend server.');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadHotels('', '', '');
  }, [loadHotels]);

  const handleSearch = () => {
    loadHotels(location, checkIn, checkOut);
  };

  const handleReset = () => {
    setLocation('');
    setCheckIn('');
    setCheckOut('');
    loadHotels('', '', '');
  };

  const handleBookNow = (hotel) => {
    setBookingHotel(hotel);
  };

  const handleViewDetails = (hotel) => {
    setDetailsHotel(hotel);
  };

  const handleBookingSuccess = (booking) => {
    setBookingHotel(null);
    setConfirmedBooking(booking);
    // Refresh hotel listing to reflect updated room capacity
    loadHotels(location, checkIn, checkOut);
  };

  const handleBookingUpdated = () => {
    // Refresh hotel listing when a reservation is cancelled
    loadHotels(location, checkIn, checkOut);
  };

  return (
    <div className="app-container">
      <Navbar onOpenRetrieveModal={() => setShowRetrieveModal(true)} />

      <main className="content-wrapper">
        <section className="hero-section">
          <h1 className="hero-title serif">Discover Exceptional Sanctuaries</h1>
          <p className="hero-subtitle">
            Curated boutique hotels, transparent nightly rates, and verified live room availability.
          </p>
        </section>

        {/* Search & Date Filter Bar */}
        <SearchBar
          location={location}
          setLocation={setLocation}
          checkIn={checkIn}
          setCheckIn={setCheckIn}
          checkOut={checkOut}
          setCheckOut={setCheckOut}
          onSearch={handleSearch}
          onReset={handleReset}
        />

        {/* Global Error Banner */}
        {errorMsg && (
          <div className="alert-banner alert-error">
            <span>⚠️ {errorMsg}</span>
            <button
              type="button"
              className="btn btn-secondary"
              style={{ padding: '4px 10px', fontSize: '12px' }}
              onClick={() => loadHotels(location, checkIn, checkOut)}
            >
              Retry
            </button>
          </div>
        )}

        {/* Surprise Feature: Smart Stay Recommendation */}
        <SmartRecommendationCard
          recommendation={smartRecommendation}
          onSelectHotel={(hotel, openBooking) => {
            if (openBooking) {
              setBookingHotel(hotel);
            } else {
              setDetailsHotel(hotel);
            }
          }}
        />

        {/* Hotel Grid Section */}
        <section>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
            <h2 style={{ fontSize: '20px', fontWeight: '700' }}>
              {location ? `Properties in "${location}"` : 'Featured Properties'}
              <span style={{ fontSize: '14px', fontWeight: '500', color: 'var(--text-muted)', marginLeft: '8px' }}>
                ({hotels.length} {hotels.length === 1 ? 'hotel' : 'hotels'} found)
              </span>
            </h2>

            {checkIn && checkOut && (
              <span style={{ fontSize: '13px', color: 'var(--accent-warm)', fontWeight: '600' }}>
                Availability for {checkIn} → {checkOut}
              </span>
            )}
          </div>

          {loading ? (
            <div style={{ textAlign: 'center', padding: '60px 20px', color: 'var(--text-muted)' }}>
              <div style={{ fontSize: '24px', marginBottom: '8px' }}>⏳</div>
              <p>Finding available luxury accommodations...</p>
            </div>
          ) : hotels.length === 0 ? (
            <div style={{ textAlign: 'center', padding: '60px 20px', background: 'var(--bg-primary)', borderRadius: 'var(--radius-lg)', border: '1px solid var(--border-light)' }}>
              <div style={{ fontSize: '32px', marginBottom: '8px' }}>🏨</div>
              <h3 style={{ fontSize: '18px', marginBottom: '6px' }}>No hotels found</h3>
              <p style={{ color: 'var(--text-muted)', fontSize: '14px', marginBottom: '16px' }}>
                No properties match your current search filters. Try searching for a different destination like "Miami" or "Paris".
              </p>
              <button type="button" className="btn btn-secondary" onClick={handleReset}>
                View All Hotels
              </button>
            </div>
          ) : (
            <div className="hotels-grid">
              {hotels.map((hotel) => (
                <HotelCard
                  key={hotel.id}
                  hotel={hotel}
                  onBookNow={handleBookNow}
                  onViewDetails={handleViewDetails}
                />
              ))}
            </div>
          )}
        </section>
      </main>

      <footer className="footer">
        <p> Aura Stays | Luxury & Boutique Hotel Booking</p>
      </footer>

      {/* Modals */}
      {detailsHotel && (
        <HotelDetailsModal
          hotel={detailsHotel}
          checkIn={checkIn}
          checkOut={checkOut}
          onClose={() => setDetailsHotel(null)}
          onBookNow={handleBookNow}
        />
      )}

      {bookingHotel && (
        <BookingModal
          hotel={bookingHotel}
          initialCheckIn={checkIn}
          initialCheckOut={checkOut}
          onClose={() => setBookingHotel(null)}
          onBookingSuccess={handleBookingSuccess}
        />
      )}

      {confirmedBooking && (
        <BookingConfirmationModal
          booking={confirmedBooking}
          onClose={() => setConfirmedBooking(null)}
        />
      )}

      {showRetrieveModal && (
        <RetrieveBookingModal
          onClose={() => setShowRetrieveModal(false)}
          onBookingUpdated={handleBookingUpdated}
        />
      )}
    </div>
  );
}
