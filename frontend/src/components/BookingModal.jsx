import React, { useState, useEffect } from 'react';
import { calculatePrice, createBooking } from '../api/api';

export default function BookingModal({ hotel, initialCheckIn, initialCheckOut, onClose, onBookingSuccess }) {
  const today = new Date().toISOString().split('T')[0];

  const [guestName, setGuestName] = useState('');
  const [guestEmail, setGuestEmail] = useState('');
  const [checkIn, setCheckIn] = useState(initialCheckIn || '');
  const [checkOut, setCheckOut] = useState(initialCheckOut || '');

  const [priceQuote, setPriceQuote] = useState(null);
  const [loadingQuote, setLoadingQuote] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [errorMsg, setErrorMsg] = useState('');

  // Dynamically query backend for price calculation when dates change
  useEffect(() => {
    if (!hotel || !checkIn || !checkOut) {
      setPriceQuote(null);
      return;
    }

    if (checkIn >= checkOut) {
      setPriceQuote(null);
      return;
    }

    if (checkIn < today) {
      setErrorMsg('Check-in date cannot be in the past.');
      setPriceQuote(null);
      return;
    }

    setErrorMsg('');
    setLoadingQuote(true);

    calculatePrice(hotel.id, checkIn, checkOut)
      .then((res) => {
        if (res.success && res.data) {
          setPriceQuote(res.data);
          setErrorMsg('');
        }
      })
      .catch((err) => {
        setPriceQuote(null);
        setErrorMsg(err.message || 'Unable to calculate price for selected dates.');
      })
      .finally(() => {
        setLoadingQuote(false);
      });
  }, [hotel, checkIn, checkOut, today]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrorMsg('');

    if (!guestName.trim()) {
      setErrorMsg('Please enter your full name.');
      return;
    }

    if (!guestEmail.trim()) {
      setErrorMsg('Please enter your email address.');
      return;
    }

    if (!checkIn || !checkOut) {
      setErrorMsg('Please select both check-in and check-out dates.');
      return;
    }

    if (checkIn < today) {
      setErrorMsg('Check-in date cannot be in the past.');
      return;
    }

    if (checkOut <= checkIn) {
      setErrorMsg('Check-out date must be after check-in date.');
      return;
    }

    try {
      setSubmitting(true);
      const payload = {
        hotelId: hotel.id,
        guestName: guestName.trim(),
        guestEmail: guestEmail.trim(),
        checkInDate: checkIn,
        checkOutDate: checkOut
      };

      const response = await createBooking(payload);
      if (response.success && response.data) {
        onBookingSuccess(response.data);
      }
    } catch (err) {
      setErrorMsg(err.message || 'Failed to complete booking. Please try again.');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <div>
            <h2 className="modal-title serif">Reserve Your Stay</h2>
            <div style={{ fontSize: '13px', color: 'var(--text-muted)' }}>
              {hotel.name} — {hotel.location}
            </div>
          </div>
          <button type="button" className="modal-close-btn" onClick={onClose} aria-label="Close modal">
            ✕
          </button>
        </div>

        <div className="modal-body">
          {errorMsg && (
            <div className="alert-banner alert-error">
              <span>⚠️ {errorMsg}</span>
            </div>
          )}

          <form onSubmit={handleSubmit} className="form-grid">
            <div className="input-group">
              <label className="input-label" htmlFor="guest-name">Full Name *</label>
              <input
                id="guest-name"
                type="text"
                className="input-control"
                placeholder="e.g. Eleanor Vance"
                required
                value={guestName}
                onChange={(e) => setGuestName(e.target.value)}
              />
            </div>

            <div className="input-group">
              <label className="input-label" htmlFor="guest-email">Email Address *</label>
              <input
                id="guest-email"
                type="email"
                className="input-control"
                placeholder="e.g. eleanor@example.com"
                required
                value={guestEmail}
                onChange={(e) => setGuestEmail(e.target.value)}
              />
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' }}>
              <div className="input-group">
                <label className="input-label" htmlFor="modal-checkin">Check-in Date *</label>
                <input
                  id="modal-checkin"
                  type="date"
                  className="input-control"
                  min={today}
                  required
                  value={checkIn}
                  onChange={(e) => setCheckIn(e.target.value)}
                />
              </div>

              <div className="input-group">
                <label className="input-label" htmlFor="modal-checkout">Check-out Date *</label>
                <input
                  id="modal-checkout"
                  type="date"
                  className="input-control"
                  min={checkIn || today}
                  required
                  value={checkOut}
                  onChange={(e) => setCheckOut(e.target.value)}
                />
              </div>
            </div>

            {/* Price Calculation (Server-Side) */}
            {loadingQuote && (
              <div style={{ padding: '16px', textAlign: 'center', color: 'var(--text-muted)', fontSize: '13px' }}>
                Calculating verified backend price...
              </div>
            )}

            {priceQuote && (
              <div className="price-breakdown-box">
                <div style={{ fontSize: '12px', fontWeight: '700', textTransform: 'uppercase', color: 'var(--text-muted)', marginBottom: '10px' }}>
                  Server-Verified Price Breakdown
                </div>
                <div className="breakdown-row">
                  <span>Room Rate:</span>
                  <span>${priceQuote.roomPricePerNight} / night</span>
                </div>
                <div className="breakdown-row">
                  <span>Length of Stay:</span>
                  <span>{priceQuote.numberOfNights} night(s)</span>
                </div>
                <div className="breakdown-row">
                  <span>Available Capacity:</span>
                  <span style={{ color: 'var(--status-success-text)', fontWeight: '600' }}>
                    {priceQuote.availableRooms} room(s) remaining
                  </span>
                </div>
                <div className="breakdown-row breakdown-total">
                  <span>Total Amount:</span>
                  <span style={{ color: 'var(--accent-warm)' }}>${priceQuote.totalAmount}</span>
                </div>
              </div>
            )}

            <div style={{ display: 'flex', gap: '12px', justifyContent: 'flex-end', marginTop: '12px' }}>
              <button type="button" className="btn btn-secondary" onClick={onClose} disabled={submitting}>
                Cancel
              </button>
              <button
                type="submit"
                className="btn btn-primary"
                disabled={submitting || (priceQuote && priceQuote.availableRooms <= 0)}
              >
                {submitting ? 'Confirming Reservation...' : 'Confirm & Book Room'}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}
