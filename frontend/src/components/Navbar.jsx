import React from 'react';

export default function Navbar({ onOpenRetrieveModal }) {
  return (
    <header className="navbar">
      <div className="navbar-inner">
        <div className="brand-logo" onClick={() => window.scrollTo({ top: 0, behavior: 'smooth' })}>
          <div className="brand-icon">A</div>
          <div>
            <div className="brand-title serif">Aura Stays</div>
            <div className="brand-subtitle">Boutique & Luxury Portals</div>
          </div>
        </div>

        <div className="nav-actions">
          <button 
            type="button" 
            className="btn btn-secondary"
            onClick={onOpenRetrieveModal}
          >
            Find My Booking
          </button>
        </div>
      </div>
    </header>
  );
}
