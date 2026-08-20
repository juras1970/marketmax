/**
 * MarketMax — lógica da página "Minha conta".
 */

document.addEventListener('DOMContentLoaded', async () => {
  if (!requireLogin('Faça login para acessar sua conta')) return;

  await loadProfile();
  await loadOrders();

  document.getElementById('account-content').classList.remove('hidden');

  document.getElementById('profile-form').addEventListener('submit', handleProfileUpdate);
  document.getElementById('password-form').addEventListener('submit', handlePasswordChange);
  document.getElementById('logout-btn-page').addEventListener('click', () => {
    auth.logout();
    toast('Você saiu da sua conta', 'info');
    window.location.href = '/index.html';
  });
});

async function loadProfile() {
  try {
    const profile = await api.profile();
    document.getElementById('profile-avatar').src = profile.avatar || IMG_PLACEHOLDER;
    document.getElementById('profile-name').textContent = profile.name;
    document.getElementById('profile-email').textContent = profile.email || '';
    document.getElementById('profile-level').textContent = profile.level || 'Nível Bronze';
    document.getElementById('profile-coupons').textContent = profile.couponsCount ?? 0;
    document.getElementById('profile-coins').textContent = profile.coinsCount ?? 0;
    document.getElementById('profile-sales').textContent = profile.salesCount ?? 0;

    document.getElementById('edit-name').value = profile.name || '';
    document.getElementById('edit-avatar').value = profile.avatar || '';

    // Mantém o cache local sincronizado (nome exibido no header, papel para admin)
    auth.setSession(auth.getToken(), { ...auth.getUser(), ...profile });
  } catch (e) {
    toast(e.message || 'Não foi possível carregar seu perfil', 'error');
  }
}

async function handleProfileUpdate(e) {
  e.preventDefault();
  const name = document.getElementById('edit-name').value.trim();
  const avatar = document.getElementById('edit-avatar').value.trim();
  try {
    await api.updateProfile({ name, avatar });
    toast('Perfil atualizado com sucesso!');
    await loadProfile();
    renderAccountNav();
  } catch (e) {
    toast(e.message || 'Não foi possível atualizar o perfil', 'error');
  }
}

async function handlePasswordChange(e) {
  e.preventDefault();
  const currentPassword = document.getElementById('current-password').value;
  const newPassword = document.getElementById('new-password').value;
  try {
    await api.changePassword(currentPassword, newPassword);
    toast('Senha atualizada com sucesso!');
    e.target.reset();
  } catch (e) {
    toast(e.message || 'Não foi possível atualizar a senha', 'error');
  }
}

async function loadOrders() {
  const list = document.getElementById('orders-list');
  const empty = document.getElementById('orders-empty');
  try {
    const orders = await api.orders();
    if (orders.length === 0) {
      list.innerHTML = '';
      empty.classList.remove('hidden');
      return;
    }
    const statusLabels = {
      processing: ['Em processamento', 'bg-yellow-50 text-yellow-700'],
      shipped: ['Enviado', 'bg-blue-50 text-[--ml-blue]'],
      delivered: ['Entregue', 'bg-green-50 text-[--ml-green]'],
      cancelled: ['Cancelado', 'bg-red-50 text-[--ml-red]'],
    };
    list.innerHTML = orders.map(order => {
      const [label, classes] = statusLabels[order.status] || [order.status, 'bg-gray-50 text-gray-600'];
      return `
        <div class="border border-gray-100 rounded-md p-4">
          <div class="flex items-center justify-between flex-wrap gap-2">
            <div>
              <p class="text-sm font-medium text-[--ml-text]">Pedido ${escapeHtml(order.id)}</p>
              <p class="text-xs text-[--ml-text-light]">${escapeHtml(order.date)}</p>
            </div>
            <span class="text-xs font-semibold px-2.5 py-1 rounded-full ${classes}">${label}</span>
          </div>
          <div class="mt-3 space-y-1">
            ${order.items.map(item => `
              <div class="flex items-center justify-between text-sm">
                <a href="/produto.html?id=${encodeURIComponent(item.productId)}" class="text-[--ml-text] hover:text-[--ml-blue] clamp-2">
                  ${item.quantity}x ${escapeHtml(item.title)}
                </a>
                <span class="text-[--ml-text-light] shrink-0 ml-2">${formatBRL(item.price)}</span>
              </div>
            `).join('')}
          </div>
          <div class="mt-3 pt-3 border-t border-gray-100 flex justify-end">
            <span class="text-sm font-semibold text-[--ml-text]">Total: ${formatBRL(order.total)}</span>
          </div>
        </div>
      `;
    }).join('');
  } catch (e) {
    list.innerHTML = `<p class="text-sm text-[--ml-text-light] text-center py-6">Não foi possível carregar seus pedidos.</p>`;
  }
}
