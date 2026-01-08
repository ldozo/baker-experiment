
<template>
  <div class="border rounded-lg bg-white">
    <div class="max-w-7xl mx-auto p-6">
      <h1 class="text-2xl font-semibold mb-6">Demo: Credit Account</h1>

      <!-- Status banners -->
      <div v-if="globalError" class="mb-4 rounded-md bg-red-50 p-4 border border-red-200">
        <p class="text-red-700 text-sm">{{ globalError }}</p>
      </div>
      <div v-if="successMessage" class="mb-4 rounded-md bg-green-50 p-4 border border-green-200">
        <p class="text-green-700 text-sm">{{ successMessage }}</p>
      </div>

      <!-- Three-column layout -->
      <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
        <!-- Customers (Col 1) -->
        <section class="bg-white shadow-sm rounded-lg border border-gray-200">
          <div class="p-4 border-b border-gray-200 flex items-center justify-between">
            <h2 class="text-lg font-medium text-gray-900">Customers</h2>
            <span v-if="loadingCustomers" class="text-sm text-gray-500">Loading…</span>
          </div>

          <div class="p-4">
            <div class="overflow-x-auto max-h-[320px] overflow-y-auto">
              <table class="min-w-full divide-y divide-gray-200">
                <thead class="bg-gray-50">
                  <tr>
                    <th class="px-3 py-2 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Select</th>
                    <th class="px-3 py-2 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">First Name</th>
                    <th class="px-3 py-2 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Last Name</th>
                    <th class="px-3 py-2 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
                  </tr>
                </thead>
                <tbody class="bg-white divide-y divide-gray-200">
                  <tr
                    v-for="c in customers"
                    :key="c.id"
                    class="hover:bg-gray-50 cursor-pointer"
                    @click="selectCustomer(c.id)"
                  >
                    <td class="px-3 py-2">
                      <input
                        type="radio"
                        name="customer"
                        :value="c.id"
                        v-model="selectedCustomerId"
                        class="h-4 w-4 text-indigo-600 border-gray-300 focus:ring-indigo-500"
                        @click.stop
                      />
                    </td>
                    <td class="px-3 py-2 text-sm text-gray-900">{{ c.firstname }}</td>
                    <td class="px-3 py-2 text-sm text-gray-900">{{ c.lastname }}</td>
                    <td class="px-3 py-2 text-sm">
                      <span
                        :class="statusBadgeClass(c.status)"
                        class="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium"
                      >
                        {{ c.status }}
                      </span>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>

            <p v-if="customersError" class="mt-2 text-sm text-red-600">{{ customersError }}</p>
            <p v-if="!loadingCustomers && customers.length === 0" class="mt-2 text-sm text-gray-500">No customers found.</p>
          </div>
        </section>

        <!-- Accounts (Col 2) -->
        <section class="bg-white shadow-sm rounded-lg border border-gray-200">
          <div class="p-4 border-b border-gray-200 flex items-center justify-between">
            <h2 class="text-lg font-medium text-gray-900">Accounts</h2>
            <span v-if="loadingAccounts" class="text-sm text-gray-500">Loading…</span>
          </div>

          <div class="p-4">
            <div class="overflow-x-auto">
              <table class="min-w-full divide-y divide-gray-200">
                <thead class="bg-gray-50">
                  <tr>
                    <th class="px-3 py-2 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Select</th>
                    <th class="px-3 py-2 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Account #</th>
                    <th class="px-3 py-2 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Balance</th>
                    <th class="px-3 py-2 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Currency</th>
                  </tr>
                </thead>
                <tbody class="bg-white divide-y divide-gray-200">
                  <tr
                    v-for="a in accounts"
                    :key="a.id"
                    class="hover:bg-gray-50 cursor-pointer"
                    @click="selectAccount(a.id)"
                  >
                    <td class="px-3 py-2">
                      <input
                        type="radio"
                        name="account"
                        :value="a.id"
                        v-model="selectedAccountId"
                        class="h-4 w-4 text-indigo-600 border-gray-300 focus:ring-indigo-500"
                        @click.stop
                      />
                    </td>
                    <td class="px-3 py-2 text-sm text-gray-900">{{ a.accountNumber ?? a.id }}</td>
                    <td class="px-3 py-2 text-sm text-gray-900">{{ formatBalance(a.balance) }}</td>
                    <td class="px-3 py-2 text-sm text-gray-500">{{ a.currency ?? '—' }}</td>
                  </tr>
                </tbody>
              </table>
            </div>

            <p v-if="accountsError" class="mt-2 text-sm text-red-600">{{ accountsError }}</p>
            <p v-if="!loadingAccounts && accounts.length === 0" class="mt-2 text-sm text-gray-500">
              Select a customer to view accounts, or no accounts found.
            </p>
          </div>
        </section>

        <!-- Credit form (Col 3) -->
        <section class="bg-white shadow-sm rounded-lg border border-gray-200">
          <div class="p-4 border-b border-gray-200">
            <h2 class="text-lg font-medium text-gray-900">Credit Form</h2>
          </div>

          <div class="p-4 space-y-4">
            <div class="text-sm text-gray-600">
              <p>
                Selected Customer:
                <span class="font-medium text-gray-900">
                  {{ selectedCustomerLabel }}
                </span>
              </p>
              <p>
                Selected Account:
                <span class="font-medium text-gray-900">
                  {{ selectedAccountLabel }}
                </span>
              </p>
            </div>

            <div>
              <label for="amount" class="block text-sm font-medium text-gray-700">Amount</label>
              <div class="mt-1 relative rounded-md shadow-sm">
                <input
                  id="amount"
                  type="number"
                  step="0.01"
                  min="0"
                  v-model="amount"
                  placeholder="e.g., 30 or 50"
                  class="block w-full rounded-md border-gray-300 pr-12 focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                  :disabled="!selectedAccountId"
                />
                <div class="absolute inset-y-0 right-0 flex items-center pr-3">
                  <span class="text-gray-500 sm:text-sm">{{ selectedAccount?.currency ?? '₺' }}</span>
                </div>
              </div>
              <p v-if="amountError" class="mt-2 text-sm text-red-600">{{ amountError }}</p>
            </div>

            <div class="flex items-center gap-3">
              <button
                @click="performCredit"
                :disabled="submitting || !canSubmit"
                class="inline-flex items-center rounded-md bg-indigo-600 px-4 py-2 text-sm font-medium text-white shadow-sm hover:bg-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed"
              >
                <svg v-if="submitting" class="animate-spin -ml-1 mr-2 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8z"></path>
                </svg>
                Credit
              </button>

              <button
                @click="resetForm"
                class="inline-flex items-center rounded-md bg-white px-4 py-2 text-sm font-medium text-gray-700 border border-gray-300 shadow-sm hover:bg-gray-50"
              >
                Reset
              </button>
            </div>

            <p v-if="creditError" class="text-sm text-red-600">{{ creditError }}</p>
          </div>
        </section>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import axios from 'axios';

// --- Axios instances
const customersApi = axios.create({ baseURL: 'http://localhost:8081' });
const accountsApi  = axios.create({ baseURL: 'http://localhost:8082' });
const txApi        = axios.create({ baseURL: 'http://localhost:8082' });

// --- State
const customers = ref([]);
const customersError = ref('');
const loadingCustomers = ref(false);

const accounts = ref([]);
const accountsError = ref('');
const loadingAccounts = ref(false);

const selectedCustomerId = ref('');
const selectedAccountId = ref('');
const amount = ref('');

const submitting = ref(false);
const successMessage = ref('');
const creditError = ref('');
const globalError = ref('');
const amountError = ref('');

// --- Computed
const selectedCustomer = computed(() =>
  customers.value.find(c => String(c.id) === String(selectedCustomerId.value))
);

const selectedCustomerLabel = computed(() => {
  if (!selectedCustomer.value) return '—';
  const { firstname, lastname, status } = selectedCustomer.value;
  return `${firstname ?? ''} ${lastname ?? ''} (${status ?? '—'})`.trim();
});

const selectedAccount = computed(() =>
  accounts.value.find(a => String(a.id) === String(selectedAccountId.value))
);

const selectedAccountLabel = computed(() => {
  if (!selectedAccount.value) return '—';
  const num = selectedAccount.value.accountNumber ?? selectedAccount.value.id;
  const bal = formatBalance(selectedAccount.value.balance);
  const cur = selectedAccount.value.currency ?? '';
  return `${num} • ${bal} ${cur}`.trim();
});

const canSubmit = computed(() => {
  const val = parseFloat(amount.value);
  return selectedAccountId.value && !isNaN(val) && val > 0;
});

// --- Helpers
const statusBadgeClass = (status) => {
  const s = String(status || '').toLowerCase();
  if (s.includes('active')) return 'bg-green-100 text-green-800';
  if (s.includes('blocked') || s.includes('inactive')) return 'bg-red-100 text-red-800';
  return 'bg-gray-100 text-gray-800';
};

const formatBalance = (b) => {
  if (b === null || b === undefined) return '—';
  const num = Number(b);
  if (Number.isNaN(num)) return String(b);
  return num.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 });
};

const selectCustomer = (id) => {
  selectedCustomerId.value = id;
};

const selectAccount = (id) => {
  selectedAccountId.value = id;
};

// --- Loaders
const loadCustomers = async () => {
  customersError.value = '';
  loadingCustomers.value = true;
  try {
    const { data } = await customersApi.get('/customers');
    // Expecting array [{ id, firstname, lastname, status }]
    customers.value = Array.isArray(data) ? data : (data?.items ?? []);
  } catch (err) {
    customersError.value = err?.response?.data?.message ?? err?.message ?? 'Failed to load customers.';
  } finally {
    loadingCustomers.value = false;
  }
};

const loadAccounts = async (customerId) => {
  accountsError.value = '';
  loadingAccounts.value = true;
  try {
    const { data } = await accountsApi.get(`/accounts/by-customer/${encodeURIComponent(customerId)}`);
    accounts.value = Array.isArray(data) ? data : (data?.items ?? []);
  } catch (err) {
    accountsError.value = err?.response?.data?.message ?? err?.message ?? 'Failed to load accounts.';
  } finally {
    loadingAccounts.value = false;
  }
};

// --- Actions
const performCredit = async () => {
  creditError.value = '';
  successMessage.value = '';
  amountError.value = '';

  const val = parseFloat(amount.value);
  if (isNaN(val) || val <= 0) {
    amountError.value = 'Please enter a valid positive amount.';
    return;
  }
  if (!selectedAccountId.value) {
    creditError.value = 'Please select an account.';
    return;
  }

  submitting.value = true;
  try {
    const payload = { accountId: selectedAccountId.value, amount: val };
    await txApi.post('/transactions/credit', payload, {
      headers: { 'Content-Type': 'application/json' },
    });

    successMessage.value = `Credited ${val} to account ${selectedAccount?.value?.accountNumber ?? selectedAccountId.value}.`;

    // Refresh accounts to reflect updated balance
    if (selectedCustomerId.value) {
      await loadAccounts(selectedCustomerId.value);
    }

    // Clear amount field
    amount.value = '';
  } catch (err) {
    creditError.value =
      err?.response?.data?.message ??
      err?.message ??
      'Credit operation failed.';
  } finally {
    submitting.value = false;
  }
};

const resetForm = () => {
  selectedCustomerId.value = '';
  selectedAccountId.value = '';
  amount.value = '';
  successMessage.value = '';
  customersError.value = '';
  accountsError.value = '';
  creditError.value = '';
  globalError.value = '';
  amountError.value = '';
};

// --- Effects
onMounted(async () => {
  try {
    await loadCustomers();
  } catch (err) {
    globalError.value = 'An unexpected error occurred while initializing the page.';
  }
});

watch(selectedCustomerId, async (id) => {
  // Reset account selection when customer changes
  selectedAccountId.value = '';
  accounts.value = [];
  accountsError.value = '';
  successMessage.value = '';
  creditError.value = '';
  if (id) {
    await loadAccounts(id);
  }
});
</script>

<style scoped>
/* Tailwind handles styling; no extra CSS required. */
</style>
``
