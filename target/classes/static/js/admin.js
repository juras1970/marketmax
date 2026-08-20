/**
 * MarketMax — Painel Administrativo.
 * CRUD de categorias, produtos, usuários e visão de pedidos/vendas com gráficos.
 */

let allCategories = [];
let allProductsMap = new Map();
let allUsersMap = new Map();
let charts = {};

// Fallback de imagem padrão caso não esteja definido no escopo global
const DEFAULT_PLACEHOLDER = 'data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="100" height="100" viewBox="0 0 24 24" fill="none" stroke="%23ccc" stroke-width="2"><rect x="3" y="3" width="18" height="18" rx="2"/><circle cx="8.5" cy="8.5" r="1.5"/><path d="m21 15-5-5L5 21"/></svg>';
const PLACEHOLDER = typeof IMG_PLACEHOLDER !== 'undefined' ? IMG_PLACEHOLDER : DEFAULT_PLACEHOLDER;

document.addEventListener('DOMContentLoaded', async () => {
  if (!auth.isLoggedIn() || !auth.isAdmin()) {
    document.getElementById('admin-denied')?.classList.remove('hidden');
    return;
  }

  document.getElementById('admin-content')?.classList.remove('hidden');

  setupTabs();
  setupModal();

  document.getElementById('new-product-btn')?.addEventListener('click', () => openProductForm());
  document.getElementById('new-category-btn')?.addEventListener('click', () => openCategoryForm());
  document.getElementById('new-user-btn')?.addEventListener('click', () => openUserForm());

  await loadDashboard();
  await loadCategoriesTab();
});

/* ============================= ABAS ============================= */

function setupTabs() {
  const buttons = document.querySelectorAll('.admin-tab-btn');
  buttons.forEach(btn => {
    btn.addEventListener('click', () => {
      const tab = btn.dataset.tab;
      buttons.forEach(b => {
        b.classList.remove('border-[--ml-blue]', 'text-[--ml-blue]');
        b.classList.add('border-transparent', 'text-[--ml-text-light]');
      });
      btn.classList.add('border-[--ml-blue]', 'text-[--ml-blue]');
      btn.classList.remove('border-transparent', 'text-[--ml-text-light]');

      document.querySelectorAll('.admin-tab-panel').forEach(p => p.classList.add('hidden'));
      document.getElementById(`tab-${tab}`)?.classList.remove('hidden');

      if (tab === 'products' && !document.getElementById('products-table-body')?.dataset.loaded) {
        loadProductsTab();
      }
      if (tab === 'users' && !document.getElementById('users-table-body')?.dataset.loaded) {
        loadUsersTab();
      }
      if (tab === 'orders' && !document.getElementById('orders-table-body')?.dataset.loaded) {
        loadOrdersTab();
      }
    });
  });
}

/* ============================= MODAL GENÉRICO ============================= */

function setupModal() {
  document.getElementById('modal-close-btn')?.addEventListener('click', closeModal);
  document.getElementById('admin-modal')?.addEventListener('click', (e) => {
    if (e.target.id === 'admin-modal') closeModal();
  });
}

function openModal(title, bodyHtml) {
  const titleEl = document.getElementById('modal-title');
  const bodyEl = document.getElementById('modal-body');
  const modalEl = document.getElementById('admin-modal');

  if (titleEl) titleEl.textContent = title;
  if (bodyEl) bodyEl.innerHTML = bodyHtml;
  if (modalEl) modalEl.classList.remove('hidden');
}

function closeModal() {
  const modalEl = document.getElementById('admin-modal');
  const bodyEl = document.getElementById('modal-body');
  if (modalEl) modalEl.classList.add('hidden');
  if (bodyEl) bodyEl.innerHTML = '';
}

/* ============================= DASHBOARD / VENDAS ============================= */

async function loadDashboard() {
  try {
    const [summary, salesByDay, topProducts, topUsers, statusBreakdown] = await Promise.all([
      api.admin.stats.summary(),
      api.admin.stats.salesByDay(),
      api.admin.stats.topProducts(),
      api.admin.stats.topUsers(),
      api.admin.stats.statusBreakdown(),
    ]);

    document.getElementById('stat-revenue').textContent = formatBRL(summary.totalRevenue || 0);
    document.getElementById('stat-orders').textContent = summary.totalOrders || 0;
    document.getElementById('stat-users').textContent = summary.totalUsers || 0;
    document.getElementById('stat-products').textContent = summary.totalProducts || 0;

    renderSalesByDayChart(salesByDay);
    renderStatusChart(statusBreakdown);
    renderTopProductsChart(topProducts);
    renderTopUsersChart(topUsers);
  } catch (e) {
    toast(e.message || 'Não foi possível carregar o dashboard', 'error');
  }
}

const STATUS_LABELS = {
  processing: 'Em processamento',
  shipped: 'Enviado',
  delivered: 'Entregue',
  cancelled: 'Cancelado',
};

function renderSalesByDayChart(data) {
  const ctx = document.getElementById('chart-sales-day');
  if (!ctx) return;
  if (charts.salesDay) charts.salesDay.destroy();
  
  charts.salesDay = new Chart(ctx, {
    type: 'line',
    data: {
      labels: data.map(d => d.date),
      datasets: [{
        label: 'Receita (R$)',
        data: data.map(d => d.total),
        borderColor: '#3483FA',
        backgroundColor: 'rgba(52,131,250,0.1)',
        tension: 0.3,
        fill: true,
      }],
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: { legend: { display: false } }
    },
  });
}

function renderStatusChart(data) {
  const ctx = document.getElementById('chart-status');
  if (!ctx) return;
  if (charts.status) charts.status.destroy();

  charts.status = new Chart(ctx, {
    type: 'doughnut',
    data: {
      labels: data.map(d => STATUS_LABELS[d.status] || d.status),
      datasets: [{
        data: data.map(d => d.count),
        backgroundColor: ['#FFC107', '#3483FA', '#00A650', '#E63535', '#9CA3AF'],
      }],
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          position: 'bottom',
          labels: { boxWidth: 12, font: { size: 11 } }
        }
      }
    },
  });
}

function renderTopProductsChart(data) {
  const ctx = document.getElementById('chart-top-products');
  if (!ctx) return;
  if (charts.topProducts) charts.topProducts.destroy();

  charts.topProducts = new Chart(ctx, {
    type: 'bar',
    data: {
      labels: data.map(d => truncate(d.title, 20)),
      datasets: [{
        label: 'Unidades vendidas',
        data: data.map(d => d.quantitySold),
        backgroundColor: '#00A650',
      }],
    },
    options: {
      indexAxis: 'y',
      responsive: true,
      maintainAspectRatio: false,
      plugins: { legend: { display: false } }
    },
  });
}

function renderTopUsersChart(data) {
  const ctx = document.getElementById('chart-top-users');
  if (!ctx) return;
  if (charts.topUsers) charts.topUsers.destroy();

  charts.topUsers = new Chart(ctx, {
    type: 'bar',
    data: {
      labels: data.map(d => truncate(d.name, 18)),
      datasets: [{
        label: 'Total gasto (R$)',
        data: data.map(d => d.totalSpent),
        backgroundColor: '#3483FA',
      }],
    },
    options: {
      indexAxis: 'y',
      responsive: true,
      maintainAspectRatio: false,
      plugins: { legend: { display: false } }
    },
  });
}

function truncate(str, max) {
  if (!str) return '';
  return str.length > max ? str.slice(0, max) + '…' : str;
}

/* ============================= PRODUTOS ============================= */

async function loadProductsTab() {
  const tbody = document.getElementById('products-table-body');
  if (!tbody) return;
  tbody.innerHTML = `<tr><td colspan="5" class="px-4 py-6 text-center text-[--ml-text-light]">Carregando...</td></tr>`;

  try {
    const [products, categories] = await Promise.all([
      api.admin.products.list(),
      api.admin.categories.list(),
    ]);

    allCategories = categories;
    allProductsMap.clear();
    products.forEach(p => allProductsMap.set(String(p.id), p));

    const catMap = Object.fromEntries(categories.map(c => [c.id, c.name]));

    if (products.length === 0) {
      tbody.innerHTML = `<tr><td colspan="5" class="px-4 py-6 text-center text-[--ml-text-light]">Nenhum produto cadastrado.</td></tr>`;
    } else {
      tbody.innerHTML = products.map(p => `
        <tr class="border-t border-gray-100">
          <td class="px-4 py-3">
            <div class="flex items-center gap-2">
              <img src="${(p.images && p.images[0]) || PLACEHOLDER}" onerror="this.src='${PLACEHOLDER}'" class="w-10 h-10 object-contain bg-gray-50 rounded shrink-0" />
              <span class="clamp-2 max-w-[220px] font-medium">${escapeHtml(p.title)}</span>
            </div>
          </td>
          <td class="px-4 py-3 whitespace-nowrap">${escapeHtml(catMap[p.category] || p.category)}</td>
          <td class="px-4 py-3 whitespace-nowrap font-semibold">${formatBRL(p.price)}</td>
          <td class="px-4 py-3 whitespace-nowrap">${p.discount ? p.discount + '%' : '—'}</td>
          <td class="px-4 py-3 text-right whitespace-nowrap">
            <button class="text-[--ml-blue] hover:underline text-xs mr-3 font-semibold" onclick="openProductFormById('${p.id}')">Editar</button>
            <button class="text-[--ml-red] hover:underline text-xs font-semibold" onclick="deleteProduct('${p.id}')">Excluir</button>
          </td>
        </tr>
      `).join('');
    }
    tbody.dataset.loaded = 'true';
  } catch (e) {
    tbody.innerHTML = `<tr><td colspan="5" class="px-4 py-6 text-center text-[--ml-text-light]">Não foi possível carregar os produtos.</td></tr>`;
  }
}

function openProductFormById(id) {
  const product = allProductsMap.get(String(id));
  openProductForm(product);
}

function openProductForm(product = null) {
  const isEdit = !!product;
  const p = product || {
    id: '', title: '', price: '', originalPrice: '', discount: 0, category: allCategories[0]?.id || '',
    freeShipping: false, description: '', rating: '', salesCountText: '', isNew: false,
    sellerReputation: '', sellerRating: '', sellerSales: '', sellerPosting: '',
    images: [], specs: {},
  };
  const imagesText = (p.images || []).join('\n');
  const specsText = Object.entries(p.specs || {}).map(([k, v]) => `${k}: ${v}`).join('\n');

  openModal(isEdit ? 'Editar produto' : 'Novo produto', `
    <form id="product-form" class="space-y-3">
      <div>
        <label class="text-xs font-medium text-[--ml-text-light]">Título</label>
        <input name="title" value="${escapeHtml(p.title)}" required class="w-full mt-1 px-3 py-2 border border-gray-300 rounded-md text-sm" />
      </div>
      <div class="grid grid-cols-3 gap-3">
        <div>
          <label class="text-xs font-medium text-[--ml-text-light]">Preço (R$)</label>
          <input name="price" type="number" step="0.01" value="${p.price}" required class="w-full mt-1 px-3 py-2 border border-gray-300 rounded-md text-sm" />
        </div>
        <div>
          <label class="text-xs font-medium text-[--ml-text-light]">Preço original</label>
          <input name="originalPrice" type="number" step="0.01" value="${p.originalPrice || ''}" class="w-full mt-1 px-3 py-2 border border-gray-300 rounded-md text-sm" />
        </div>
        <div>
          <label class="text-xs font-medium text-[--ml-text-light]">Desconto (%)</label>
          <input name="discount" type="number" value="${p.discount || 0}" class="w-full mt-1 px-3 py-2 border border-gray-300 rounded-md text-sm" />
        </div>
      </div>
      <div>
        <label class="text-xs font-medium text-[--ml-text-light]">Categoria</label>
        <select name="category" required class="w-full mt-1 px-3 py-2 border border-gray-300 rounded-md text-sm">
          ${allCategories.map(c => `<option value="${c.id}" ${c.id === p.category ? 'selected' : ''}>${escapeHtml(c.name)}</option>`).join('')}
        </select>
      </div>
      <div>
        <label class="text-xs font-medium text-[--ml-text-light]">Descrição</label>
        <textarea name="description" rows="3" class="w-full mt-1 px-3 py-2 border border-gray-300 rounded-md text-sm">${escapeHtml(p.description || '')}</textarea>
      </div>
      <div class="grid grid-cols-2 gap-3">
        <div>
          <label class="text-xs font-medium text-[--ml-text-light]">Avaliação (0-5)</label>
          <input name="rating" type="number" step="0.1" min="0" max="5" value="${p.rating || ''}" class="w-full mt-1 px-3 py-2 border border-gray-300 rounded-md text-sm" />
        </div>
        <div>
          <label class="text-xs font-medium text-[--ml-text-light]">Texto de vendas</label>
          <input name="salesCountText" value="${escapeHtml(p.salesCountText || p.salesCount || '')}" placeholder="+500 vendidos" class="w-full mt-1 px-3 py-2 border border-gray-300 rounded-md text-sm" />
        </div>
      </div>
      <div class="flex items-center gap-4 py-1">
        <label class="flex items-center gap-2 text-sm"><input type="checkbox" name="freeShipping" ${p.freeShipping ? 'checked' : ''} /> Frete grátis</label>
        <label class="flex items-center gap-2 text-sm"><input type="checkbox" name="isNew" ${p.isNew ? 'checked' : ''} /> Produto novo</label>
      </div>
      <div class="grid grid-cols-3 gap-3">
        <div>
          <label class="text-xs font-medium text-[--ml-text-light]">Reputação vendedor</label>
          <input name="sellerReputation" value="${escapeHtml(p.sellerReputation || '')}" class="w-full mt-1 px-3 py-2 border border-gray-300 rounded-md text-sm" />
        </div>
        <div>
          <label class="text-xs font-medium text-[--ml-text-light]">Nota vendedor</label>
          <input name="sellerRating" value="${escapeHtml(p.sellerRating || '')}" class="w-full mt-1 px-3 py-2 border border-gray-300 rounded-md text-sm" />
        </div>
        <div>
          <label class="text-xs font-medium text-[--ml-text-light]">Vendas vendedor</label>
          <input name="sellerSales" value="${escapeHtml(p.sellerSales || '')}" class="w-full mt-1 px-3 py-2 border border-gray-300 rounded-md text-sm" />
        </div>
      </div>
      <div>
        <label class="text-xs font-medium text-[--ml-text-light]">Tempo como vendedor</label>
        <input name="sellerPosting" value="${escapeHtml(p.sellerPosting || '')}" class="w-full mt-1 px-3 py-2 border border-gray-300 rounded-md text-sm" />
      </div>
      <div>
        <label class="text-xs font-medium text-[--ml-text-light]">Imagens (uma URL por linha)</label>
        <textarea name="images" rows="3" class="w-full mt-1 px-3 py-2 border border-gray-300 rounded-md text-sm font-mono text-xs">${escapeHtml(imagesText)}</textarea>
      </div>
      <div>
        <label class="text-xs font-medium text-[--ml-text-light]">Especificações ("chave: valor", uma por linha)</label>
        <textarea name="specs" rows="3" class="w-full mt-1 px-3 py-2 border border-gray-300 rounded-md text-sm font-mono text-xs">${escapeHtml(specsText)}</textarea>
      </div>
      <div class="flex justify-end gap-2 pt-3 border-t border-gray-100">
        <button type="button" onclick="closeModal()" class="px-4 py-2 text-sm text-[--ml-text-light] hover:text-[--ml-text]">Cancelar</button>
        <button type="submit" class="bg-[--ml-blue] hover:bg-blue-600 text-white text-sm font-semibold px-4 py-2 rounded-md">Salvar</button>
      </div>
    </form>
  `);

  document.getElementById('product-form')?.addEventListener('submit', (e) => handleProductSubmit(e, isEdit ? p.id : null));
}

async function handleProductSubmit(e, existingId) {
  e.preventDefault();
  const form = e.target;
  const fd = new FormData(form);

  const images = (fd.get('images') || '').split('\n').map(s => s.trim()).filter(Boolean);
  const specs = {};
  (fd.get('specs') || '').split('\n').forEach(line => {
    const idx = line.indexOf(':');
    if (idx > -1) {
      const key = line.slice(0, idx).trim();
      const value = line.slice(idx + 1).trim();
      if (key) specs[key] = value;
    }
  });

  const payload = {
    title: fd.get('title'),
    price: parseFloat(fd.get('price')),
    originalPrice: fd.get('originalPrice') ? parseFloat(fd.get('originalPrice')) : null,
    discount: parseInt(fd.get('discount') || '0', 10),
    category: fd.get('category'),
    freeShipping: fd.get('freeShipping') === 'on',
    description: fd.get('description'),
    rating: fd.get('rating') ? parseFloat(fd.get('rating')) : null,
    salesCountText: fd.get('salesCountText'),
    isNew: fd.get('isNew') === 'on',
    sellerReputation: fd.get('sellerReputation'),
    sellerRating: fd.get('sellerRating'),
    sellerSales: fd.get('sellerSales'),
    sellerPosting: fd.get('sellerPosting'),
    images,
    specs,
  };

  try {
    if (existingId) {
      await api.admin.products.update(existingId, payload);
      toast('Produto atualizado com sucesso!');
    } else {
      await api.admin.products.create(payload);
      toast('Produto criado com sucesso!');
    }
    closeModal();
    const tbody = document.getElementById('products-table-body');
    if (tbody) delete tbody.dataset.loaded;
    await loadProductsTab();
    await loadDashboard();
  } catch (e) {
    toast(e.message || 'Não foi possível salvar o produto', 'error');
  }
}

async function deleteProduct(id) {
  if (!confirm('Tem certeza que deseja excluir este produto?')) return;
  try {
    await api.admin.products.remove(id);
    toast('Produto excluído.');
    const tbody = document.getElementById('products-table-body');
    if (tbody) delete tbody.dataset.loaded;
    await loadProductsTab();
  } catch (e) {
    toast(e.message || 'Não foi possível excluir o produto', 'error');
  }
}

/* ============================= CATEGORIAS ============================= */

async function loadCategoriesTab() {
  const tbody = document.getElementById('categories-table-body');
  if (!tbody) return;
  tbody.innerHTML = `<tr><td colspan="5" class="px-4 py-6 text-center text-[--ml-text-light]">Carregando...</td></tr>`;

  try {
    const categories = await api.admin.categories.list();
    allCategories = categories;

    tbody.innerHTML = categories.map(c => `
      <tr class="border-t border-gray-100">
        <td class="px-4 py-3 text-xl">${c.icon || '🛍️'}</td>
        <td class="px-4 py-3 font-mono text-xs text-[--ml-text-light]">${escapeHtml(c.id)}</td>
        <td class="px-4 py-3 font-medium">${escapeHtml(c.name)}</td>
        <td class="px-4 py-3">${c.productCount ?? 0}</td>
        <td class="px-4 py-3 text-right whitespace-nowrap">
          <button class="text-[--ml-blue] hover:underline text-xs mr-3 font-semibold" onclick='openCategoryForm(${JSON.stringify(c).replace(/'/g, "&apos;")})'>Editar</button>
          <button class="text-[--ml-red] hover:underline text-xs font-semibold" onclick="deleteCategory('${c.id}')">Excluir</button>
        </td>
      </tr>
    `).join('');
  } catch (e) {
    tbody.innerHTML = `<tr><td colspan="5" class="px-4 py-6 text-center text-[--ml-text-light]">Não foi possível carregar as categorias.</td></tr>`;
  }
}

function openCategoryForm(category = null) {
  const isEdit = !!category;
  const c = category || { id: '', name: '', icon: '', displayOrder: 0 };

  openModal(isEdit ? 'Editar categoria' : 'Nova categoria', `
    <form id="category-form" class="space-y-3">
      <div>
        <label class="text-xs font-medium text-[--ml-text-light]">ID (slug, sem espaços)</label>
        <input name="id" value="${escapeHtml(c.id)}" ${isEdit ? 'readonly' : 'required'}
          class="w-full mt-1 px-3 py-2 border border-gray-300 rounded-md text-sm ${isEdit ? 'bg-gray-50 text-gray-500' : ''}" />
      </div>
      <div>
        <label class="text-xs font-medium text-[--ml-text-light]">Nome</label>
        <input name="name" value="${escapeHtml(c.name)}" required class="w-full mt-1 px-3 py-2 border border-gray-300 rounded-md text-sm" />
      </div>
      <div class="grid grid-cols-2 gap-3">
        <div>
          <label class="text-xs font-medium text-[--ml-text-light]">Ícone (emoji)</label>
          <input name="icon" value="${escapeHtml(c.icon || '')}" class="w-full mt-1 px-3 py-2 border border-gray-300 rounded-md text-sm" />
        </div>
        <div>
          <label class="text-xs font-medium text-[--ml-text-light]">Ordem de exibição</label>
          <input name="displayOrder" type="number" value="${c.displayOrder || 0}" class="w-full mt-1 px-3 py-2 border border-gray-300 rounded-md text-sm" />
        </div>
      </div>
      <div class="flex justify-end gap-2 pt-3 border-t border-gray-100">
        <button type="button" onclick="closeModal()" class="px-4 py-2 text-sm text-[--ml-text-light] hover:text-[--ml-text]">Cancelar</button>
        <button type="submit" class="bg-[--ml-blue] hover:bg-blue-600 text-white text-sm font-semibold px-4 py-2 rounded-md">Salvar</button>
      </div>
    </form>
  `);

  document.getElementById('category-form')?.addEventListener('submit', (e) => handleCategorySubmit(e, isEdit ? c.id : null));
}

async function handleCategorySubmit(e, existingId) {
  e.preventDefault();
  const fd = new FormData(e.target);
  const payload = {
    id: fd.get('id'),
    name: fd.get('name'),
    icon: fd.get('icon'),
    displayOrder: parseInt(fd.get('displayOrder') || '0', 10),
  };
  try {
    if (existingId) {
      await api.admin.categories.update(existingId, payload);
      toast('Categoria atualizada com sucesso!');
    } else {
      await api.admin.categories.create(payload);
      toast('Categoria criada com sucesso!');
    }
    closeModal();
    await loadCategoriesTab();
  } catch (e) {
    toast(e.message || 'Não foi possível salvar a categoria', 'error');
  }
}

async function deleteCategory(id) {
  if (!confirm('Tem certeza que deseja excluir esta categoria?')) return;
  try {
    await api.admin.categories.remove(id);
    toast('Categoria excluída.');
    await loadCategoriesTab();
  } catch (e) {
    toast(e.message || 'Não foi possível excluir a categoria', 'error');
  }
}

/* ============================= USUÁRIOS ============================= */

async function loadUsersTab() {
  const tbody = document.getElementById('users-table-body');
  if (!tbody) return;
  tbody.innerHTML = `<tr><td colspan="5" class="px-4 py-6 text-center text-[--ml-text-light]">Carregando...</td></tr>`;

  try {
    const users = await api.admin.users.list();
    allUsersMap.clear();
    users.forEach(u => allUsersMap.set(String(u.id), u));

    tbody.innerHTML = users.map(u => `
      <tr class="border-t border-gray-100">
        <td class="px-4 py-3 font-medium">${escapeHtml(u.name)}</td>
        <td class="px-4 py-3 text-gray-600">${escapeHtml(u.email || '')}</td>
        <td class="px-4 py-3">
          <span class="text-xs font-semibold px-2 py-0.5 rounded-full ${u.role === 'ADMIN' ? 'bg-purple-50 text-purple-700' : 'bg-gray-50 text-gray-600'}">${u.role}</span>
        </td>
        <td class="px-4 py-3">
          <span class="text-xs font-semibold px-2 py-0.5 rounded-full ${u.active ? 'bg-green-50 text-[--ml-green]' : 'bg-red-50 text-[--ml-red]'}">${u.active ? 'Ativo' : 'Inativo'}</span>
        </td>
        <td class="px-4 py-3 text-right whitespace-nowrap">
          <button class="text-[--ml-blue] hover:underline text-xs mr-3 font-semibold" onclick="openUserFormById('${u.id}')">Editar</button>
          <button class="text-[--ml-red] hover:underline text-xs font-semibold" onclick="deleteUser('${u.id}')">Excluir</button>
        </td>
      </tr>
    `).join('');
    tbody.dataset.loaded = 'true';
  } catch (e) {
    tbody.innerHTML = `<tr><td colspan="5" class="px-4 py-6 text-center text-[--ml-text-light]">Não foi possível carregar os usuários.</td></tr>`;
  }
}

function openUserFormById(id) {
  const user = allUsersMap.get(String(id));
  openUserForm(user);
}

function openUserForm(user = null) {
  const isEdit = !!user;
  const u = user || { id: '', name: '', email: '', role: 'USER', active: true, level: '' };

  openModal(isEdit ? 'Editar usuário' : 'Novo usuário', `
    <form id="user-form" class="space-y-3">
      <div>
        <label class="text-xs font-medium text-[--ml-text-light]">Nome</label>
        <input name="name" value="${escapeHtml(u.name)}" required class="w-full mt-1 px-3 py-2 border border-gray-300 rounded-md text-sm" />
      </div>
      <div>
        <label class="text-xs font-medium text-[--ml-text-light]">E-mail</label>
        <input name="email" type="email" value="${escapeHtml(u.email || '')}" required class="w-full mt-1 px-3 py-2 border border-gray-300 rounded-md text-sm" />
      </div>
      <div>
        <label class="text-xs font-medium text-[--ml-text-light]">${isEdit ? 'Nova senha (deixe em branco para manter)' : 'Senha'}</label>
        <input name="password" type="password" ${isEdit ? '' : 'required'} minlength="6" class="w-full mt-1 px-3 py-2 border border-gray-300 rounded-md text-sm" />
      </div>
      <div class="grid grid-cols-2 gap-3">
        <div>
          <label class="text-xs font-medium text-[--ml-text-light]">Papel</label>
          <select name="role" class="w-full mt-1 px-3 py-2 border border-gray-300 rounded-md text-sm">
            <option value="USER" ${u.role === 'USER' ? 'selected' : ''}>Usuário</option>
            <option value="ADMIN" ${u.role === 'ADMIN' ? 'selected' : ''}>Administrador</option>
          </select>
        </div>
        <div>
          <label class="text-xs font-medium text-[--ml-text-light]">Nível</label>
          <input name="level" value="${escapeHtml(u.level || '')}" placeholder="Nível Bronze" class="w-full mt-1 px-3 py-2 border border-gray-300 rounded-md text-sm" />
        </div>
      </div>
      <label class="flex items-center gap-2 text-sm py-1"><input type="checkbox" name="active" ${u.active !== false ? 'checked' : ''} /> Conta ativa</label>
      <div class="flex justify-end gap-2 pt-3 border-t border-gray-100">
        <button type="button" onclick="closeModal()" class="px-4 py-2 text-sm text-[--ml-text-light] hover:text-[--ml-text]">Cancelar</button>
        <button type="submit" class="bg-[--ml-blue] hover:bg-blue-600 text-white text-sm font-semibold px-4 py-2 rounded-md">Salvar</button>
      </div>
    </form>
  `);

  document.getElementById('user-form')?.addEventListener('submit', (e) => handleUserSubmit(e, isEdit ? u.id : null));
}

async function handleUserSubmit(e, existingId) {
  e.preventDefault();
  const fd = new FormData(e.target);
  const payload = {
    name: fd.get('name'),
    email: fd.get('email'),
    password: fd.get('password') || null,
    role: fd.get('role'),
    active: fd.get('active') === 'on',
    level: fd.get('level'),
  };
  try {
    if (existingId) {
      await api.admin.users.update(existingId, payload);
      toast('Usuário atualizado com sucesso!');
    } else {
      await api.admin.users.create(payload);
      toast('Usuário criado com sucesso!');
    }
    closeModal();
    const tbody = document.getElementById('users-table-body');
    if (tbody) delete tbody.dataset.loaded;
    await loadUsersTab();
    await loadDashboard();
  } catch (e) {
    toast(e.message || 'Não foi possível salvar o usuário', 'error');
  }
}

async function deleteUser(id) {
  if (!confirm('Excluir este usuário também removerá seus pedidos e carrinho. Continuar?')) return;
  try {
    await api.admin.users.remove(id);
    toast('Usuário excluído.');
    const tbody = document.getElementById('users-table-body');
    if (tbody) delete tbody.dataset.loaded;
    await loadUsersTab();
    await loadDashboard();
  } catch (e) {
    toast(e.message || 'Não foi possível excluir o usuário', 'error');
  }
}

/* ============================= PEDIDOS ============================= */

async function loadOrdersTab() {
  const tbody = document.getElementById('orders-table-body');
  if (!tbody) return;
  tbody.innerHTML = `<tr><td colspan="4" class="px-4 py-6 text-center text-[--ml-text-light]">Carregando...</td></tr>`;

  try {
    const orders = await api.admin.orders.list();
    if (orders.length === 0) {
      tbody.innerHTML = `<tr><td colspan="4" class="px-4 py-6 text-center text-[--ml-text-light]">Nenhum pedido registrado.</td></tr>`;
    } else {
      tbody.innerHTML = orders.map(o => `
        <tr class="border-t border-gray-100">
          <td class="px-4 py-3 font-mono text-xs whitespace-nowrap">${escapeHtml(o.id)}</td>
          <td class="px-4 py-3 whitespace-nowrap">${escapeHtml(o.date)}</td>
          <td class="px-4 py-3 whitespace-nowrap font-semibold">${formatBRL(o.total)}</td>
          <td class="px-4 py-3 whitespace-nowrap">
            <select onchange="updateOrderStatus('${o.id}', this.value)" class="text-xs border border-gray-300 rounded-md px-2 py-1 bg-white focus:outline-none focus:ring-1 focus:ring-[--ml-blue]">
              ${Object.entries(STATUS_LABELS).map(([value, label]) =>
                `<option value="${value}" ${o.status === value ? 'selected' : ''}>${label}</option>`
              ).join('')}
            </select>
          </td>
        </tr>
      `).join('');
    }
    tbody.dataset.loaded = 'true';
  } catch (e) {
    tbody.innerHTML = `<tr><td colspan="4" class="px-4 py-6 text-center text-[--ml-text-light]">Não foi possível carregar os pedidos.</td></tr>`;
  }
}

async function updateOrderStatus(orderId, status) {
  try {
    await api.admin.orders.updateStatus(orderId, status);
    toast('Status do pedido atualizado!');
  } catch (e) {
    toast(e.message || 'Não foi possível atualizar o status', 'error');
  }
}