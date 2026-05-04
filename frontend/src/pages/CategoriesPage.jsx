import { useState, useEffect } from 'react'
import { getAllCategories, createCategory, updateCategory, deleteCategory } from '../api/categoryApi.js'
import { getAllSkills } from '../api/skillApi.js'
import Modal from '../components/Modal.jsx'
import Pagination from '../components/Pagination.jsx'
import styles from './CategoriesPage.module.css'

function CategoryForm({ initial, onSave, onCancel }) {
    const [form, setForm] = useState({
        name: initial?.name || '',
        color: initial?.color || '#f0a500',
        description: initial?.description || '',
        iconUrl: initial?.iconUrl || '',
    })
    const [saving, setSaving] = useState(false)
    const [error, setError] = useState('')

    // FIX: используем функциональное обновление, чтобы не было stale closure
    const handle = (e) => {
        const { name, value } = e.target
        setForm(prev => ({ ...prev, [name]: value }))
    }

    const submit = async (e) => {
        e.preventDefault()
        setSaving(true)
        setError('')
        try {
            await onSave(form)
        } catch (err) {
            setError(err.response?.data?.message || 'Ошибка')
        } finally {
            setSaving(false)
        }
    }

    return (
        <form onSubmit={submit}>
            {error && <div className="error-msg">{error}</div>}
            <div className="field">
                <label className="label">Название *</label>
                <input className="input" name="name" value={form.name} onChange={handle} required />
            </div>
            <div className="field">
                <label className="label">Цвет *</label>
                <div style={{ display: 'flex', gap: '10px', alignItems: 'center' }}>
                    <input className="input" name="color" value={form.color} onChange={handle} required style={{ flex: 1 }} />
                    <input type="color" value={form.color}
                           onChange={e => setForm(prev => ({ ...prev, color: e.target.value }))}
                           style={{ width: 40, height: 40, border: 'none', background: 'none', cursor: 'pointer' }} />
                </div>
            </div>
            <div className="field">
                <label className="label">Описание</label>
                <textarea className="input" name="description" rows={3} value={form.description} onChange={handle} style={{ resize: 'vertical' }} />
            </div>
            <div className="field">
                <label className="label">URL иконки</label>
                {/* FIX: добавлен предпросмотр иконки рядом с полем */}
                <div style={{ display: 'flex', gap: '10px', alignItems: 'center' }}>
                    <input
                        className="input"
                        name="iconUrl"
                        placeholder="https://example.com/icon.png"
                        value={form.iconUrl}
                        onChange={handle}
                        style={{ flex: 1 }}
                    />
                    {form.iconUrl && (
                        <img
                            src={form.iconUrl}
                            alt="preview"
                            style={{ width: 36, height: 36, objectFit: 'contain', borderRadius: 6, border: '1px solid var(--border)' }}
                            onError={e => { e.target.style.display = 'none' }}
                        />
                    )}
                </div>
            </div>
            <div style={{ display: 'flex', gap: '10px', justifyContent: 'flex-end' }}>
                <button type="button" className="btn btn-ghost" onClick={onCancel}>Отмена</button>
                <button type="submit" className="btn btn-primary" disabled={saving}>
                    {saving ? 'Сохранение...' : 'Сохранить'}
                </button>
            </div>
        </form>
    )
}

export default function CategoriesPage() {
    const [categories, setCategories] = useState([])
    const [skills, setSkills] = useState([])
    const [loading, setLoading] = useState(true)
    const [page, setPage] = useState(0)
    const [totalPages, setTotalPages] = useState(1)
    const [modal, setModal] = useState(null)

    const load = async (p = page) => {
        setLoading(true)
        try {
            const res = await getAllCategories(p, 10)
            setCategories(res.data.content || [])
            setTotalPages(res.data.totalPages || 1)
        } finally {
            setLoading(false)
        }
    }

    useEffect(() => { load() }, [page])
    useEffect(() => {
        getAllSkills(0, 100).then(r => setSkills(r.data.content || []))
    }, [])

    const handleCreate = async (dto) => {
        await createCategory(dto)
        setModal(null)
        load()
    }

    const handleEdit = async (dto) => {
        await updateCategory(modal.cat.id, dto)
        setModal(null)
        load()
    }

    const handleDelete = async (id) => {
        if (confirm('Удалить категорию?')) {
            await deleteCategory(id)
            load()
        }
    }

    // FIX: добавлен запасной вариант — если backend возвращает навыки внутри категории,
    // используем их; иначе фильтруем отдельно загруженные навыки по categoryId
    const getSkillsForCategory = (cat) => {
        if (cat.skills && cat.skills.length > 0) return cat.skills
        return skills.filter(s => s.categoryId === cat.id)
    }

    return (
        <div>
            <div className="page-header">
                <h1 className="page-title">Категории <span>/ Categories</span></h1>
                <button className="btn btn-primary" onClick={() => setModal('create')}>+ Категория</button>
            </div>

            {loading
                ? <div className="spinner" />
                : (
                    <div className={styles.grid}>
                        {categories.length === 0 && (
                            <p style={{ color: 'var(--text-muted)', gridColumn: '1/-1' }}>Категорий нет</p>
                        )}
                        {categories.map(cat => {
                            const catSkills = getSkillsForCategory(cat)
                            return (
                                <div key={cat.id} className={styles.card}>
                                    <div className={styles.cardTop}>
                                        {/* FIX: отображаем иконку если есть iconUrl, иначе цветной dot */}
                                        {cat.iconUrl
                                            ? (
                                                <img
                                                    src={cat.iconUrl}
                                                    alt={cat.name}
                                                    style={{ width: 24, height: 24, objectFit: 'contain', borderRadius: 4 }}
                                                    onError={e => { e.target.replaceWith(Object.assign(document.createElement('div'), {
                                                        className: styles.colorDot,
                                                        style: `background: ${cat.color}`
                                                    })) }}
                                                />
                                            )
                                            : <div className={styles.colorDot} style={{ background: cat.color }} />
                                        }
                                        <span className={styles.catName}>{cat.name}</span>
                                        <div className={styles.actions}>
                                            <button className="btn btn-ghost btn-sm" onClick={() => setModal({ cat })}>✎</button>
                                            <button className="btn btn-danger btn-sm" onClick={() => handleDelete(cat.id)}>✕</button>
                                        </div>
                                    </div>
                                    {cat.description && <p className={styles.desc}>{cat.description}</p>}

                                    {/* OneToMany: Category → Skills */}
                                    <div className={styles.relation}>
                                        <span className={styles.relLabel}>
                                            Навыки ({catSkills.length}):
                                        </span>
                                        {catSkills.length === 0
                                            ? <span className={styles.noSkills}>нет навыков</span>
                                            : catSkills.map(s => (
                                                <span key={s.id} className={styles.skillChip}>
                                                    ✦ {s.name}
                                                    {s.level != null && <span className={styles.lvl}>Ур.{s.level}</span>}
                                                </span>
                                            ))
                                        }
                                    </div>
                                </div>
                            )
                        })}
                    </div>
                )}

            <Pagination page={page} totalPages={totalPages} onChange={setPage} />

            {modal === 'create' && (
                <Modal title="Новая категория" onClose={() => setModal(null)}>
                    <CategoryForm onSave={handleCreate} onCancel={() => setModal(null)} />
                </Modal>
            )}
            {modal?.cat && (
                <Modal title="Редактировать категорию" onClose={() => setModal(null)}>
                    <CategoryForm initial={modal.cat} onSave={handleEdit} onCancel={() => setModal(null)} />
                </Modal>
            )}
        </div>
    )
}