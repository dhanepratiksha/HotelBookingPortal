import React from 'react';

export default function SmartRecommendationCard({ recommendation, onSelectHotel }) {
  if (!recommendation || !recommendation.hotel) {
    return null;
  }

  const { hotel, reason, valueScore, availableRooms } = recommendation;

  return (
    <div className="smart-recommendation-box">
      <div>
        <span className="smart-badge">
          ✦ Smart Stay Recommendation (Score: {valueScore})
        </span>
        <h3 className="smart-hotel-title">{hotel.name} — {hotel.location}</h3>
        <p className="smart-reason">{reason}</p>
      </div>

      <div style={{ display: 'flex', alignItems: 'center', gap: '16px', flexShrink: 0 }}>
        <div style={{ textAlign: 'right' }}>
          <div style={{ fontSize: '22px', fontWeight: '700', color: 'var(--text-primary)' }}>
            ${hotel.pricePerNight}
            <span style={{ fontSize: '13px', fontWeight: '400', color: 'var(--text-muted)' }}> / night</span>
          </div>
          <div style={{ fontSize: '12px', color: 'var(--status-success-text)', fontWeight: '600' }}>
            {availableRooms} room(s) available
          </div>
        </div>

        <button
          type="button"
          className="btn btn-accent"
          onClick={() => onSelectHotel(hotel, true)}
        >
          Book This Deal
        </button>
      </div>
    </div>
  );
}
