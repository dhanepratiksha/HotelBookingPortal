import React, { useState } from 'react';
import { retrieveBooking, cancelBooking } from '../api/api';

export default function RetrieveBookingModal({ onClose, onBookingUpdated }) {
  const [reference, setReference] = useState('');
  const [email, setEmail] = useState('');
  const [booking, setBooking] = useState(null);
  const [loading, setLoading] = useState(false);
  const [cancelling, setCancelling] = useState(false);
  const [errorMsg, setErrorMsg] = useState('');
  const [successMsg, setSuccessMsg] = useState('');

  const handleLookup = async (e) => {
    e.preventDefault();
    setErrorMsg('');
    setSuccessMsg('');
    setBooking(null);

    if (!reference.trim() || !email.trim()) {
      setErrorMsg('Please enter both booking reference and guest email.');
      return;
    }

    try {
      setLoading(true);
      const res = await retrieveBooking(reference.trim(), email.trim());
      if (res.success && res.data) {
        setBooking(res.data);
      }
    } catch (err) {
      setErrorMsg(err.message || 'No reservation found matching this reference and email.');
    } finally {
      setLoading(false);
    }
  };

  const handleCancelBooking = async () => {
    if (!window.confirm('Are you sure you want to cancel this reservation? The room will be released.')) {
      return;
    }

    try {
      setCancelling(true);
      setErrorMsg('');
      const res = await cancelBooking(booking.bookingReference, email.trim());
      if (res.success && res.data) {
        setBooking(res.data);
        setSuccessMsg('Reservation successfully cancelled. Room availability has been released.');
        if (onBookingUpdated) {
          onBookingUpdated(); // Trigger refresh on hotel listings
        }
      }
    } catch (err) {
      setErrorMsg(err.message || 'Unable to cancel this reservation.');
    } finally {
      setCancelling(false);
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <div>
            <h2 className="modal-title serif">Manage Your Stay</h2>
            <div style={{ fontSize: '13px', color: 'var(--text-muted)' }}>
              Retrieve or cancel your reservation
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

          {successMsg && (
            <div className="alert-banner alert-success">
              <span>✓ {successMsg}</span>
            </div>
          )}

          {/* Search Form */}
          <form onSubmit={handleLookup} className="form-grid" style={{ marginBottom: '20px' }}>
            <div className="input-group">
              <label className="input-label" htmlFor="lookup-ref">Booking Reference *</label>
              <input
                id="lookup-ref"
                type="text"
                className="input-control"
                placeholder="e.g. HB-9B217D90"
                required
                value={reference}
                onChange={(e) => setReference(e.target.value.toUpperCase())}
              />
            </div>

            <div className="input-group">
              <label className="input-label" htmlFor="lookup-email">Guest Email *</label>
              <input
                id="lookup-email"
                type="email"
                className="input-control"
                placeholder="e.g. guest@example.com"
                required
                value={email}
                onChange={(e) => setEmail(e.target.value)}
              />
            </div>

            <button type="submit" className="btn btn-primary" disabled={loading}>
              {loading ? 'Searching...' : 'Find Reservation'}
            </button>
          </form>

          {/* Retrieved Booking Display */}
          {booking && (
            <div className="price-breakdown-box" style={{ background: 'var(--bg-card)' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '14px' }}>
                <span className="reference-code" style={{ fontSize: '18px', margin: 0 }}>
                  {booking.bookingReference}
                </span>

                <span
                  className="capacity-badge"
                  style={{
                    background: booking.status === 'CONFIRMED' ? 'var(--status-success-bg)' : 'var(--status-danger-bg)',
                    color: booking.status === 'CONFIRMED' ? 'var(--status-success-text)' : 'var(--status-danger-text)',
                    border: `1px solid ${booking.status === 'CONFIRMED' ? 'var(--status-success-border)' : 'var(--status-danger-border)'}`
                  }}
                >
                  {booking.status}
                </span>
              </div>

              <div className="breakdown-row">
                <span>Hotel:</span>
                <strong>{booking.hotelName}</strong>
              </div>
              <div className="breakdown-row">
                <span>Location:</span>
                <span>{booking.hotelLocation}</span>
              </div>
              <div className="breakdown-row">
                <span>Room:</span>
                <span>Room {booking.assignedRoomNumber || 'Standard'}</span>
              </div>
              <div className="breakdown-row">
                <span>Guest:</span>
                <span>{booking.guestName} ({booking.guestEmail})</span>
              </div>
              <div className="breakdown-row">
                <span>Dates:</span>
                <span>{booking.checkInDate} to {booking.checkOutDate} ({booking.numberOfNights} nights)</span>
              </div>
              <div className="breakdown-row breakdown-total">
                <span>Total Amount:</span>
                <span style={{ color: 'var(--accent-warm)' }}>${booking.totalAmount}</span>
              </div>

              {booking.status === 'CONFIRMED' && (
                <div style={{ marginTop: '16px', display: 'flex', justifyContent: 'flex-end' }}>
                  <button
                    type="button"
                    className="btn btn-danger"
                    disabled={cancelling}
                    onClick={handleCancelBooking}
                  >
                    {cancelling ? 'Cancelling...' : 'Cancel This Reservation'}
                  </button>
                </div>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
