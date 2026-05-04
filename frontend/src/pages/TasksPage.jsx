import { useState, useEffect, useCallback } from 'react'
import { getAllTasks, searchTasks, createTask, updateTask, deleteTask } from '../api/taskApi.js'
import { getAllSkills } from '../api/skillApi.js'
import Modal from '../components/Modal.jsx'
import styles from './TasksPage.module.css'

const STATUSES = [
    { key: 'TODO',        label: 'К выполнению',  cls: 'badge-todo',     icon: '○' },
    { key: 'IN_PROGRESS', label: 'В процессе',     cls: 'badge-progress', icon: '◑' },
    { key: 'DONE',        label: 'Выполнено',      cls: 'badge-done',     icon: '●' },
]

function StatusBadge({ status }) {
    const s = STATUSES.find(x => x.key === status) || STATUSES[0]
    return <span className={`badge ${s.cls}`}>{s.icon} {s.label}</span>
}

function TaskRow({ task, skills, onEdit, onDelete, onStatusChange }) {
    const [open, setOpen] = useState(false)
    const skillNames = (task.skillIds || [])
        .map(id => skills.find(s => s.id === id)?.name).filter(Boolean)

    return (
        <div className={`${styles.taskRow} ${open ? styles.expanded : ''}`}>
            <div className={styles.taskHeader} onClick={() => setOpen(!open)}>
                <span className={`${styles.arrow} ${open ? styles.arrowOpen : ''}`}>›</span>
                <span className={styles.taskTitle}>{task.title}</span>
                <StatusBadge status={task.status} />
                {task.focusScore != null && (
                    <span className={styles.focusScore} title="Focus score">⚡ {task.focusScore}</span>
                )}
            </div>

            {open && (
                <div className={styles.taskBody}>
                    {task.description && (
                        <p className={styles.taskDesc}>{task.description}</p>
                    )}

                    {skillNames.length > 0 && (
                        <div className={styles.skillTags}>
                            <span className={styles.skillLabel}>Навыки (ManyToMany):</span>
                            {skillNames.map(n => (
                                <span key={n} className={styles.skillTag}>✦ {n}</span>
                            ))}
                        </div>
                    )}

                    <div className={styles.statusRow}>
                        <span className={styles.skillLabel}>Изменить статус:</span>
                        {STATUSES.map(s => (
                            <button
                                key={s.key}
                                className={`${styles.statusBtn} ${task.status === s.key ? styles.statusBtnActive : ''}`}
                                onClick={() => onStatusChange(task, s.key)}
                                disabled={task.status === s.key}
                            >
                                {s.icon} {s.label}
                            </button>
                        ))}
                    </div>

                    <div className={styles.taskActions}>
                        <button className="btn btn-ghost btn-sm" onClick={() => onEdit(task)}>✎ Редактировать</button>
                        <button className="btn btn-danger btn-sm" onClick={() => onDelete(task.id)}>✕ Удалить</button>
                    </div>
                </div>
            )}
        </div>
    )
}

function TaskGroup({ status, tasks, skills, onEdit, onDelete, onStatusChange }) {
    const [open, setOpen] = useState(true)
    const s = STATUSES.find(x => x.key === status)
    const colorMap = { TODO: 'var(--text-secondary)', IN_PROGRESS: 'var(--blue)', DONE: 'var(--green)' }

    return (
        <div className={styles.group}>
            <button className={styles.groupHeader} onClick={() => setOpen(!open)}>
                <span className={`${styles.groupArrow} ${open ? styles.groupArrowOpen : ''}`}>▸</span>
                <span className={styles.groupTitle} style={{ color: colorMap[status] }}>
          {s?.icon} {s?.label}
        </span>
                <span className={styles.groupCount}>{tasks.length}</span>
            </button>
            {open && (
                <div className={styles.groupBody}>
                    {tasks.length === 0
                        ? <p className={styles.empty}>Задач нет</p>
                        : tasks.map(t => (
                            <TaskRow
                                key={t.id}
                                task={t}
                                skills={skills}
                                onEdit={onEdit}
                                onDelete={onDelete}
                                onStatusChange={onStatusChange}
                            />
                        ))}
                </div>
            )}
        </div>
    )
}

function TaskForm({ initial, skills, onSave, onCancel }) {
    const [form, setForm] = useState({
        title: initial?.title || '',
        description: initial?.description || '',
        status: initial?.status || 'TODO',
        skillIds: initial?.skillIds ? [...initial.skillIds] : [],
    })
    const [saving, setSaving] = useState(false)
    const [error, setError] = useState('')

    const toggleSkill = (id) => {
        setForm(f => ({
            ...f,
            skillIds: f.skillIds.includes(id)
                ? f.skillIds.filter(x => x !== id)
                : [...f.skillIds, id]
        }))
    }

    const submit = async (e) => {
        e.preventDefault()
        setSaving(true)
        setError('')
        try {
            await onSave({ ...form, skillIds: new Set(form.skillIds) })
        } catch (err) {
            setError(err.response?.data?.message || 'Ошибка сохранения')
        } finally {
            setSaving(false)
        }
    }

    return (
        <form onSubmit={submit}>
            {error && <div className="error-msg">{error}</div>}
            <div className="field">
                <label className="label">Заголовок *</label>
                <input className="input" value={form.title} onChange={e => setForm({...form, title: e.target.value})} required />
            </div>
            <div className="field">
                <label className="label">Описание</label>
                <textarea className="input" rows={3} value={form.description} onChange={e => setForm({...form, description: e.target.value})} style={{resize:'vertical'}} />
            </div>
            {initial && (
                <div className="field">
                    <label className="label">Статус</label>
                    <select className="select" value={form.status} onChange={e => setForm({...form, status: e.target.value})}>
                        {STATUSES.map(s => <option key={s.key} value={s.key}>{s.label}</option>)}
                    </select>
                </div>
            )}
            {skills.length > 0 && (
                <div className="field">
                    <label className="label">Навыки (ManyToMany)</label>
                    <div className={styles.skillSelect}>
                        {skills.map(s => (
                            <label key={s.id} className={`${styles.skillCheck} ${form.skillIds.includes(s.id) ? styles.skillChecked : ''}`}>
                                <input
                                    type="checkbox"
                                    checked={form.skillIds.includes(s.id)}
                                    onChange={() => toggleSkill(s.id)}
                                    style={{ display: 'none' }}
                                />
                                ✦ {s.name} <span style={{color:'var(--text-muted)',fontSize:'11px'}}>Ур.{s.level}</span>
                            </label>
                        ))}
                    </div>
                </div>
            )}
            <div style={{ display: 'flex', gap: '10px', justifyContent: 'flex-end' }}>
                <button type="button" className="btn btn-ghost" onClick={onCancel}>Отмена</button>
                <button type="submit" className="btn btn-primary" disabled={saving}>
                    {saving ? 'Сохранение...' : 'Сохранить'}
                </button>
            </div>
        </form>
    )
}

export default function TasksPage() {
    const [tasks, setTasks] = useState([])
    const [skills, setSkills] = useState([])
    const [loading, setLoading] = useState(true)
    const [modal, setModal] = useState(null) // null | 'create' | {task}
    const [filter, setFilter] = useState({ title: '', skillId: '', status: '' })

    const load = useCallback(async () => {
        setLoading(true)
        try {
            const hasFilter = filter.title || filter.skillId || filter.status
            const res = hasFilter
                ? await searchTasks({ ...filter, size: 100 })
                : await getAllTasks(0, 100)
            setTasks(res.data.content || [])
        } finally {
            setLoading(false)
        }
    }, [filter])

    useEffect(() => { load() }, [load])

    useEffect(() => {
        getAllSkills(0, 100).then(r => setSkills(r.data.content || []))
    }, [])

    const grouped = STATUSES.reduce((acc, s) => {
        acc[s.key] = tasks.filter(t => t.status === s.key || (!t.status && s.key === 'TODO'))
        return acc
    }, {})

    const handleCreate = async (dto) => {
        await createTask(dto)
        setModal(null)
        load()
    }

    const handleEdit = async (dto) => {
        await updateTask(modal.task.id, dto)
        setModal(null)
        load()
    }

    const handleDelete = async (id) => {
        if (confirm('Удалить задачу?')) {
            await deleteTask(id)
            load()
        }
    }

    const handleStatusChange = async (task, status) => {
        await updateTask(task.id, { title: task.title, description: task.description, status, skillIds: task.skillIds })
        load()
    }

    return (
        <div>
            <div className="page-header">
                <h1 className="page-title">Задачи <span>/ Tasks</span></h1>
                <button className="btn btn-primary" onClick={() => setModal('create')}>
                    + Новая задача
                </button>
            </div>

            {/* Filters */}
            <div className={styles.filters}>
                <input
                    className="input"
                    placeholder="🔍 Поиск по названию..."
                    value={filter.title}
                    onChange={e => setFilter({...filter, title: e.target.value})}
                    style={{ flex: 2 }}
                />
                <select
                    className="select"
                    value={filter.skillId}
                    onChange={e => setFilter({...filter, skillId: e.target.value})}
                    style={{ flex: 1 }}
                >
                    <option value="">Все навыки</option>
                    {skills.map(s => <option key={s.id} value={s.id}>{s.name}</option>)}
                </select>
                <select
                    className="select"
                    value={filter.status}
                    onChange={e => setFilter({...filter, status: e.target.value})}
                    style={{ flex: 1 }}
                >
                    <option value="">Все статусы</option>
                    {STATUSES.map(s => <option key={s.key} value={s.key}>{s.label}</option>)}
                </select>
                {(filter.title || filter.skillId || filter.status) && (
                    <button className="btn btn-ghost btn-sm" onClick={() => setFilter({ title: '', skillId: '', status: '' })}>
                        ✕ Сбросить
                    </button>
                )}
            </div>

            {loading
                ? <div className="spinner" />
                : STATUSES.map(s => (
                    <TaskGroup
                        key={s.key}
                        status={s.key}
                        tasks={grouped[s.key]}
                        skills={skills}
                        onEdit={(task) => setModal({ task })}
                        onDelete={handleDelete}
                        onStatusChange={handleStatusChange}
                    />
                ))}

            {modal === 'create' && (
                <Modal title="Новая задача" onClose={() => setModal(null)}>
                    <TaskForm skills={skills} onSave={handleCreate} onCancel={() => setModal(null)} />
                </Modal>
            )}

            {modal?.task && (
                <Modal title="Редактировать задачу" onClose={() => setModal(null)}>
                    <TaskForm initial={modal.task} skills={skills} onSave={handleEdit} onCancel={() => setModal(null)} />
                </Modal>
            )}
        </div>
    )
}