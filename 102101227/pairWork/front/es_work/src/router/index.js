import { createRouter, createWebHashHistory } from 'vue-router'
import DiceGameVue from '../views/DiceGameVue.vue'
import Option from '../views/OptionView.vue'
import VsPeople from '../views/GameVsPeople.vue'
import VsAi from '../views/GameVsAi.vue'

const routes = [
  {
    path: '/',
    name: 'home',
    component: Option
  },
  {
    path: '/game/local',
    name: 'GameVsLocal',
    component: DiceGameVue
  },
  {
    path: '/game/people',
    name: 'GameVsPeople',
    component: VsPeople
  },
  {
    path: '/game/ai',
    name: 'GameVsAi',
    component: VsAi
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

export default router
