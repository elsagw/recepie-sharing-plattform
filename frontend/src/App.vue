<script setup>
import { onMounted, ref } from 'vue'

const recipes = ref([])
const loading = ref(true)
const error = ref('')
const saving = ref(false)
const formError = ref('')
const newRecipe = ref({
  title: '',
  description: '',
})

const loadRecipes = async () => {
  try {
    const response = await fetch('http://localhost:8080/api/recipes')
    if (!response.ok) {
      throw new Error(`Request failed: ${response.status}`)
    }
    recipes.value = await response.json()
  } catch (err) {
    error.value = err instanceof Error ? err.message : 'Could not load recipes'
  } finally {
    loading.value = false
  }
}

const createRecipe = async () => {
  formError.value = ''
  saving.value = true

  try {
    const response = await fetch('http://localhost:8080/api/recipes', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(newRecipe.value),
    })

    if (!response.ok) {
      throw new Error(`Could not save recipe (${response.status})`)
    }

    recipes.value.push(await response.json())
    newRecipe.value = { title: '', description: '' }
  } catch (err) {
    formError.value = err instanceof Error ? err.message : 'Could not save recipe'
  } finally {
    saving.value = false
  }
}

onMounted(loadRecipes)
</script>

<template>
  <main class="recipe-app">
    <h1>Recipe Social Network</h1>

    <form class="recipe-form" @submit.prevent="createRecipe">
      <h2>Share a recipe</h2>
      <label>
        Title
        <input v-model="newRecipe.title" type="text" required placeholder="e.g. Tomato Soup" />
      </label>
      <label>
        Description
        <textarea
          v-model="newRecipe.description"
          required
          placeholder="Describe the recipe"
          rows="4"
        ></textarea>
      </label>
      <button type="submit" :disabled="saving">
        {{ saving ? 'Saving...' : 'Save recipe' }}
      </button>
      <p v-if="formError" class="error">{{ formError }}</p>
    </form>

    <p v-if="loading">Loading recipes...</p>
    <p v-else-if="error" class="error">{{ error }}</p>

    <ul v-else class="recipe-list">
      <li v-for="recipe in recipes" :key="recipe.id">
        <h2>{{ recipe.title }} ({{ recipe.rating }})</h2>
        <p>{{ recipe.description }}</p>
      </li>
    </ul>
  </main>
</template>
