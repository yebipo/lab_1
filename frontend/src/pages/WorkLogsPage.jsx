import { useState, useEffect } from 'react'
import { getAllWorkLogs, createWorkLog, updateWorkLog, deleteWorkLog } from '../api/worklogApi.js'
import { getAllTasks } from '../api/taskApi.js'
import Modal from '../components/Modal.jsx'
import Pagination from '../components/Pagination.jsx'
import styles from './WorkLogsPage.module.css'

function fmt(dt) {
    if (!dt) return '—'
    return new Date(dt).toLocaleString('ru-RU', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' })
}

function toInputDate(dt) {
    if (!dt) return ''
    return new Date(dt).toISOString().slice(0, 16)
}

function WorkLogForm({ initial, tasks, onSave, onCancel }) {
    const [form, setForm] = useState({
        taskId: initial?.taskId || '',
        startTime: initial?.startTime ? toInputDate(initial.startTime) : '',
        durationMinutes: initial?.durationMinutes || 25,
        comment: initial?.comment || '',
        interruptionCount: initial?.interruptionCount || 0,
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
                taskId: Number(form.taskId),
                durationMinutes: Number(form.durationMinutes),
                interruptionCount: Number(form.interruptionCount),
                startTime: form.startTime ? new Date(form.startTime).toISOString() : null,
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
                <label className="label">Задача *</label>
                <select className="select" name="taskId" value={form.taskId} onChange={handle} required>
                    <option value="">— Выберите задачу —</option>
                    {tasks.map(t => <option key={t.id} value={t.id}>{t.title}</option>)}
                </select>
            </div>
            <div className="field">
                <label className="label">Время начала *</label>
                <input className="input" name="startTime" type="datetime-local" value={form.startTime} onChange={handle} required />
            </div>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' }}>
                <div className="field">
                    <label className="label">Длительность (мин) *</label>
                    <input className="input" name="durationMinutes" type="number" min="1" value={form.durationMinutes} onChange={handle} required />
                </div>
                <div className="field">
                    <label className="label">Кол-во прерываний</label>
                    <input className="input" name="interruptionCount" type="number" min="0" value={form.interruptionCount} onChange={handle} />
                </div>
            </div>
            <div className="field">
                <label className="label">Комментарий</label>
                <textarea className="input" name="comment" rows={2} value={form.comment} onChange={handle} style={{ resize: 'vertical' }} />
            </div>
            <div style={{ display: 'flex', gap: '10px', justifyContent: 'flex-end' }}>
                <button type="button" className="btn btn-ghost" onClick={onCancel}>Отмена</button>
                <button type="submit" className="btn btn-primary" disabled={saving}>{saving ? 'Сохранение...' : 'Сохранить'}</button>
            </div>
        </form>
    )
}

export default function WorkLogsPage() {
    const [logs, setLogs] = useState([])
    const [tasks, setTasks] = useState([])
    const [loading, setLoading] = useState(true)
    const [page, setPage] = useState(0)
    const [totalPages, setTotalPages] = useState(1)
    const [modal, setModal] = useState(null)

    const load = async (p = page) => {
        setLoading(true)
        try {
            const res = await getAllWorkLogs(p, 15)
            setLogs(res.data.content || [])
            setTotalPages(res.data.totalPages || 1)
        } finally {
            setLoading(false)
        }
    }

    useEffect(() => { load() }, [page])
    useEffect(() => {
        getAllTasks(0, 100).then(r => setTasks(r.data.content || []))
    }, [])

    const getTaskTitle = (id) => tasks.find(t => t.id === id)?.title || `Задача #${id}`

    const totalMinutes = logs.reduce((s, l) => s + (l.durationMinutes || 0), 0)

    const handleCreate = async (dto) => { await createWorkLog(dto); setModal(null); load() }
    const handleEdit = async (dto) => { await updateWorkLog(modal.log.id, dto); setModal(null); load() }
    const handleDelete = async (id) => {
        if (confirm('Удалить запись?')) { await deleteWorkLog(id); load() }
    }

    return (
        <div>
            <div className="page-header">
                <h1 className="page-title">Трекер времени <span>/ Work Logs</span></h1>
                <button className="btn btn-primary" onClick={() => setModal('create')}>+ Запись</button>
            </div>

            {/* Stats bar */}
            <div className={styles.statsBar}>
                <div className={styles.stat}>
                    <span className={styles.statNum}>{logs.length}</span>
                    <span className={styles.statLbl}>Записей на странице</span>
                </div>
                <div className={styles.stat}>
                    <span className={styles.statNum}>{totalMinutes}</span>
                    <span className={styles.statLbl}>Минут всего</span>
                </div>
                <div className={styles.stat}>
                    <span className={styles.statNum}>{Math.floor(totalMinutes / 60)}ч {totalMinutes % 60}м</span>
                    <span className={styles.statLbl}>Время</span>
                </div>
            </div>

            {loading
                ? <div className="spinner" />
                : (
                    <div className={styles.table}>
                        {logs.length === 0 && <p style={{ color: 'var(--text-muted)', padding: '20px' }}>Записей нет</p>}
                        {logs.map(log => (
                            <div key={log.id} className={styles.row}>
                                <div className={styles.rowMain}>
                                    <div className={styles.timeBlock}>
                                        <span className={styles.duration}>◷ {log.durationMinutes} мин</span>
                                        <span className={styles.startTime}>{fmt(log.startTime)}</span>
                                    </div>
                                    <div className={styles.taskRef}>
                                        <span className={styles.taskName}>⚔ {getTaskTitle(log.taskId)}</span>
                                        {log.comment && <span className={styles.comment}>{log.comment}</span>}
                                    </div>
                                    <div className={styles.meta}>
                                        {log.interruptionCount > 0 && (
                                            <span className={styles.interrupts} title="Прерываний">
                        ⚡ {log.interruptionCount}
                      </span>
                                        )}
                                    </div>
                                    <div className={styles.rowActions}>
                                        <button className="btn btn-ghost btn-sm" onClick={() => setModal({ log })}>✎</button>
                                        <button className="btn btn-danger btn-sm" onClick={() => handleDelete(log.id)}>✕</button>
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                )}

            <Pagination page={page} totalPages={totalPages} onChange={setPage} />

            {modal === 'create' && (
                <Modal title="Новая запись" onClose={() => setModal(null)}>
                    <WorkLogForm tasks={tasks} onSave={handleCreate} onCancel={() => setModal(null)} />
                </Modal>
            )}
            {modal?.log && (
                <Modal title="Редактировать запись" onClose={() => setModal(null)}>
                    <WorkLogForm initial={modal.log} tasks={tasks} onSave={handleEdit} onCancel={() => setModal(null)} />
                </Modal>
            )}
        </div>
    )
}