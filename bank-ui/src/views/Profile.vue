
<template>
  <!-- Base layout = regular div, constrained width -->
  <div class="bg-white border rounded-lg">
    <!-- Header with link to root -->
    <header class="px-6 py-4">
      <div class="mx-auto max-w-6xl flex items-center justify-between">
        <div>
          <h1 class="text-xl font-semibold text-gray-900">People</h1>
          <p class="mt-1 text-xs text-gray-500">Edit existing records</p>
        </div>

        <!-- Link to root folder (/) -->
        <router-link
          to="/"
          class="inline-flex items-center text-emerald-600 hover:text-emerald-700 text-sm font-medium"
        >
          ← Back to root
        </router-link>
      </div>
    </header>

    <!-- Content: small list (1/3) + wider form (2/3) -->
    <main class="px-6 pb-8">
      <div class="mx-auto max-w-6xl grid grid-cols-1 xl:grid-cols-3 gap-6">
        <!-- LEFT: Small, striped list -->
        <section class="xl:col-span-1">
          <div class="rounded-lg border border-gray-200 bg-white shadow-sm">
            <div class="border-b border-gray-200 px-4 py-3">
              <h2 class="text-sm font-semibold text-gray-900">List</h2>
              <p class="text-xs text-gray-500">Click a row to edit</p>
            </div>

            <!-- Compact list with striping and scroll -->
            <div class="overflow-x-auto max-h-[320px] overflow-y-auto">
              <table class="min-w-full">
                <thead class="bg-gray-50 sticky top-0 z-10 border-b border-gray-200">
                  <tr>
                    <th class="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">First</th>
                    <th class="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Last</th>
                    <th class="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
                  </tr>
                </thead>
                <tbody class="text-sm">
                  <tr
                    v-for="p in people"
                    :key="p.id"
                    class="odd:bg-white even:bg-gray-50 hover:bg-emerald-50 cursor-pointer"
                    :class="selectedId === p.id ? 'bg-emerald-50/70' : ''"
                    @click="startEdit(p)"
                  >
                    <td class="px-4 py-2 font-medium text-gray-900">{{ p.firstname }}</td>
                    <td class="px-4 py-2 text-gray-900">{{ p.lastname }}</td>
                    <td class="px-4 py-2">
                      <span class="rounded-full px-2 py-1 text-xs font-medium uppercase tracking-wide" :class="statusPillClass(p.status)">
                        {{ p.status }}
                      </span>
                    </td>
                  </tr>

                  <tr v-if="people.length === 0">
                    <td colspan="3" class="px-4 py-6 text-center text-sm text-gray-500">
                      No records loaded.
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>

            <div class="p-3 text-xs text-gray-500">
              Loaded from <code>http://localhost:8081/customers</code>
            </div>
          </div>
        </section>

        <!-- RIGHT: Wider form -->
        <aside class="xl:col-span-2">
          <div class="rounded-lg border border-gray-200 bg-white shadow-sm">
            <div class="flex items-center justify-between border-b border-gray-200 px-4 py-3">
              <h2 class="text-lg font-semibold text-gray-900">
                {{ isEditing ? 'Edit Person' : 'Select a row to edit' }}
              </h2>
              <span v-if="isEditing" class="text-xs text-gray-500">ID: {{ selectedId }}</span>
            </div>

            <form class="px-4 py-4 space-y-4" @submit.prevent="savePerson()">
              <!-- Name row -->
              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label class="block text-sm font-medium text-gray-700">First name</label>
                  <input
                    v-model.trim="form.firstname"
                    type="text"
                    :disabled="!isEditing"
                    required
                    class="mt-1 w-full rounded-md border border-gray-300 px-3 py-2 text-sm focus:border-emerald-500 focus:ring-emerald-500 disabled:bg-gray-100 disabled:text-gray-500"
                    placeholder="First name"
                  />
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700">Last name</label>
                  <input
                    v-model.trim="form.lastname"
                    type="text"
                    :disabled="!isEditing"
                    required
                    class="mt-1 w-full rounded-md border border-gray-300 px-3 py-2 text-sm focus:border-emerald-500 focus:ring-emerald-500 disabled:bg-gray-100 disabled:text-gray-500"
                    placeholder="Last name"
                  />
                </div>
              </div>

              <!-- Status -->
              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label class="block text-sm font-medium text-gray-700">Status</label>
                  <select
                    v-model="form.status"
                    :disabled="!isEditing"
                    class="mt-1 w-full rounded-md border border-gray-300 px-3 py-2 text-sm focus:border-emerald-500 focus:ring-emerald-500 disabled:bg-gray-100 disabled:text-gray-500"
                  >
                    <option value="initial">INITIAL</option>
                    <option value="underage">UNDERAGE</option>
                    <option value="active">ACTIVE</option>
                    <option value="deceased">DECEASED</option>
                  </select>
                </div>
              </div>

              <!-- Email + Age row -->
              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label class="block text-sm font-medium text-gray-700">Email</label>
                  <input
                    v-model.trim="form.email"
                    type="email"
                    inputmode="email"
                    :disabled="!isEditing"
                    class="mt-1 w-full rounded-md border border-gray-300 px-3 py-2 text-sm focus:border-emerald-500 focus:ring-emerald-500 disabled:bg-gray-100 disabled:text-gray-500"
                    placeholder="name@example.com"
                  />
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700">Age</label>
                  <input
                    v-model.number="form.age"
                    type="number"
                    min="0"
                    max="120"
                    :disabled="!isEditing"
                    class="mt-1 w-full rounded-md border border-gray-300 px-3 py-2 text-sm focus:border-emerald-500 focus:ring-emerald-500 disabled:bg-gray-100 disabled:text-gray-500"
                    placeholder="e.g., 29"
                  />
                </div>
              </div>

              <!-- Nationality -->
              <div>
                <label class="block text-sm font-medium text-gray-700">Nationality</label>
                <input
                  v-model.trim="form.nationality"
                  type="text"
                  :disabled="!isEditing"
                  class="mt-1 w-full rounded-md border border-gray-300 px-3 py-2 text-sm focus:border-emerald-500 focus:ring-emerald-500 disabled:bg-gray-100 disabled:text-gray-500"
                  placeholder="e.g., Turkish"
                  list="nationalities"
                />
                <datalist id="nationalities">
                  <option value="Turkish" />
                  <option value="German" />
                  <option value="French" />
                  <option value="British" />
                  <option value="American" />
                </datalist>
              </div>

              <!-- Actions: only Update -->
              <div class="pt-2 flex items-center justify-end">
                <button
                  type="submit"
                  class="rounded-md bg-emerald-600 px-4 py-2 text-sm text-white hover:bg-emerald-700 disabled:bg-emerald-300"
                  :disabled="!isEditing"
                >
                  Update
                </button>
              </div>
            </form>
          </div>
        </aside>
      </div>
    </main>

    <!-- Toast-ish inline message -->
    <transition name="fade">
      <div v-if="banner" class="fixed bottom-4 right-4 z-40">
        <div class="rounded-md bg-gray-900/90 px-4 py-3 text-sm text-white shadow-lg">
          {{ banner }}
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'

type Status = 'initial' | 'underage' | 'active' | 'deceased'

type Person = {
  id: number | string
  firstname: string
  lastname: string
  status: Status
  email?: string
  age?: number
  nationality?: string
}

const people = ref<Person[]>([])
const banner = ref<string | null>(null)

const isEditing = ref(false)
const selectedId = ref<number | string | null>(null)
const form = ref<{
  firstname: string
  lastname: string
  status: Status
  email: string
  age: number | null
  nationality: string
}>({
  firstname: '',
  lastname: '',
  status: 'initial',
  email: '',
  age: null,
  nationality: '',
})

function statusPillClass(status: Status) {
  switch (status) {
    case 'active':
      return 'bg-emerald-50 text-emerald-700 ring-1 ring-inset ring-emerald-600/20'
    case 'underage':
      return 'bg-amber-50 text-amber-700 ring-1 ring-inset ring-amber-600/20'
    case 'deceased':
      return 'bg-rose-50 text-rose-700 ring-1 ring-inset ring-rose-600/20'
    case 'initial':
    default:
      return 'bg-gray-50 text-gray-700 ring-1 ring-inset ring-gray-500/20'
  }
}

function showBanner(msg: string) {
  banner.value = msg
  setTimeout(() => (banner.value = null), 1800)
}

function startEdit(p: Person) {
  isEditing.value = true
  selectedId.value = p.id
  form.value = {
    firstname: p.firstname ?? '',
    lastname: p.lastname ?? '',
    status: (p.status ?? 'initial') as Status,
    email: p.email ?? '',
    age: typeof p.age === 'number' ? p.age : null,
    nationality: p.nationality ?? '',
  }
}

// Fetch list from localhost:8081/customers
async function loadCustomers() {
  try {
    const res = await fetch('http://localhost:8081/customers')
    if (!res.ok) throw new Error(`HTTP ${res.status}`)
    const raw = await res.json()

    // Normalize to Person shape (maps typical variants to required statuses)
    const toStatus = (s: any): Status => {
      const val = String(s ?? '').toLowerCase()
      if (['active'].includes(val)) return 'active'
      if (['underage'].includes(val)) return 'underage'
      if (['deceased'].includes(val)) return 'deceased'
      if (['initial', 'new', 'pending', 'created'].includes(val)) return 'initial'
      return 'initial'
    }

    people.value = (Array.isArray(raw) ? raw : []).map((c: any, idx: number) => ({
      id: c.id ?? c.customerId ?? idx + 1,
      firstname: c.firstname ?? c.givenName ?? c.name?.first ?? '',
      lastname: c.lastname ?? c.surname ?? c.name?.last ?? '',
      status: toStatus(c.status),
      email: c.email ?? '',
      age: typeof c.age === 'number' ? c.age : undefined,
      nationality: c.nationality ?? c.country ?? undefined,
    }))

    showBanner(`Loaded ${people.value.length} customers`)
  } catch (err: any) {
    console.error(err)
    showBanner('Failed to load customers')
  }
}

onMounted(loadCustomers)
 
async function savePerson() {
  if (!isEditing.value || selectedId.value == null) return

  // Minimal client validation
  if (!form.value.firstname || !form.value.lastname) {
    showBanner('First and last name are required')
    return
  }
  if (form.value.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.value.email)) {
    showBanner('Invalid email format')
    return
  }
  if (form.value.age != null && (form.value.age < 0 || form.value.age > 120)) {
    showBanner('Age must be between 0 and 120')
    return
  }

  // Local state update
  people.value = people.value.map(p =>
    p.id === selectedId.value
      ? {
          ...p,
          firstname: form.value.firstname.trim(),
          lastname: form.value.lastname.trim(),
          status: form.value.status,
          email: form.value.email.trim() || undefined,
          age: form.value.age ?? undefined,
          nationality: form.value.nationality.trim() || undefined,
        }
      : p
  )

  showBanner('Person updated')
}
</script>

<style scoped>
/* Simple fade transition */
.fade-enter-active, .fade-leave-active { transition: opacity 0.2s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>
