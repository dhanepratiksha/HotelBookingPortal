/**
 * API client communicating with Spring Boot REST Backend.
 */

const API_BASE = '/api';

/**
 * Helper to process JSON responses and handle errors uniformly.
 */
async function handleResponse(response) {
  const isJson = response.headers.get('content-type')?.includes('application/json');
  const data = isJson ? await response.json() : null;

  if (!response.ok) {
    let errorMessage = (data && data.message) || response.statusText || 'Request failed';
    if (data && data.validationErrors) {
      const fieldErrors = Object.entries(data.validationErrors)
        .map(([field, msg]) => `${field}: ${msg}`)
        .join(', ');
      errorMessage = `${errorMessage} (${fieldErrors})`;
    }
    const error = new Error(errorMessage);
    error.status = response.status;
    error.data = data;
    throw error;
  }

  return data;
}

/**
 * Search/browse hotels with optional location and check-in / check-out dates.
 */
export async function searchHotels(location = '', checkIn = '', checkOut = '') {
  const params = new URLSearchParams();
  if (location && location.trim()) params.append('location', location.trim());
  if (checkIn) params.append('checkIn', checkIn);
  if (checkOut) params.append('checkOut', checkOut);

  const url = `${API_BASE}/hotels${params.toString() ? `?${params.toString()}` : ''}`;
  const response = await fetch(url);
  return handleResponse(response);
}

/**
 * Fetch detailed information for a specific hotel.
 */
export async function getHotelDetails(hotelId, checkIn = '', checkOut = '') {
  const params = new URLSearchParams();
  if (checkIn) params.append('checkIn', checkIn);
  if (checkOut) params.append('checkOut', checkOut);

  const url = `${API_BASE}/hotels/${hotelId}${params.toString() ? `?${params.toString()}` : ''}`;
  const response = await fetch(url);
  return handleResponse(response);
}

/**
 * Request real-time backend price calculation and availability quote.
 */
export async function calculatePrice(hotelId, checkIn, checkOut) {
  const params = new URLSearchParams({ checkIn, checkOut });
  const response = await fetch(`${API_BASE}/hotels/${hotelId}/calculate-price?${params.toString()}`);
  return handleResponse(response);
}

/**
 * Create a new room booking reservation.
 */
export async function createBooking(bookingData) {
  const response = await fetch(`${API_BASE}/bookings`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(bookingData)
  });
  return handleResponse(response);
}

/**
 * Retrieve an existing booking by reference and verified email.
 */
export async function retrieveBooking(bookingReference, email) {
  const response = await fetch(`${API_BASE}/bookings/retrieve`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ bookingReference, email })
  });
  return handleResponse(response);
}

/**
 * Cancel an active booking and release room availability.
 */
export async function cancelBooking(bookingReference, email) {
  const response = await fetch(`${API_BASE}/bookings/${encodeURIComponent(bookingReference)}/cancel`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email })
  });
  return handleResponse(response);
}

/**
 * Retrieve the "Smart Stay Recommendation" for best value.
 */
export async function getSmartStayRecommendation(location = '', checkIn = '', checkOut = '') {
  const params = new URLSearchParams();
  if (location && location.trim()) params.append('location', location.trim());
  if (checkIn) params.append('checkIn', checkIn);
  if (checkOut) params.append('checkOut', checkOut);

  const url = `${API_BASE}/hotels/smart-recommendation${params.toString() ? `?${params.toString()}` : ''}`;
  const response = await fetch(url);
  return handleResponse(response);
}
