/**
 * MarketMax — cabeçalho, menu de categorias e rodapé compartilhados.
 * Renderizados via JS em todas as páginas para evitar duplicar HTML.
 */

function renderHeader() {
  const mount = document.getElementById('site-header');
  if (!mount) return;

  const params = new URLSearchParams(window.location.search);
  const currentQuery = params.get('q') || '';

  mount.innerHTML = `
    <!-- BARRA SUPERIOR DO CABEÇALHO -->
    <div class="bg-[--ml-yellow] sticky top-0 z-40 shadow-sm">
      <div class="max-w-[1200px] mx-auto px-4 py-2.5 flex items-center justify-between gap-3 md:gap-4">
        
        <!-- LADO ESQUERDO: HAMBÚRGUER (MOBILE) + LOGO -->
        <div class="flex items-center gap-2 shrink-0">
          <button id="hamburger-btn" class="md:hidden text-[#2D2D2D] p-1 rounded-md hover:bg-yellow-400/60 focus:outline-none" aria-label="Abrir menu de categorias">
            <svg xmlns="http://www.w3.org/2000/svg" class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M4 6h16M4 12h16M4 18h16" />
            </svg>
          </button>

          <a href="/index.html" class="flex items-center gap-1.5 shrink-0">
            <span class="text-xl md:text-2xl font-black tracking-tight text-[#2D2D2D]">Market<span class="text-[--ml-blue]">Max</span></span>
          </a>
        </div>

        <!-- FORMULÁRIO DE BUSCA -->
        <form id="search-form" class="flex-1 flex max-w-2xl">
          <input
            id="search-input"
            type="text"
            value="${escapeHtml(currentQuery)}"
            placeholder="Buscar produtos, marcas e muito mais..."
            class="w-full rounded-l-md px-3 md:px-4 py-2 text-xs md:text-sm text-[--ml-text] placeholder-gray-500 border-0 focus:ring-2 focus:ring-[--ml-blue]"
          />
          <button type="submit" aria-label="Buscar" class="bg-white hover:bg-gray-50 px-3 md:px-4 rounded-r-md border-l border-gray-200 flex items-center justify-center">
            <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4 md:w-5 md:h-5 text-gray-600" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="7"/><path d="m21 21-4.35-4.35"/></svg>
          </button>
        </form>

        <!-- NAVEGAÇÃO DESKTOP -->
        <nav class="hidden md:flex items-center gap-4 text-sm text-[#2D2D2D] shrink-0">
          <a href="/favoritos.html" class="hover:underline flex items-center gap-1">
            <svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20.8 4.6a5.5 5.5 0 0 0-7.8 0L12 5.6l-1-1a5.5 5.5 0 0 0-7.8 7.8l1 1L12 21l7.8-7.6 1-1a5.5 5.5 0 0 0 0-7.8Z"/></svg>
            Favoritos
          </a>
          <span id="admin-link-slot"></span>
          <span id="account-nav-slot"></span>
        </nav>

        <!-- CARRINHO -->
        <a href="/carrinho.html" class="relative flex items-center shrink-0 p-1" aria-label="Carrinho de compras">
          <svg xmlns="http://www.w3.org/2000/svg" class="w-6 h-6 md:w-7 md:h-7 text-[#2D2D2D]" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
            <circle cx="9" cy="21" r="1"/><circle cx="20" cy="21" r="1"/>
            <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"/>
          </svg>
          <span id="cart-badge" class="hidden absolute -top-1 -right-1 bg-[--ml-blue] text-white text-[10px] md:text-[11px] font-bold rounded-full w-4 h-4 md:w-5 md:h-5 flex items-center justify-center">0</span>
        </a>
      </div>
    </div>

    <!-- NAVEGAÇÃO DE CATEGORIAS (DESKTOP) -->
    <div id="category-nav" class="hidden md:block bg-white border-b border-gray-200 z-30">
      <div class="max-w-[1200px] mx-auto px-4 flex items-center gap-5 overflow-x-auto scrollbar-hide py-2.5 text-sm text-[--ml-text]">
        <a href="/index.html#ofertas" class="whitespace-nowrap font-semibold text-[--ml-red] hover:underline">🔥 Ofertas do dia</a>
        <span id="category-links" class="flex items-center gap-5"></span>
      </div>
    </div>

    <!-- MENU HAMBÚRGUER SLIDE-OVER (MOBILE) -->
    <div id="mobile-menu-overlay" class="fixed inset-0 bg-black/50 z-50 hidden transition-opacity">
      <div id="mobile-menu-drawer" class="w-72 bg-white h-full shadow-2xl p-5 flex flex-col transform -translate-x-full transition-transform duration-300">
        <div class="flex items-center justify-between pb-3 border-b border-gray-200">
          <span class="font-bold text-lg text-[--ml-text]">Menu</span>
          <button id="close-menu-btn" class="p-1 text-gray-500 hover:text-gray-800" aria-label="Fechar menu">
            <svg xmlns="http://www.w3.org/2000/svg" class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

        <div class="flex-1 overflow-y-auto py-4 space-y-4">
          <!-- Links da Conta no Mobile -->
          <div id="mobile-account-slot" class="pb-3 border-b border-gray-100 text-sm font-medium space-y-2"></div>

          <!-- Links de Categorias no Mobile -->
          <div>
            <span class="text-xs font-semibold uppercase text-gray-400 tracking-wider block mb-2">Categorias</span>
            <nav class="space-y-1">
              <a href="/index.html#ofertas" class="flex items-center gap-2 p-2 rounded-md font-semibold text-[--ml-red] hover:bg-gray-100">
                🔥 Ofertas do dia
              </a>
              <div id="mobile-category-links" class="space-y-1"></div>
            </nav>
          </div>
        </div>
      </div>
    </div>
  `;

  // Listener para busca
  const form = document.getElementById('search-form');
  form.addEventListener('submit', (e) => {
    e.preventDefault();
    const q = document.getElementById('search-input').value.trim();
    window.location.href = `/index.html${q ? `?q=${encodeURIComponent(q)}` : ''}`;
  });

  // Configurações do Drawer Mobile
  setupMobileMenu();

  renderAccountNav();
  refreshCartBadge();
  loadCategoryLinks();
}

/** Configura os eventos de abrir/fechar o Menu Hambúrguer */
function setupMobileMenu() {
  const btnOpen = document.getElementById('hamburger-btn');
  const btnClose = document.getElementById('close-menu-btn');
  const overlay = document.getElementById('mobile-menu-overlay');
  const drawer = document.getElementById('mobile-menu-drawer');

  if (!btnOpen || !btnClose || !overlay || !drawer) return;

  function openMenu() {
    overlay.classList.remove('hidden');
    setTimeout(() => drawer.classList.remove('-translate-x-full'), 10);
  }

  function closeMenu() {
    drawer.classList.add('-translate-x-full');
    setTimeout(() => overlay.classList.add('hidden'), 300);
  }

  btnOpen.addEventListener('click', openMenu);
  btnClose.addEventListener('click', closeMenu);
  overlay.addEventListener('click', (e) => {
    if (e.target === overlay) closeMenu();
  });
}

/** Renderiza a conta nos slots Desktop e Mobile */
function renderAccountNav() {
  const slot = document.getElementById('account-nav-slot');
  const adminSlot = document.getElementById('admin-link-slot');
  const mobileSlot = document.getElementById('mobile-account-slot');

  if (auth.isLoggedIn()) {
    const user = auth.getUser();
    const firstName = escapeHtml(user?.name?.split(' ')[0] || 'Minha conta');

    if (slot) {
      slot.innerHTML = `
        <div class="relative" id="account-menu-wrapper">
          <button id="account-menu-btn" class="hover:underline flex items-center gap-1">
            ${firstName}
            <svg xmlns="http://www.w3.org/2000/svg" class="w-3 h-3" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><path d="m6 9 6 6 6-6"/></svg>
          </button>
          <div id="account-menu" class="hidden absolute right-0 mt-2 w-48 bg-white rounded-lg shadow-lg border border-gray-100 py-1 z-50">
            <a href="/minha-conta.html" class="block px-4 py-2 text-sm hover:bg-gray-50">Minha conta</a>
            <a href="/favoritos.html" class="block px-4 py-2 text-sm hover:bg-gray-50">Favoritos</a>
            <button id="logout-btn" class="w-full text-left px-4 py-2 text-sm hover:bg-gray-50 text-[--ml-red]">Sair</button>
          </div>
        </div>
      `;
      const btn = document.getElementById('account-menu-btn');
      const menu = document.getElementById('account-menu');
      btn?.addEventListener('click', () => menu.classList.toggle('hidden'));
      document.addEventListener('click', (e) => {
        if (!document.getElementById('account-menu-wrapper')?.contains(e.target)) {
          menu?.classList.add('hidden');
        }
      });
      document.getElementById('logout-btn')?.addEventListener('click', handleLogout);
    }

    if (adminSlot && auth.isAdmin()) {
      adminSlot.innerHTML = `<a href="/admin.html" class="hover:underline font-semibold text-[--ml-blue]">Painel Admin</a>`;
    }

    if (mobileSlot) {
      mobileSlot.innerHTML = `
        <div class="font-semibold text-gray-800">Olá, ${firstName}</div>
        <a href="/minha-conta.html" class="block p-2 rounded hover:bg-gray-100">Minha conta</a>
        <a href="/favoritos.html" class="block p-2 rounded hover:bg-gray-100">Favoritos</a>
        ${auth.isAdmin() ? '<a href="/admin.html" class="block p-2 rounded hover:bg-gray-100 font-semibold text-[--ml-blue]">Painel Admin</a>' : ''}
        <button id="mobile-logout-btn" class="block w-full text-left p-2 rounded hover:bg-gray-100 text-[--ml-red]">Sair</button>
      `;
      document.getElementById('mobile-logout-btn')?.addEventListener('click', handleLogout);
    }
  } else {
    if (slot) slot.innerHTML = `<a href="/login.html" class="hover:underline">Entrar</a>`;
    if (adminSlot) adminSlot.innerHTML = '';
    if (mobileSlot) {
      mobileSlot.innerHTML = `
        <a href="/login.html" class="block p-2 text-center bg-[--ml-blue] text-white rounded-md font-medium">Entrar</a>
      `;
    }
  }
}

function handleLogout() {
  auth.logout();
  toast('Você saiu da sua conta', 'info');
  window.location.href = '/index.html';
}

async function loadCategoryLinks() {
  const mount = document.getElementById('category-links');
  const mobileMount = document.getElementById('mobile-category-links');
  if (!mount && !mobileMount) return;

  try {
    const categories = await api.categories.list();

    const linksHtml = categories.map(c => `
      <a href="/index.html?category=${encodeURIComponent(c.id)}" class="whitespace-nowrap hover:text-[--ml-blue] hover:underline">
        ${c.icon} ${escapeHtml(c.name)}
      </a>
    `).join('');

    const mobileLinksHtml = categories.map(c => `
      <a href="/index.html?category=${encodeURIComponent(c.id)}" class="flex items-center gap-2 p-2 rounded-md text-gray-700 hover:bg-gray-100 text-sm">
        <span>${c.icon}</span> ${escapeHtml(c.name)}
      </a>
    `).join('');

    if (mount) mount.innerHTML = linksHtml;
    if (mobileMount) mobileMount.innerHTML = mobileLinksHtml;
  } catch (e) {
    console.error('Falha ao carregar categorias', e);
  }
}

function renderFooter() {
  const mount = document.getElementById('site-footer');
  if (!mount) return;
  mount.innerHTML = `
    <footer class="bg-white border-t border-gray-200 mt-12">
      <div class="max-w-[1200px] mx-auto px-4 py-10 grid grid-cols-2 md:grid-cols-4 gap-8 text-sm">
        <div>
          <h4 class="font-bold mb-3 text-[--ml-text]">Sobre o MarketMax</h4>
          <ul class="space-y-2 text-[--ml-text-light]">
            <li>Quem somos</li>
            <li>Trabalhe conosco</li>
            <li>Termos e condições</li>
          </ul>
        </div>
        <div>
          <h4 class="font-bold mb-3 text-[--ml-text]">Ajuda</h4>
          <ul class="space-y-2 text-[--ml-text-light]">
            <li>Comprar no MarketMax</li>
            <li>Como vender</li>
            <li>Central de segurança</li>
          </ul>
        </div>
        <div>
          <h4 class="font-bold mb-3 text-[--ml-text]">Formas de pagamento</h4>
          <ul class="space-y-2 text-[--ml-text-light]">
            <li>Cartões de crédito</li>
            <li>Pix</li>
            <li>Boleto</li>
          </ul>
        </div>
        <div>
          <h4 class="font-bold mb-3 text-[--ml-text]">MarketMax</h4>
          <p class="text-[--ml-text-light]">Projeto de demonstração: Spring Boot + MySQL + HTML/JS + Tailwind.</p>
        </div>
      </div>
      <div class="border-t border-gray-100 py-4 text-center text-xs text-[--ml-text-light]">
        © ${new Date().getFullYear()} MarketMax — projeto de estudo inspirado no Mercado Livre.
      </div>
    </footer>
  `;
}

function escapeHtml(str) {
  const div = document.createElement('div');
  div.textContent = str ?? '';
  return div.innerHTML;
}

document.addEventListener('DOMContentLoaded', () => {
  renderHeader();
  renderFooter();
});