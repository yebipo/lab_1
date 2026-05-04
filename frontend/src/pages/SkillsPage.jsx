import { useState, useEffect } from 'react'
import { getAllSkills, createSkill, updateSkill, deleteSkill } from '../api/skillApi.js'
import { getAllCategories } from '../api/categoryApi.js'
import { getAllTasks } from '../api/taskApi.js'
import Modal from '../components/Modal.jsx'
import Pagination from '../components/Pagination.jsx'
import styles from './SkillsPage.module.css'

function XpBar({ current, required }) {
    const pct = required ? Math.min(100, Math.round((current / required) * 100)) : 0
    return (
        <div className={styles.xpBar}>
            <div className={styles.xpFill} style={{ width: `${pct}%` }} />
        </div>
    )
}

function SkillForm({ initial, categories, onSave, onCancel }) {
    const [form, setForm] = useState({
        name: initial?.name || '',
        description: initial?.description || '',
        iconUrl: initial?.iconUrl || '',
        level: initial?.level || 1,
        requiredXp: initial?.requiredXp || 100,
        categoryId: initial?.categoryId || '',
    })
    const [saving, setSaving] = useState(false)
    const [error, setError] = useState('')

    const handle = (e) => setForm({ ...form, [e.target.name]: e.target.value })

    const submit = async (e) => {
        e.preventDefault()
        setSaving(true)
        setError('')
        try {
            const dto = {
                ...form,
                level: Number(form.level),
                requiredXp: Number(form.requiredXp),
                categoryId: form.categoryId ? Number(form.categoryId) : null,
            }
            await onSave(dto)
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
                <label className="label">Описание</label>
                <textarea className="input" name="description" rows={2} value={form.description} onChange={handle} style={{ resize: 'vertical' }} />
            </div>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' }}>
                <div className="field">
                    <label className="label">Уровень</label>
                    <input className="input" name="level" type="number" min="1" value={form.level} onChange={handle} />
                </div>
                <div className="field">
                    <label className="label">Требуемый XP</label>
                    <input className="input" name="requiredXp" type="number" min="1" value={form.requiredXp} onChange={handle} />
                </div>
            </div>
            <div className="field">
                <label className="label">Категория</label>
                <select className="select" name="categoryId" value={form.categoryId} onChange={handle}>
                    <option value="">— Без категории —</option>
                    {categories.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
                </select>
            </div>
            <div className="field">
                <label className="label">URL иконки</label>
                <input className="input" name="iconUrl" placeholder="https://..." value={form.iconUrl} onChange={handle} />
            </div>
            <div style={{ display: 'flex', gap: '10px', justifyContent: 'flex-end' }}>
                <button type="button" className="btn btn-ghost" onClick={onCancel}>Отмена</button>
                <button type="submit" className="btn btn-primary" disabled={saving}>{saving ? 'Сохранение...' : 'Сохранить'}</button>
            </div>
        </form>
    )
}

export default function SkillsPage() {
    const [skills, setSkills] = useState([])
    const [categories, setCategories] = useState([])
    const [tasks, setTasks] = useState([])
    const [loading, setLoading] = useState(true)
    const [page, setPage] = useState(0)
    const [totalPages, setTotalPages] = useState(1)
    const [modal, setModal] = useState(null)

    const load = async (p = page) => {
        setLoading(true)
        try {
            const res = await getAllSkills(p, 10)
            setSkills(res.data.content || [])
            setTotalPages(res.data.totalPages || 1)
        } finally {
            setLoading(false)
        }
    }

    useEffect(() => { load() }, [page])
    useEffect(() => {
        getAllCategories(0, 100).then(r => setCategories(r.data.content || []))
        getAllTasks(0, 100).then(r => setTasks(r.data.content || []))
    }, [])

    const handleCreate = async (dto) => { await createSkill(dto); setModal(null); load() }
    const handleEdit = async (dto) => { await updateSkill(modal.skill.id, dto); setModal(null); load() }
    const handleDelete = async (id) => {
        if (confirm('Удалить навык?')) { await deleteSkill(id); load() }
    }

    const getTasksForSkill = (skillId) => tasks.filter(t => (t.skillIds || []).includes(skillId))
    const getCategoryName = (catId) => categories.find(c => c.id === catId)?.name

    return (
        <div>
            <div className="page-header">
                <h1 className="page-title">Навыки <span>/ Skills</span></h1>
                <button className="btn btn-primary" onClick={() => setModal('create')}>+ Навык</button>
            </div>

            {loading
                ? <div className="spinner" />
                : (
                    <div className={styles.list}>
                        {skills.length === 0 && <p style={{ color: 'var(--text-muted)' }}>Навыков нет</p>}
                        {skills.map(skill => {
                            const catName = getCategoryName(skill.categoryId)
                            const linkedTasks = getTasksForSkill(skill.id)
                            return (
                                <div key={skill.id} className={styles.skillCard}>
                                    <div className={styles.skillMain}>
                                        <div className={styles.skillInfo}>
                                            <div className={styles.skillHeader}>
                                                <span className={styles.skillName}>✦ {skill.name}</span>
                                                <span className={styles.skillLevel}>Ур. {skill.level}</span>
                                                {catName && (
                                                    <span className={styles.catChip}>◈ {catName}</span>
                                                )}
                                            </div>
                                            {skill.description && <p className={styles.skillDesc}>{skill.description}</p>}
                                            <div className={styles.xpRow}>
                                                <XpBar current={skill.currentXp || 0} required={skill.requiredXp || 100} />
                                                <span className={styles.xpText}>
                          {skill.currentXp ?? 0} / {skill.requiredXp ?? 100} XP
                        </span>
                                            </div>
                                        </div>
                                        <div className={styles.skillActions}>
                                            <button className="btn btn-ghost btn-sm" onClick={() => setModal({ skill })}>✎</button>
                                            <button className="btn btn-danger btn-sm" onClick={() => handleDelete(skill.id)}>✕</button>
                                        </div>
                                    </div>

                                    {/* ManyToMany: Skill ↔ Tasks */}
                                    <div className={styles.taskRelation}>
                                        <span className={styles.relLabel}>Задачи с этим навыком:</span>
                                        {linkedTasks.length === 0
                                            ? <span className={styles.noTasks}>нет связанных задач</span>
                                            : linkedTasks.map(t => (
                                                <span key={t.id} className={styles.taskChip}>{t.title}</span>
                                            ))}
                                    </div>
                                </div>
                            )
                        })}
                    </div>
                )}

            <Pagination page={page} totalPages={totalPages} onChange={setPage} />

            {modal === 'create' && (
                <Modal title="Новый навык" onClose={() => setModal(null)}>
                    <SkillForm categories={categories} onSave={handleCreate} onCancel={() => setModal(null)} />
                </Modal>
            )}
            {modal?.skill && (
                <Modal title="Редактировать навык" onClose={() => setModal(null)}>
                    <SkillForm initial={modal.skill} categories={categories} onSave={handleEdit} onCancel={() => setModal(null)} />
                </Modal>
            )}
        </div>
    )
}