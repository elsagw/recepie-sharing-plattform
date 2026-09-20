<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { api } from './api'

const pathView = window.location.pathname === '/add-review'
  ? 'review'
  : window.location.pathname === '/saved'
    ? 'saved'
    : 'feed'
const view = ref(pathView)
const user = ref(null)
const feed = ref([])
const savedRecipes = ref([])
const loading = ref(true)
const saving = ref(null)
const message = ref('')
const error = ref('')
const authMode = ref('login')
const authForm = ref({ username: '', email: '', password: '' })
const authBusy = ref(false)
const reviewForm = ref({ url: '', rating: 8, comment: '' })
const preview = ref(null)
const reviewBusy = ref(false)
const isLoggedIn = computed(() => Boolean(user.value))

function clearNotice() {
  error.value = ''
  message.value = ''
}

async function loadFeed() {
  loading.value = true
  try {
    const page = await api.feed()
    feed.value = page.content || []
  } catch (err) {
    error.value = err.message
  } finally {
    loading.value = false
  }
}

async function loadSaved() {
  if (!isLoggedIn.value) return
  try {
    savedRecipes.value = await api.savedRecipes()
  } catch (err) {
    error.value = err.message
  }
}

async function loadSession() {
  try {
    user.value = await api.me()
    await loadSaved()
  } catch {
    user.value = null
  }
  await loadFeed()
}

async function submitAuth() {
  authBusy.value = true
  clearNotice()
  try {
    user.value = authMode.value === 'login'
      ? await api.login({ username: authForm.value.username, password: authForm.value.password })
      : await api.register(authForm.value)
    authForm.value = { username: '', email: '', password: '' }
    await loadSaved()
    view.value = 'feed'
    message.value = `Welcome, ${user.value.username}`
  } catch (err) {
    error.value = err.message
  } finally {
    authBusy.value = false
  }
}

async function logout() {
  await api.logout()
  user.value = null
  savedRecipes.value = []
  view.value = 'feed'
  message.value = 'You are signed out.'
}

async function scrapeRecipe() {
  reviewBusy.value = true
  clearNotice()
  try {
    preview.value = await api.scrape(reviewForm.value.url)
  } catch (err) {
    error.value = err.message
    preview.value = null
  } finally {
    reviewBusy.value = false
  }
}

async function publishReview() {
  reviewBusy.value = true
  clearNotice()
  try {
    const existing = feed.value.find((item) => item.recipeId === preview.value.id && item.username === user.value.username)
    const data = { rating: reviewForm.value.rating, comment: reviewForm.value.comment }
    if (existing) {
      await api.updateReview(preview.value.id, data)
    } else {
      try {
        await api.createReview({ recipeId: preview.value.id, ...data })
      } catch (err) {
        if (err.status !== 409) throw err
        await api.updateReview(preview.value.id, data)
      }
    }
    reviewForm.value = { url: '', rating: 8, comment: '' }
    preview.value = null
    await loadFeed()
    view.value = 'feed'
    message.value = 'Your review is live.'
  } catch (err) {
    error.value = err.message
  } finally {
    reviewBusy.value = false
  }
}

async function toggleSaved(item) {
  if (!isLoggedIn.value) {
    view.value = 'auth'
    return
  }
  saving.value = item.recipeId
  clearNotice()
  try {
    if (item.saved) {
      await api.deleteSavedRecipe(item.recipeId)
      item.saved = false
      savedRecipes.value = savedRecipes.value.filter((recipe) => recipe.recipeId !== item.recipeId)
      message.value = 'Removed from your list.'
    } else {
      const saved = await api.saveRecipe(item.recipeId)
      item.saved = true
      savedRecipes.value.unshift(saved)
      message.value = 'Added to your list.'
    }
  } catch (err) {
    error.value = err.message
  } finally {
    saving.value = null
  }
}

async function removeSaved(recipe) {
  saving.value = recipe.recipeId
  try {
    await api.deleteSavedRecipe(recipe.recipeId)
    savedRecipes.value = savedRecipes.value.filter((item) => item.recipeId !== recipe.recipeId)
    const feedItem = feed.value.find((item) => item.recipeId === recipe.recipeId)
    if (feedItem) feedItem.saved = false
  } catch (err) {
    error.value = err.message
  } finally {
    saving.value = null
  }
}

watch(view, (nextView) => {
  const path = nextView === 'review' ? '/add-review' : `/${nextView}`
  if (window.location.pathname !== path) {
    window.history.replaceState({}, '', path)
  }
})

onMounted(loadSession)
</script>

<template>
  <div class="app-shell">
    <header class="topbar">
      <button class="brand" type="button" @click="view = 'feed'"><span class="brand-mark">r/</span><span>recipe circle</span></button>
      <nav class="main-nav" aria-label="Main navigation">
        <button :class="{ active: view === 'feed' }" type="button" @click="view = 'feed'">Feed</button>
        <button v-if="isLoggedIn" :class="{ active: view === 'saved' }" type="button" @click="view = 'saved'; loadSaved()">Ska laga</button>
        <button v-if="isLoggedIn" class="primary-nav" type="button" @click="view = 'review'">+ Add review</button>
      </nav>
      <div class="account-area"><span v-if="user" class="user-chip">{{ user.username }}</span><button v-if="user" class="quiet-button" type="button" @click="logout">Log out</button><button v-else class="quiet-button" type="button" @click="view = 'auth'">Log in</button></div>
    </header>

    <main class="page">
      <div v-if="message" class="notice success">{{ message }}</div>
      <div v-if="error" class="notice error">{{ error }}</div>

      <section v-if="view === 'feed'" class="feed-page">
        <div class="page-intro"><div><p class="eyebrow">THE TASTIEST PART OF THE INTERNET</p><h1>What are people<br /><em>cooking lately?</em></h1></div><button v-if="user" class="outline-button" type="button" @click="view = 'review'">Share a review <span>↗</span></button></div>
        <div v-if="loading" class="empty-state">Loading the latest plates...</div>
        <div v-else-if="!feed.length" class="empty-state"><span class="empty-icon">✦</span><h2>The table is empty</h2><p>Be the first person to bring a recipe to the circle.</p><button class="primary-button" type="button" @click="user ? view = 'review' : view = 'auth'">Add the first review</button></div>
        <div v-else class="feed-list">
          <article v-for="item in feed" :key="`${item.recipeId}-${item.username}-${item.createdAt}`" class="feed-card"><img class="recipe-image" :src="item.imageUrl" :alt="item.title" /><div class="feed-card-body"><div class="card-meta"><span>{{ item.username }} reviewed</span><span>{{ new Date(item.createdAt).toLocaleDateString() }}</span></div><h2>{{ item.title }}</h2><p class="domain">{{ item.domain }}</p><div class="rating" :aria-label="`${item.rating} out of 10`"><span v-for="star in 10" :key="star" :class="{ filled: star <= item.rating }">★</span><b>{{ item.rating }}/10</b></div><p class="comment">“{{ item.comment }}”</p><button class="save-button" type="button" :disabled="saving === item.recipeId" @click="toggleSaved(item)"><span>{{ item.saved ? '♥' : '♡' }}</span> {{ item.saved ? 'Saved' : 'Save to my list' }}</button></div></article>
        </div>
      </section>

      <section v-else-if="view === 'review'" class="form-page"><p class="eyebrow">ADD TO THE CIRCLE</p><h1>Tell us what<br /><em>you cooked.</em></h1>
        <form v-if="!preview" class="editor-form" @submit.prevent="scrapeRecipe"><label for="recipe-url">Recipe URL</label><div class="url-row"><input id="recipe-url" v-model="reviewForm.url" type="url" required placeholder="https://your-favourite-recipe.com/..." /><button class="primary-button" :disabled="reviewBusy">{{ reviewBusy ? 'Reading...' : 'Preview recipe' }} <span>↗</span></button></div><p class="field-hint">We save only the recipe's public metadata, never the full instructions.</p></form>
        <form v-else class="review-editor" @submit.prevent="publishReview"><div class="preview-card"><img :src="preview.imageUrl" :alt="preview.title" /><div><p class="eyebrow">PREVIEW</p><h2>{{ preview.title }}</h2><p class="domain">{{ preview.domain }}</p></div></div><label for="rating">Your rating <strong>{{ reviewForm.rating }}/10</strong></label><input id="rating" v-model.number="reviewForm.rating" type="range" min="1" max="10" /><div class="range-labels"><span>Not for me</span><span>Make it again</span></div><label for="comment">Your note</label><textarea id="comment" v-model="reviewForm.comment" required maxlength="2000" rows="5" placeholder="What made this recipe worth sharing?"></textarea><div class="form-actions"><button class="quiet-button" type="button" @click="preview = null">Change URL</button><button class="primary-button" :disabled="reviewBusy">{{ reviewBusy ? 'Publishing...' : 'Publish review' }} <span>↗</span></button></div></form>
      </section>

      <section v-else-if="view === 'saved'" class="saved-page"><p class="eyebrow">YOUR PRIVATE SHELF</p><h1>Ska laga<br /><em>later.</em></h1><div v-if="!savedRecipes.length" class="empty-state"><span class="empty-icon">♡</span><h2>Nothing saved yet</h2><p>Keep the recipes that make you want to put on an apron.</p></div><div v-else class="saved-grid"><article v-for="recipe in savedRecipes" :key="recipe.recipeId" class="saved-card"><img :src="recipe.imageUrl" :alt="recipe.title" /><div><p class="domain">{{ recipe.domain }}</p><h2>{{ recipe.title }}</h2><button class="save-button" type="button" :disabled="saving === recipe.recipeId" @click="removeSaved(recipe)">Remove <span>×</span></button></div></article></div></section>

      <section v-else class="auth-page"><div class="auth-panel"><p class="eyebrow">WELCOME TO THE TABLE</p><h1>Good food<br /><em>travels.</em></h1><p class="auth-copy">Join a small, opinionated circle of people who like finding and sharing excellent recipes.</p><div class="auth-tabs"><button :class="{ active: authMode === 'login' }" type="button" @click="authMode = 'login'">Log in</button><button :class="{ active: authMode === 'register' }" type="button" @click="authMode = 'register'">Create account</button></div><form class="auth-form" @submit.prevent="submitAuth"><label>Username<input v-model="authForm.username" required autocomplete="username" /></label><label v-if="authMode === 'register'">Email<input v-model="authForm.email" type="email" required autocomplete="email" /></label><label>Password<input v-model="authForm.password" type="password" required minlength="8" autocomplete="current-password" /></label><button class="primary-button" :disabled="authBusy">{{ authBusy ? 'Working...' : authMode === 'login' ? 'Log in' : 'Create account' }} <span>↗</span></button></form></div></section>
    </main>
  </div>
</template>
