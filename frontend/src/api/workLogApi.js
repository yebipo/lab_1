import api from './axios.js'

export const getAllWorkLogs = (page = 0, size = 20) =>
  api.get(`/worklogs?page=${page}&size=${size}`)
export const getWorkLogById = (id) => api.get(`/worklogs/${id}`)
export const createWorkLog = (dto) => api.post('/worklogs', dto)
export const updateWorkLog = (id, dto) => api.put(`/worklogs/${id}`, dto)
export const deleteWorkLog = (id) => api.delete(`/worklogs/${id}`)