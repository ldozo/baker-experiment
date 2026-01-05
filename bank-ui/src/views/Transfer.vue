<template>
  <div class="bg-white border rounded-lg p-6">
    <div class="mx-auto max-w-7xl">
      <h1 class="text-2xl font-semibold text-gray-800 mb-4">Money Transfer</h1>

      <!-- Status banners -->
      <div v-if="globalError" class="mb-4 rounded-md bg-red-50 p-4 text-red-700">
        {{ globalError }}
      </div>
      <div v-if="successMessage" class="mb-4 rounded-md bg-green-50 p-4 text-green-700">
        {{ successMessage }}
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <!-- Source Panel -->
        <section class="bg-white shadow-sm rounded-lg border-gray-200 border ">
          <header class="px-4 py-3 border-b">
            <h2 class="text-lg font-medium text-gray-900">Source</h2>
            <p class="text-sm text-gray-500">Choose the customer and source account</p>
          </header>
          <div class="p-4 space-y-4">
            <!-- Customers list -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Customers</label>
              <div class="flex items-center mb-2">
                <input
                  v-model="sourceSearch"
                  type="text"
                  placeholder="Search by name…"
                  class="w-full rounded-md border-gray-300 focus:border-indigo-500 focus:ring-indigo-500"
                />
              </div>

              <div class="max-h-64 overflow-auto border rounded-md divide-y">
                <button
                  v-for="c in filteredCustomers(sourceSearch)"
                  :key="'src-' + c.id"
                  class="w-full text-left px-3 py-2 hover:bg-gray-50 flex items-center justify-between"
                  :class="sourceCustomer?.id === c.id ? 'bg-indigo-50' : ''"
                  @click="onSelectCustomer('source', c)"
                >
                  <span class="text-sm text-gray-800">
                    {{ fullName(c) }}
                  </span>
                  <span
                    v-if="sourceCustomer?.id === c.id"
                    class="ml-2 inline-flex items-center rounded bg-indigo-100 px-2 py-0.5 text-xs font-medium text-indigo-700"
                  >
                    Selected
                  </span>
                </button>
              </div>
            </div>

            <!-- Accounts list -->
            <div>
              <div class="flex items-center justify-between mb-2">
                <label class="block text-sm font-medium text-gray-700">Accounts</label>
                <div v-if="sourceAccountsLoading" class="text-xs text-gray-500">Loading accounts…</div>
              </div>

              <div
                class="max-h-64 overflow-auto border rounded-md divide-y"
                :class="!sourceCustomer ? 'opacity-50 pointer-events-none' : ''"
              >
                <button
                  v-for="a in accountsByCustomerId.get(sourceCustomer?.id) || []"
                  :key="'src-acc-' + a.id"
                  class="w-full text-left px-3 py-2 hover:bg-gray-50 flex items-center justify-between"
                  :class="sourceAccount?.id === a.id ? 'bg-indigo-50' : ''"
                  @click="sourceAccount = a"
                >
                  <span class="text-sm text-gray-800 grid">
                    <span>{{ accountLabel(a) }}</span>
                    <span>{{ a.balance + ' ' + a.currency }}</span>
                  </span>
                  <span
                    v-if="sourceAccount?.id === a.id"
                    class="ml-2 inline-flex items-center rounded bg-indigo-100 px-2 py-0.5 text-xs font-medium text-indigo-700"
                  >
                    Selected
                  </span>
                </button>

                <div
                  v-if="sourceCustomer && !sourceAccountsLoading && (accountsByCustomerId.get(sourceCustomer.id) || []).length === 0"
                  class="px-3 py-2 text-sm text-gray-500"
                >
                  No accounts for this customer.
                </div>
              </div>
              <p v-if="sourceError" class="mt-2 text-sm text-red-600">{{ sourceError }}</p>
            </div>
          </div>
        </section>

        <!-- Target Panel -->
        <section class="bg-white shadow-sm rounded-lg border border-gray-200">
          <header class="px-4 py-3 border-b">
            <h2 class="text-lg font-medium text-gray-900">Target</h2>
            <p class="text-sm text-gray-500">Choose the customer and target account</p>
          </header>
          <div class="p-4 space-y-4">
            <!-- Customers list -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Customers</label>
              <div class="flex items-center mb-2">
                <input
                  v-model="targetSearch"
                  type="text"
                  placeholder="Search by name…"
                  class="w-full rounded-md border-gray-300 focus:border-indigo-500 focus:ring-indigo-500"
                />
              </div>

              <div class="max-h-64 overflow-auto border rounded-md divide-y">
                <button
                  v-for="c in filteredCustomers(targetSearch)"
                  :key="'tgt-' + c.id"
                  class="w-full text-left px-3 py-2 hover:bg-gray-50 flex items-center justify-between"
                  :class="targetCustomer?.id === c.id ? 'bg-indigo-50' : ''"
                  @click="onSelectCustomer('target', c)"
                >
                  <span class="text-sm text-gray-800">
                    {{ fullName(c) }}
                  </span>
                  <span
                    v-if="targetCustomer?.id === c.id"
                    class="ml-2 inline-flex items-center rounded bg-indigo-100 px-2 py-0.5 text-xs font-medium text-indigo-700"
                  >
                    Selected
                  </span>
                </button>
              </div>
            </div>

            <!-- Accounts list -->
            <div>
              <div class="flex items-center justify-between mb-2">
                <label class="block text-sm font-medium text-gray-700">Accounts</label>
                <div v-if="targetAccountsLoading" class="text-xs text-gray-500">Loading accounts…</div>
              </div>

              <div
                class="max-h-64 overflow-auto border rounded-md divide-y"
                :class="!targetCustomer ? 'opacity-50 pointer-events-none' : ''"
              >
                <button
                  v-for="a in accountsByCustomerId.get(targetCustomer?.id) || []"
                  :key="'tgt-acc-' + a.id"
                  class="w-full text-left px-3 py-2 hover:bg-gray-50 flex items-center justify-between"
                  :class="targetAccount?.id === a.id ? 'bg-indigo-50' : ''"
                  @click="targetAccount = a"
                >
                <span class="text-sm text-gray-800 grid">
                    <span>{{ accountLabel(a) }}</span>
                    <span>{{ a.balance + ' ' + a.currency }}</span>
                </span>
                  <span
                    v-if="targetAccount?.id === a.id"
                    class="ml-2 inline-flex items-center rounded bg-indigo-100 px-2 py-0.5 text-xs font-medium text-indigo-700"
                  >
                    Selected
                  </span>
                </button>

                <div
                  v-if="targetCustomer && !targetAccountsLoading && (accountsByCustomerId.get(targetCustomer.id) || []).length === 0"
                  class="px-3 py-2 text-sm text-gray-500"
                >
                  No accounts for this customer.
                </div>
              </div>
              <p v-if="targetError" class="mt-2 text-sm text-red-600">{{ targetError }}</p>
            </div>
          </div>
        </section>
      </div>

      <!-- Amount & Send -->
      <section class="mt-6 bg-white shadow-sm rounded-lg border">
        <div class="p-4 space-y-4">
          <div class="grid grid-cols-1 md:grid-cols-3 gap-4 items-end">
            <div class="md:col-span-2">
              <label class="block text-sm font-medium text-gray-700 mb-1">Amount</label>
              <input
                v-model.number="amount"
                type="number"
                step="0.01"
                min="0"
                placeholder="0.00"
                class="w-full rounded-md border-gray-300 focus:border-indigo-500 focus:ring-indigo-500"
              />
              <p v-if="amountError" class="mt-1 text-sm text-red-600">{{ amountError }}</p>
            </div>

            <div class="flex md:justify-end">
              <button
                class="inline-flex items-center rounded-md bg-indigo-600 px-4 py-2 text-white hover:bg-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed"
                :disabled="!canSubmit || sending"
                @click="sendMoney"
              >
                <svg v-if="sending" class="animate-spin -ml-1 mr-2 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v4l3-3-3-3v4A8 8 0 104 12z"></path>
                </svg>
                Send Money
              </button>
            </div>
          </div>

          <div class="text-sm text-gray-500">
            <div>Source Account: <span class="font-medium">{{ sourceAccount ? accountLabel(sourceAccount) : '-' }}</span></div>
            <div>Target Account: <span class="font-medium">{{ targetAccount ? accountLabel(targetAccount) : '-' }}</span></div>
          </div>

          <p v-if="submitError" class="text-sm text-red-600">{{ submitError }}</p>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'

/** ---- State ---- */
const customers = ref([])
const customersLoading = ref(false)
const globalError = ref('')
const successMessage = ref('')

const sourceCustomer = ref(null)
const targetCustomer = ref(null)
const sourceAccount = ref(null)
const targetAccount = ref(null)

const sourceAccountsLoading = ref(false)
const targetAccountsLoading = ref(false)
const sourceError = ref('')
const targetError = ref('')

const accountsByCustomerId = ref(new Map()) // cache: customerId -> accounts[]

const sourceSearch = ref('')
const targetSearch = ref('')

const amount = ref(null)
const amountError = ref('')
const sending = ref(false)
const submitError = ref('')

/** ---- Helpers ---- */
const fullName = (c) => `${c.firstname ?? ''} ${c.lastname ?? ''}`.trim() || `Customer #${c.id}`

const filteredCustomers = (query) => {
  const q = (query || '').toLowerCase()
  if (!q) return customers.value
  return customers.value.filter(c =>
    fullName(c).toLowerCase().includes(q)
  )
}

const accountLabel = (a) => {
  // Defensive labeling (fields may vary)
  const parts = []
  parts.push(`Account #${a.id}`) 
  return parts.join(' • ')
}

const canSubmit = computed(() => {
  return !!(sourceAccount.value && targetAccount.value && validAmount(amount.value) && !sending.value && sourceAccount.value.id !== targetAccount.value.id)
})

const validAmount = (val) => {
  const n = Number(val)
  return Number.isFinite(n) && n > 0
}

/** ---- Data fetching ---- */
const loadCustomers = async () => {
  customersLoading.value = true
  globalError.value = ''
  try {
    const { data } = await axios.get('http://localhost:8081/customers')
    customers.value = Array.isArray(data) ? data : []
  } catch (err) {
    console.error(err)
    globalError.value = 'Failed to load customers. Please try again.'
  } finally {
    customersLoading.value = false
  }
}

const loadAccountsForCustomer = async (customerId, which) => {
  // which: 'source' | 'target' (for loading flags / error placement)
  const setLoading = which === 'source' ? sourceAccountsLoading : targetAccountsLoading
  const setError = which === 'source' ? sourceError : targetError

  // Use cache if present
  if (accountsByCustomerId.value.has(customerId)) {
    setError.value = ''
    return
  }

  setLoading.value = true
  setError.value = ''

  try {
    const { data } = await axios.get(`http://localhost:8082/accounts/by-customer/${customerId}`)
    const list = Array.isArray(data) ? data : []
    accountsByCustomerId.value.set(customerId, list)
  } catch (err) {
    console.error(err)
    setError.value = 'Failed to load accounts for the selected customer.'
  } finally {
    setLoading.value = false
  }
}

/** ---- Selection handlers ---- */
const onSelectCustomer = async (which, customer) => {
  if (which === 'source') {
    sourceCustomer.value = customer
    sourceAccount.value = null
    await loadAccountsForCustomer(customer.id, 'source')
  } else {
    targetCustomer.value = customer
    targetAccount.value = null
    await loadAccountsForCustomer(customer.id, 'target')
  }
}

/** ---- Submit ---- */
const sendMoney = async () => {
  submitError.value = ''
  successMessage.value = ''
  amountError.value = ''

  if (!validAmount(amount.value)) {
    amountError.value = 'Enter a valid positive amount.'
    return
  }
  if (!sourceAccount.value || !targetAccount.value) {
    submitError.value = 'Please select both source and target accounts.'
    return
  }
  if (sourceAccount.value.id === targetAccount.value.id) {
    submitError.value = 'Source and target accounts cannot be the same.'
    return
  }

  const payload = {
    // NOTE: Adjust keys if your backend expects different field names
    sourceAccountId: sourceAccount.value.id,
    targetAccountId: targetAccount.value.id,
    amount: Number(amount.value),
  }

  sending.value = true
  try {
    await axios.post('http://localhost:8080/sendMoney', payload, {
      headers: { 'Content-Type': 'application/json' },
    })
    successMessage.value = 'Transfer successful.'
    // Optional: clear amount & keep selections
    amount.value = null
  } catch (err) {
    console.error(err)
    submitError.value = parseErrorMessage(err) || 'Transfer failed. Please try again.'
  } finally {
    sending.value = false
  }
}

const parseErrorMessage = (err) => {
  // Try to extract a meaningful message
  const msg =
    err?.response?.data?.message ||
    err?.response?.data?.error ||
    err?.message
  return typeof msg === 'string' ? msg : ''
}

/** ---- Lifecycle ---- */
onMounted(async () => {
  await loadCustomers()
})
</script>