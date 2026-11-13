import React from 'react';

export default function HotelDetailsModal({ hotel, checkIn, checkOut, onClose, onBookNow }) {
  if (!hotel) return null;

  const isAvailable = hotel.availableRooms > 0;

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <div>
            <h2 className="modal-title serif">{hotel.name}</h2>
            <div style={{ fontSize: '13px', color: 'var(--accent-warm)', marginTop: '2px' }}>
              📍 {hotel.location}
            </div>
          </div>
          <button type="button" className="modal-close-btn" onClick={onClose} aria-label="Close modal">
            ✕
          </button>
        </div>

        <div className="modal-body">
          <div style={{ width: '100%', height: '230px', borderRadius: 'var(--radius-md)', overflow: 'hidden', marginBottom: '16px' }}>
            <img
              src={hotel.imageUrl || 'https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=800&q=80'}
              alt={hotel.name}
              style={{ width: '100%', height: '100%', objectFit: 'cover' }}
            />
          </div>

          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
            <div>
              <div style={{ fontSize: '12px', textTransform: 'uppercase', color: 'var(--text-muted)', fontWeight: '600' }}>
                Rate per Night
              </div>
              <div style={{ fontSize: '24px', fontWeight: '800', color: 'var(--text-primary)' }}>
                ${hotel.pricePerNight}
              </div>
            </div>

            <div style={{ textAlign: 'right' }}>
              <div style={{ fontSize: '12px', textTransform: 'uppercase', color: 'var(--text-muted)', fontWeight: '600' }}>
                Availability Status
              </div>
              <span className={`capacity-badge ${isAvailable ? 'capacity-available' : 'capacity-full'}`} style={{ display: 'inline-block', marginTop: '4px' }}>
                {isAvailable ? `${hotel.availableRooms} of ${hotel.totalRooms} Rooms Available` : 'Fully Booked for Dates'}
              </span>
            </div>
          </div>

          {checkIn && checkOut && (
            <div style={{ background: 'var(--bg-secondary)', padding: '10px 14px', borderRadius: 'var(--radius-sm)', fontSize: '13px', color: 'var(--text-secondary)', marginBottom: '16px' }}>
              Selected Stay: <strong>{checkIn}</strong> to <strong>{checkOut}</strong>
            </div>
          )}

          <div style={{ marginBottom: '18px' }}>
            <h4 style={{ fontSize: '14px', textTransform: 'uppercase', color: 'var(--text-secondary)', marginBottom: '8px', letterSpacing: '0.5px' }}>
              Description
            </h4>
            <p style={{ fontSize: '14px', color: 'var(--text-secondary)', lineHeight: '1.6' }}>
              {hotel.description}
            </p>
          </div>

          <div style={{ marginBottom: '24px' }}>
            <h4 style={{ fontSize: '14px', textTransform: 'uppercase', color: 'var(--text-secondary)', marginBottom: '10px', letterSpacing: '0.5px' }}>
              Amenities & Services
            </h4>
            <div className="amenities-wrap" style={{ gap: '8px' }}>
              {hotel.amenities && hotel.amenities.map((amenity, idx) => (
                <span key={idx} className="amenity-chip" style={{ padding: '6px 12px', fontSize: '12px' }}>
                  ✓ {amenity}
                </span>
              ))}
            </div>
          </div>

          <div style={{ display: 'flex', gap: '12px', justifyContent: 'flex-end', borderTop: '1px solid var(--border-light)', paddingTop: '16px' }}>
            <button type="button" className="btn btn-secondary" onClick={onClose}>
              Close
            </button>
            <button
              type="button"
              className="btn btn-primary"
              disabled={!isAvailable}
              onClick={() => {
                onClose();
                onBookNow(hotel);
              }}
            >
              {isAvailable ? 'Proceed to Book' : 'Unavailable'}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
