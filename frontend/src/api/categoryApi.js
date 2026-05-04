import api from './axios.js'

export const getAllTasks = (page = 0, size = 50) =>
    api.get(`/tasks?page=${page}&size=${size}`)

export const getTaskById = (id) => api.get(`/tasks/${id}`)

export const searchTasks = ({ skillId, status, title, page = 0, size = 50 }) => {
    const params = new URLSearchParams({ page, size })
    if (skillId) params.append('skillId', skillId)
    if (status) params.append('status', status)
    if (title) params.append('title', title)
    return api.get(`/tasks/search?${params}`)
}

export const createTask = (dto) => api.post('/tasks', dto)
export const updateTask = (id, dto) => api.put(`/tasks/${id}`, dto)
export const deleteTask = (id) => api.delete(`/tasks/${id}`)