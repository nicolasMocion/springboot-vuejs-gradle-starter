<script setup>
import { ref } from 'vue'
import SimilarityAnalysis from './components/SimilarityAnalysis.vue'
import RiskAndPatternAnalysis from './components/RiskAndPatternAnalysis.vue'
import MarketDashboard from './components/MarketDashboard.vue'
import StockChart from "@/components/StockChart.vue";

const activeTab = ref('dashboard')
</script>

<template>
  <div id="app">
    <header class="dodgers-header">
      <div class="header-content">
        <h1><span class="tech-prefix">CYBER</span> ANALYTICS <span class="dodger-dot">.</span></h1>
        <p class="subtitle">Análisis Algorítmico de Mercados Financieros</p>
      </div>

      <!-- Pestañas de Navegación -->
      <nav class="nav-tabs">
        <button :class="{ active: activeTab === 'stock' }" @click="activeTab = 'stock'">Stock chart </button>
        <button :class="{ active: activeTab === 'dashboard' }" @click="activeTab = 'dashboard'">Dashboard</button>
        <button :class="{ active: activeTab === 'similarity' }" @click="activeTab = 'similarity'">Similitud</button>
        <button :class="{ active: activeTab === 'risk' }" @click="activeTab = 'risk'">Riesgo y Patrones </button>
      </nav>

      <div class="dodger-red-line"></div>
    </header>

    <main class="dashboard-main">

      <section v-if="activeTab === 'stock'" class="card-futurista">
        <StockChart/>
      </section>

      <section v-if="activeTab === 'dashboard'" class="card-futurista">
        <MarketDashboard/>
      </section>

      <section v-if="activeTab === 'similarity'" class="card-futurista">
        <SimilarityAnalysis/>
      </section>

      <section v-if="activeTab === 'risk'" class="card-futurista">
        <RiskAndPatternAnalysis/>
      </section>
    </main>

    <footer class="dodgers-footer">
      <p>&copy; 2026 Universidad del Quindío - Ingeniería de Sistemas</p>
    </footer>
  </div>
</template>

<style>
/* =========================================================
   DEFINICIÓN DE LA PALETA DE COLORES (Dodgers & Futurist)
   ========================================================= */
:root {
  --dodger-blue: #005A9C;     /* Azul Real Dodgers */
  --dodger-red: #EF3E42;      /* Rojo Acento Dodgers */
  --dodger-white: #FFFFFF;    /* Blanco Puro uniformes de local */
  --dodger-silver: #A2AAAD;   /* Gris/Plata detalles uniformes visitante */

  /* Variaciones para efectos futuristas */
  --dark-bg: #F0F4F8;         /* Fondo muy claro con un toque azul */
  --card-bg: rgba(255, 255, 255, 0.95); /* Blanco translúcido */
  --blue-glow: rgba(0, 90, 156, 0.3);   /* Brillo azul para sombras */
  --text-dark: #1A1A1A;
}

/* =========================================================
   ESTILOS GLOBALES Y TIPOGRAFÍA MODERNA/FUTURISTA
   ========================================================= */
@import url('https://fonts.googleapis.com/css2?family=Exo+2:wght@300;400;600;700&display=swap');

body {
  margin: 0;
  padding: 0;
  font-family: 'Exo 2', sans-serif; /* Tipografía geométrica futurista */
  background-color: var(--dark-bg);
  color: var(--text-dark);
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

/* Estructura principal */
#app {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

/* =========================================================
   HEADER ESTILO DODGERS FUTURISTA
   ========================================================= */
.dodgers-header {
  background-color: var(--dodger-blue);
  color: var(--dodger-white);
  padding: 30px 20px 0 20px; /* Padding bottom es 0 por la línea roja */
  text-align: center;
  position: relative;
  /* Sombra de neón azul suave */
  box-shadow: 0 4px 15px var(--blue-glow);
  overflow: hidden;
}

/* Efecto geométrico sutil de fondo en el header (toque futurista) */
.dodgers-header::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -10%;
  width: 120%;
  height: 200%;
  background-image:
      linear-gradient(45deg, rgba(255,255,255,0.03) 25%, transparent 25%),
      linear-gradient(-45deg, rgba(255,255,255,0.03) 25%, transparent 25%);
  background-size: 60px 60px;
  transform: rotate(10deg);
}

.header-content {
  position: relative; /* Por encima del efecto geométrico */
  max-width: 1200px;
  margin: 0 auto;
}

.dodgers-header h1 {
  margin: 0;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 3px;
  font-size: 2.5rem;
  /* Brillo en el texto blanco */
  text-shadow: 0 0 10px rgba(255, 255, 255, 0.5);
}

.tech-prefix {
  font-weight: 300;
  color: var(--dodger-silver);
}

.dodger-dot {
  color: var(--dodger-red);
  animation: parpadeo 1.5s infinite;
}

.subtitle {
  margin: 5px 0 25px 0;
  font-weight: 400;
  color: var(--dodger-silver);
  letter-spacing: 1px;
}

/* La icónica línea roja decorativa de los Dodgers */
.dodger-red-line {
  height: 6px;
  background-color: var(--dodger-red);
  width: 100%;
  box-shadow: 0 -2px 10px rgba(239, 62, 66, 0.5);
}

/* =========================================================
   MAIN Y CONTENEDORES DE TARJETAS FUTURISTAS
   ========================================================= */
.dashboard-main {
  flex: 1;
  max-width: 1400px;
  width: 100%;
  margin: 0 auto;
  padding: 40px 20px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  gap: 40px; /* Espacio entre las tarjetas */
}

/* Estilo común para envolver tus componentes como tarjetas */
.card-futurista {
  background-color: var(--card-bg);
  border-radius: 12px;
  padding: 25px;
  /* Sombra suave con tinte azul de neón */
  box-shadow: 0 8px 32px var(--blue-glow);
  /* Borde sutil ciber-plateado */
  border: 1px solid rgba(162, 170, 173, 0.2);
  backdrop-filter: blur(4px); /* Efecto cristal esmerilado sutil */
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

/* Efecto 'hover' tecnológico al pasar el mouse por la tarjeta */
.card-futurista:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 40px rgba(0, 90, 156, 0.5); /* Aumenta el brillo azul */
  border: 1px solid rgba(0, 90, 156, 0.4);
}

/* =========================================================
   FOOTER
   ========================================================= */
.dodgers-footer {
  background-color: var(--dodger-blue);
  color: var(--dodger-silver);
  text-align: center;
  padding: 15px;
  border-top: 2px solid var(--dodger-red);
  font-size: 0.9rem;
}

/* =========================================================
   ANIMACIONES
   ========================================================= */
@keyframes parpadeo {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.3; }
}

/* Responsividad básica */
@media (max-width: 768px) {
  .dodgers-header h1 { font-size: 1.8rem; }
  .dashboard-main { padding: 20px 10px; gap: 20px; }
  .card-futurista { padding: 15px; }
}

/* Nuevos estilos para las pestañas de navegación */
.nav-tabs {
  display: flex;
  justify-content: center;
  gap: 15px;
  margin-top: 20px;
  padding-bottom: 20px;
  position: relative;
  z-index: 10; /* Asegura que sean cliqueables por encima del fondo */
}

.nav-tabs button {
  background-color: rgba(255, 255, 255, 0.1);
  color: var(--dodger-silver);
  border: 1px solid rgba(255,255,255,0.2);
  border-radius: 4px;
  padding: 10px 20px;
  font-size: 1rem;
  font-weight: 600;
  text-transform: uppercase;
  cursor: pointer;
  transition: all 0.3s ease;
}

.nav-tabs button:hover {
  background-color: rgba(255, 255, 255, 0.2);
  color: var(--dodger-white);
}

.nav-tabs button.active {
  background-color: var(--dodger-white);
  color: var(--dodger-blue);
  box-shadow: 0 0 15px rgba(255, 255, 255, 0.5);
  border-color: var(--dodger-white);
}




</style>