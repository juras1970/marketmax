/**
 * MarketMax — lógica da página inicial: categorias, ofertas, busca e grid geral.
 */

const CATEGORY_ICONS_FALLBACK = '🛍️';

document.addEventListener('DOMContentLoaded', async () => {
  const params = new URLSearchParams(window.location.search);
  const q = params.get('q');
  const category = params.get('category');

  setupSearchContext(q, category);
  loadCategoriesGrid();
  loadPromotions();
  loadProductsGrid(q, category);
});

function setupSearchContext(q, category) {
  const context = document.getElementById('search-context');
  const title = document.getElementById('search-title');
  const clearBtn = document.getElementById('clear-search');
  const heading = document.getElementById('products-heading');

  if (q || category) {
    context.classList.remove('hidden');
    title.textContent = q
      ? `Resultados para "${q}"`
      : `Categoria: ${category}`;
    heading.textContent = q || category ? 'Resultados' : 'Mais vendidos';
    clearBtn.addEventListener('click', () => { window.location.href = '/index.html'; });

    // Esconde seções de vitrine quando há busca ativa, para focar no resultado
    document.getElementById('categories-section').classList.add('hidden');
    document.getElementById('ofertas').classList.add('hidden');
    document.getElementById('banner').classList.add('hidden');
  }
}

async function loadCategoriesGrid() {
  const grid = document.getElementById('categories-grid');
  if (!grid) return;
  try {
    const categories = await api.categories.list();
    grid.innerHTML = categories.map(c => `
      <a href="/index.html?category=${encodeURIComponent(c.id)}"
         class="bg-white rounded-lg p-3 flex flex-col items-center gap-1.5 text-center hover:shadow-md transition product-card">
        <span class="text-2xl">${c.icon || CATEGORY_ICONS_FALLBACK}</span>
        <span class="text-xs font-medium text-[--ml-text]">${escapeHtml(c.name)}</span>
        <span class="text-[10px] text-[--ml-text-light]">${c.productCount} produtos</span>
      </a>
    `).join('');
  } catch (e) {
    grid.innerHTML = `<p class="col-span-full text-sm text-[--ml-text-light]">Não foi possível carregar as categorias.</p>`;
  }
}

async function loadPromotions() {
  const grid = document.getElementById('promotions-grid');
  const section = document.getElementById('ofertas');
  if (!grid) return;
  grid.innerHTML = Array(5).fill(skeletonCardHtml()).join('');
  try {
    const products = await api.products.promotions();
    if (products.length === 0) {
      section.classList.add('hidden');
      return;
    }
    grid.innerHTML = products.map(p => productCardHtml(p)).join('');
    markFavoriteCards('#promotions-grid .product-card');
  } catch (e) {
    grid.innerHTML = `<p class="col-span-full text-sm text-[--ml-text-light]">Não foi possível carregar as ofertas.</p>`;
  }
}

async function loadProductsGrid(q, category) {
  const grid = document.getElementById('products-grid');
  const empty = document.getElementById('empty-state');
  grid.innerHTML = Array(10).fill(skeletonCardHtml()).join('');
  try {
    const products = await api.products.list({ q, category });
    if (products.length === 0) {
      grid.innerHTML = '';
      empty.classList.remove('hidden');
      return;
    }
    empty.classList.add('hidden');
    grid.innerHTML = products.map(p => productCardHtml(p)).join('');
    markFavoriteCards('#products-grid .product-card');
  } catch (e) {
    grid.innerHTML = `<p class="col-span-full text-sm text-[--ml-text-light]">Não foi possível carregar os produtos. Verifique se o backend está rodando.</p>`;
  }
}
