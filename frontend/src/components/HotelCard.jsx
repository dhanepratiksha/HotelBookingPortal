import React from 'react';

export default function HotelCard({ hotel, onBookNow, onViewDetails }) {
  const isAvailable = hotel.availableRooms > 0;

  return (
    <article className="hotel-card">
      <div className="card-image-wrap">
        <img
          src={hotel.imageUrl || 'https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=800&q=80'}
          alt={hotel.name}
          className="card-img"
          loading="lazy"
        />
        {hotel.rating && (
          <div className="card-rating-badge">
            <span style={{ color: 'var(--accent-gold)' }}>★</span> {hotel.rating.toFixed(1)}
          </div>
        )}
      </div>

      <div className="card-body">
        <div className="card-location">
          <span>📍</span> {hotel.location}
        </div>

        <h3 className="card-title">{hotel.name}</h3>
        <p className="card-desc">{hotel.description}</p>

        {hotel.amenities && hotel.amenities.length > 0 && (
          <div className="amenities-wrap">
            {hotel.amenities.slice(0, 4).map((amenity, idx) => (
              <span key={idx} className="amenity-chip">
                {amenity}
              </span>
            ))}
            {hotel.amenities.length > 4 && (
              <span className="amenity-chip" style={{ background: 'var(--bg-secondary)' }}>
                +{hotel.amenities.length - 4} more
              </span>
            )}
          </div>
        )}

        <div className="card-footer">
          <div className="price-box">
            <span className="price-label">Price from</span>
            <div>
              <span className="price-amount">${hotel.pricePerNight}</span>
              <span className="price-period"> / night</span>
            </div>
          </div>

          <div style={{ textAlign: 'right', display: 'flex', flexDirection: 'column', alignItems: 'flex-end', gap: '8px' }}>
            <span className={`capacity-badge ${isAvailable ? 'capacity-available' : 'capacity-full'}`}>
              {isAvailable ? `${hotel.availableRooms} of ${hotel.totalRooms} rooms left` : 'Fully Booked'}
            </span>

            <div style={{ display: 'flex', gap: '6px' }}>
              <button
                type="button"
                className="btn btn-secondary"
                style={{ padding: '8px 12px', fontSize: '13px' }}
                onClick={() => onViewDetails(hotel)}
              >
                Details
              </button>
              <button
                type="button"
                className="btn btn-primary"
                style={{ padding: '8px 14px', fontSize: '13px' }}
                disabled={!isAvailable}
                onClick={() => onBookNow(hotel)}
              >
                {isAvailable ? 'Book' : 'Unavailable'}
              </button>
            </div>
          </div>
        </div>
      </div>
    </article>
  );
}
