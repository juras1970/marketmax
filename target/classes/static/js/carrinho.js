/**
 * MarketMax — lógica do carrinho de compras e checkout.
 */

document.addEventListener('DOMContentLoaded', () => {
  if (!requireLogin('Faça login para ver seu carrinho')) return;
  loadCart();
  document.getElementById('clear-cart-btn').addEventListener('click', handleClearCart);
  document.getElementById('checkout-btn').addEventListener('click', handleCheckout);
});

async function loadCart() {
  const loading = document.getElementById('cart-loading');
  const empty = document.getElementById('cart-empty');
  const content = document.getElementById('cart-content');

  try {
    const items = await api.cart.get();
    loading.classList.add('hidden');

    if (items.length === 0) {
      empty.classList.remove('hidden');
      content.classList.add('hidden');
      return;
    }

    content.classList.remove('hidden');
    empty.classList.add('hidden');
    renderCartItems(items);
    renderSummary(items);
  } catch (e) {
    loading.innerHTML = `<p class="text-center text-[--ml-text-light] py-10">Não foi possível carregar o carrinho.</p>`;
  }
}

function renderCartItems(items) {
  document.getElementById('cart-items-count').textContent =
    `${items.length} ${items.length === 1 ? 'produto' : 'produtos'}`;

  document.getElementById('cart-items').innerHTML = items.map(item => {
    const p = item.product;
    const image = (p.images && p.images[0]) || IMG_PLACEHOLDER;
    const lineTotal = Number(p.price) * item.quantity;

    return `
      <div class="p-4 flex gap-4 items-start" data-item-id="${item.id}">
        <a href="/produto.html?id=${encodeURIComponent(p.id)}" class="w-20 h-20 shrink-0 bg-white border border-gray-100 rounded flex items-center justify-center overflow-hidden">
          <img src="${image}" onerror="this.src=IMG_PLACEHOLDER" class="w-full h-full object-contain" />
        </a>
        <div class="flex-1 min-w-0">
          <a href="/produto.html?id=${encodeURIComponent(p.id)}" class="text-sm text-[--ml-text] hover:text-[--ml-blue] clamp-2">${escapeHtml(p.title)}</a>
          ${p.freeShipping ? '<p class="text-xs text-[--ml-green] font-semibold mt-1">FRETE GRÁTIS</p>' : ''}

          <div class="flex items-center gap-3 mt-2">
            <div class="flex items-center border border-gray-300 rounded">
              <button class="qty-btn w-7 h-7 text-[--ml-blue] hover:bg-gray-50" onclick="changeQuantity('${item.id}', '${p.id}', ${item.quantity - 1})">−</button>
              <span class="w-8 text-center text-sm">${item.quantity}</span>
              <button class="qty-btn w-7 h-7 text-[--ml-blue] hover:bg-gray-50" onclick="changeQuantity('${item.id}', '${p.id}', ${item.quantity + 1})">+</button>
            </div>
            <button class="text-xs text-[--ml-blue] hover:underline" onclick="removeCartItem('${item.id}')">Remover</button>
          </div>
        </div>
        <div class="text-right shrink-0">
          <span class="font-medium text-[--ml-text]">${formatBRL(lineTotal)}</span>
        </div>
      </div>
    `;
  }).join('');
}

function renderSummary(items) {
  const total = items.reduce((sum, i) => sum + Number(i.product.price) * i.quantity, 0);
  const count = items.reduce((sum, i) => sum + i.quantity, 0);
  document.getElementById('summary-items-label').textContent = `Produtos (${count})`;
  document.getElementById('summary-subtotal').textContent = formatBRL(total);
  document.getElementById('summary-total').textContent = formatBRL(total);
}

async function changeQuantity(cartItemId, productId, newQty) {
  if (newQty < 1) {
    await removeCartItem(cartItemId);
    return;
  }
  try {
    await api.cart.add(productId, newQty, true);
    await Promise.all([loadCart(), refreshCartBadge()]);
  } catch (e) {
    toast(e.message || 'Não foi possível atualizar a quantidade', 'error');
  }
}

async function removeCartItem(cartItemId) {
  try {
    await api.cart.remove(cartItemId);
    await Promise.all([loadCart(), refreshCartBadge()]);
    toast('Item removido do carrinho', 'info');
  } catch (e) {
    toast(e.message || 'Não foi possível remover o item', 'error');
  }
}

async function handleClearCart() {
  try {
    await api.cart.clear();
    await Promise.all([loadCart(), refreshCartBadge()]);
  } catch (e) {
    toast(e.message || 'Não foi possível esvaziar o carrinho', 'error');
  }
}

async function handleCheckout() {
  const btn = document.getElementById('checkout-btn');
  btn.disabled = true;
  btn.textContent = 'Processando...';
  try {
    const result = await api.checkout();
    const order = result.order;
    document.getElementById('order-modal-text').textContent =
      `Pedido ${order.id} no valor de ${formatBRL(order.total)} confirmado.`;
    document.getElementById('order-modal').classList.remove('hidden');
    await refreshCartBadge();
  } catch (e) {
    toast(e.message || 'Não foi possível finalizar a compra', 'error');
    btn.disabled = false;
    btn.textContent = 'Finalizar compra';
  }
}
