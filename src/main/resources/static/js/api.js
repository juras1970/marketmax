/**
 * MarketMax — camada de acesso à API (fetch) + utilitários compartilhados.
 * Como o frontend é servido pelo próprio Spring Boot (mesma origem),
 * usamos caminhos relativos e não precisamos nos preocupar com CORS.
 */

const API_BASE = '/api';
const AUTH_TOKEN_KEY = 'marketmax_token';
const AUTH_USER_KEY = 'marketmax_user';

/** Placeholder usado quando uma imagem de produto falha ao carregar */
const IMG_PLACEHOLDER =
  'data:image/svg+xml;utf8,' + encodeURIComponent(`
    <svg xmlns="http://www.w3.org/2000/svg" width="400" height="400" viewBox="0 0 400 400">
      <rect width="400" height="400" fill="#F0F0F0"/>
      <g fill="#BDBDBD">
        <circle cx="150" cy="150" r="30"/>
        <path d="M60 320 L160 200 L220 260 L280 180 L340 320 Z"/>
      </g>
    </svg>`);

/** Utilitários de sessão (token JWT + perfil em cache no localStorage) */
const auth = {
  getToken: () => localStorage.getItem(AUTH_TOKEN_KEY),
  setSession: (token, user) => {
    localStorage.setItem(AUTH_TOKEN_KEY, token);
    localStorage.setItem(AUTH_USER_KEY, JSON.stringify(user));
  },
  getUser: () => {
    try {
      const raw = localStorage.getItem(AUTH_USER_KEY);
      return raw ? JSON.parse(raw) : null;
    } catch (_) {
      return null;
    }
  },
  isLoggedIn: () => !!auth.getToken(),
  isAdmin: () => (auth.getUser()?.role === 'ADMIN'),
  logout: () => {
    localStorage.removeItem(AUTH_TOKEN_KEY);
    localStorage.removeItem(AUTH_USER_KEY);
  },
};

async function apiRequest(path, options = {}) {
  const headers = { 'Content-Type': 'application/json', ...(options.headers || {}) };
  const token = auth.getToken();
  if (token) headers['Authorization'] = `Bearer ${token}`;

  const res = await fetch(`${API_BASE}${path}`, {
    ...options,
    headers,
  });

  if (res.status === 401 || res.status === 403) {
    let message = 'Sessão expirada. Faça login novamente.';
    try {
      const body = await res.json();
      message = body.message || message;
    } catch (_) { /* corpo sem JSON (comum em 401/403 do Spring Security) */ }
    const err = new Error(message);
    err.status = res.status;
    throw err;
  }

  if (!res.ok) {
    let message = `Erro ${res.status}`;
    try {
      const body = await res.json();
      message = body.message || message;
    } catch (_) { /* corpo sem JSON */ }
    const err = new Error(message);
    err.status = res.status;
    throw err;
  }

  if (res.status === 204) return null;
  return res.json();
}

const api = {
  get: (path) => apiRequest(path),
  post: (path, data) => apiRequest(path, { method: 'POST', body: JSON.stringify(data ?? {}) }),
  put: (path, data) => apiRequest(path, { method: 'PUT', body: JSON.stringify(data ?? {}) }),
  delete: (path) => apiRequest(path, { method: 'DELETE' }),

  auth: {
    register: (name, email, password) => api.post('/auth/register', { name, email, password }),
    login: (email, password) => api.post('/auth/login', { email, password }),
    me: () => api.get('/auth/me'),
    forgotPassword: (email) => api.post('/auth/forgot-password', { email }),
    validateResetToken: (token) => api.get(`/auth/reset-password/validate?token=${encodeURIComponent(token)}`),
    resetPassword: (token, newPassword) => api.post('/auth/reset-password', { token, newPassword }),
  },

  products: {
    list: (params = {}) => {
      const qs = new URLSearchParams();
      if (params.q) qs.set('q', params.q);
      if (params.category) qs.set('category', params.category);
      const query = qs.toString();
      return api.get(`/products${query ? `?${query}` : ''}`);
    },
    getById: (id) => api.get(`/products/${encodeURIComponent(id)}`),
    promotions: () => api.get('/products/promotions'),
  },

  categories: {
    list: () => api.get('/categories'),
  },

  cart: {
    get: () => api.get('/cart'),
    add: (productId, quantity = 1, replaceQty = false) =>
      api.post('/cart', { productId, quantity, replaceQty }),
    remove: (cartItemId) => api.delete(`/cart/${cartItemId}`),
    clear: () => api.delete('/cart'),
  },

  favorites: {
    list: () => api.get('/favorites'),
    toggle: (productId) => api.post('/favorites/toggle', { productId }),
  },

  checkout: () => api.post('/checkout'),
  orders: () => api.get('/orders'),
  profile: () => api.get('/profile'),
  updateProfile: (data) => api.put('/profile', data),
  changePassword: (currentPassword, newPassword) => api.post('/profile/password', { currentPassword, newPassword }),
  notifications: () => api.get('/notifications'),

  admin: {
    products: {
      list: () => api.get('/admin/products'),
      create: (data) => api.post('/admin/products', data),
      update: (id, data) => api.put(`/admin/products/${encodeURIComponent(id)}`, data),
      remove: (id) => api.delete(`/admin/products/${encodeURIComponent(id)}`),
    },
    categories: {
      list: () => api.get('/admin/categories'),
      create: (data) => api.post('/admin/categories', data),
      update: (id, data) => api.put(`/admin/categories/${encodeURIComponent(id)}`, data),
      remove: (id) => api.delete(`/admin/categories/${encodeURIComponent(id)}`),
    },
    users: {
      list: () => api.get('/admin/users'),
      create: (data) => api.post('/admin/users', data),
      update: (id, data) => api.put(`/admin/users/${id}`, data),
      remove: (id) => api.delete(`/admin/users/${id}`),
    },
    orders: {
      list: () => api.get('/admin/orders'),
      updateStatus: (id, status) => api.put(`/admin/orders/${encodeURIComponent(id)}/status`, { status }),
    },
    stats: {
      summary: () => api.get('/admin/stats/summary'),
      salesByDay: () => api.get('/admin/stats/sales-by-day'),
      topProducts: () => api.get('/admin/stats/top-products'),
      topUsers: () => api.get('/admin/stats/top-users'),
      statusBreakdown: () => api.get('/admin/stats/status-breakdown'),
    },
  },
};

/** Formata um número/BigDecimal-string como moeda brasileira */
function formatBRL(value) {
  const n = Number(value ?? 0);
  return n.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
}

/** Retorna o texto de parcelamento "em 12x de R$ X sem juros" */
function installmentsText(price, max = 12) {
  const n = Number(price ?? 0);
  if (n <= 0) return '';
  const per = n / max;
  return `em ${max}x ${formatBRL(per)} sem juros`;
}

/** Gera estrelas (cheia/meia/vazia) a partir de uma nota 0–5 */
function starsHtml(rating) {
  const r = Number(rating ?? 0);
  let html = '';
  for (let i = 1; i <= 5; i++) {
    if (r >= i) html += '★';
    else if (r >= i - 0.5) html += '⯨';
    else html += '☆';
  }
  return html;
}

/** Exibe uma notificação temporária no canto da tela */
function toast(message, type = 'success') {
  let container = document.getElementById('toast-container');
  if (!container) {
    container = document.createElement('div');
    container.id = 'toast-container';
    document.body.appendChild(container);
  }
  const colors = {
    success: 'bg-[--ml-green] text-white',
    error: 'bg-[--ml-red] text-white',
    info: 'bg-gray-800 text-white',
  };
  const el = document.createElement('div');
  el.className = `toast ${colors[type] || colors.info} px-4 py-3 rounded-lg shadow-lg text-sm font-medium max-w-xs`;
  el.textContent = message;
  container.appendChild(el);
  setTimeout(() => {
    el.style.opacity = '0';
    el.style.transition = 'opacity .3s';
    setTimeout(() => el.remove(), 300);
  }, 2600);
}

/** Debounce simples para inputs de busca */
function debounce(fn, wait = 300) {
  let t;
  return (...args) => {
    clearTimeout(t);
    t = setTimeout(() => fn(...args), wait);
  };
}

/** Atualiza o badge do carrinho no header (busca a contagem no backend) */
async function refreshCartBadge() {
  const badge = document.getElementById('cart-badge');
  if (!badge) return;
  if (!auth.isLoggedIn()) {
    badge.classList.add('hidden');
    return;
  }
  try {
    const items = await api.cart.get();
    const count = items.reduce((sum, i) => sum + i.quantity, 0);
    badge.textContent = count;
    badge.classList.toggle('hidden', count === 0);
  } catch (e) {
    console.error('Falha ao atualizar carrinho', e);
  }
}

/** Verifica se o usuário está logado; caso não esteja, redireciona para o login. Retorna true/false. */
function requireLogin(message = 'Faça login para continuar') {
  if (!auth.isLoggedIn()) {
    toast(message, 'info');
    const redirect = encodeURIComponent(window.location.pathname + window.location.search);
    window.location.href = `/login.html?redirect=${redirect}`;
    return false;
  }
  return true;
}

/** Adiciona produto ao carrinho e atualiza o badge, com feedback visual */
async function addToCart(productId, quantity = 1) {
  if (!requireLogin('Faça login para adicionar produtos ao carrinho')) return;
  try {
    await api.cart.add(productId, quantity);
    await refreshCartBadge();
    toast('Produto adicionado ao carrinho!');
  } catch (e) {
    toast(e.message || 'Não foi possível adicionar ao carrinho', 'error');
  }
}
