import api from './axios.js'

export const getAllSkills = (page = 0, size = 50) =>
    api.get(`/skills?page=${page}&size=${size}`)
export const getSkillById = (id) => api.get(`/skills/${id}`)
export const createSkill = (dto) => api.post('/skills', dto)
export const updateSkill = (id, dto) => api.put(`/skills/${id}`, dto)
export const deleteSkill = (id) => api.delete(`/skills/${id}`)