import api from './axios.js'

export const login = (username, password) =>
    api.post('/auth/login', { username, password })

export const register = (dto) =>
    api.post('/auth/register', dto)