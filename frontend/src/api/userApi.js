import api from './axios.js'

export const getMe = () => api.get('/users/me')
export const updateMe = (dto) => api.put('/users/me', dto)
export const getAllUsers = (page = 0, size = 10) =>
    api.get(`/users?page=${page}&size=${size}`)
export const getUserById = (id) => api.get(`/users/${id}`)
export const createUser = (dto) => api.post('/users', dto)
export const updateUser = (id, dto) => api.put(`/users/${id}`, dto)
export const deleteUser = (id) => api.delete(`/users/${id}`)