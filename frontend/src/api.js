const API_URL = 'http://localhost:8080/api'

async function request(path, options = {}) {
  const response = await fetch(`${API_URL}${path}`, {
    credentials: 'include',
    ...options,
    headers: {
      ...(options.body ? { 'Content-Type': 'application/json' } : {}),
      ...(options.headers || {}),
    },
  })

  if (!response.ok) {
    let message = `Request failed (${response.status})`
    try {
      const body = await response.json()
      message = body.message || body.error || message
    } catch {
      // Keep the HTTP status when the server returns no JSON error body.
    }
    throw new Error(message)
  }

  if (response.status === 204) {
    return null
  }
  return response.json()
}

export const api = {
  me: () => request('/auth/me'),
  login: (data) => request('/auth/login', { method: 'POST', body: JSON.stringify(data) }),
  register: (data) => request('/auth/register', { method: 'POST', body: JSON.stringify(data) }),
  logout: () => request('/auth/logout', { method: 'POST' }),
  feed: (page = 0) => request(`/feed?page=${page}&size=20`),
  scrape: (url) => request(`/recipes/scrape?url=${encodeURIComponent(url)}`, { method: 'POST' }),
  createReview: (data) => request('/reviews', { method: 'POST', body: JSON.stringify(data) }),
  updateReview: (recipeId, data) => request(`/reviews/${recipeId}`, {
    method: 'PUT',
    body: JSON.stringify(data),
  }),
  saveRecipe: (recipeId) => request(`/recipes/${recipeId}/save`, { method: 'POST' }),
  deleteSavedRecipe: (recipeId) => request(`/recipes/${recipeId}/save`, { method: 'DELETE' }),
  savedRecipes: () => request('/recipes/saved'),
}
