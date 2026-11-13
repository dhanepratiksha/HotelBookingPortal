import React from 'react';

export default function SearchBar({
  location,
  setLocation,
  checkIn,
  setCheckIn,
  checkOut,
  setCheckOut,
  onSearch,
  onReset
}) {
  const today = new Date().toISOString().split('T')[0];

  const handleSubmit = (e) => {
    e.preventDefault();
    onSearch();
  };

  return (
    <div className="search-card">
      <form onSubmit={handleSubmit} className="search-grid">
        <div className="input-group">
          <label className="input-label" htmlFor="location-input">Destination / Hotel</label>
          <input
            id="location-input"
            type="text"
            className="input-control"
            placeholder="e.g. Miami, Paris, Tokyo, New York"
            value={location}
            onChange={(e) => setLocation(e.target.value)}
          />
        </div>

        <div className="input-group">
          <label className="input-label" htmlFor="checkin-input">Check-in Date</label>
          <input
            id="checkin-input"
            type="date"
            className="input-control"
            min={today}
            value={checkIn}
            onChange={(e) => {
              setCheckIn(e.target.value);
              if (checkOut && e.target.value >= checkOut) {
                // Clear checkout if checkIn is set equal to or after current checkOut
                setCheckOut('');
              }
            }}
          />
        </div>

        <div className="input-group">
          <label className="input-label" htmlFor="checkout-input">Check-out Date</label>
          <input
            id="checkout-input"
            type="date"
            className="input-control"
            min={checkIn || today}
            value={checkOut}
            onChange={(e) => setCheckOut(e.target.value)}
          />
        </div>

        <div style={{ display: 'flex', gap: '8px' }}>
          <button type="submit" className="btn btn-primary" style={{ flex: 1 }}>
            Search
          </button>
          {(location || checkIn || checkOut) && (
            <button
              type="button"
              className="btn btn-secondary"
              onClick={onReset}
              title="Clear search filters"
            >
              Clear
            </button>
          )}
        </div>
      </form>
    </div>
  );
}
