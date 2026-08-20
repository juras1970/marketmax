/**
 * MarketMax — renderização do card de produto usado em grids e carrosséis.
 */

function productCardHtml(p, isFavorite = false) {
  const image = (p.images && p.images[0]) || IMG_PLACEHOLDER;
  const hasDiscount = p.discount && p.discount > 0;

  return `
    <a href="/produto.html?id=${encodeURIComponent(p.id)}"
       class="product-card bg-white rounded-lg overflow-hidden flex flex-col group">
      <div class="relative aspect-square bg-white p-4">
        ${hasDiscount ? `<span class="absolute top-2 left-2 bg-[--ml-green] text-white text-[11px] font-bold px-1.5 py-0.5 rounded">${p.discount}% OFF</span>` : ''}
        <button
          onclick="event.preventDefault(); event.stopPropagation(); toggleFavoriteFromCard('${p.id}', this)"
          class="absolute top-2 right-2 w-8 h-8 rounded-full bg-white/90 shadow flex items-center justify-center hover:scale-110 transition"
          aria-label="Favoritar">
          <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4 ${isFavorite ? 'text-[--ml-red]' : 'text-gray-500'}" viewBox="0 0 24 24" fill="${isFavorite ? '#E63535' : 'none'}" stroke="${isFavorite ? '#E63535' : 'currentColor'}" stroke-width="2"><path d="M20.8 4.6a5.5 5.5 0 0 0-7.8 0L12 5.6l-1-1a5.5 5.5 0 0 0-7.8 7.8l1 1L12 21l7.8-7.6 1-1a5.5 5.5 0 0 0 0-7.8Z"/></svg>
        </button>
        <img src="${image}" alt="${escapeHtml(p.title)}" loading="lazy"
             onerror="this.src='${IMG_PLACEHOLDER}'"
             class="w-full h-full object-contain group-hover:scale-105 transition-transform duration-200" />
      </div>
      <div class="px-3 pb-3 pt-1 flex flex-col gap-1 flex-1">
        <p class="text-sm text-[--ml-text] clamp-2 min-h-[2.5rem]">${escapeHtml(p.title)}</p>
        ${p.rating ? `
          <div class="flex items-center gap-1 text-[11px] text-[--ml-text-light]">
            <span class="text-[--ml-blue] tracking-tighter">${starsHtml(p.rating)}</span>
            <span>(${p.rating})</span>
          </div>` : ''}
        <div class="mt-0.5">
          ${hasDiscount ? `<span class="text-xs text-[--ml-text-light] line-through">${formatBRL(p.originalPrice)}</span>` : ''}
          <div class="flex items-center gap-2">
            <span class="text-xl font-medium text-[--ml-text]">${formatBRL(p.price)}</span>
            ${hasDiscount ? `<span class="text-sm text-[--ml-green] font-medium">${p.discount}% OFF</span>` : ''}
          </div>
          <p class="text-xs text-[--ml-text-light]">${installmentsText(p.price)}</p>
        </div>
        ${p.freeShipping ? `<p class="text-xs text-[--ml-green] font-semibold mt-1">FRETE GRÁTIS</p>` : ''}
      </div>
    </a>
  `;
}

/** Marca visualmente os cards cujos produtos já estão nos favoritos do usuário logado */
async function markFavoriteCards(containerSelector = '.product-card') {
  if (!auth.isLoggedIn()) return;
  try {
    const favoriteIds = await api.favorites.list();
    document.querySelectorAll(containerSelector).forEach(card => {
      const href = card.getAttribute('href') || '';
      const match = href.match(/id=([^&]+)/);
      if (!match) return;
      const id = decodeURIComponent(match[1]);
      if (favoriteIds.includes(id)) {
        const svg = card.querySelector('button[aria-label="Favoritar"] svg');
        if (svg) {
          svg.setAttribute('fill', '#E63535');
          svg.setAttribute('stroke', '#E63535');
          svg.classList.remove('text-gray-500');
          svg.classList.add('text-[--ml-red]');
        }
      }
    });
  } catch (e) {
    console.error('Falha ao marcar favoritos', e);
  }
}

function skeletonCardHtml() {
  return `
    <div class="bg-white rounded-lg overflow-hidden flex flex-col">
      <div class="aspect-square skeleton"></div>
      <div class="p-3 space-y-2">
        <div class="h-3 skeleton rounded w-3/4"></div>
        <div class="h-3 skeleton rounded w-1/2"></div>
        <div class="h-5 skeleton rounded w-2/3"></div>
      </div>
    </div>
  `;
}

async function toggleFavoriteFromCard(productId, btnEl) {
  if (!requireLogin('Faça login para favoritar produtos')) return;
  try {
    const favorites = await api.favorites.toggle(productId);
    const isFav = favorites.includes(productId);
    btnEl.querySelector('svg').setAttribute('fill', isFav ? '#E63535' : 'none');
    btnEl.querySelector('svg').setAttribute('stroke', isFav ? '#E63535' : 'currentColor');
    toast(isFav ? 'Adicionado aos favoritos' : 'Removido dos favoritos', 'info');
  } catch (e) {
    toast(e.message || 'Não foi possível atualizar favoritos', 'error');
  }
}
