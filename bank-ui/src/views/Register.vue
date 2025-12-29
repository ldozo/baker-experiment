
<template>
  <div class="container mx-auto my-10 px-4">
    <div class="mx-auto max-w-5xl grid grid-cols-1 lg:grid-cols-2 gap-6 items-start">
      <!-- Left: Form card -->
      <div class="bg-white rounded-lg shadow-lg p-6">
        <h1 class="text-2xl font-bold text-gray-900 mb-4">Register</h1>

        <!-- Success / Error banners -->
        <div v-if="successMessage" class="mb-4 p-3 rounded bg-emerald-100 text-emerald-800">
          {{ successMessage }}
        </div>
        <div v-if="errorMessage" class="mb-4 p-3 rounded bg-red-100 text-red-800">
          {{ errorMessage }}
        </div>

        <form
          @submit.prevent="submit"
          class="grid grid-cols-1 md:grid-cols-2 gap-4 items-start content-start auto-rows-min"
        >
          <!-- First Name -->
          <div class="self-start">
            <label class="block text-gray-700 font-medium mb-1" for="firstname">First Name</label>
            <input
              id="firstname"
              v-model.trim="form.firstname"
              type="text"
              class="w-full border border-gray-300 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-emerald-500"
              placeholder="John"
            />
            <p v-if="errors.firstname" class="mt-1 text-sm text-red-600">{{ errors.firstname }}</p>
          </div>

          <!-- Last Name -->
          <div class="self-start">
            <label class="block text-gray-700 font-medium mb-1" for="lastname">Last Name</label>
            <input
              id="lastname"
              v-model.trim="form.lastname"
              type="text"
              class="w-full border border-gray-300 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-emerald-500"
              placeholder="Doe"
            />
            <p v-if="errors.lastname" class="mt-1 text-sm text-red-600">{{ errors.lastname }}</p>
          </div>

          <!-- Email -->
          <div class="self-start">
            <label class="block text-gray-700 font-medium mb-1" for="email">Email</label>
            <input
              id="email"
              v-model.trim="form.email"
              type="email"
              class="w-full border border-gray-300 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-emerald-500"
              placeholder="john.doe@example.com"
            />
            <p v-if="errors.email" class="mt-1 text-sm text-red-600">{{ errors.email }}</p>
          </div>

          <!-- Age -->
          <div class="self-start">
            <label class="block text-gray-700 font-medium mb-1" for="age">Age</label>
            <input
              id="age"
              v-model.number="form.age"
              type="number"
              min="0"
              class="w-full border border-gray-300 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-emerald-500"
              placeholder="30"
            />
            <p v-if="errors.age" class="mt-1 text-sm text-red-600">{{ errors.age }}</p>
          </div>

          <!-- Nationality -->
          <div class="md:col-span-2 self-start">
            <label class="block text-gray-700 font-medium mb-1" for="nationality">Nationality</label>
            <select
              id="nationality"
              v-model="form.nationality"
              class="w-full border border-gray-300 rounded px-3 py-2 bg-white focus:outline-none focus:ring-2 focus:ring-emerald-500"
            >
              <option disabled value="">Select nationality</option>
              <option value="tr">Turkish</option>
              <option value="usa">American</option>
              <option value="gr">German</option>
              <option value="fr">French</option>
              <option value="br">British</option>
              <option value="Other">Other</option>
            </select>
            <p v-if="errors.nationality" class="mt-1 text-sm text-red-600">{{ errors.nationality }}</p>
          </div>

          <!-- Submit -->
          <div class="md:col-span-2 self-start">
            <button
              type="submit"
              :disabled="submitting"
              class="w-full bg-emerald-600 text-white font-semibold py-2 rounded hover:bg-emerald-700 disabled:opacity-60 disabled:cursor-not-allowed"
            >
              <span v-if="submitting">Submitting...</span>
              <span v-else>Register</span>
            </button>
          </div>
        </form>
      </div>

      <!-- Right: Request/Response inspector -->
      <div class="bg-white   p-6">
        <div class="space-y-5">
          <!-- Request Body -->
          <section>
            <div class="flex items-center justify-between mb-2">
              <span class="text-gray-700 font-medium">Request body</span>
              <button
                v-if="lastRequest"
                @click="copy(formatJson(lastRequest))"
                class="text-xs text-emerald-700 hover:underline"
              >
                Copy
              </button>
            </div>
            <pre
              class="bg-gray-50 border border-gray-200 rounded p-3 text-sm font-mono whitespace-pre overflow-auto max-h-64"
            >{{ formatJson(lastRequest) }}</pre>
          </section>

          <!-- Response Body -->
          <section>
            <div class="flex items-center justify-between mb-2">
              <span class="text-gray-700 font-medium">Response body</span>
              <button
                v-if="lastResponse"
                @click="copy(formatJson(lastResponse))"
                class="text-xs text-emerald-700 hover:underline"
              >
                Copy
              </button>
            </div>
            <pre
              class="bg-gray-50 border border-gray-200 rounded p-3 text-sm font-mono whitespace-pre overflow-auto max-h-64"
            >{{ formatJson(lastResponse) }}</pre>
          </section>

          <!-- Optional meta -->
          <section class="flex grid-cols-2 gap-3">
              <div>
                  <p class="text-xs text-gray-500">Endpoint</p>
                  <p class="text-sm font-mono truncate">POST {{ endpoint }}</p>
                </div>
                <div>
                  <p class="text-xs text-gray-500">Status</p>
                  <p class="text-sm font-mono">{{ statusText }}</p>
                </div>
          </section>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import axios from 'axios'
import { reactive, ref, computed } from 'vue'

const endpoint = 'http://localhost:8080/register'

type RegisterForm = {
  firstname: string
  lastname: string
  email: string
  age: number | null
  nationality: string
}

const form = reactive<RegisterForm>({
  firstname: '',
  lastname: '',
  email: '',
  age: null,
  nationality: '',
})

const errors = reactive<Record<string, string>>({})
const submitting = ref(false)
const successMessage = ref('')
const errorMessage = ref('')

// Inspector state
const lastRequest = ref<any>(null)
const lastResponse = ref<any>(null)
const lastStatus = ref<number | null>(null)

const statusText = computed(() => {
  if (submitting.value) return 'Submitting...'
  if (lastStatus.value === null) return '—'
  return String(lastStatus.value)
})

function formatJson(obj: any) {
  try {
    if (obj === null || obj === undefined) return '—'
    return JSON.stringify(obj, null, 2)
  } catch {
    return String(obj)
  }
}

async function copy(text: string) {
  try {
    await navigator.clipboard.writeText(text)
  } catch {
    // No-op if clipboard not available
  }
}

function validate() {
  // Clear previous errors
  for (const key of Object.keys(errors)) delete errors[key]

  if (!form.firstname) errors.firstname = 'First name is required'
  if (!form.lastname) errors.lastname = 'Last name is required'

  if (!form.email) {
    errors.email = 'Email is required'
  } else {
    const emailOk = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)
    if (!emailOk) errors.email = 'Please enter a valid email'
  }

  if (form.age === null || Number.isNaN(form.age)) {
    errors.age = 'Age is required'
  } else if (form.age < 0) {
    errors.age = 'Age must be a positive number'
  }

  if (!form.nationality) errors.nationality = 'Please select a nationality'

  return Object.keys(errors).length === 0
}

async function submit() {
  errorMessage.value = ''
  successMessage.value = ''

  if (!validate()) return
  submitting.value = true

  // Capture request body for the inspector
  const payload = {
    firstname: form.firstname,
    lastname: form.lastname,
    email: form.email,
    age: form.age,
    nationality: form.nationality,
  }
  lastRequest.value = payload

  try {
    const resp = await axios.post(endpoint, payload, {
      headers: { 'Content-Type': 'application/json' },
      // withCredentials: true, // uncomment if your API uses cookies
    })

    lastStatus.value = resp.status
    lastResponse.value = resp.data

    successMessage.value = 'Registration successful.'
    // Optionally reset the form
    Object.assign(form, { firstname: '', lastname: '', email: '', age: null, nationality: '' })
  } catch (err: any) {
    lastStatus.value = err?.response?.status ?? null
    lastResponse.value =
      err?.response?.data ??
      { error: err?.message ?? 'Registration failed.' }

    const msg =
      err?.response?.data?.message ||
      err?.response?.data?.error ||
      err?.message ||
      'Registration failed.'
    errorMessage.value = msg
  } finally {
    submitting.value = false
  }
}
</script>
