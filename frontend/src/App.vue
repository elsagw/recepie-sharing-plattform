<script setup>
import { onMounted, ref } from 'vue'

const recipes = ref([])
const loading = ref(true)
const error = ref('')

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

onMounted(loadRecipes)
</script>

<template>
  <main>
    <h1>Recipe Social Network</h1>

    <p v-if="loading">Loading recipes...</p>
    <p v-else-if="error">{{ error }}</p>

    <ul v-else>
      <li v-for="recipe in recipes" :key="recipe.id">
        <h2>{{ recipe.title }} ({{ recipe.rating }})</h2>
        <p>{{ recipe.description }}</p>
      </li>
    </ul>
  </main>
</template>
