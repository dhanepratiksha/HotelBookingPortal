import React from 'react';

export default function BookingConfirmationModal({ booking, onClose }) {
  if (!booking) return null;

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <div>
            <div style={{ display: 'inline-flex', alignItems: 'center', gap: '6px', color: 'var(--status-success-text)', fontSize: '13px', fontWeight: '700' }}>
              ✓ Reservation Confirmed
            </div>
            <h2 className="modal-title serif">Booking Confirmation</h2>
          </div>
          <button type="button" className="modal-close-btn" onClick={onClose} aria-label="Close modal">
            ✕
          </button>
        </div>

        <div className="modal-body">
          <div className="reference-card">
            <div className="reference-label">Your Booking Reference</div>
            <div className="reference-code">{booking.bookingReference}</div>
            <div style={{ fontSize: '12px', color: 'var(--text-muted)' }}>
              Save this reference code with your email to retrieve or manage your stay.
            </div>
          </div>

          <div className="price-breakdown-box">
            <h4 style={{ fontSize: '13px', textTransform: 'uppercase', color: 'var(--text-muted)', marginBottom: '12px', letterSpacing: '0.5px' }}>
              Reservation Summary
            </h4>

            <div className="breakdown-row">
              <span>Hotel:</span>
              <strong>{booking.hotelName}</strong>
            </div>
            <div className="breakdown-row">
              <span>Location:</span>
              <span>{booking.hotelLocation}</span>
            </div>
            <div className="breakdown-row">
              <span>Assigned Room:</span>
              <span>Room {booking.assignedRoomNumber || 'Standard'}</span>
            </div>
            <div className="breakdown-row">
              <span>Guest Name:</span>
              <span>{booking.guestName}</span>
            </div>
            <div className="breakdown-row">
              <span>Guest Email:</span>
              <span>{booking.guestEmail}</span>
            </div>
            <div className="breakdown-row">
              <span>Dates:</span>
              <span>{booking.checkInDate} to {booking.checkOutDate}</span>
            </div>
            <div className="breakdown-row">
              <span>Nights:</span>
              <span>{booking.numberOfNights} night(s)</span>
            </div>
            <div className="breakdown-row">
              <span>Room Rate:</span>
              <span>${booking.roomPricePerNight} / night</span>
            </div>
            <div className="breakdown-row breakdown-total">
              <span>Total Paid / Due:</span>
              <span style={{ color: 'var(--accent-warm)' }}>${booking.totalAmount}</span>
            </div>
          </div>

          <div style={{ display: 'flex', justifyContent: 'flex-end', marginTop: '16px' }}>
            <button type="button" className="btn btn-primary" onClick={onClose}>
              Done
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
