/**
 * MarketMax — lógica da página de detalhe do produto.
 */

let currentProduct = null;

document.addEventListener('DOMContentLoaded', async () => {
  const params = new URLSearchParams(window.location.search);
  const id = params.get('id');

  if (!id) {
    showNotFound();
    return;
  }

  try {
    currentProduct = await api.products.getById(id);
    renderProduct(currentProduct);
  } catch (e) {
    showNotFound();
  }
});

function showNotFound() {
  document.getElementById('loading-state').classList.add('hidden');
  document.getElementById('not-found').classList.remove('hidden');
}

function renderProduct(p) {
  document.getElementById('loading-state').classList.add('hidden');
  document.getElementById('product-content').classList.remove('hidden');
  document.title = `${p.title} — MarketMax`;

  // Breadcrumb
  document.getElementById('breadcrumb').innerHTML = `
    <a href="/index.html" class="hover:underline">Início</a> ›
    <a href="/index.html?category=${encodeURIComponent(p.category)}" class="hover:underline capitalize">${escapeHtml(p.category)}</a> ›
    <span class="text-[--ml-text]">${escapeHtml(truncate(p.title, 60))}</span>
  `;

  // Galeria
  const images = (p.images && p.images.length > 0) ? p.images : [IMG_PLACEHOLDER];
  document.getElementById('main-image').src = images[0];
  document.getElementById('main-image').alt = p.title;
  document.getElementById('thumbnails').innerHTML = images.map((img, i) => `
    <button onclick="setMainImage('${img.replace(/'/g, "\\'")}')"
      class="w-16 h-16 shrink-0 border rounded-md overflow-hidden ${i === 0 ? 'border-[--ml-blue]' : 'border-gray-200'}">
      <img src="${img}" onerror="this.src=IMG_PLACEHOLDER" class="w-full h-full object-contain" />
    </button>
  `).join('');

  // Informações
  document.getElementById('p-sales').textContent = [p.isNew ? 'Novo' : null, p.salesCount].filter(Boolean).join(' | ');
  document.getElementById('p-title').textContent = p.title;

  const ratingEl = document.getElementById('p-rating');
  if (p.rating) {
    ratingEl.innerHTML = `
      <span class="text-[--ml-text-light]">${p.rating}</span>
      <span class="text-[--ml-blue]">${starsHtml(p.rating)}</span>
    `;
  }

  const hasDiscount = p.discount && p.discount > 0;
  document.getElementById('p-original-price').textContent = hasDiscount ? formatBRL(p.originalPrice) : '';
  document.getElementById('p-price').textContent = formatBRL(p.price);
  document.getElementById('p-discount').textContent = hasDiscount ? `${p.discount}% OFF` : '';
  document.getElementById('p-installments').textContent = installmentsText(p.price);
  document.getElementById('p-free-shipping').innerHTML = p.freeShipping
    ? `<svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="1" y="3" width="15" height="13"/><path d="M16 8h4l3 3v5h-7V8Z"/><circle cx="5.5" cy="18.5" r="2.5"/><circle cx="18.5" cy="18.5" r="2.5"/></svg> Frete grátis`
    : '';

  document.getElementById('p-description').textContent = p.description || 'Sem descrição disponível.';

  const specsEl = document.getElementById('p-specs');
  const specEntries = Object.entries(p.specs || {});
  specsEl.innerHTML = specEntries.length
    ? specEntries.map(([k, v], i) => `
        <tr class="${i % 2 === 0 ? 'bg-gray-50' : ''}">
          <td class="py-2 px-3 text-[--ml-text-light] w-1/3">${escapeHtml(k)}</td>
          <td class="py-2 px-3 text-[--ml-text]">${escapeHtml(v)}</td>
        </tr>`).join('')
    : `<tr><td class="text-[--ml-text-light] text-sm">Nenhuma característica cadastrada.</td></tr>`;

  // Caixa de compra
  document.getElementById('box-shipping').textContent = p.freeShipping ? 'Chega grátis' : 'Calcule o frete e o prazo';
  document.getElementById('seller-initial').textContent = (p.sellerReputation || 'V')[0].toUpperCase();
  document.getElementById('p-seller-reputation').textContent = p.sellerReputation || 'Vendedor MarketMax';
  document.getElementById('p-seller-meta').textContent = [p.sellerSales, p.sellerRating ? `★ ${p.sellerRating}` : null].filter(Boolean).join(' · ');

  setupQuantityControls();
  setupPurchaseButtons(p.id);
  setupFavoriteButton(p.id);
}

async function setupFavoriteButton(productId) {
  const btn = document.getElementById('favorite-btn');
  const icon = document.getElementById('favorite-icon');
  if (!btn) return;

  if (auth.isLoggedIn()) {
    try {
      const favoriteIds = await api.favorites.list();
      setFavoriteIconState(icon, favoriteIds.includes(productId));
    } catch (e) {
      console.error('Falha ao carregar favoritos', e);
    }
  }

  btn.addEventListener('click', async () => {
    if (!requireLogin('Faça login para favoritar produtos')) return;
    try {
      const favoriteIds = await api.favorites.toggle(productId);
      const isFav = favoriteIds.includes(productId);
      setFavoriteIconState(icon, isFav);
      toast(isFav ? 'Adicionado aos favoritos' : 'Removido dos favoritos', 'info');
    } catch (e) {
      toast(e.message || 'Não foi possível atualizar favoritos', 'error');
    }
  });
}

function setFavoriteIconState(icon, isFavorite) {
  icon.setAttribute('fill', isFavorite ? '#E63535' : 'none');
  icon.setAttribute('stroke', isFavorite ? '#E63535' : 'currentColor');
  icon.classList.toggle('text-[--ml-red]', isFavorite);
  icon.classList.toggle('text-gray-500', !isFavorite);
}

function setMainImage(url) {
  document.getElementById('main-image').src = url;
}

function setupQuantityControls() {
  const input = document.getElementById('qty-input');
  document.getElementById('qty-minus').addEventListener('click', () => {
    input.value = Math.max(1, Number(input.value) - 1);
  });
  document.getElementById('qty-plus').addEventListener('click', () => {
    input.value = Number(input.value) + 1;
  });
  input.addEventListener('change', () => {
    if (Number(input.value) < 1 || isNaN(Number(input.value))) input.value = 1;
  });
}

function setupPurchaseButtons(productId) {
  const qtyInput = document.getElementById('qty-input');

  document.getElementById('add-cart-btn').addEventListener('click', async () => {
    await addToCart(productId, Number(qtyInput.value));
  });

  document.getElementById('buy-now-btn').addEventListener('click', async () => {
    if (!requireLogin('Faça login para continuar a compra')) return;
    try {
      await api.cart.add(productId, Number(qtyInput.value));
      window.location.href = '/carrinho.html';
    } catch (e) {
      toast(e.message || 'Não foi possível continuar a compra', 'error');
    }
  });
}

function truncate(str, len) {
  return str.length > len ? str.slice(0, len) + '…' : str;
}
