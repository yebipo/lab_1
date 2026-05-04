import api from './axios.js'

export function getAllCategories(page = 0, size = 50) {
    return api.get(`/categories?page=${page}&size=${size}`)
}
export function getCategoryById(id) {
    return api.get(`/categories/${id}`)
}
export function createCategory(dto) {
    return api.post('/categories', dto)
}
export function updateCategory(id, dto) {
    return api.put(`/categories/${id}`, dto)
}
export function deleteCategory(id) {
    return api.delete(`/categories/${id}`)
}